package velsis.back.exceptions;

public class UpdateDocumentException extends RuntimeException{

    public UpdateDocumentException(){
        super("O document (cpf/id) não pode ser nulo!");
    }
}
