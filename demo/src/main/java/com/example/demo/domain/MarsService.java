package com.example.demo.domain;

import com.example.demo.exception.NoDataFoundException;
import com.example.demo.model.DataSyncInfo;
import com.example.demo.model.MarsDailyWeather;
import com.example.demo.repo.DataSyncInfoRepo;
import com.example.demo.repo.MarsRepo;
import com.example.demo.web.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarsService {

    private final NasaClient nasaClient;
    private final MarsMapper marsMapper;
    private final MarsRepo marsRepo;
    private final DataSyncInfoRepo syncRepo;

    private static final int OUTDATED_AFTER_DAYS = 7;
    private static final int MARTIAN_YEAR_IN_DAYS = 687;
    private static int forecastDaysForward;

    private Optional<List<MarsWeatherDetailsDto>> getValidSolesIfPresent(MarsWeatherDto marsWeatherDto) {
        return Optional.ofNullable(marsWeatherDto)
                .map(MarsWeatherDto::soles)
                .map(soles -> soles.stream()
                        .filter(this::hasNoNullFields)
                        .toList())
                .filter(soles -> !soles.isEmpty());
    }

    boolean hasNoNullFields(Object obj) {
        for (Field field: obj.getClass().getDeclaredFields()){
            field.setAccessible(true);
            try {
                if (field.get(obj) == null || field.get(obj).equals("--")){
                    return false;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    public void initData(){
        if (hasAnyRecords()){
            if (isDataOutdated()){
                log.info("Baza danych jest przestarzała - wykonuje aktualizację");
                synchronizeMarsWeatherData();
            } else {
                log.info("Baza danych aktualna - nie wymagana aktualizacja");
            }
        } else {
            log.info("Brak rekordów w bazie - pobieram dane i zapisuję");
            synchronizeMarsWeatherData();
        }
    }

    private boolean hasAnyRecords() {
        return marsRepo.count() > 0;
    }

    boolean isDataOutdated() {
        Optional<DataSyncInfo> lastUpdate = syncRepo.findFirstByOrderByLastUpdateDesc();
        return lastUpdate.isPresent() && lastUpdate.get().getLastUpdate().isBefore(LocalDateTime.now().minusDays(OUTDATED_AFTER_DAYS));
    }

    private void synchronizeMarsWeatherData() {
        Optional<List<MarsWeatherDetailsDto>> validSoles = getValidSolesIfPresent(fetchMarsWeatherFromNasa());
        validSoles.ifPresent(this::processAndSaveSoles);
    }

    private void processAndSaveSoles(List<MarsWeatherDetailsDto> soles) {
        List<MarsDailyWeather> entitiesFromDb = marsRepo.findAll();
        List<MarsDailyWeather> entities = soles.stream()
                .map(marsMapper::toEntityFromDto)
                .filter(solToCheck -> isUniqueRecord(solToCheck, entitiesFromDb))
                .toList();
        saveAllWeatherRecords(entities);
        saveSyncInfo();
    }

    private void saveAllWeatherRecords(List<MarsDailyWeather> entities) {
        marsRepo.saveAll(entities);
        log.info("Zapisano {} rekordów do bazy", entities.size());
    }

    private void saveSyncInfo() {
        syncRepo.save(new DataSyncInfo(LocalDateTime.now()));
        log.info("Synchronizacja danych zakończona o {}", LocalDateTime.now());
    }

    private boolean isUniqueRecord(MarsDailyWeather solToCheck, List<MarsDailyWeather> entities){
        return entities != null && !entities.contains(solToCheck);
    }

    private MarsWeatherDto fetchMarsWeatherFromNasa() {
        try {
            return nasaClient.getWeather();
        } catch (Exception e) {
            log.error("Błąd podczas pobierania danych pogodowych z NASA");
            //todo ogarnij to jakoś lepiej
            return null;
        }
    }

    public List<MarsDailyWeather> removeRecords(){
        List<MarsDailyWeather> recordsToRemove = marsRepo.findFirst7ByOrderBySolDesc();
        log.info("Ilość rekordów do skasowania {}, Liczba rekordów w bazie przed skasowanie {}", recordsToRemove.size(), marsRepo.count());
        marsRepo.deleteAll(recordsToRemove);
        log.info("Liczba rekordów w bazie, po skasowaniu {}", marsRepo.count());
        return recordsToRemove;
    }

    public void synchronizeData(){
        synchronizeMarsWeatherData();
    }

    public List<SolDataDto> aggregateWeatherForLast7Days(){
        List<MarsDailyWeather> last7Days = marsRepo.findFirst7ByOrderBySolDesc();
        List<SolDataDto> solDataDto = new ArrayList<>();
        last7Days.forEach(sol -> {
            List<Characteristics> characteristics = new ArrayList<>();
            characteristics.add(new Characteristics("MaxTemp", sol.getMaxTemp()));
            characteristics.add(new Characteristics("MinTemp", sol.getMinTemp()));
            characteristics.add(new Characteristics("Date", sol.getTerrestrialDate()));
            characteristics.add(new Characteristics("DayName", getDayOfWeek(sol.getTerrestrialDate())));
            solDataDto.add(buildSolDataDto(sol.getSol(), characteristics));
        });
        return solDataDto;
    }

    private String getDayOfWeek(String dateStr){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateStr, formatter);
        return date.getDayOfWeek().toString();
    }

    private static SolDataDto buildSolDataDto(int sol, List<Characteristics> characteristics) {
        return new SolDataDto(sol, characteristics);
    }

    public SolDataDto getWeatherDetailsForSol(int solNum){
        if (solNum < 0) {
            throw new IllegalArgumentException("Invalid sol number, must be a non-negative value");
        }
        Optional<MarsDailyWeather> solEntity = marsRepo.findBySol(solNum);
        return solEntity.map(sol -> {
            List<Characteristics> characteristics = new ArrayList<>();
            characteristics.add(new Characteristics("Date", sol.getTerrestrialDate()));
            characteristics.add(new Characteristics("DayName", getDayOfWeek(sol.getTerrestrialDate())));
            characteristics.add(new Characteristics("MaxTemp", sol.getMaxTemp()));
            characteristics.add(new Characteristics("MinTemp", sol.getMinTemp()));
            characteristics.add(new Characteristics("MaxGtsTemp", sol.getMaxGtsTemp()));
            characteristics.add(new Characteristics("MinGtsTemp", sol.getMinGtsTemp()));
            characteristics.add(new Characteristics("Pressure", sol.getPressure()));
            characteristics.add(new Characteristics("UV", sol.getLocalUvIrradianceIndex()));
            characteristics.add(new Characteristics("Opacity", sol.getAtmoOpacity()));
            characteristics.add(new Characteristics("Sunrise", sol.getSunrise()));
            characteristics.add(new Characteristics("Sunset", sol.getSunset()));
            characteristics.add(new Characteristics("Month", sol.getSeason().replace("Month ", "")));
            characteristics.add(new Characteristics("Season", getMartianSeason(sol.getLs())));
            return buildSolDataDto(sol.getSol(),characteristics);
        }).orElseThrow(() -> new IllegalArgumentException("Sol data not found for given sol number"));
    }

    String getMartianSeason(String ls){
        if (ls.matches("\\d+")){
            int lsNum = Integer.parseInt(ls);
            if (lsNum >= 0 && lsNum < 90){
                return "Wiosna";
            } else if (lsNum >= 90 && lsNum < 180) {
                return "Lato";
            } else if (lsNum >= 180 && lsNum < 270) {
                return "Jesień";
            } else if (lsNum >= 270 && lsNum <= 360) {
                return "Zima";
            } else {
                throw new IllegalArgumentException("Invalid ls value: " + lsNum);
            }
        }
        throw new IllegalArgumentException("Invalid ls " + ls);
    }

    public List<SolDataDto> getWeatherFor20Days(){
        List<MarsDailyWeather> soles = marsRepo.findAllByOrderBySolDesc();
        return aggregateTempsForYears(soles);
    }

    private List<SolDataDto> aggregateTempsForYears(List<MarsDailyWeather> soles) {
        if (soles.isEmpty()) throw new NoDataFoundException("No weather data available");
        int lastSol = soles.stream().findFirst().get().getSol();
        setDefaultForecastDays();
        int remainingSoles = lastSol - MARTIAN_YEAR_IN_DAYS + forecastDaysForward;
        int totalMartianYears = lastSol / MARTIAN_YEAR_IN_DAYS;
        Map<Integer,Integer> maxTempMap = new HashMap<>(); 
        Map<Integer,Integer> minTempMap = new HashMap<>();
        List<SolDataDto> solDataDtos = new ArrayList<>();
        while (hasSolesToProcess(remainingSoles)){
            for (var sol : soles) {
                if (isSolInRange(sol, remainingSoles)){
                    aggregateTemps(sol, maxTempMap, minTempMap);
                    stepBackOneDay();
                    if (shouldMoveBackOneYear()) break;
                }
            }
            remainingSoles = moveBackOneMartianYear(remainingSoles);
        }
        calculateAvgTempsForDays(maxTempMap, totalMartianYears, minTempMap);
        return buildCharacteristicsForNext20Days(maxTempMap, minTempMap, solDataDtos);
    }

    private static void setDefaultForecastDays() {
        forecastDaysForward = 20;
    }

    private static boolean hasSolesToProcess(int remainingSoles) {
        return remainingSoles > 0;
    }

    private static boolean isSolInRange(MarsDailyWeather sol, int remainingSoles) {
        return sol.getSol() <= remainingSoles;
    }

    private static void aggregateTemps(MarsDailyWeather sol, Map<Integer, Integer> maxTempMap, Map<Integer, Integer> minTempMap) {
        int currentMaxTemp = maxTempMap.getOrDefault(forecastDaysForward,0);
        currentMaxTemp += Integer.parseInt(sol.getMaxTemp());
        maxTempMap.put(forecastDaysForward, currentMaxTemp);

        int currentMinTemp = minTempMap.getOrDefault(forecastDaysForward, 0);
        currentMinTemp += Integer.parseInt(sol.getMinTemp());
        minTempMap.put(forecastDaysForward, currentMinTemp);
    }

    private static void stepBackOneDay() {
        forecastDaysForward--;
    }

    private static boolean shouldMoveBackOneYear() {
        if (forecastDaysForward == 0){
            setDefaultForecastDays();
            return true;
        }
        return false;
    }

    private static int moveBackOneMartianYear(int remainingSoles) {
        remainingSoles -= MARTIAN_YEAR_IN_DAYS;
        return remainingSoles;
    }

    void calculateAvgTempsForDays(Map<Integer, Integer> maxTempMap, int totalMartianYears, Map<Integer, Integer> minTempMap) {
        maxTempMap.replaceAll((key, value) -> value / totalMartianYears);
        minTempMap.replaceAll((key, value) -> value / totalMartianYears);
    }

    private static List<SolDataDto> buildCharacteristicsForNext20Days(Map<Integer, Integer> maxTempMap, Map<Integer, Integer> minTempMap, List<SolDataDto> solDataDtos) {
        while (forecastDaysForward > 0){
            List<Characteristics> characteristics = new ArrayList<>();
            characteristics.add(new Characteristics("MaxTempAvg",
                    String.valueOf(maxTempMap.get(forecastDaysForward))));
            characteristics.add(new Characteristics("MinTempAvg",
                    String.valueOf(minTempMap.get(forecastDaysForward))));
            solDataDtos.add(buildSolDataDto(forecastDaysForward, characteristics));
            stepBackOneDay();
        }
        return solDataDtos;
    }
}
