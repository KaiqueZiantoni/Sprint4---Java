package br.com.soulmove.exception;

/** Lancada quando o registro procurado nao existe no banco (HTTP 404). */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
