package kdu.ibe.backend.controllers;

import kdu.ibe.backend.dto.request.PropertyIdRequestDto;
import kdu.ibe.backend.models.Configuration;
import kdu.ibe.backend.services.ConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/configuration")
@Slf4j
public class ConfigurationController {
    @Autowired
    private ConfigurationService configurationService;

    /**
     * Endpoint to save a new configuration.
     *
     * @param configuration The configuration to save.
     * @return ResponseEntity containing the saved configuration if successful, or INTERNAL_SERVER_ERROR if an error occurs.
     */
    @PostMapping
    public ResponseEntity<Configuration> saveConfiguration(@RequestBody Configuration configuration) {
        try {
            Configuration savedConfiguration = configurationService.saveConfiguration(configuration);

            return ResponseEntity.ok(savedConfiguration);
        } catch (Exception e) {
            log.error("Error saving configuration", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Handles PUT requests to update the data of a configuration with the specified ID.
     *
     * @param id The ID of the configuration to be updated.
     * @param newData The new data to be updated for the configuration.
     * @return ResponseEntity containing the updated Configuration if successful,
     *         or NOT_FOUND status if the configuration with the given ID is not found,
     *         or INTERNAL_SERVER_ERROR status if an error occurs during the update process.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Configuration> updateConfigurationData(@PathVariable Long id, @RequestBody Configuration newData) {
        try {
            Configuration updatedData = configurationService.updateConfigurationData(id, newData);
            if (updatedData != null) {
                return ResponseEntity.ok(updatedData);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error updating configuration data", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Value("${azure.blob.storage.link.en}")
    private String blobStorageLinkEn;

    @Value("${azure.blob.storage.link.de}")
    private String blobStorageLinkDe;

    /**
     * Retrieve configuration data by property ID.
     *
     * @param propertyIdRequestDto The request DTO containing the IDs.
     * @return ResponseEntity containing the configuration data or error status.
     */
    @PostMapping("/property")
    public ResponseEntity<Map<String, Object>> getConfigurationByPropertyId(@RequestBody PropertyIdRequestDto propertyIdRequestDto) {
        try {
            Map<String, Object> propertyData = configurationService.getConfigurationById(propertyIdRequestDto.getId(), propertyIdRequestDto.getPropertyId());
            if (propertyData != null) {
                propertyData.put("blobStorageLinkEn", blobStorageLinkEn);
                propertyData.put("blobStorageLinkDe", blobStorageLinkDe);
                return ResponseEntity.ok(propertyData);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error getting configuration by ID and property ID", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





    @GetMapping("/propertyid")
    public ResponseEntity<Map<String, Object>> getConfigurationByPropertyId() {
        try {
            Long propertyId = 10L;
            Long id = 1L;

            Map<String, Object> propertyData = configurationService.getConfigurationById(id, propertyId);
            if (propertyData != null) {
                propertyData.put("blobStorageLinkEn", blobStorageLinkEn);
                propertyData.put("blobStorageLinkDe", blobStorageLinkDe);
                return ResponseEntity.ok(propertyData);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error getting configuration by ID and property ID", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
