package com.example.demo.domain;

import com.example.demo.model.DataSyncInfo;
import com.example.demo.model.MarsDailyWeather;
import com.example.demo.repo.DataSyncInfoRepo;
import com.example.demo.repo.MarsRepo;
import com.example.demo.web.MarsWeatherDetailsDto;
import com.example.demo.web.MarsWeatherDto;
import com.example.demo.web.NasaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
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

    public List<MarsWeatherDetailsDto> getWeather() {
        initData();
        log.info("Lista obiektów w bazie danych: {}", marsRepo.count());
        return marsMapper.toDtoListFromEntity(marsRepo.findAll());
    }

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
}
