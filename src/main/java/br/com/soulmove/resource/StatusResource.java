package br.com.soulmove.resource;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.soulmove.connection.ConnectionFactory;

/** Endpoint simples para conferir se a API e o banco estao no ar (usado no deploy). */
@RestController
public class StatusResource {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("aplicacao", "SoulMove API");
        resposta.put("versao", "4.0.0");
        resposta.put("banco", ConnectionFactory.usandoH2() ? "H2 (local)" : "Oracle");
        resposta.put("bancoDisponivel", ConnectionFactory.testarConexao());
        return ResponseEntity.ok(resposta);
    }
}
