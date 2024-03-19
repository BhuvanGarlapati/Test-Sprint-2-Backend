package kdu.ibe.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyIdRequestDto {
    private Long id;
    private Long propertyId;

}
