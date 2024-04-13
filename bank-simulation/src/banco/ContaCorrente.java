package banco;

import java.io.Serializable;

import cliente.Cliente; 

// Classe para representar uma conta bancária
public class ContaCorrente implements Serializable {
    private String numeroConta;
    private double saldo;
    private double rendimentoPoupanca;
    private double rendimentoRendaFixa;
    private Cliente cliente;
    private double saldoPoupanca;
    private double saldoRendaFixa;

    // Implementação de Serializable
    private static final long serialVersionUID = 1L;

    public ContaCorrente(String numeroConta, Cliente cliente) {
        this.numeroConta = numeroConta;
        this.cliente = cliente;
        this.saldo = 0.0;
        this.rendimentoPoupanca = 0.005; // 0.5% ao mês
        this.rendimentoRendaFixa = 0.015; // 1.5% ao mês
    }
    
    public double getSaldoPoupanca() {
        return saldoPoupanca;
    }

    public void setSaldoPoupanca(double saldoPoupanca) {
        this.saldoPoupanca = saldoPoupanca;
    }

    public double getSaldoRendaFixa() {
        return saldoRendaFixa;
    }

    public void setSaldoRendaFixa(double saldoRendaFixa) {
        this.saldoRendaFixa = saldoRendaFixa;
    }

    public double verificarSaldo() {
        return saldo;
    }

    public void setSaldoNeg(double valor) {
        this.saldo -= valor;
    }

    public void setSaldoPos(double valor) {
        this.saldo += valor;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public double getRendimentoPoupanca() {
        return rendimentoPoupanca;
    }

    public void setRendimentoPoupanca(double rendimentoPoupanca) {
        this.rendimentoPoupanca += rendimentoPoupanca;
    }

    public double getRendimentoRendaFixa() {
        return rendimentoRendaFixa;
    }

    public void setRendimentoRendaFixa(double rendimentoRendaFixa) {
        this.rendimentoRendaFixa += rendimentoRendaFixa;
    }

    public Cliente getCliente() {
        return cliente;
    }
}