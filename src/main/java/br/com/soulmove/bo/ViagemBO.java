package br.com.soulmove.bo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.soulmove.dao.UsuarioDAO;
import br.com.soulmove.dao.ViagemDAO;
import br.com.soulmove.dto.ViagemDTO;
import br.com.soulmove.exception.BancoDadosException;
import br.com.soulmove.exception.RecursoNaoEncontradoException;
import br.com.soulmove.exception.ValidacaoException;
import br.com.soulmove.model.MeioTransporte;
import br.com.soulmove.model.Usuario;
import br.com.soulmove.model.Viagem;

/**
 * Regras de negocio das viagens. Ao registrar uma viagem, o BO aplica o
 * SoulMove Engine (score antifraude, CO2 evitado e pontos) definido na
 * classe Viagem e credita os pontos no usuario.
 */
@Service
public class ViagemBO {

    private final ViagemDAO viagemDAO = new ViagemDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final UsuarioBO usuarioBO;
    private final MeioTransporteBO transporteBO;

    public ViagemBO(UsuarioBO usuarioBO, MeioTransporteBO transporteBO) {
        this.usuarioBO = usuarioBO;
        this.transporteBO = transporteBO;
    }

    public List<Viagem> listar() {
        try {
            return viagemDAO.listarTodos();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao listar as viagens.", e);
        }
    }

    public List<Viagem> listarPorUsuario(int idUsuario) {
        usuarioBO.buscar(idUsuario);
        try {
            return viagemDAO.listarPorUsuario(idUsuario);
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao listar as viagens do usuario.", e);
        }
    }

    public Viagem buscar(int id) {
        try {
            Viagem viagem = viagemDAO.buscarPorId(id);
            if (viagem == null) {
                throw new RecursoNaoEncontradoException("Viagem " + id + " nao encontrada.");
            }
            return viagem;
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar a viagem.", e);
        }
    }

    public Viagem registrar(ViagemDTO dto) {
        validar(dto);
        Usuario usuario = usuarioBO.buscar(dto.getIdUsuario());
        MeioTransporte transporte = transporteBO.buscar(dto.getIdTransporte());

        if (!usuario.isCadastroAtivo()) {
            throw new ValidacaoException("Cadastro inativo nao pode registrar viagens.");
        }

        Viagem viagem = new Viagem(0, usuario, transporte,
                dto.getDistanciaEmKm(), dto.getDuracaoEmMinutos(), dto.getDataViagem());

        // SoulMove Engine: calcula score, CO2 evitado e pontos, e credita no usuario
        viagem.processarRecompensa();

        try {
            viagemDAO.inserir(viagem);
            if (viagem.getPontosGerados() > 0) {
                usuarioDAO.atualizar(usuario);
            }
            return viagem;
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao registrar a viagem.", e);
        }
    }

    public Viagem atualizar(int id, ViagemDTO dto) {
        validar(dto);
        Viagem existente = buscar(id);
        Usuario usuario = usuarioBO.buscar(dto.getIdUsuario());
        MeioTransporte transporte = transporteBO.buscar(dto.getIdTransporte());

        if (usuario.getIdUsuario() != existente.getUsuario().getIdUsuario()) {
            throw new ValidacaoException("Nao e permitido transferir a viagem para outro usuario.");
        }

        int pontosAntigos = existente.getPontosGerados();

        existente.setMeioTransporte(transporte);
        existente.setDistanciaEmKm(dto.getDistanciaEmKm());
        existente.setDuracaoEmMinutos(dto.getDuracaoEmMinutos());
        existente.setDataViagem(dto.getDataViagem());

        // Desfaz o credito antigo e recalcula a recompensa com os novos dados
        usuario.setSaldoPontosSoulUp(Math.max(0, usuario.getSaldoPontosSoulUp() - pontosAntigos));
        existente.setUsuario(usuario);
        existente.processarRecompensa();

        try {
            viagemDAO.atualizar(existente);
            usuarioDAO.atualizar(usuario);
            return existente;
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao atualizar a viagem.", e);
        }
    }

    public void remover(int id) {
        Viagem viagem = buscar(id);
        Usuario usuario = viagem.getUsuario();
        try {
            viagemDAO.remover(id);
            // Os pontos da viagem removida saem do saldo do usuario
            if (viagem.getPontosGerados() > 0 && usuario != null) {
                usuario.setSaldoPontosSoulUp(Math.max(0, usuario.getSaldoPontosSoulUp() - viagem.getPontosGerados()));
                usuarioDAO.atualizar(usuario);
            }
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao remover a viagem.", e);
        }
    }

    private void validar(ViagemDTO dto) {
        if (dto == null) {
            throw new ValidacaoException("Dados da viagem nao informados.");
        }
        if (dto.getDistanciaEmKm() <= 0) {
            throw new ValidacaoException("A distancia deve ser maior que zero.");
        }
        if (dto.getDistanciaEmKm() > 500) {
            throw new ValidacaoException("Distancia acima do limite de 500 km para uma viagem urbana.");
        }
        if (dto.getDuracaoEmMinutos() <= 0) {
            throw new ValidacaoException("A duracao deve ser maior que zero.");
        }
        if (dto.getDataViagem() == null) {
            throw new ValidacaoException("A data da viagem e obrigatoria (formato AAAA-MM-DD).");
        }
        if (dto.getDataViagem().isAfter(LocalDate.now())) {
            throw new ValidacaoException("A data da viagem nao pode ser futura.");
        }
    }
}
