package com.example.station;

import com.example.station.dto.StationDTO;
import com.example.station.model.Station;
import com.example.station.repo.StationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StationRepository repository;

    @Test
    public void testBatchIngestAndGet() {
        StationDTO dto = new StationDTO();
        dto.setStationId("TEST-1");
        dto.setName("Test Station");
        dto.setCapacity(2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<StationDTO[]> req = new HttpEntity<>(new StationDTO[]{dto}, headers);

        ResponseEntity<String> resp = restTemplate.postForEntity("/api/v1/stations/batch", req, String.class);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());

        List<Station> all = repository.findAll();
        Assertions.assertFalse(all.isEmpty());
        Station s = all.get(0);
        Assertions.assertEquals("TEST-1", s.getCode());
    }

    @Test
    public void testCsvUpload() {
        ClassPathResource resource = new ClassPathResource("/samples/StationInventory.csv");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/stations/upload", requestEntity, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Station> all = repository.findAll();
        Assertions.assertTrue(all.size() >= 3);
    }
}
