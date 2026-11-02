package br.com.soulmove.exception;

/** Lancada quando ocorre um erro de acesso ao banco de dados (HTTP 500). */
public class BancoDadosException extends RuntimeException {

    public BancoDadosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
