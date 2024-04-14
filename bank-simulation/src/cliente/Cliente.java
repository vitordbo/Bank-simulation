package cliente;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

import senha.ImplHashSalt;

public class Cliente implements Serializable {
    private String senha;
    private String cpfCliente;
    private String nomeCliente;
    private String telefoneCliente;
    private String enderecoCliente;
    private SecretKey chaveHMAC;
    private String senhaHash; 
    private byte[] salt; 

    // Implementação de Serializable
    private static final long serialVersionUID = 1L;

    public Cliente(String senha, String cpfCliente, String nomeCliente, String telefoneCliente, String enderecoCliente) throws NoSuchAlgorithmException {
        this.senha = senha;
        this.cpfCliente = cpfCliente;
        this.nomeCliente = nomeCliente;
        this.telefoneCliente = telefoneCliente;
        this.enderecoCliente = enderecoCliente;
        this.salt = ImplHashSalt.getSalt();
        this.senhaHash = ImplHashSalt.getSenhaSegura(senha, salt);
        
        gerarChaves();
    }

    public Cliente(String senha) {
        this.senha = senha;
        gerarChaves();
    }

    public void gerarChaves() {
        try {
            KeyGenerator geradorHMAC = KeyGenerator.getInstance("HmacSHA256");
            chaveHMAC = geradorHMAC.generateKey();
            System.out.println("Chaves geradas.");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
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

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }
}