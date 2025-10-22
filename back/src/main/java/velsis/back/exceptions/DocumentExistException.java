package velsis.back.exceptions;

public class DocumentExistException extends RuntimeException{

    public DocumentExistException(){
        super("Usuário com cpf/id existente");
    }
}
