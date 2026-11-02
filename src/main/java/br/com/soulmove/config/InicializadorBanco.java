package br.com.soulmove.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.com.soulmove.connection.ConnectionFactory;

/**
 * Quando a API sobe sem o Oracle configurado (DB_URL vazia), cria as tabelas
 * e alguns registros no banco H2 em memoria, lendo database/script_h2.sql.
 * No Oracle da FIAP nada e executado: o script e rodado uma vez pelo SQL Developer.
 */
@Component
public class InicializadorBanco implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        if (!ConnectionFactory.usandoH2()) {
            System.out.println("[Banco] Usando Oracle: " + ConnectionFactory.getUrl());
            return;
        }

        System.out.println("[Banco] DB_URL nao informada: usando H2 em memoria com dados de exemplo.");

        StringBuilder script = new StringBuilder();
        try (BufferedReader leitor = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/database/script_h2.sql"), StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (!linha.trim().startsWith("--")) {
                    script.append(linha).append('\n');
                }
            }
        }

        try (Connection conexao = ConnectionFactory.obterConexao();
             Statement stmt = conexao.createStatement()) {
            for (String comando : script.toString().split(";")) {
                if (!comando.trim().isEmpty()) {
                    stmt.execute(comando);
                }
            }
        }
    }
}
