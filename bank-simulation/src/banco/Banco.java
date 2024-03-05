package banco;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import cliente.Cliente;
import servidor.ServidorChaves;

public class Banco extends UnicastRemoteObject implements BancoInterface {
    private Map<String, ContaCorrente> contas;

    private SecretKey chaveAES;
    private ServidorChaves servidorChaves = new ServidorChaves();

    private double rendimentoPoupanca = 0.005; // 0.5% ao mês
    private double rendimentoRendaFixa = 0.015; // 1.5% ao mês

    public Banco(SecretKey chaveAES) throws RemoteException {
        super();
        this.contas = new HashMap<>();

        ServidorChaves servidorChaves = new ServidorChaves();
        this.chaveAES = servidorChaves.getChave();

        this.contas = new HashMap<>();

        // Criando os clientes
        Cliente vitor = new Cliente("12345", "111222333-45", "Vitor Duarte", "84992359696", "Pres Dutra 555");
        Cliente fabio = new Cliente("54321", "222333444-56", "Fabio Oliveira", "8499256354", "Cunha da mota 16");
        Cliente paulo = new Cliente("12526", "333444555-67", "Paulo Henrique", "84956854125", "Ufersa 68");

        // Criando as contas correntes
        ContaCorrente contaVitor = new ContaCorrente("01", vitor);
        ContaCorrente contaFabio = new ContaCorrente("02", fabio);
        ContaCorrente contaPaulo = new ContaCorrente("03", paulo);

        // Adicionando as contas ao mapa de contas
        this.contas.put(contaVitor.getNumeroConta(), contaVitor);
        this.contas.put(contaFabio.getNumeroConta(), contaFabio);
        this.contas.put(contaPaulo.getNumeroConta(), contaPaulo);
    }

    public void criarConta(String numeroConta, String nomeCliente, Cliente cliente) {
        ContaCorrente novaConta = new ContaCorrente(numeroConta, cliente);
        contas.put(numeroConta, novaConta);
        System.out.println("Conta corrente criada com sucesso para o cliente " + nomeCliente + ".");
    }

    public ContaCorrente autenticarCliente(String numeroConta, String senha) {
        if (contas.containsKey(numeroConta)) {
            ContaCorrente conta = contas.get(numeroConta);
            if (conta.getCliente().getSenha().equals(senha)) {
                System.out.println("Cliente autenticado com sucesso.");
                return conta;
            }
        }
        System.out.println("Falha na autenticação do cliente.");
        return null;
    }

    // Método para adicionar contas correntes ao banco
    public void adicionarContas(List<ContaCorrente> contas) {
        for (ContaCorrente conta : contas) {
            this.contas.put(conta.getNumeroConta(), conta);
        }
    }

    // Métodos para distribuição de chaves simétricas
    public SecretKey distribuirChaveSimetrica() {
        // Aqui poderíamos implementar a lógica para distribuir a chave simétrica
        return chaveAES;
    }

    @Override
    public String sacar(String numeroConta, double valor, String mensagemCifrada, String mac) throws RemoteException {
        // Verifica a autenticidade da mensagem
        if (!servidorChaves.autenticarMensagem(mensagemCifrada, mac)) {
            return "Falha na autenticação da mensagem.";
        }
    
        // Descriptografa a mensagem
        String mensagem = servidorChaves.decifrar(mensagemCifrada);
    
        // Localiza a conta corrente com base no número da conta
        ContaCorrente conta = contas.get(numeroConta);
        if (conta != null) {
            // lógica de saque
            System.out.println("Desejo: " + mensagem);
    
            if (conta.verificarSaldo() >= valor) {
                conta.setSaldoNeg(valor);
                System.out.println("Saque de " + valor + " realizado com sucesso.");
                System.out.println("Seu saldo é de: " + conta.verificarSaldo());
                return "Operação de saque realizada";
            } else {
                System.out.println("Saldo insuficiente.");
                return "Saldo insuficiente para saque.";
            }
        } else {
            System.out.println("Conta corrente não encontrada.");
            return "Conta corrente não encontrada.";
        }
    }
    
    @Override
    public String depositar(String numeroConta, double valor, String mensagemCifrada, String mac) throws RemoteException {
        // Verifica a autenticidade da mensagem
        if (!servidorChaves.autenticarMensagem(mensagemCifrada, mac)) {
            return "Falha na autenticação da mensagem.";
        }
    
        // Descriptografa a mensagem
        String mensagem = servidorChaves.decifrar(mensagemCifrada);
    
        // Localiza a conta corrente com base no número da conta
        ContaCorrente conta = contas.get(numeroConta);
        if (conta != null) {
            // Implementa a lógica de depósito
            System.out.println("Desejo: " + mensagem);
    
            if (valor > 0) {
                // Realiza o depósito na conta corrente encontrada
                conta.setSaldoPos(valor);
                System.out.println("Depósito de R$" + valor + " realizado com sucesso.");
                return "Operação de depósito realizada. Seu saldo é de: " + conta.verificarSaldo();
            } else {
                System.out.println("Valor de depósito inválido.");
                return "Valor de depósito inválido.";
            }
        } else {
            System.out.println("Conta corrente não encontrada.");
            return "Conta corrente não encontrada.";
        }
    }
    
