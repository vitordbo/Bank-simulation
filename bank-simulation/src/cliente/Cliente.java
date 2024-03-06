package cliente;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

public class Cliente implements Serializable {
    private String senha;
    private String cpfCliente;
    private String nomeCliente;
    private String telefoneCliente;
    private String enderecoCliente;
    private SecretKey chaveAES;
    private SecretKey chaveVernam;
    private SecretKey chaveHMAC;

    // Implementação de Serializable
    private static final long serialVersionUID = 1L;

    public Cliente(String senha, String cpfCliente, String nomeCliente, String telefoneCliente, String enderecoCliente) {
        this.senha = senha;
        this.cpfCliente = cpfCliente;
        this.nomeCliente = nomeCliente;
        this.telefoneCliente = telefoneCliente;
        this.enderecoCliente = enderecoCliente;
        gerarChaves();
    }

    public Cliente(String senha) {
        this.senha = senha;
        gerarChaves();
    }

    public void gerarChaves() {
        try {
            KeyGenerator geradorAES = KeyGenerator.getInstance("AES");
            KeyGenerator geradorVernam = KeyGenerator.getInstance("AES");
            KeyGenerator geradorHMAC = KeyGenerator.getInstance("HmacSHA256");
            chaveAES = geradorAES.generateKey();
            chaveVernam = geradorVernam.generateKey(); // Gerar chave para Vernam
            chaveHMAC = geradorHMAC.generateKey();
            System.out.println("Chaves geradas.");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String cifrarMensagem(String mensagem) throws Exception {
        // Aplicar a cifra de Vernam na mensagem
        String mensagemCifradaVernam = cifrarVernam(mensagem);

        // Utilizar o AES para cifrar a mensagem cifrada pela cifra de Vernam
        Cipher cifrador = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cifrador.init(Cipher.ENCRYPT_MODE, chaveAES);
        byte[] textoCifrado = cifrador.doFinal(mensagemCifradaVernam.getBytes());

        return Base64.getEncoder().encodeToString(textoCifrado);
    }

    private String cifrarVernam(String mensagem) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < mensagem.length(); i++) {
            char caractere = mensagem.charAt(i);
            char chaveChar = chaveVernam.toString().charAt(i % chaveVernam.toString().length()); // Usar a chave de Vernam
            // XOR para cifrar com a cifra de Vernam
            char cifrado = (char) (caractere ^ chaveChar);
            resultado.append(cifrado);
        }
        return resultado.toString();
    }

    public String gerarMAC(String mensagem) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(chaveHMAC);
        byte[] macBytes = mac.doFinal(mensagem.getBytes());
        return Base64.getEncoder().encodeToString(macBytes);
    }

    public boolean verificarMAC(String mensagem, String macRecebido) throws NoSuchAlgorithmException, InvalidKeyException {
        String macCalculado = gerarMAC(mensagem);
        return macRecebido.equals(macCalculado);
    }

    public String getSenha() {
        return senha;
    }

    public String getCpfCliente() {
        return cpfCliente;
    }

    public void setCpfCliente(String cpfCliente) {
        this.cpfCliente = cpfCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getEnderecoCliente() {
        return enderecoCliente;
    }

    public void setEnderecoCliente(String enderecoCliente) {
        this.enderecoCliente = enderecoCliente;
    }

    public String getTelefoneCliente() {
        return telefoneCliente;
    }

    public void setTelefoneCliente(String telefoneCliente) {
        this.telefoneCliente = telefoneCliente;
    }
}