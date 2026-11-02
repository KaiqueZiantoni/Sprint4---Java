package br.com.soulmove.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Converte as excecoes lancadas pelas camadas BO e DAO em respostas JSON
 * com o codigo HTTP adequado. Assim os Resources nao precisam repetir
 * try/catch em cada endpoint.
 */
@RestControllerAdvice
public class TratadorDeExcecoes {

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<Map<String, String>> tratarValidacao(ValidacaoException e) {
        return montarResposta(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> tratarNaoEncontrado(RecursoNaoEncontradoException e) {
        return montarResposta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(BancoDadosException.class)
    public ResponseEntity<Map<String, String>> tratarBanco(BancoDadosException e) {
        System.out.println("[ERRO BANCO] " + e.getCause().getMessage());
        return montarResposta(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<Map<String, String>> tratarRequisicaoInvalida(Exception e) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Requisicao invalida: verifique o formato dos dados enviados.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> tratarRotaInexistente(NoResourceFoundException e) {
        return montarResposta(HttpStatus.NOT_FOUND, "Rota nao encontrada: /" + e.getResourcePath());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> tratarMetodoInvalido(HttpRequestMethodNotSupportedException e) {
        return montarResposta(HttpStatus.METHOD_NOT_ALLOWED, "Metodo " + e.getMethod() + " nao suportado nesta rota.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> tratarGenerico(Exception e) {
        System.out.println("[ERRO] " + e.getMessage());
        return montarResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
    }

    private ResponseEntity<Map<String, String>> montarResposta(HttpStatus status, String mensagem) {
        Map<String, String> corpo = new HashMap<>();
        corpo.put("status", String.valueOf(status.value()));
        corpo.put("erro", mensagem);
        return ResponseEntity.status(status).body(corpo);
    }
}
