package com.example.station.controller;

import com.example.station.dto.StationDTO;
import com.example.station.model.Station;
import com.example.station.service.StationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(controllers = StationController.class)
public class StationControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private StationService stationService;

    @BeforeEach
    void setup() {
        StationController controller = new StationController(stationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void postBatch_returnsOk() throws Exception {
        StationDTO dto = new StationDTO(); dto.setStationId("X"); dto.setName("X"); dto.setCapacity(1);
        Station s = new Station(); s.setId(UUID.randomUUID()); s.setCode("X"); s.setDisplayName("X"); s.setCapacity(1);

        when(stationService.saveAllFromDtos(anyList())).thenReturn(List.of(s));

        String body = "[{\"stationId\":\"X\",\"name\":\"X\",\"capacity\":1}]";

        mockMvc.perform(post("/api/v1/stations/batch").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
