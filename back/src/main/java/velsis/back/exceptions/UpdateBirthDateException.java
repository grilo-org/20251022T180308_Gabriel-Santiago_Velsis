package velsis.back.exceptions;

public class UpdateBirthDateException extends RuntimeException{

    public UpdateBirthDateException(){
        super("A data de aniversário não pode ser nula!");
    }
}
