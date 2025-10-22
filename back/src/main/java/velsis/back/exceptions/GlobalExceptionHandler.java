package velsis.back.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import velsis.back.dtos.ErrorDTO;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata exceções de CEP inválido ou não encontrado no ViaCEP
     * Retorna status 400 (Bad Request) pois indica um erro nos dados fornecidos pelo cliente
     * Ocorre quando o CEP não existe ou está em formato inválido
     */
    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleCepNotFound(CepNotFoundException ex) {
        ErrorDTO error = new ErrorDTO("INVALID_CEP", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata falhas na comunicação com o serviço ViaCEP
     * Retorna status 503 (Service Unavailable) pois indica indisponibilidade de serviço externo
     * Ocorre em casos de timeout, conexão recusada ou erro interno do ViaCEP
     */
    @ExceptionHandler(CepServiceException.class)
    public ResponseEntity<ErrorDTO> handleCepErro(CepServiceException ex) {
        ErrorDTO error = new ErrorDTO("CEP_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    /**
     * Trata tentativas de cadastro com document (cpf/id) já existente
     * Retorna status 409 (Conflict) pois indica conflito com regra de unicidade
     * Aplica-se tanto para criação quanto atualização de usuários
     */
    @ExceptionHandler(DocumentExistException.class)
    public ResponseEntity<ErrorDTO> handleDocumentExist(DocumentExistException ex) {
        ErrorDTO error = new ErrorDTO("DOCUMENT_EXISTS", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Trata validações de dados inválidos em operações de atualização
     * Agrupa exceções de nome, documento e data de nascimento nulos ou inválidos
     * Retorna status 400 (Bad Request) para indicar dados incorretos
     */
    @ExceptionHandler({UpdateBirthDateException.class, UpdateDocumentException.class, UpdateNameException.class})
    public ResponseEntity<ErrorDTO> handleUpdateValidationExceptions(RuntimeException ex) {
        ErrorDTO error = new ErrorDTO("INVALID_UPDATE", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata tentativas de operações com usuários inexistentes
     * Retorna status 404 (Not Found) para recursos não encontrados
     * Aplica-se a operações de busca, atualização e exclusão
     */
    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleUsuarioNotFound(UsuarioNotFoundException ex) {
        ErrorDTO error = new ErrorDTO("USER_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Trata erros de validação automática do Bean Validation (@Valid)
     * Captura violações de constraints como @Past, @Size, @Pattern
     * Retorna lista detalhada de erros de campo com status 400 (Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorDTO errorResponse = new ErrorDTO("VALIDATION_ERROR", "Erro de validação", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Captura exceções genéricas não tratadas especificamente
     * Retorna status 500 (Internal Server Error) para erros inesperados
     * Funciona como fallback global para garantir resposta padronizada
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex) {
        ErrorDTO errorDTO = new ErrorDTO("INTERNAL_ERROR", "Erro interno no servidor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }
}
