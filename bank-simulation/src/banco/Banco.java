package banco;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import cliente.Cliente;
import servidor.ServidorChaves;

public class Banco extends UnicastRemoteObject implements BancoInterface {
    private Map<String, ContaCorrente> contas;

    private SecretKey chaveAES;
    private ServidorChaves servidorChaves;

    private double rendimentoPoupanca = 0.005; // 0.5% ao mês
    private double rendimentoRendaFixa = 0.015; // 1.5% ao mês

    public Banco(ServidorChaves servidorChaves) throws RemoteException {
        super();
        this.servidorChaves = servidorChaves;
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

    public SecretKey getChaveAES() {
        return chaveAES;
    }

    @Override
    public String cifrarComChaveAES(String texto) {
        try {
            Cipher cifrador = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cifrador.init(Cipher.ENCRYPT_MODE, chaveAES);
            byte[] bytesCifrados = cifrador.doFinal(texto.getBytes());
            return Base64.getEncoder().encodeToString(bytesCifrados);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public String decifrarComChaveAES(String textoCifrado) {
        try {
            byte[] bytesCifrados = Base64.getDecoder().decode(textoCifrado);
            Cipher decifrador = Cipher.getInstance("AES/ECB/PKCS5Padding");
            decifrador.init(Cipher.DECRYPT_MODE, chaveAES);
            byte[] bytesDecifrados = decifrador.doFinal(bytesCifrados);
            return new String(bytesDecifrados);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String gerarMACComChaveAES(String mensagem) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(chaveAES);
            byte[] macBytes = mac.doFinal(mensagem.getBytes());
            return Base64.getEncoder().encodeToString(macBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean verificarMACComChaveAES(String mensagem, String macRecebido) {
        String macCalculado = gerarMACComChaveAES(mensagem);
        return macCalculado != null && macCalculado.equals(macRecebido);
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
                return "Operação de saque realizada. Seu saldo é de: " + conta.verificarSaldo(); // mudar retorno aqui para mostrar o saldo ou colocar consultar saldo
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
    public String verSaldo(String numeroConta, String mensagemCifrada, String mac) throws RemoteException {
        // Verifica a autenticidade da mensagem
        if (!servidorChaves.autenticarMensagem(mensagemCifrada, mac)) {
            return "Falha na autenticação da mensagem.";
        }

        // Descriptografa a mensagem
        String mensagem = servidorChaves.decifrar(mensagemCifrada);

        // Localiza a conta corrente com base no número da conta
        ContaCorrente conta = contas.get(numeroConta);
        if (conta != null) {
            System.out.println("Desejo: " + mensagem);
    
            double saldo = conta.verificarSaldo();
            System.out.println("Seu saldo é de R$" + saldo);
            return "Consulta de saldo realizada. Seu saldo é de: " + saldo;
        } else {
            System.out.println("Conta corrente não encontrada.");
            return "Conta corrente não encontrada.";
        }
    }

    @Override
    public String investirPoupanca(String numeroConta, double valor, String mensagemCifrada, String mac) throws RemoteException {
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
                // Realiza o investimento na conta corrente encontrada
                double rendimento = valor * rendimentoPoupanca;
                double valorFinal = valor + rendimento;
                conta.setSaldoPoupanca(valorFinal);

                System.out.println("Investimento de R$" + valor + " na poupança realizado com sucesso.");
                return "Investimento de R$" + valor + " na poupança realizado com sucesso. Seu saldo é de: " + conta.getSaldoPoupanca();
            } else {
                System.out.println("Valor de investimento inválido.");
                return "Valor de investimento inválido.";
            }
        } else {
            System.out.println("Conta poupança não encontrada.");
            return "Conta poupança não encontrada.";
        }
    }

    @Override
    public String investirRendaFixa(String numeroConta, double valor, String mensagemCifrada, String mac) throws RemoteException {
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
                // Realiza o investimento na conta corrente encontrada
                double rendimento = valor * rendimentoRendaFixa;
                double valorFinal = valor + rendimento;
                conta.setSaldoRendaFixa(valorFinal);

                System.out.println("Investimento de R$" + valor + " na renda fixa realizado com sucesso.");
                return "Investimento de R$" + valor + " na renda fixa realizado com sucesso. Seu saldo é de: " + conta.getSaldoRendaFixa();
            } else {
                System.out.println("Valor de investimento inválido.");
                return "Valor de investimento inválido.";
            }
        } else {
            System.out.println("Conta poupança não encontrada.");
            return "Conta poupança não encontrada.";
        }
    }

    @Override
    public String simularInvestimento(String numeroConta, double valor, int meses, String mensagemCifrada, String mac) throws RemoteException {
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
                // Realiza o investimento na conta corrente encontrada
                double rendimentoPoupanca = valor * this.rendimentoPoupanca * meses;
                double rendimentoRendaFixa = valor * this.rendimentoRendaFixa * meses;
                double valorTotalPou = valor + rendimentoPoupanca;
                double valorTotalFixa = valor + rendimentoRendaFixa;

                System.out.println("Simulação de investimento para " + meses + " meses:");
                System.out.println("Rendimento da poupança: R$" + rendimentoPoupanca);
                System.out.println("Rendimento da renda fixa: R$" + rendimentoRendaFixa);

                return "Rendimento da poupança: R$" + valorTotalPou + "\n" + "Rendimento da renda fixa: R$" + valorTotalFixa + " em " + meses + " meses";
            } else {
                System.out.println("Valor de investimento inválido.");
                return "Valor de investimento inválido.";
            }
        } else {
            System.out.println("Conta poupança não encontrada.");
            return "Conta poupança não encontrada.";
        }
    }

    @Override
    public boolean existeConta(String numeroConta) throws RemoteException {
        return contas.containsKey(numeroConta);
    }

    @Override
    public ContaCorrente obterConta(String numeroConta) throws RemoteException {
        return contas.get(numeroConta);
    }

    @Override
    public ServidorChaves getServidorChaves() {
        return this.servidorChaves;
    }
}
