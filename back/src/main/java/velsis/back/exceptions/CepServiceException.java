package velsis.back.exceptions;

public class CepServiceException extends RuntimeException{

    public CepServiceException(){
        super("Erro ao consultar o ViaCep");
    }
}
