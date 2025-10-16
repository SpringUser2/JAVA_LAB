package com.example.station.repo;

import com.example.station.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StationRepository extends JpaRepository<Station, UUID> {
    Optional<Station> findByCode(String code);
}
