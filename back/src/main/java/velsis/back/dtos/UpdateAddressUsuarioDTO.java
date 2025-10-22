package velsis.back.dtos;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateAddressUsuarioDTO(
        @Size(min = 8, max = 8, message = "CEP deve ter exatamente 8 caracteres")
        @Pattern(regexp = "\\d{8}", message = "CEP deve conter apenas números")
        String zip,
        Integer address_number,
        Long id
) {
}
