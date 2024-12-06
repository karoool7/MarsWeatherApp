package com.example.demo.repo;

import com.example.demo.model.DataSyncInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DataSyncInfoRepo extends JpaRepository<DataSyncInfo, Long> {
    Optional<DataSyncInfo> findFirstByOrderByLastUpdateDesc();
}