    @Override
    public String transferir(String numeroContaOrigem, String numeroContaDestino, double valor, String mensagemCifrada, String mac) throws RemoteException {
        // Verifica a autenticidade da mensagem
        if (!servidorChaves.autenticarMensagem(mensagemCifrada, mac)) {
            return "Falha na autenticação da mensagem.";
        }
    
        // Descriptografa a mensagem
        String mensagem = servidorChaves.decifrar(mensagemCifrada);
    
        // Localiza as contas correntes com base nos números das contas
        ContaCorrente contaOrigem = contas.get(numeroContaOrigem);
        ContaCorrente contaDestino = contas.get(numeroContaDestino);
        if (contaOrigem != null && contaDestino != null) {
            // Implementa a lógica de transferência
            System.out.println("Desejo: " + mensagem);
    
            if (valor > 0 && valor <= contaOrigem.verificarSaldo()) {
                // Realiza o saque na conta de origem
                contaOrigem.setSaldoNeg(valor);
    
                // Realiza o depósito na conta de destino
                contaDestino.setSaldoPos(valor);
    
                System.out.println("Transferência de R$" + valor + " realizada com sucesso da conta " + numeroContaOrigem + " para a conta " + numeroContaDestino + ".");
                return "Operação de transferência realizada";
            } else {
                System.out.println("Saldo insuficiente para transferência.");
                return "Saldo insuficiente para transferência.";
            }
        } else {
            System.out.println("Uma das contas correntes não foi encontrada.");
            return "Uma das contas correntes não foi encontrada.";
        }
    }
    

    @Override
    public String investirPoupanca(ContaCorrente conta, double valor, String mensagemCifrada, String mac) throws RemoteException {
        // Verifica a autenticidade da mensagem
        if (!servidorChaves.autenticarMensagem(mensagemCifrada, mac)) {
            return "Falha na autenticação da mensagem.";
        }

        // Descriptografa a mensagem
        String mensagem = servidorChaves.decifrar(mensagemCifrada);

        // Implementa a lógica de investimento na poupança
        System.out.println("Desejo: " + mensagem);

        double rendimento = valor * rendimentoPoupanca;
        conta.setSaldoPos(rendimento); // ver se é so rendimento

        System.out.println("Investimento de R$" + valor + " na poupança realizado com sucesso.");

        // Retorna a resposta ao cliente
        return "Operação de investimento na poupança realizada";
    }

    @Override
    public String investirRendaFixa(ContaCorrente conta, double valor, String mensagemCifrada, String mac) throws RemoteException {
        // Verifica a autenticidade da mensagem
        if (!servidorChaves.autenticarMensagem(mensagemCifrada, mac)) {
            return "Falha na autenticação da mensagem.";
        }

        // Descriptografa a mensagem
        String mensagem = servidorChaves.decifrar(mensagemCifrada);

        // Implementa a lógica de investimento na renda fixa
        System.out.println("Desejo: " + mensagem);

        double rendimento = valor * rendimentoRendaFixa;
        conta.setSaldoPos(rendimento);

        System.out.println("Investimento de R$" + valor + " na renda fixa realizado com sucesso.");
 
        // Retorna a resposta ao cliente
        return "Operação de investimento na renda fixa realizada";
    }

    @Override
    public String simularInvestimento(ContaCorrente conta, double valor, int meses, String mensagemCifrada, String mac) throws RemoteException {
        // Verifica a autenticidade da mensagem
        if (!servidorChaves.autenticarMensagem(mensagemCifrada, mac)) {
            return "Falha na autenticação da mensagem.";
        }

        // Descriptografa a mensagem
        String mensagem = servidorChaves.decifrar(mensagemCifrada);

        // Implementa a lógica de simulação de investimento
        System.out.println("Desejo: " + mensagem);

        double rendimentoPoupanca = valor * this.rendimentoPoupanca * meses;
        double rendimentoRendaFixa = valor * this.rendimentoRendaFixa * meses;
        System.out.println("Simulação de investimento para " + meses + " meses:");
        System.out.println("Rendimento da poupança: R$" + rendimentoPoupanca);
        System.out.println("Rendimento da renda fixa: R$" + rendimentoRendaFixa);

        // Retorna a resposta ao cliente
        return "Simulação de investimento realizada";
    }

    @Override
    public boolean existeConta(String numeroConta) throws RemoteException {
        return contas.containsKey(numeroConta);
    }

    @Override
    public ContaCorrente obterConta(String numeroConta) throws RemoteException {
        return contas.get(numeroConta);
    }
}
