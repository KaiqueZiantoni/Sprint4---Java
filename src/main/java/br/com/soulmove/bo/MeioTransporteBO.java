package br.com.soulmove.bo;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.soulmove.dao.MeioTransporteDAO;
import br.com.soulmove.dto.TransporteDTO;
import br.com.soulmove.exception.BancoDadosException;
import br.com.soulmove.exception.RecursoNaoEncontradoException;
import br.com.soulmove.exception.ValidacaoException;
import br.com.soulmove.model.MeioTransporte;
import br.com.soulmove.model.Metro;
import br.com.soulmove.model.Onibus;

@Service
public class MeioTransporteBO {

    private final MeioTransporteDAO transporteDAO = new MeioTransporteDAO();

    public List<MeioTransporte> listar() {
        try {
            return transporteDAO.listarTodos();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao listar os meios de transporte.", e);
        }
    }

    public MeioTransporte buscar(int id) {
        try {
            MeioTransporte transporte = transporteDAO.buscarPorId(id);
            if (transporte == null) {
                throw new RecursoNaoEncontradoException("Meio de transporte " + id + " nao encontrado.");
            }
            return transporte;
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar o meio de transporte.", e);
        }
    }

    public MeioTransporte cadastrar(TransporteDTO dto) {
        MeioTransporte transporte = montar(dto);
        try {
            transporteDAO.inserir(transporte);
            return transporte;
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao cadastrar o meio de transporte.", e);
        }
    }

    public MeioTransporte atualizar(int id, TransporteDTO dto) {
        buscar(id);
        MeioTransporte transporte = montar(dto);
        transporte.setIdTransporte(id);
        try {
            transporteDAO.atualizar(transporte);
            return transporte;
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao atualizar o meio de transporte.", e);
        }
    }

    public void remover(int id) {
        buscar(id);
        try {
            transporteDAO.remover(id);
        } catch (SQLException e) {
            // Falha mais comum: existem viagens apontando para este transporte (FK)
            throw new ValidacaoException("O meio de transporte possui viagens registradas e nao pode ser removido.");
        }
    }

    /** Valida o DTO e cria a subclasse certa (Onibus ou Metro). */
    private MeioTransporte montar(TransporteDTO dto) {
        if (dto == null) {
            throw new ValidacaoException("Dados do meio de transporte nao informados.");
        }
        if (dto.getTipoModal() == null) {
            throw new ValidacaoException("Informe o tipoModal: ONIBUS ou METRO.");
        }
        if (dto.getIdentificacao() == null || dto.getIdentificacao().isBlank()) {
            throw new ValidacaoException("A identificacao (linha ou prefixo) e obrigatoria.");
        }
        if (dto.getCombustivelUtilizado() == null || dto.getCombustivelUtilizado().isBlank()) {
            throw new ValidacaoException("O combustivel utilizado e obrigatorio.");
        }
        if (dto.getFatorEmissaoCo2() < 0) {
            throw new ValidacaoException("O fator de emissao nao pode ser negativo.");
        }
        if (dto.getConsumo() <= 0) {
            throw new ValidacaoException("O consumo deve ser maior que zero.");
        }

        MeioTransporte transporte;
        String modal = dto.getTipoModal().trim().toUpperCase();
        if (modal.equals("ONIBUS")) {
            transporte = new Onibus();
        } else if (modal.equals("METRO")) {
            transporte = new Metro();
        } else {
            throw new ValidacaoException("tipoModal invalido: use ONIBUS ou METRO.");
        }
        transporte.setIdentificacao(dto.getIdentificacao().trim());
        transporte.setCombustivelUtilizado(dto.getCombustivelUtilizado().trim());
        transporte.setFatorEmissaoCo2(dto.getFatorEmissaoCo2());
        transporte.setConsumo(dto.getConsumo());
        transporte.setParticipaDoPrograma(dto.isParticipaDoPrograma());
        return transporte;
    }
}
