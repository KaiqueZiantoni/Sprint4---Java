package br.com.soulmove.model;

/**
 * Modal metrô. Movido a energia elétrica: a emissão de CO2 por
 * passageiro é calculada a partir do consumo de energia por km
 * e do fator de emissão da matriz energética brasileira.
 */
public class Metro extends MeioTransporte {

    private String estacaoEmbarque;
    private String estacaoDesembarque;

    public Metro() {
        super();
    }

    public Metro(int idTransporte, String linha, String combustivelUtilizado,
                 double fatorEmissaoCo2, double consumoKwhPorKm, boolean participaDoPrograma,
                 String estacaoEmbarque, String estacaoDesembarque) {
        super(idTransporte, linha, combustivelUtilizado,
                fatorEmissaoCo2, consumoKwhPorKm, participaDoPrograma);
        this.estacaoEmbarque = estacaoEmbarque;
        this.estacaoDesembarque = estacaoDesembarque;
    }


    @Override
    public double calcularEmissaoCo2PorPassageiro(double distanciaEmKm) {
        if (distanciaEmKm <= 0) {
            return 0;
        }
        double energiaConsumida = distanciaEmKm * getConsumo();
        return energiaConsumida * getFatorEmissaoCo2();
    }

    @Override
    public String getTipoModal() {
        return "METRO";
    }

    public String getEstacaoEmbarque() {
        return estacaoEmbarque;
    }

    public void setEstacaoEmbarque(String estacaoEmbarque) {
        this.estacaoEmbarque = estacaoEmbarque;
    }

    public String getEstacaoDesembarque() {
        return estacaoDesembarque;
    }

    public void setEstacaoDesembarque(String estacaoDesembarque) {
        this.estacaoDesembarque = estacaoDesembarque;
    }
}
