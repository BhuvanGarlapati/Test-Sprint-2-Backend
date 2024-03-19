package kdu.ibe.backend.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import kdu.ibe.backend.models.RoomRate;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a JSON response containing room rate data and converts it into a list of RoomRate objects.
 *
 * @return A list of RoomRate objects parsed from the JSON response. If parsing fails or the JSON structure is invalid,
 *         an empty list is returned.
 */
public class ResponseParser {
    public static List<RoomRate> parseRoomRates(String jsonResponse) {
        List<RoomRate> roomRates = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            ArrayNode listRoomRatesNode = (ArrayNode) rootNode;

            for (JsonNode roomRateNode : listRoomRatesNode) {
                String date = roomRateNode.path("date").asText();
                double basicNightlyRate = roomRateNode.path("basic_nightly_rate").asDouble();
                int roomRateId = roomRateNode.path("room_rate_id").asInt();

                RoomRate roomRate = new RoomRate(date, basicNightlyRate, roomRateId);
                roomRates.add(roomRate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return roomRates;
    }
}