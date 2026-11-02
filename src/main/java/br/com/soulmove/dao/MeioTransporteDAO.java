package br.com.soulmove.dao;

import br.com.soulmove.connection.ConnectionFactory;
import br.com.soulmove.model.MeioTransporte;
import br.com.soulmove.model.Metro;
import br.com.soulmove.model.Onibus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da entidade MeioTransporte.
 * Implementa o CRUD completo sobre a tabela TB_SOULMOVE_TRANSPORTE.
 *
 * A hierarquia Onibus/Metro é persistida em tabela única com a coluna
 * discriminadora TP_MODAL (estratégia "single table"): na leitura, o DAO
 * instancia a subclasse correta de acordo com o valor da coluna.
 */
public class MeioTransporteDAO {

    public int inserir(MeioTransporte transporte) throws SQLException {
        String sqlProximoId = "SELECT SQ_SOULMOVE_TRANSPORTE.NEXTVAL FROM DUAL";
        String sqlInsert = "INSERT INTO TB_SOULMOVE_TRANSPORTE "
                + "(ID_TRANSPORTE, TP_MODAL, DS_IDENTIFICACAO, DS_COMBUSTIVEL, "
                + "VL_FATOR_EMISSAO, VL_CONSUMO, ST_PARTICIPA) "
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
                stmt.setString(2, transporte.getTipoModal());
                stmt.setString(3, transporte.getIdentificacao());
                stmt.setString(4, transporte.getCombustivelUtilizado());
                stmt.setDouble(5, transporte.getFatorEmissaoCo2());
                stmt.setDouble(6, transporte.getConsumo());
                stmt.setInt(7, transporte.isParticipaDoPrograma() ? 1 : 0);
                stmt.executeUpdate();
            }
            transporte.setIdTransporte(novoId);
            return novoId;
        }
    }

    public MeioTransporte buscarPorId(int idTransporte) throws SQLException {
        String sql = "SELECT ID_TRANSPORTE, TP_MODAL, DS_IDENTIFICACAO, DS_COMBUSTIVEL, "
                + "VL_FATOR_EMISSAO, VL_CONSUMO, ST_PARTICIPA "
                + "FROM TB_SOULMOVE_TRANSPORTE WHERE ID_TRANSPORTE = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idTransporte);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarTransporte(rs);
                }
                return null;
            }
        }
    }

    public List<MeioTransporte> listarTodos() throws SQLException {
        String sql = "SELECT ID_TRANSPORTE, TP_MODAL, DS_IDENTIFICACAO, DS_COMBUSTIVEL, "
                + "VL_FATOR_EMISSAO, VL_CONSUMO, ST_PARTICIPA "
                + "FROM TB_SOULMOVE_TRANSPORTE ORDER BY ID_TRANSPORTE";

        List<MeioTransporte> transportes = new ArrayList<>();
        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                transportes.add(montarTransporte(rs));
            }
        }
        return transportes;
    }

    public boolean atualizar(MeioTransporte transporte) throws SQLException {
        String sql = "UPDATE TB_SOULMOVE_TRANSPORTE SET TP_MODAL = ?, DS_IDENTIFICACAO = ?, "
                + "DS_COMBUSTIVEL = ?, VL_FATOR_EMISSAO = ?, VL_CONSUMO = ?, ST_PARTICIPA = ? "
                + "WHERE ID_TRANSPORTE = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, transporte.getTipoModal());
            stmt.setString(2, transporte.getIdentificacao());
            stmt.setString(3, transporte.getCombustivelUtilizado());
            stmt.setDouble(4, transporte.getFatorEmissaoCo2());
            stmt.setDouble(5, transporte.getConsumo());
            stmt.setInt(6, transporte.isParticipaDoPrograma() ? 1 : 0);
            stmt.setInt(7, transporte.getIdTransporte());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean remover(int idTransporte) throws SQLException {
        String sql = "DELETE FROM TB_SOULMOVE_TRANSPORTE WHERE ID_TRANSPORTE = ?";

        try (Connection conexao = ConnectionFactory.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idTransporte);
            return stmt.executeUpdate() > 0;
        }
    }

    private MeioTransporte montarTransporte(ResultSet rs) throws SQLException {
        String tipoModal = rs.getString("TP_MODAL");

        MeioTransporte transporte;
        if ("METRO".equalsIgnoreCase(tipoModal)) {
            transporte = new Metro();
        } else {
            transporte = new Onibus();
        }
        transporte.setIdTransporte(rs.getInt("ID_TRANSPORTE"));
        transporte.setIdentificacao(rs.getString("DS_IDENTIFICACAO"));
        transporte.setCombustivelUtilizado(rs.getString("DS_COMBUSTIVEL"));
        transporte.setFatorEmissaoCo2(rs.getDouble("VL_FATOR_EMISSAO"));
        transporte.setConsumo(rs.getDouble("VL_CONSUMO"));
        transporte.setParticipaDoPrograma(rs.getInt("ST_PARTICIPA") == 1);
        return transporte;
    }
}
