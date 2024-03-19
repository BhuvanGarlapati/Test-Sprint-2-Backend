package kdu.ibe.backend.services;

import kdu.ibe.backend.models.Configuration;
import kdu.ibe.backend.repositories.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for managing configurations.
 */
@Service
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;
    /**
     * Constructor for ConfigurationService.
     *
     * @param configurationRepository The repository for Configuration entities.
     */
    @Autowired
    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }
    /**
     * Save a configuration.
     *
     * @param configuration The configuration to save.
     * @return The saved configuration.
     */
    public Configuration saveConfiguration(Configuration configuration) {
        return configurationRepository.save(configuration);
    }

    /**
     * Updates the data of a configuration with the specified ID.
     *
     * @param id The ID of the configuration to be updated.
     * @param newData The new data to be updated for the configuration.
     * @return The updated Configuration object if the update is successful,
     *         or null if the configuration with the given ID is not found.
     */
    public Configuration updateConfigurationData(Long id, Configuration newData) {
        Configuration existingConfiguration = configurationRepository.findById(id).orElse(null);
        if (existingConfiguration != null) {
            existingConfiguration.setData(newData.getData());
            configurationRepository.save(existingConfiguration);
            return newData;
        }
        return null;
    }

    /**
     * Retrieve configuration data by ID and property ID.
     *
     * @param id The ID of the configuration.
     * @param propertyId The ID of the property within the configuration.
     * @return A map containing global and property-specific data, or null if no data is found.
     */

    public Map<String, Object> getConfigurationById(Long id, Long propertyId) {
        Configuration configuration = configurationRepository.findById(id).orElse(null);
        if (configuration == null) {
            return null;
        }
        Map<String, Object> data = configuration.getData();

        Map<String, Object> responseData = new HashMap<>();

        Map<String, Object> globalData = (Map<String, Object>) data.get("global");
        responseData.put("global", globalData != null ? globalData : new HashMap<>());

        Map<String, Object> propertyData = (Map<String, Object>) data.get("properties");
        if (propertyData == null) {
            return null;
        }

        Map<String, Object> property = (Map<String, Object>) propertyData.get(String.valueOf(propertyId));
        if (property == null) {
            return null;
        }
        responseData.put("property", property);

        return responseData;
    }

    /**
     * Retrieves the global configuration data for the specified configuration ID.
     *
     * @param id The ID of the configuration.
     * @return A {@code Map<String, Object>} containing the global configuration data,
     *         or {@code null} if no configuration with the specified ID is found.
     */

    public Map<String, Object> getGlobalConfigurationData(Long id) {
        Configuration configuration = configurationRepository.findById(id).orElse(null);
        if (configuration == null) {
            return null;
        }
        Map<String, Object> data = configuration.getData();

        Map<String, Object> globalData = (Map<String, Object>) data.get("global");
        return globalData != null ? globalData : new HashMap<>();
    }



}
