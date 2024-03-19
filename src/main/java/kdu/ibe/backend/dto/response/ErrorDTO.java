package kdu.ibe.backend.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {

    @NonNull
    @NotBlank(message = "Message should not be blank")
    String message;

    @NonNull
    @NotBlank(message = "HTTP Status should not be blank")
    int httpStatus;
}