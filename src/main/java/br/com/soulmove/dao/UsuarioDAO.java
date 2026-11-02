package br.com.soulmove.dao;

import br.com.soulmove.connection.ConnectionFactory;
import br.com.soulmove.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UsuarioDAO {

    /** CREATE — insere um novo usuário e devolve o id gerado pela sequence. */
    public int inserir(Usuario usuario) throws SQLException {
        String sqlProximoId = "SELECT SQ_SOULMOVE_USUARIO.NEXTVAL FROM DUAL";
        String sqlInsert = "INSERT INTO TB_SOULMOVE_USUARIO "
                + "(ID_USUARIO, NM_USUARIO, NR_CPF, DS_EMAIL, NR_BILHETE, ST_ATIVO, NR_SALDO_PONTOS) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConnectionFactory.obterConexao()) {
            int novoId;
            try (PreparedStatement stmtId = conexao.prepareStatement(sqlProximoId);
                 ResultSet rs = stmtId.executeQuery()) {
                rs.next();
                novoId = rs.getInt(1);
            }
            try (PreparedStatement stmt = conexao.prepareStatement(sqlInsert)) {
                stmt.setInt(1, novoId);
                stmt.setString(2, usuario.getNome());
                stmt.setString(3, usuario.getCpf());
                stmt.setString(4, usuario.getEmail());
                stmt.setString(5, usuario.getNumeroBilheteTransporte());
                stmt.setInt(6, usuario.isCadastroAtivo() ? 1 : 0);
                stmt.setInt(7, usuario.getSaldoPontosSoulUp());
                stmt.executeUpdate();
            }
            usuario.setIdUsuario(novoId);
            return novoId;
        }
    }

    /** READ — busca um usuário pelo id. Devolve null se não existir. */
    public Usuario buscarPorId(int idUsuario) throws SQLException {
        String sql = "SELECT ID_USUARIO, NM_USUARIO, NR_CPF, DS_EMAIL, NR_BILHETE, "
                + "ST_ATIVO, NR_SALDO_PONTOS FROM TB_SOULMOVE_USUARIO WHERE ID_USUARIO = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarUsuario(rs);
                }
                return null;
            }
        }
    }

    /** READ — lista todos os usuários cadastrados. */
    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT ID_USUARIO, NM_USUARIO, NR_CPF, DS_EMAIL, NR_BILHETE, "
                + "ST_ATIVO, NR_SALDO_PONTOS FROM TB_SOULMOVE_USUARIO ORDER BY ID_USUARIO";

        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(montarUsuario(rs));
            }
        }
        return usuarios;
    }

    /** UPDATE — atualiza os dados de um usuário existente. */
    public boolean atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE TB_SOULMOVE_USUARIO SET NM_USUARIO = ?, NR_CPF = ?, "
                + "DS_EMAIL = ?, NR_BILHETE = ?, ST_ATIVO = ?, NR_SALDO_PONTOS = ? "
                + "WHERE ID_USUARIO = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getNumeroBilheteTransporte());
            stmt.setInt(5, usuario.isCadastroAtivo() ? 1 : 0);
            stmt.setInt(6, usuario.getSaldoPontosSoulUp());
            stmt.setInt(7, usuario.getIdUsuario());
            return stmt.executeUpdate() > 0;
        }
    }

    /** DELETE — remove um usuário pelo id. */
    public boolean remover(int idUsuario) throws SQLException {
        String sql = "DELETE FROM TB_SOULMOVE_USUARIO WHERE ID_USUARIO = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            return stmt.executeUpdate() > 0;
        }
    }

    /** READ — busca um usuário pelo CPF. Devolve null se não existir. */
    public Usuario buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT ID_USUARIO, NM_USUARIO, NR_CPF, DS_EMAIL, NR_BILHETE, "
                + "ST_ATIVO, NR_SALDO_PONTOS FROM TB_SOULMOVE_USUARIO WHERE NR_CPF = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarUsuario(rs);
                }
                return null;
            }
        }
    }

    /** Converte a linha atual do ResultSet em um objeto Usuario. */
    private Usuario montarUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("ID_USUARIO"),
                rs.getString("NM_USUARIO"),
                rs.getString("NR_CPF"),
                rs.getString("DS_EMAIL"),
                rs.getString("NR_BILHETE"),
                rs.getInt("ST_ATIVO") == 1,
                rs.getInt("NR_SALDO_PONTOS")
        );
    }
}
