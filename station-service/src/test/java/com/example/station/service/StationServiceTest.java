package com.example.station.service;

import com.example.station.dto.StationDTO;
import com.example.station.model.Station;
import com.example.station.repo.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StationServiceTest {

    private StationRepository repository;
    private StationService service;

    @BeforeEach
    void setup() {
        repository = mock(StationRepository.class);
        service = new StationService(repository);
    }

    @Test
    void saveFromDto_shouldMapAndSave() {
        StationDTO dto = new StationDTO();
        dto.setStationId("T-100");
        dto.setName("Test Station");
        dto.setCapacity(5);

        when(repository.save(any(Station.class))).thenAnswer(inv -> inv.getArgument(0));

        Station saved = service.saveFromDto(dto);

        assertNotNull(saved.getId());
        assertEquals("T-100", saved.getCode());
        assertEquals("Test Station", saved.getDisplayName());
        assertEquals(5, saved.getCapacity());

        ArgumentCaptor<Station> captor = ArgumentCaptor.forClass(Station.class);
        verify(repository, times(1)).save(captor.capture());
        Station captured = captor.getValue();
        assertEquals("T-100", captured.getCode());
    }

    @Test
    void saveAllFromDtos_shouldSaveAll() {
        StationDTO d1 = new StationDTO(); d1.setStationId("A"); d1.setName("A"); d1.setCapacity(1);
        StationDTO d2 = new StationDTO(); d2.setStationId("B"); d2.setName("B"); d2.setCapacity(2);

        when(repository.save(any(Station.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Station> saved = service.saveAllFromDtos(List.of(d1, d2));
        assertEquals(2, saved.size());
        verify(repository, times(2)).save(any(Station.class));
    }
}
