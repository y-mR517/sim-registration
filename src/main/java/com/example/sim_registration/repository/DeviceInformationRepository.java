package com.example.sim_registration.repository;

import com.example.sim_registration.entity.DeviceInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeviceInformationRepository
        extends JpaRepository<DeviceInformation, UUID> {

    Optional<DeviceInformation> findByDeviceFingerprintHash(
            String deviceFingerprintHash
    );

    boolean existsByDeviceFingerprintHash(
            String deviceFingerprintHash
    );
}
