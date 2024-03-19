package kdu.ibe.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kdu.ibe.backend.models.Property;
import kdu.ibe.backend.utils.ResponseParser;
import kdu.ibe.backend.models.RoomRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PropertyService {
    RestTemplate restTemplate = new RestTemplate();


    @Value("${graphql_baseUrl}")
    private String baseUrl;
    @Value("${graphql_key}")
    private String graphQlKey;

private static final String QUERY = "{ listProperties (where: {property_id: {equals: %d}}){ room_type { room_rates { room_rate { basic_nightly_rate date room_rate_id } } } } }";

private static final String QUERY_PROPERTY = "{listProperties(where: {tenant_id: {equals: %d}}) {property_id property_name tenant_id}}";

    /**
     * Method to execute graph QL query
     * @return ResponseEntity<String>
     */

    public ResponseEntity<String> executeGraphQLQuery(Long Id) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", graphQlKey);

        String query = String.format(QUERY, Id);
        String requestBody = "{ \"query\": \"" + query + "\" }";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());
        JsonNode dataNode = rootNode.path("data");
        JsonNode listPropertiesNode = dataNode.path("listProperties");

        List<JsonNode> roomRates = new ArrayList<>();
        for (JsonNode propertyNode : listPropertiesNode) {
            JsonNode roomTypeNode = propertyNode.path("room_type");
            for (JsonNode roomType : roomTypeNode) {
                JsonNode roomRatesNode = roomType.path("room_rates");
                for (JsonNode roomRate : roomRatesNode) {
                    JsonNode rateNode = roomRate.path("room_rate");
                    roomRates.add(rateNode);
                }
            }
        }
        ArrayNode formattedRoomRates = objectMapper.createArrayNode();
        for (JsonNode roomRate : roomRates) {
            ObjectNode formattedRate = objectMapper.createObjectNode();
            formattedRate.put("date", roomRate.path("date").asText());
            formattedRate.put("basic_nightly_rate", roomRate.path("basic_nightly_rate").asInt());
            formattedRate.put("room_rate_id", roomRate.path("room_rate_id").asInt());
            formattedRoomRates.add(formattedRate);
        }
        String formattedRoomRatesJson = objectMapper.writeValueAsString(formattedRoomRates);

        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(formattedRoomRatesJson, headers, HttpStatus.OK);
    }


    /**
     * @param startDateStr the start date of the range in the format "yyyy-MM-dd"
     * @param endDateStr the end date of the range in the format "yyyy-MM-dd"
     * @return a map of dates to the minimum nightly rate for each date within the specified range
     * @throws IOException if an error occurs while executing the GraphQL query
     * @see LocalDate
     * @see RoomRate
     */
    public Map<LocalDate, Double> getMinimumNightRate(String startDateStr, String endDateStr,Long Id) throws IOException {
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        ResponseEntity<String> response = executeGraphQLQuery(Id);
        List<RoomRate> roomRates = ResponseParser.parseRoomRates(response.getBody());
        Map<LocalDate, Double> minNightRates = new HashMap<>();

        for (RoomRate rate : roomRates) {
            String dateString = rate.getDate().substring(0, 10);
            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);

            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                double nightRate = rate.getBasic_nightly_rate();
                minNightRates.putIfAbsent(date, nightRate);
                minNightRates.put(date, Math.min(minNightRates.get(date), nightRate));
            }
        }

        return minNightRates;
    }

    /**
     * Executes a GraphQL query to fetch properties for a specific tenant.
     *
     * @param tenantId The ID of the tenant whose properties to fetch.
     * @return A ResponseEntity containing a list of Property objects if successful, otherwise an empty list.
     * @throws IOException If there is an error reading the response body.
     */
    public ResponseEntity<List<Property>> executeGraphQLQueryProperty(Long tenantId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", graphQlKey);

        String query = String.format(QUERY_PROPERTY, tenantId);
        String requestBody = "{ \"query\": \"" + query + "\" }";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());
        JsonNode dataNode = rootNode.path("data");
        JsonNode listPropertiesNode = dataNode.path("listProperties");

        List<Property> properties = new ArrayList<>();
        for (JsonNode propertyNode : listPropertiesNode) {
            Long propertyId = propertyNode.path("property_id").asLong();
            String propertyName = propertyNode.path("property_name").asText();

            Property property = new Property(propertyId, tenantId.toString(), propertyName);
            properties.add(property);
        }
        return new ResponseEntity<>(properties, HttpStatus.OK);
    }


}


