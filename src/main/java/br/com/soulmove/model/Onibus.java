package br.com.soulmove.model;

/**
 * Modal ônibus. A emissão de CO2 é calculada a partir do consumo de
 * combustível do veículo (km/L) e dividida pela ocupação média,
 * pois a emissão total do veículo é compartilhada entre os passageiros.
 */
public class Onibus extends MeioTransporte {

    /** Ocupação média de um ônibus urbano em São Paulo (passageiros). */
    public static final int OCUPACAO_MEDIA = 40;

    private int numeroRegistroOlhoVivo;
    private int quantidadeDeParadasPercorridas;

    public Onibus() {
        super();
    }

    public Onibus(int idTransporte, String prefixoVeiculo, String combustivelUtilizado,
                  double fatorEmissaoCo2, double consumoKmPorLitro, boolean participaDoPrograma,
                  int numeroRegistroOlhoVivo, int quantidadeDeParadasPercorridas) {
        super(idTransporte, prefixoVeiculo, combustivelUtilizado,
                fatorEmissaoCo2, consumoKmPorLitro, participaDoPrograma);
        this.numeroRegistroOlhoVivo = numeroRegistroOlhoVivo;
        this.quantidadeDeParadasPercorridas = quantidadeDeParadasPercorridas;
    }


    @Override
    public double calcularEmissaoCo2PorPassageiro(double distanciaEmKm) {
        if (distanciaEmKm <= 0 || getConsumo() <= 0) {
            return 0;
        }
        double litrosConsumidos = distanciaEmKm / getConsumo();
        double emissaoTotal = litrosConsumidos * getFatorEmissaoCo2();
        return emissaoTotal / OCUPACAO_MEDIA;
    }

    @Override
    public String getTipoModal() {
        return "ONIBUS";
    }

    public int getNumeroRegistroOlhoVivo() {
        return numeroRegistroOlhoVivo;
    }

    public void setNumeroRegistroOlhoVivo(int numeroRegistroOlhoVivo) {
        this.numeroRegistroOlhoVivo = numeroRegistroOlhoVivo;
    }

    public int getQuantidadeDeParadasPercorridas() {
        return quantidadeDeParadasPercorridas;
    }

    public void setQuantidadeDeParadasPercorridas(int quantidadeDeParadasPercorridas) {
        this.quantidadeDeParadasPercorridas = quantidadeDeParadasPercorridas;
    }
}
