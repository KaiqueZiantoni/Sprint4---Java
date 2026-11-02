package br.com.soulmove.dao;

import br.com.soulmove.connection.ConnectionFactory;
import br.com.soulmove.model.MeioTransporte;
import br.com.soulmove.model.Usuario;
import br.com.soulmove.model.Viagem;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ViagemDAO {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final MeioTransporteDAO meioTransporteDAO = new MeioTransporteDAO();

    public int inserir(Viagem viagem) throws SQLException {
        String sqlProximoId = "SELECT SQ_SOULMOVE_VIAGEM.NEXTVAL FROM DUAL";
        String sqlInsert = "INSERT INTO TB_SOULMOVE_VIAGEM "
                + "(ID_VIAGEM, ID_USUARIO, ID_TRANSPORTE, VL_DISTANCIA_KM, NR_DURACAO_MIN, "
                + "DT_VIAGEM, VL_CO2_EVITADO, NR_PONTOS, ST_VALIDADA) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConnectionFactory.obterConexao()) {
            int novoId;
            try (PreparedStatement stmtId = conexao.prepareStatement(sqlProximoId);
                 ResultSet rs = stmtId.executeQuery()) {
                rs.next();
                novoId = rs.getInt(1);
            }
            try (PreparedStatement stmt = conexao.prepareStatement(sqlInsert)) {
                stmt.setInt(1, novoId);
                stmt.setInt(2, viagem.getUsuario().getIdUsuario());
                stmt.setInt(3, viagem.getMeioTransporte().getIdTransporte());
                stmt.setDouble(4, viagem.getDistanciaEmKm());
                stmt.setInt(5, viagem.getDuracaoEmMinutos());
                stmt.setDate(6, Date.valueOf(viagem.getDataViagem()));
                stmt.setDouble(7, viagem.getCo2EvitadoKg());
                stmt.setInt(8, viagem.getPontosGerados());
                stmt.setInt(9, viagem.isValidada() ? 1 : 0);
                stmt.executeUpdate();
            }
            viagem.setIdViagem(novoId);
            return novoId;
        }
    }

    public Viagem buscarPorId(int idViagem) throws SQLException {
        String sql = "SELECT ID_VIAGEM, ID_USUARIO, ID_TRANSPORTE, VL_DISTANCIA_KM, "
                + "NR_DURACAO_MIN, DT_VIAGEM, VL_CO2_EVITADO, NR_PONTOS, ST_VALIDADA "
                + "FROM TB_SOULMOVE_VIAGEM WHERE ID_VIAGEM = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idViagem);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarViagem(rs);
                }
                return null;
            }
        }
    }

    public List<Viagem> listarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT ID_VIAGEM, ID_USUARIO, ID_TRANSPORTE, VL_DISTANCIA_KM, "
                + "NR_DURACAO_MIN, DT_VIAGEM, VL_CO2_EVITADO, NR_PONTOS, ST_VALIDADA "
                + "FROM TB_SOULMOVE_VIAGEM WHERE ID_USUARIO = ? ORDER BY DT_VIAGEM DESC";

        List<Viagem> viagens = new ArrayList<>();
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    viagens.add(montarViagem(rs));
                }
            }
        }
        return viagens;
    }

    public List<Viagem> listarTodos() throws SQLException {
        String sql = "SELECT ID_VIAGEM, ID_USUARIO, ID_TRANSPORTE, VL_DISTANCIA_KM, "
                + "NR_DURACAO_MIN, DT_VIAGEM, VL_CO2_EVITADO, NR_PONTOS, ST_VALIDADA "
                + "FROM TB_SOULMOVE_VIAGEM ORDER BY DT_VIAGEM DESC, ID_VIAGEM DESC";

        List<Viagem> viagens = new ArrayList<>();
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                viagens.add(montarViagem(rs));
            }
        }
        return viagens;
    }

    public boolean atualizar(Viagem viagem) throws SQLException {
        String sql = "UPDATE TB_SOULMOVE_VIAGEM SET ID_USUARIO = ?, ID_TRANSPORTE = ?, "
                + "VL_DISTANCIA_KM = ?, NR_DURACAO_MIN = ?, DT_VIAGEM = ?, "
                + "VL_CO2_EVITADO = ?, NR_PONTOS = ?, ST_VALIDADA = ? "
                + "WHERE ID_VIAGEM = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, viagem.getUsuario().getIdUsuario());
            stmt.setInt(2, viagem.getMeioTransporte().getIdTransporte());
            stmt.setDouble(3, viagem.getDistanciaEmKm());
            stmt.setInt(4, viagem.getDuracaoEmMinutos());
            stmt.setDate(5, Date.valueOf(viagem.getDataViagem()));
            stmt.setDouble(6, viagem.getCo2EvitadoKg());
            stmt.setInt(7, viagem.getPontosGerados());
            stmt.setInt(8, viagem.isValidada() ? 1 : 0);
            stmt.setInt(9, viagem.getIdViagem());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean remover(int idViagem) throws SQLException {
        String sql = "DELETE FROM TB_SOULMOVE_VIAGEM WHERE ID_VIAGEM = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idViagem);
            return stmt.executeUpdate() > 0;
        }
    }

    private Viagem montarViagem(ResultSet rs) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorId(rs.getInt("ID_USUARIO"));
        MeioTransporte transporte = meioTransporteDAO.buscarPorId(rs.getInt("ID_TRANSPORTE"));

        Viagem viagem = new Viagem(
                rs.getInt("ID_VIAGEM"),
                usuario,
                transporte,
                rs.getDouble("VL_DISTANCIA_KM"),
                rs.getInt("NR_DURACAO_MIN"),
                rs.getDate("DT_VIAGEM").toLocalDate()
        );
        viagem.setCo2EvitadoKg(rs.getDouble("VL_CO2_EVITADO"));
        viagem.setPontosGerados(rs.getInt("NR_PONTOS"));
        viagem.setValidada(rs.getInt("ST_VALIDADA") == 1);
        return viagem;
    }
}
