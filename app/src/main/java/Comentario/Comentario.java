package Comentario;

import Comercio.Comercio;
import Usuario.Usuario;

/**
 * Created by nathan on 10/10/2017.
 */

public class Comentario {

    Usuario usuario;
    Comercio comercio;
    String text;

    public Comentario(Usuario usuario, Comercio comercio, String text)
    {
        this.usuario = usuario;
        this.comercio = comercio;
        this.text = text;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Comercio getComercio() {
        return comercio;
    }

    public void setComercio(Comercio comercio) {
        this.comercio = comercio;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
