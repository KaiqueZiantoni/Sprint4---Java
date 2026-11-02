package br.com.soulmove.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Libera o acesso da API para o front-end (React), que roda em outro
 * endereco. Sem este filtro o navegador bloqueia as requisicoes (CORS).
 */
@Component
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest requisicao = (HttpServletRequest) request;
        HttpServletResponse resposta = (HttpServletResponse) response;

        resposta.setHeader("Access-Control-Allow-Origin", "*");
        resposta.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resposta.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resposta.setHeader("Access-Control-Max-Age", "3600");

        // O navegador envia um OPTIONS antes do POST/PUT/DELETE (preflight)
        if ("OPTIONS".equalsIgnoreCase(requisicao.getMethod())) {
            resposta.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }
}
