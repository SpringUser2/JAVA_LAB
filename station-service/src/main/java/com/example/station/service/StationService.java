package com.example.station.service;

import com.example.station.dto.StationDTO;
import com.example.station.model.Station;
import com.example.station.repo.StationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StationService {

    private final StationRepository repository;

    public StationService(StationRepository repository) {
        this.repository = repository;
    }

    public Station saveFromDto(StationDTO dto) {
        Station s = new Station();
        s.setId(UUID.randomUUID());
        s.setCode(dto.getStationId());
        s.setDisplayName(dto.getName());
        s.setLat(dto.getLatitude());
        s.setLon(dto.getLongitude());
        s.setCapacity(dto.getCapacity());
        s.setStatus(dto.getStatus());
        s.setCreatedAt(Instant.now());
        return repository.save(s);
    }

    public List<Station> saveAllFromDtos(List<StationDTO> dtos) {
        List<Station> results = new ArrayList<>();
        for (StationDTO dto : dtos) {
            results.add(saveFromDto(dto));
        }
        return results;
    }

    // Expose repository for simple controller lookup and tests
    public StationRepository getRepository() {
        return this.repository;
    }
}
