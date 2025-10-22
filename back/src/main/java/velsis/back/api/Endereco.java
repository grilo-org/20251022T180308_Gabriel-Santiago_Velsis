package velsis.back.api;

public record Endereco(
        String cep,
        String logradouro,
        String localidade,
        String uf
) {
}
