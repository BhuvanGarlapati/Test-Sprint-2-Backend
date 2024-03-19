package kdu.ibe.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomRate {
    private String date;
    private double basic_nightly_rate;
    private int room_rate_id;

}
