package velsis.back.dtos;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UsuarioForUpdateDTO(
        Long id,
        @Size(min = 3, message = "Nome deve ter pelo menos 3 caracteres")
        String name,
        @Past(message = "Data de nascimento deve ser uma data passada")
        LocalDate birth_date,
        @Size(min = 11, max = 11, message = "Documento deve ter exatamente 11 caracteres")
        @Pattern(regexp = "\\d{11}", message = "Documento deve conter apenas números")
        String document,
        @Size(min = 8, max = 8, message = "CEP deve ter exatamente 8 caracteres")
        @Pattern(regexp = "\\d{8}", message = "CEP deve conter apenas números")
        String zip,
        Integer address_number
) {
}
