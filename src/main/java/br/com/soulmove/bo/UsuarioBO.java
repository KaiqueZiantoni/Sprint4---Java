package br.com.soulmove.bo;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.soulmove.dao.UsuarioDAO;
import br.com.soulmove.dao.ViagemDAO;
import br.com.soulmove.exception.BancoDadosException;
import br.com.soulmove.exception.RecursoNaoEncontradoException;
import br.com.soulmove.exception.ValidacaoException;
import br.com.soulmove.model.Usuario;

/**
 * Regras de negocio do cadastro de usuarios. Fica entre o Resource e o DAO:
 * valida os dados, consulta o banco e traduz os erros de SQL.
 */
@Service
public class UsuarioBO {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ViagemDAO viagemDAO = new ViagemDAO();

    public List<Usuario> listar() {
        try {
            return usuarioDAO.listarTodos();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao listar os usuarios.", e);
        }
    }

    public Usuario buscar(int id) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(id);
            if (usuario == null) {
                throw new RecursoNaoEncontradoException("Usuario " + id + " nao encontrado.");
            }
            return usuario;
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar o usuario.", e);
        }
    }

    public Usuario cadastrar(Usuario usuario) {
        validar(usuario);
        try {
            if (usuarioDAO.buscarPorCpf(usuario.getCpf()) != null) {
                throw new ValidacaoException("Ja existe um usuario com o CPF informado.");
            }
            // Todo cadastro novo comeca ativo e sem pontos; os pontos vem das viagens
            usuario.setCadastroAtivo(true);
            usuario.setSaldoPontosSoulUp(0);
            usuarioDAO.inserir(usuario);
            return usuario;
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao cadastrar o usuario.", e);
        }
    }

    public Usuario atualizar(int id, Usuario dados) {
        Usuario existente = buscar(id);
        validar(dados);
        try {
            Usuario outro = usuarioDAO.buscarPorCpf(dados.getCpf());
            if (outro != null && outro.getIdUsuario() != id) {
                throw new ValidacaoException("O CPF informado pertence a outro usuario.");
            }
            existente.setNome(dados.getNome());
            existente.setCpf(dados.getCpf());
            existente.setEmail(dados.getEmail());
            existente.setNumeroBilheteTransporte(dados.getNumeroBilheteTransporte());
            existente.setCadastroAtivo(dados.isCadastroAtivo());
            // O saldo nao e alterado por aqui: so muda com viagens e resgates
            usuarioDAO.atualizar(existente);
            return existente;
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao atualizar o usuario.", e);
        }
    }

    public void remover(int id) {
        buscar(id);
        try {
            if (!viagemDAO.listarPorUsuario(id).isEmpty()) {
                throw new ValidacaoException("O usuario possui viagens registradas e nao pode ser removido.");
            }
            usuarioDAO.remover(id);
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao remover o usuario.", e);
        }
    }

    public Usuario resgatarPontos(int id, int pontos) {
        Usuario usuario = buscar(id);
        if (pontos <= 0) {
            throw new ValidacaoException("A quantidade de pontos deve ser maior que zero.");
        }
        if (!usuario.isCadastroAtivo()) {
            throw new ValidacaoException("Cadastro inativo nao pode resgatar pontos.");
        }
        if (!usuario.resgatarPontos(pontos)) {
            throw new ValidacaoException("Saldo insuficiente. Saldo atual: "
                    + usuario.getSaldoPontosSoulUp() + " pontos.");
        }
        try {
            usuarioDAO.atualizar(usuario);
            return usuario;
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao registrar o resgate.", e);
        }
    }

    private void validar(Usuario usuario) {
        if (usuario == null) {
            throw new ValidacaoException("Dados do usuario nao informados.");
        }
        if (usuario.getNome() == null || usuario.getNome().trim().length() < 3) {
            throw new ValidacaoException("O nome deve ter ao menos 3 caracteres.");
        }
        if (!usuario.validarCpf()) {
            throw new ValidacaoException("CPF invalido.");
        }
        if (usuario.getEmail() == null || !usuario.getEmail().matches("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$")) {
            throw new ValidacaoException("E-mail invalido.");
        }
        if (usuario.getNumeroBilheteTransporte() == null || usuario.getNumeroBilheteTransporte().isBlank()) {
            throw new ValidacaoException("O numero do bilhete de transporte e obrigatorio.");
        }
        if (usuario.getNumeroBilheteTransporte().length() > 20) {
            throw new ValidacaoException("O numero do bilhete deve ter no maximo 20 caracteres.");
        }
    }
}
