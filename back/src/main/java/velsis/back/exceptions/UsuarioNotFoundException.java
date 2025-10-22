package velsis.back.exceptions;

public class UsuarioNotFoundException extends RuntimeException{

    public UsuarioNotFoundException(){
        super("Usuario não encontrado");
    }
}
