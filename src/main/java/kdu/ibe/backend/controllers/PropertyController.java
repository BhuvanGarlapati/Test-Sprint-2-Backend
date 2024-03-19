package kdu.ibe.backend.controllers;

import kdu.ibe.backend.dto.request.DateRangeRequestDto;
import kdu.ibe.backend.models.Property;
import kdu.ibe.backend.services.ConfigurationService;
import kdu.ibe.backend.services.PropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class PropertyController {
    private final PropertyService propertyService;

    @Autowired
    private ConfigurationService configurationService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    /**
     * Retrieves the minimum night rate for a given date range.
     *
     * @param dateRange A DTO containing the start and end dates for the rate query.
     * @return A ResponseEntity containing a map of LocalDate to Double representing the minimum night rates for each date in the range, or an error status.
     */
    @PostMapping("/calendar/{id}")
    public ResponseEntity<Map<LocalDate, Double>> getMinimumNightRate(
            @PathVariable Long id,
            @RequestBody DateRangeRequestDto dateRange
    ) {
        try {
            Map<LocalDate, Double> minNightRates = propertyService.getMinimumNightRate(
                    dateRange.getStartDate(),
                    dateRange.getEndDate(),
                    id
            );
            return new ResponseEntity<>(minNightRates, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting minimum rates", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/calendar/10")
    public ResponseEntity<Map<LocalDate, Double>> getMinimumNightRate() {
        try {

            String startDateStr = "2024-03-01";
            String endDateStr = "2024-06-01";


            Map<LocalDate, Double> minNightRates = propertyService.getMinimumNightRate(startDateStr, endDateStr, 10L);
            return new ResponseEntity<>(minNightRates, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting minimum rates", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





    /**
     * Retrieve all properties associated with a given tenant ID.
     *
     * @param id The ID of the tenant.
     * @return ResponseEntity containing a list of properties or an error status.
     */
    @GetMapping("/tenant/{id}")
    public ResponseEntity<Map<String, Object>> getAllPropertiesByTenantId(@PathVariable Long id) {
        try {
            ResponseEntity<List<Property>> propertiesResponse = propertyService.executeGraphQLQueryProperty(id);
            List<Property> properties = propertiesResponse.getBody();

            Map<String, Object> globalData = configurationService.getGlobalConfigurationData(id);

            Map<String, Object> response = new HashMap<>();
            response.put("properties", properties);
            response.put("global", globalData);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
           log.error("Error getting tenant Id",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}