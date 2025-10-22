package velsis.back.exceptions;

public class UpdateNameException extends RuntimeException{

    public UpdateNameException(){
        super("O nome não pode ser nulo/vazio!");
    }
}
