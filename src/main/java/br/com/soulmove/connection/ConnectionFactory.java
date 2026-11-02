package br.com.soulmove.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Responsavel por abrir as conexoes com o banco de dados.
 *
 * Os dados de acesso vem de variaveis de ambiente, para que o usuario e a
 * senha do Oracle nao fiquem gravados no codigo-fonte:
 *   DB_URL      - ex.: jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
 *   DB_USUARIO  - ex.: RM000000
 *   DB_SENHA    - senha do Oracle
 *
 * Se DB_URL nao for informada, a aplicacao sobe com um banco H2 em memoria
 * (modo de compatibilidade Oracle), util para testes locais sem o Oracle.
 */
public class ConnectionFactory {

    private static final String URL_H2_LOCAL = "jdbc:h2:mem:soulmove;MODE=Oracle;DB_CLOSE_DELAY=-1";

    public static String getUrl() {
        String url = System.getenv("DB_URL");
        if (url == null || url.isBlank()) {
            return URL_H2_LOCAL;
        }
        return url;
    }

    public static String getUsuario() {
        String usuario = System.getenv("DB_USUARIO");
        return usuario == null ? "sa" : usuario;
    }

    private static String getSenha() {
        String senha = System.getenv("DB_SENHA");
        return senha == null ? "" : senha;
    }

    public static boolean usandoH2() {
        return getUrl().startsWith("jdbc:h2:");
    }

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(getUrl(), getUsuario(), getSenha());
    }

    public static boolean testarConexao() {
        try (Connection conexao = obterConexao()) {
            return conexao != null && !conexao.isClosed();
        } catch (SQLException e) {
            System.out.println("[ConnectionFactory] Banco indisponivel: " + e.getMessage());
            return false;
        }
    }
}
