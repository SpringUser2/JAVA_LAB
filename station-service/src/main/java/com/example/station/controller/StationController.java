package com.example.station.controller;

import com.example.station.dto.StationDTO;
import com.example.station.model.Station;
import com.example.station.service.StationService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stations")
public class StationController {

    private final StationService service;

    public StationController(StationService service) {
        this.service = service;
    }

    @PostMapping(path = "/batch", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> ingestBatch(@RequestBody List<@Valid StationDTO> dtos, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("validation error");
        }
        List<Station> saved = service.saveAllFromDtos(dtos);
        return ResponseEntity.ok(new ProcessingSummary(dtos.size(), saved.size(), dtos.size() - saved.size()));
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCsv(@RequestParam("file") MultipartFile file) throws Exception {
        List<StationDTO> dtos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                StationDTO dto = new StationDTO();
                dto.setStationId(parts[0]);
                dto.setName(parts[1]);
                dto.setAddress(parts.length > 2 ? parts[2] : null);
                dto.setLatitude(parts.length > 3 && !parts[3].isEmpty() ? Double.valueOf(parts[3]) : null);
                dto.setLongitude(parts.length > 4 && !parts[4].isEmpty() ? Double.valueOf(parts[4]) : null);
                dto.setCapacity(parts.length > 5 && !parts[5].isEmpty() ? Integer.valueOf(parts[5]) : null);
                dto.setStatus(parts.length > 6 ? parts[6] : null);
                dtos.add(dto);
            }
        }
        List<Station> saved = service.saveAllFromDtos(dtos);
        return ResponseEntity.ok(new ProcessingSummary(dtos.size(), saved.size(), dtos.size() - saved.size()));
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {
        return ResponseEntity.of(service.getRepository().findByCode(code));
    }

    public static class ProcessingSummary {
        public String processingId = java.util.UUID.randomUUID().toString();
        public int receivedCount;
        public int successCount;
        public int failedCount;

        public ProcessingSummary(int received, int success, int failed) {
            this.receivedCount = received;
            this.successCount = success;
            this.failedCount = failed;
        }
    }
}
