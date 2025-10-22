package velsis.back.dtos;

import java.time.LocalDate;

public record UsuarioDTO(
        Long id,
        String name,
        LocalDate birth_date,
        String city,
        String state
) {
}
