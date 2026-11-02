package br.com.soulmove.exception;

/** Lancada quando os dados recebidos nao passam nas regras de negocio (HTTP 400). */
public class ValidacaoException extends RuntimeException {

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
