package velsis.back.exceptions;

public class CepNotFoundException extends RuntimeException{

    public CepNotFoundException(String zip){
        super("Cep não encontrado: " + zip);
    }
}
