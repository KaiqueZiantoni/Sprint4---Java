package br.com.soulmove.dto;

/**
 * Dados recebidos no JSON para cadastrar ou alterar um meio de transporte.
 * O BO transforma este objeto em Onibus ou Metro conforme o tipoModal.
 */
public class TransporteDTO {

    private String tipoModal;            // ONIBUS ou METRO
    private String identificacao;
    private String combustivelUtilizado;
    private double fatorEmissaoCo2;
    private double consumo;
    private boolean participaDoPrograma = true;

    public String getTipoModal() {
        return tipoModal;
    }

    public void setTipoModal(String tipoModal) {
        this.tipoModal = tipoModal;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public String getCombustivelUtilizado() {
        return combustivelUtilizado;
    }

    public void setCombustivelUtilizado(String combustivelUtilizado) {
        this.combustivelUtilizado = combustivelUtilizado;
    }

    public double getFatorEmissaoCo2() {
        return fatorEmissaoCo2;
    }

    public void setFatorEmissaoCo2(double fatorEmissaoCo2) {
        this.fatorEmissaoCo2 = fatorEmissaoCo2;
    }

    public double getConsumo() {
        return consumo;
    }

    public void setConsumo(double consumo) {
        this.consumo = consumo;
    }

    public boolean isParticipaDoPrograma() {
        return participaDoPrograma;
    }

    public void setParticipaDoPrograma(boolean participaDoPrograma) {
        this.participaDoPrograma = participaDoPrograma;
    }
}
