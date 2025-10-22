package velsis.back.dtos;

import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UpdateBirthDateUsuarioDTO(
        @Past(message = "Data de nascimento deve ser uma data passada")
        LocalDate birth_date,
        Long id
) {
}
