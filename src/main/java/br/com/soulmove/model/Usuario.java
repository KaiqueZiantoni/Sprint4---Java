package br.com.soulmove.model;

/**
 * Representa um usuário cadastrado no SoulMove.
 * Herda os dados básicos de Pessoa e adiciona a carteira digital
 * de Pontos Soul Up, o bilhete de transporte e o estado do cadastro.
 */
public class Usuario extends Pessoa {

    private int idUsuario;
    private String numeroBilheteTransporte;
    private boolean cadastroAtivo;
    private int saldoPontosSoulUp;

    public Usuario() {
        super();
    }

    public Usuario(int idUsuario, String nome, String cpf, String email,
                   String numeroBilheteTransporte, boolean cadastroAtivo,
                   int saldoPontosSoulUp) {
        super(nome, cpf, email);
        this.idUsuario = idUsuario;
        this.numeroBilheteTransporte = numeroBilheteTransporte;
        this.cadastroAtivo = cadastroAtivo;
        this.saldoPontosSoulUp = saldoPontosSoulUp;
    }


    public boolean creditarPontos(int pontos) {
        if (!cadastroAtivo || !validarCpf() || pontos <= 0) {
            return false;
        }
        this.saldoPontosSoulUp += pontos;
        return true;
    }


    public boolean resgatarPontos(int pontos) {
        if (!cadastroAtivo || pontos <= 0 || pontos > saldoPontosSoulUp) {
            return false;
        }
        this.saldoPontosSoulUp -= pontos;
        return true;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNumeroBilheteTransporte() {
        return numeroBilheteTransporte;
    }

    public void setNumeroBilheteTransporte(String numeroBilheteTransporte) {
        this.numeroBilheteTransporte = numeroBilheteTransporte;
    }

    public boolean isCadastroAtivo() {
        return cadastroAtivo;
    }

    public void setCadastroAtivo(boolean cadastroAtivo) {
        this.cadastroAtivo = cadastroAtivo;
    }

    public int getSaldoPontosSoulUp() {
        return saldoPontosSoulUp;
    }

    public void setSaldoPontosSoulUp(int saldoPontosSoulUp) {
        this.saldoPontosSoulUp = saldoPontosSoulUp;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + idUsuario
                + ", nome='" + getNome() + '\''
                + ", cpf='" + getCpf() + '\''
                + ", bilhete='" + numeroBilheteTransporte + '\''
                + ", ativo=" + cadastroAtivo
                + ", saldoPontosSoulUp=" + saldoPontosSoulUp + '}';
    }
}
