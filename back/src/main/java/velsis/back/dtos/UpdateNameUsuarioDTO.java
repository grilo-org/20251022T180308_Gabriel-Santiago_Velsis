package velsis.back.dtos;

import jakarta.validation.constraints.Size;

public record UpdateNameUsuarioDTO(
        @Size(min = 3, message = "Nome deve ter pelo menos 3 caracteres")
        String name,
        Long id
) {
}
