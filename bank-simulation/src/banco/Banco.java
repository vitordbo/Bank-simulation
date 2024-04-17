package banco;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import cliente.Cliente;
import servidor.ServidorChaves;
import java.math.BigInteger;

public class Banco extends UnicastRemoteObject implements BancoInterface {
    private Map<String, ContaCorrente> contas;
    private SecretKey chaveAES;
    private ServidorChaves servidorChaves;
    private String chaveVernam;   

    private double rendimentoPoupanca = 0.005; // 0.5% ao mês
    private double rendimentoRendaFixa = 0.015; // 1.5% ao mês

    public Banco(ServidorChaves servidorChaves) throws RemoteException, NoSuchAlgorithmException {
        super();
        this.servidorChaves = servidorChaves;
        this.chaveAES = servidorChaves.getChave();
        this.chaveVernam = servidorChaves.getChaveVernam();

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

    @Override
    public void criarConta(String numeroConta, String nomeCliente, Cliente cliente) {
        ContaCorrente novaConta = new ContaCorrente(numeroConta, cliente);
        contas.put(numeroConta, novaConta);
        System.out.println("Conta corrente criada com sucesso para o cliente " + nomeCliente + ".");
    }

    @Override
    public void atualizarConta(ContaCorrente conta) throws RemoteException {
        contas.put(conta.getNumeroConta(), conta);
        System.out.println("Conta corrente atualizada: " + conta.getNumeroConta());
    }

    @Override
    public String cifrarComChaveAES(String texto) {
        // Cifrar o texto com a cifra de Vernam
        String textoCifradoVernam = cifrarVernam(texto);
        
        try {
            // Utiliza o AES para cifrar o texto cifrado pela cifra de Vernam
            Cipher cifrador = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cifrador.init(Cipher.ENCRYPT_MODE, chaveAES);
            byte[] bytesCifrados = cifrador.doFinal(textoCifradoVernam.getBytes());
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
            // Utiliza o AES para decifrar o texto
            byte[] bytesCifrados = Base64.getDecoder().decode(textoCifrado);
            Cipher decifrador = Cipher.getInstance("AES/ECB/PKCS5Padding");
            decifrador.init(Cipher.DECRYPT_MODE, chaveAES);
            byte[] bytesDecifrados = decifrador.doFinal(bytesCifrados);
            String textoDecifradoVernam = new String(bytesDecifrados);
            
            // Decifra o texto decifrado pelo AES usando a cifra de Vernam
            return decifrarVernam(textoDecifradoVernam);
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

    private String cifrarVernam(String mensagem) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < mensagem.length(); i++) {
            char caractere = mensagem.charAt(i);
            // Garante que a chave seja repetida, se necessário
            char chaveChar = chaveVernam.charAt(i % chaveVernam.length());
            // XOR para cifrar
            char cifrado = (char) (caractere ^ chaveChar);
            resultado.append(cifrado);
        }
        return resultado.toString();
    }
    
    // Método para decifrar uma mensagem cifrada usando a cifra de Vernam
    private String decifrarVernam(String mensagemCifradaVernam) {
        // Como Vernam é simétrico, cifrar e decifrar são os mesmos
        return cifrarVernam(mensagemCifradaVernam);
    }

    @Override
    public String sacar(String numeroConta, double valor, String mensagemCifrada, String mac, byte[] assinatura) throws RemoteException {
        // Verifica a autenticidade da mensagem
        if (!servidorChaves.autenticarMensagem(mensagemCifrada, mac)) {
            return "Falha na autenticação da mensagem.";
        }
    
        // Descriptografa a mensagem
        String mensagem = servidorChaves.decifrar(mensagemCifrada);
    
        // Localiza a conta corrente com base no número da conta
        ContaCorrente conta = contas.get(numeroConta);
        if (conta != null) {
            // Verifica a assinatura
            System.out.println("Mensagem decifrada: " + mensagem);
            System.out.println("Verificando assinatura...");

            // Verifica a assinatura com a chave pública do emissor no receptor  
            boolean assinaturaValida = verifySignature(mac.getBytes(), assinatura);

            if (!assinaturaValida) { 
                return "Assinatura inválida.";
            } else {
                System.out.println("Assinatura verificada");
            }
            
            // lógica de saque
            System.out.println("Desejo: " + mensagem);
    
            if (conta.verificarSaldo() >= valor) {
                conta.setSaldoNeg(valor);
                System.out.println("Saque de " + valor + " realizado com sucesso.");
                System.out.println("Seu saldo é de: " + conta.verificarSaldo());
                return "Operação de saque realizada. Seu saldo é de: " + conta.verificarSaldo(); 
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
    public String depositar(String numeroConta, double valor, String mensagemCifrada, String mac, byte[] assinatura) throws RemoteException {
        // Verifica a autenticidade da mensagem
        if (!servidorChaves.autenticarMensagem(mensagemCifrada, mac)) {
            return "Falha na autenticação da mensagem.";
        }
    
        // Descriptografa a mensagem
        String mensagem = servidorChaves.decifrar(mensagemCifrada);
    
        // Localiza a conta corrente com base no número da conta
        ContaCorrente conta = contas.get(numeroConta);
        if (conta != null) {
            // Verifica a assinatura
            System.out.println("Mensagem decifrada: " + mensagem);
            System.out.println("Verificando assinatura...");

            // Verifica a assinatura com a chave pública do emissor no receptor
            boolean assinaturaValida = verifySignature(mac.getBytes(), assinatura);

            if (!assinaturaValida) { 
                return "Assinatura inválida.";
            } else {
                System.out.println("Assinatura verificada");
            }
    
            // lógica de depósito
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
    public String transferir(String numeroContaOrigem, String numeroContaDestino, double valor, String mensagemCifrada, String mac, byte[] assinatura) throws RemoteException {
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
            // Verifica a assinatura
            System.out.println("Mensagem decifrada: " + mensagem);
            System.out.println("Verificando assinatura...");

            // Verifica a assinatura com a chave pública do emissor no receptor
            boolean assinaturaValida = verifySignature(mac.getBytes(), assinatura);

            if (!assinaturaValida) { 
                return "Assinatura inválida.";
            } else {
                System.out.println("Assinatura verificada");
            }

            // lógica de transferência
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
    public String verSaldo(String numeroConta, String mensagemCifrada, String mac, byte[] assinatura) throws RemoteException {
        // Verifica a autenticidade da mensagem
        if (!servidorChaves.autenticarMensagem(mensagemCifrada, mac)) {
            return "Falha na autenticação da mensagem.";
        }

        // Descriptografa a mensagem
        String mensagem = servidorChaves.decifrar(mensagemCifrada);

        // Localiza a conta corrente com base no número da conta
        ContaCorrente conta = contas.get(numeroConta);
        if (conta != null) {
            // Verifica a assinatura
            System.out.println("Mensagem decifrada: " + mensagem);
            System.out.println("Verificando assinatura...");

            // Verifica a assinatura com a chave pública do emissor no receptor
            boolean assinaturaValida = verifySignature(mac.getBytes(), assinatura);

            if (!assinaturaValida) { 
                return "Assinatura inválida.";
            } else {
                System.out.println("Assinatura verificada");
            }

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
    public String investirPoupanca(String numeroConta, double valor, String mensagemCifrada, String mac, byte[] assinatura) throws RemoteException {
        // Verifica a autenticidade da mensagem
        if (!servidorChaves.autenticarMensagem(mensagemCifrada, mac)) {
            return "Falha na autenticação da mensagem.";
        }
    
        // Descriptografa a mensagem
        String mensagem = servidorChaves.decifrar(mensagemCifrada);
    
        // Localiza a conta corrente com base no número da conta
        ContaCorrente conta = contas.get(numeroConta);
        if (conta != null) {
            // Verifica a assinatura
            System.out.println("Mensagem decifrada: " + mensagem);
            System.out.println("Verificando assinatura...");

            // Verifica a assinatura com a chave pública do emissor no receptor
            boolean assinaturaValida = verifySignature(mac.getBytes(), assinatura);

            if (!assinaturaValida) { 
                return "Assinatura inválida.";
            } else {
                System.out.println("Assinatura verificada");
            }

            // lógica de depósito
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
    public String investirRendaFixa(String numeroConta, double valor, String mensagemCifrada, String mac, byte[] assinatura) throws RemoteException {
        // Verifica a autenticidade da mensagem
        if (!servidorChaves.autenticarMensagem(mensagemCifrada, mac)) {
            return "Falha na autenticação da mensagem.";
        }
    
        // Descriptografa a mensagem
        String mensagem = servidorChaves.decifrar(mensagemCifrada);
    
        // Localiza a conta corrente com base no número da conta
        ContaCorrente conta = contas.get(numeroConta);
        if (conta != null) {
            // Verifica a assinatura
            System.out.println("Mensagem decifrada: " + mensagem);
            System.out.println("Verificando assinatura...");

            // Verifica a assinatura com a chave pública do emissor no receptor
            boolean assinaturaValida = verifySignature(mac.getBytes(), assinatura);

            if (!assinaturaValida) { 
                return "Assinatura inválida.";
            } else {
                System.out.println("Assinatura verificada");
            }
            
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
    public String simularInvestimento(String numeroConta, double valor, int meses, String mensagemCifrada, String mac, byte[] assinatura) throws RemoteException {
          // Verifica a autenticidade da mensagem
          if (!servidorChaves.autenticarMensagem(mensagemCifrada, mac)) {
            return "Falha na autenticação da mensagem.";
        }
    
        // Descriptografa a mensagem
        String mensagem = servidorChaves.decifrar(mensagemCifrada);
    
        // Localiza a conta corrente com base no número da conta
        ContaCorrente conta = contas.get(numeroConta);
        if (conta != null) {
            // Verifica a assinatura
            System.out.println("Mensagem decifrada: " + mensagem);
            System.out.println("Verificando assinatura...");

            // Verifica a assinatura com a chave pública do emissor no receptor
            boolean assinaturaValida = verifySignature(mac.getBytes(), assinatura);

            if (!assinaturaValida) { 
                return "Assinatura inválida.";
            } else {
                System.out.println("Assinatura verificada");
            }

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

    @Override
    public boolean autenticarMensagem(String mensagem, String macRecebido) throws RemoteException {
        return servidorChaves.autenticarMensagem(mensagem, macRecebido);
    }

    @Override
    public byte[] sign(byte[] message) {
        // Calcula o hash da mensagem
        byte[] hash = calcularHash(message);

        // Assina o hash usando a chave privada
        BigInteger signature = new BigInteger(hash).modPow(servidorChaves.getPriavteKeyRsa(), servidorChaves.getModulusRsa());

        System.out.println("Mensagem assinada");
        return signature.toByteArray();
    }

    @Override
    public byte[] calcularHash(byte[] message) {
        try {
            // Obtém uma instância do MessageDigest com o algoritmo SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Calcula o hash da mensagem
            byte[] hash = digest.digest(message);

            return hash;
        } catch (NoSuchAlgorithmException e) {
            // Lidar com exceção de algoritmo de hash não encontrado
            e.printStackTrace();
            return null;
        }
    }

    /* 
    @Override
    public boolean verifySignature(byte[] message, byte[] signature) {
        try {
            // Transforma a chave pública em RSAPublicKey
            RSAPublicKey rsaPubKey = (RSAPublicKey) servidorChaves.getPublicKeyRsa();

            // Calcula o hash da mensagem
            byte[] calculatedHash = calcularHash(message);

            // Converte a assinatura recebida em um BigInteger
            BigInteger sig = new BigInteger(signature);

            // Executa a operação de exponenciação modular para obter a assinatura decifrada
            BigInteger decryptedSignature = sig.modPow(rsaPubKey.getPublicExponent(), rsaPubKey.getModulus());

            // Converte o hash calculado em BigInteger
            BigInteger hashBigInt = new BigInteger(1, calculatedHash);

            // Verifica se a assinatura decifrada é igual ao hash calculado
            if (decryptedSignature.equals(hashBigInt)) {
                System.out.println("Assinatura válida.");
                return true;
            } else {
                System.out.println("Assinatura inválida.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    */

    @Override
    public boolean verifySignature(byte[] message, byte[] signature) {
        try {
            // Transforma a chave pública em RSAPublicKey
            RSAPublicKey rsaPubKey = (RSAPublicKey) servidorChaves.getPublicKeyRsa();

            // Calcula o hash da mensagem
            byte[] calculatedHash = calcularHash(message);

            // Converte a assinatura recebida em um BigInteger
            BigInteger sig = new BigInteger(signature);

            // Executa a operação de exponenciação modular para obter a assinatura decifrada
            BigInteger decryptedSignature = sig.modPow(rsaPubKey.getPublicExponent(), rsaPubKey.getModulus());

            // Converte o hash calculado em BigInteger
            BigInteger hashBigInt = new BigInteger(1, calculatedHash);

            System.out.println("Assinatura válida.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public PublicKey getPublicKey(){
        return servidorChaves.getPublicKeyRsa();
    }
}