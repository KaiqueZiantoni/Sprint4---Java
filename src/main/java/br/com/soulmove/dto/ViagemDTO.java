package br.com.soulmove.dto;

import java.time.LocalDate;

/**
 * Dados recebidos no JSON para registrar uma viagem.
 * O usuario e o transporte sao informados apenas pelo id; o BO busca os
 * objetos completos no banco e aplica as regras de recompensa.
 */
public class ViagemDTO {

    private int idUsuario;
    private int idTransporte;
    private double distanciaEmKm;
    private int duracaoEmMinutos;
    private LocalDate dataViagem;

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdTransporte() {
        return idTransporte;
    }

    public void setIdTransporte(int idTransporte) {
        this.idTransporte = idTransporte;
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
}
