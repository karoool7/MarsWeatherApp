package com.example.demo.domain;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarsService {

    private final NasaClient nasaClient;
    private final MarsMapper marsMapper;
    private final MarsRepo marsRepo;
    private final DataSyncInfoRepo syncRepo;

    private static final int OUTDATED_AFTER_DAYS = 7;

    private Optional<List<MarsWeatherDetailsDto>> getValidSolesIfPresent(MarsWeatherDto marsWeatherDto) {
        return Optional.ofNullable(marsWeatherDto)
                .map(MarsWeatherDto::soles)
                .map(soles -> soles.stream()
                        .filter(this::hasNoNullFields)
                        .toList())
                .filter(soles -> !soles.isEmpty());
    }

    private boolean hasNoNullFields(Object obj) {
        for (Field field: obj.getClass().getDeclaredFields()){
            field.setAccessible(true);
            try {
                if (field.get(obj) == null){
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

    private boolean isDataOutdated() {
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
            solDataDto.add(buildSolDataDto(sol, characteristics));
        });
        return solDataDto;
    }

    private String getDayOfWeek(String dateStr){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateStr, formatter);
        return date.getDayOfWeek().toString();
    }

    private static SolDataDto buildSolDataDto(MarsDailyWeather sol, List<Characteristics> characteristics) {
        return new SolDataDto(sol.getSol(), characteristics);
    }
}
