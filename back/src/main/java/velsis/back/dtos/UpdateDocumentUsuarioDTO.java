package velsis.back.dtos;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateDocumentUsuarioDTO(
        @Size(min = 11, max = 11, message = "Documento deve ter exatamente 11 caracteres")
        @Pattern(regexp = "\\d{11}", message = "Documento deve conter apenas números")
        String document,
        Long id
) {
}
