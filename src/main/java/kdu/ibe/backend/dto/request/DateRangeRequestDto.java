package kdu.ibe.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateRangeRequestDto {
    private String startDate;
    private String endDate;
}
