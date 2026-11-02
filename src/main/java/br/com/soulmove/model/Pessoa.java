package br.com.soulmove.model;

/**
 * Classe base que representa uma pessoa no ecossistema SoulMove.
 * Contém os dados básicos de identificação e a regra de negócio
 * de validação de CPF.
 */
public class Pessoa {

    private String nome;
    private String cpf;
    private String email;

    public Pessoa() {
    }

    public Pessoa(String nome, String cpf, String email) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
    }


    public boolean validarCpf() {
        if (cpf == null) {
            return false;
        }

        // Remove pontos e traço, mantendo apenas os dígitos
        String numeros = cpf.replaceAll("[^0-9]", "");

        // CPF deve ter 11 dígitos e não pode ter todos os dígitos iguais
        if (numeros.length() != 11 || numeros.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Cálculo do primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += (numeros.charAt(i) - '0') * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) {
            primeiroDigito = 0;
        }

        // Cálculo do segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += (numeros.charAt(i) - '0') * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) {
            segundoDigito = 0;
        }

        return primeiroDigito == (numeros.charAt(9) - '0')
                && segundoDigito == (numeros.charAt(10) - '0');
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
