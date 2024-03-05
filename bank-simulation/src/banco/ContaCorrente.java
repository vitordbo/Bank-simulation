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

    // Implementação de Serializable
    private static final long serialVersionUID = 1L;

    public ContaCorrente(String numeroConta, Cliente cliente) {
        this.numeroConta = numeroConta;
        this.cliente = cliente;
        this.saldo = 0.0;
        this.rendimentoPoupanca = 0.005; // 0.5% ao mês
        this.rendimentoRendaFixa = 0.015; // 1.5% ao mês
    }

    // // Métodos para saque, depósito e transferência
    // public void sacar(double valor) {
    //     if (this.saldo >= valor) {
    //         this.saldo -= valor;
    //         System.out.println("Saque de " + valor + " realizado com sucesso.");
    //     } else {
    //         System.out.println("Saldo insuficiente.");
    //     }
    // }

    // public void depositar(double valor) {
    //     if (valor > 0) {
    //         saldo += valor;
    //         System.out.println("Depósito de R$" + valor + " realizado com sucesso.");
    //     } else {
    //         System.out.println("Valor de depósito inválido.");
    //     }
    // }

    // public void transferir(ContaCorrente destino, double valor) {
    //     if (valor > 0 && valor <= saldo) {
    //         this.sacar(valor);
    //         destino.depositar(valor);
    //         System.out.println("Transferência de R$" + valor + " realizada com sucesso para a conta " + destino.getNumeroConta() + ".");
    //     } else {
    //         System.out.println("Saldo insuficiente para transferência.");
    //     }
    // }

    // // Métodos para investimentos
    // public void investirPoupanca(double valor) {
    //     double rendimento = valor * rendimentoPoupanca;
    //     saldo += valor + rendimento;
    //     System.out.println("Investimento de R$" + valor + " na poupança realizado com sucesso.");
    // }

    // public void investirRendaFixa(double valor) {
    //     double rendimento = valor * rendimentoRendaFixa;
    //     saldo += valor + rendimento;
    //     System.out.println("Investimento de R$" + valor + " na renda fixa realizado com sucesso.");
    // }

    // public void simularInvestimento(double valor, int meses) {
    //     double rendimentoPoupanca = valor * this.rendimentoPoupanca * meses;
    //     double rendimentoRendaFixa = valor * this.rendimentoRendaFixa * meses;
    //     System.out.println("Simulação de investimento para " + meses + " meses:");
    //     System.out.println("Rendimento da poupança: R$" + rendimentoPoupanca);
    //     System.out.println("Rendimento da renda fixa: R$" + rendimentoRendaFixa);
    // }

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
        this.rendimentoPoupanca = rendimentoPoupanca;
    }

    public double getRendimentoRendaFixa() {
        return rendimentoRendaFixa;
    }

    public void setRendimentoRendaFixa(double rendimentoRendaFixa) {
        this.rendimentoRendaFixa = rendimentoRendaFixa;
    }

    public Cliente getCliente() {
        return cliente;
    }
    
}