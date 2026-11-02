package br.com.soulmove.model;

import java.time.LocalDate;

/**
 * Representa uma viagem registrada por um usuário em um meio de
 * transporte público. É a entidade central do SoulMove: a partir dela
 * o sistema valida o trajeto (SoulMove Engine), calcula o CO2 evitado
 * em relação ao carro e converte o impacto em Pontos Soul Up.
 */
public class Viagem {

    /** Emissão média de um carro a gasolina, em kg de CO2 por km. */
    public static final double EMISSAO_CARRO_KG_POR_KM = 0.19;

    /** Fator de conversão: pontos gerados por kg de CO2 evitado. */
    public static final int PONTOS_POR_KG_CO2_EVITADO = 100;

    /** Score mínimo para uma viagem ser considerada válida. */
    public static final double SCORE_MINIMO_VALIDACAO = 0.6;

    private int idViagem;
    private Usuario usuario;
    private MeioTransporte meioTransporte;
    private double distanciaEmKm;
    private int duracaoEmMinutos;
    private LocalDate dataViagem;
    private double co2EvitadoKg;
    private int pontosGerados;
    private boolean validada;

    public Viagem() {
    }

    public Viagem(int idViagem, Usuario usuario, MeioTransporte meioTransporte,
                  double distanciaEmKm, int duracaoEmMinutos, LocalDate dataViagem) {
        this.idViagem = idViagem;
        this.usuario = usuario;
        this.meioTransporte = meioTransporte;
        this.distanciaEmKm = distanciaEmKm;
        this.duracaoEmMinutos = duracaoEmMinutos;
        this.dataViagem = dataViagem;
    }


    public double calcularScoreConfiabilidade() {
        double score = 1.0;

        if (meioTransporte == null || !meioTransporte.isParticipaDoPrograma()) {
            this.validada = false;
            return 0.0;
        }
        if (distanciaEmKm <= 0 || duracaoEmMinutos <= 0) {
            this.validada = false;
            return 0.0;
        }

        double velocidadeMediaKmH = distanciaEmKm / (duracaoEmMinutos / 60.0);

        double velocidadeMinima;
        double velocidadeMaxima;
        if (meioTransporte instanceof Metro) {
            velocidadeMinima = 15.0;
            velocidadeMaxima = 90.0;
        } else {
            velocidadeMinima = 5.0;
            velocidadeMaxima = 60.0;
        }

        if (velocidadeMediaKmH > velocidadeMaxima) {
            // Velocidade impossível para o modal: forte indício de fraude
            score -= 0.7;
        } else if (velocidadeMediaKmH < velocidadeMinima) {
            // Muito lento: pode ser trânsito, penalidade leve
            score -= 0.2;
        }

        // Viagens muito longas para um único trajeto urbano perdem confiança
        if (distanciaEmKm > 80) {
            score -= 0.3;
        }

        if (score < 0) {
            score = 0;
        }

        this.validada = score >= SCORE_MINIMO_VALIDACAO;
        return score;
    }


    public double calcularCo2Evitado() {
        if (meioTransporte == null || distanciaEmKm <= 0) {
            this.co2EvitadoKg = 0;
            return 0;
        }
        double emissaoCarro = distanciaEmKm * EMISSAO_CARRO_KG_POR_KM;
        double emissaoModal = meioTransporte.calcularEmissaoCo2PorPassageiro(distanciaEmKm);
        double evitado = emissaoCarro - emissaoModal;
        this.co2EvitadoKg = Math.max(evitado, 0);
        return this.co2EvitadoKg;
    }


    public int processarRecompensa() {
        double score = calcularScoreConfiabilidade();
        if (!validada || usuario == null) {
            this.pontosGerados = 0;
            return 0;
        }
        double co2Evitado = calcularCo2Evitado();
        this.pontosGerados = (int) Math.round(co2Evitado * PONTOS_POR_KG_CO2_EVITADO * score);

        boolean creditado = usuario.creditarPontos(this.pontosGerados);
        if (!creditado) {
            this.pontosGerados = 0;
        }
        return this.pontosGerados;
    }

    public int getIdViagem() {
        return idViagem;
    }

    public void setIdViagem(int idViagem) {
        this.idViagem = idViagem;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public MeioTransporte getMeioTransporte() {
        return meioTransporte;
    }

    public void setMeioTransporte(MeioTransporte meioTransporte) {
        this.meioTransporte = meioTransporte;
    }

    public double getDistanciaEmKm() {
        return distanciaEmKm;
    }

    public void setDistanciaEmKm(double distanciaEmKm) {
        this.distanciaEmKm = distanciaEmKm;
    }

    public int getDuracaoEmMinutos() {
        return duracaoEmMinutos;
    }

    public void setDuracaoEmMinutos(int duracaoEmMinutos) {
        this.duracaoEmMinutos = duracaoEmMinutos;
    }

    public LocalDate getDataViagem() {
        return dataViagem;
    }

    public void setDataViagem(LocalDate dataViagem) {
        this.dataViagem = dataViagem;
    }

    public double getCo2EvitadoKg() {
        return co2EvitadoKg;
    }

    public void setCo2EvitadoKg(double co2EvitadoKg) {
        this.co2EvitadoKg = co2EvitadoKg;
    }

    public int getPontosGerados() {
        return pontosGerados;
    }

    public void setPontosGerados(int pontosGerados) {
        this.pontosGerados = pontosGerados;
    }

    public boolean isValidada() {
        return validada;
    }

    public void setValidada(boolean validada) {
        this.validada = validada;
    }

    @Override
    public String toString() {
        return "Viagem{id=" + idViagem
                + ", usuario=" + (usuario != null ? usuario.getNome() : "n/d")
                + ", modal=" + (meioTransporte != null ? meioTransporte.getTipoModal() : "n/d")
                + ", distanciaKm=" + distanciaEmKm
                + ", duracaoMin=" + duracaoEmMinutos
                + ", data=" + dataViagem
                + ", co2EvitadoKg=" + String.format("%.3f", co2EvitadoKg)
                + ", pontos=" + pontosGerados
                + ", validada=" + validada + '}';
    }
}
