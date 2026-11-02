package br.com.soulmove.model;


public abstract class MeioTransporte {

    private int idTransporte;
    private String identificacao;       // prefixo do veículo ou linha
    private String combustivelUtilizado;
    private double fatorEmissaoCo2;     // kg de CO2 por unidade de combustível/energia
    private double consumo;             // rendimento do modal (km/L ou kWh/km)
    private boolean participaDoPrograma;

    protected MeioTransporte() {
    }

    protected MeioTransporte(int idTransporte, String identificacao,
                             String combustivelUtilizado, double fatorEmissaoCo2,
                             double consumo, boolean participaDoPrograma) {
        this.idTransporte = idTransporte;
        this.identificacao = identificacao;
        this.combustivelUtilizado = combustivelUtilizado;
        this.fatorEmissaoCo2 = fatorEmissaoCo2;
        this.consumo = consumo;
        this.participaDoPrograma = participaDoPrograma;
    }


    public abstract double calcularEmissaoCo2PorPassageiro(double distanciaEmKm);


    public abstract String getTipoModal();

    public int getIdTransporte() {
        return idTransporte;
    }

    public void setIdTransporte(int idTransporte) {
        this.idTransporte = idTransporte;
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

    @Override
    public String toString() {
        return getTipoModal() + "{id=" + idTransporte
                + ", identificacao='" + identificacao + '\''
                + ", combustivel='" + combustivelUtilizado + '\''
                + ", fatorEmissao=" + fatorEmissaoCo2
                + ", consumo=" + consumo
                + ", participa=" + participaDoPrograma + '}';
    }
}
