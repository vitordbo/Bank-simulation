package servidor;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class ServidorChaves {
    private SecretKey chave;
    private String chaveVernam;
    private KeyGenerator geradorDeChaves;

    public ServidorChaves() {
        gerarChave();
    }

    public void gerarChave() {
        try {
            geradorDeChaves = KeyGenerator.getInstance("AES");
            chave = geradorDeChaves.generateKey();
            System.out.println("Chave AES gerada: " + chave.toString());

            // Gera chave de Vernam com o mesmo tamanho da chave AES
            chaveVernam = gerarChaveVernam(chave.getEncoded().length);
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // Gera uma chave aleatória com o mesmo tamanho da chave AES
    private String gerarChaveVernam(int tamanhoChave) {
        StringBuilder chave = new StringBuilder();

        for (int i = 0; i < tamanhoChave; i++) {
            // Gera um caractere aleatório (letra minúscula)
            char caractere = (char) ('a' + Math.random() * ('z' - 'a' + 1));
            chave.append(caractere);
        }
        return chave.toString();
    }

    public String gerarMAC(String mensagem) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(chave);
        byte[] macBytes = mac.doFinal(mensagem.getBytes());
        
        return Base64.getEncoder().encodeToString(macBytes);
    }

    // retorna boolean caso o MAC esteja certo
    public boolean verificarMAC(String mensagem, String macRecebido) {
        try {
            String macCalculado = gerarMAC(mensagem);

            System.out.println("MAC CALCULADO " + macCalculado);

            return macRecebido.equals(macCalculado);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para autenticação de mensagens 
    public boolean autenticarMensagem(String mensagem, String macRecebido) {
        String decifrado = decifrar(mensagem);

        System.out.println("MAC RECEBIDO " + macRecebido);

        return verificarMAC(decifrado, macRecebido);
    }

    public SecretKey getChave() {
        return chave;
    }

    public String cifrar(String textoAberto){
        // Aplicar a cifra de Vernam na mensagem
        String mensagemCifradaVernam = cifrarVernam(textoAberto);

        // Utilizar o AES para cifrar a mensagem cifrada pela cifra de Vernam
        String mensagemCifradaAES = cifrarAES(mensagemCifradaVernam);

        return mensagemCifradaAES;
    }

    public String decifrar(String textoCifradoAES) {
        // Utilizar o AES para decifrar a mensagem
        String mensagemDecifradaVernam = decifrarAES(textoCifradoAES);

        // Aplicar a decifra de Vernam na mensagem decifrada pelo AES
        String mensagemDecifrada = decifrarVernam(mensagemDecifradaVernam);

        return mensagemDecifrada;
    }

    private String cifrarVernam(String mensagem) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < mensagem.length(); i++) {
            char caractere = mensagem.charAt(i);
            char chaveChar = chaveVernam.charAt(i % chaveVernam.length());
            // XOR para cifrar com a cifra de Vernam
            char cifrado = (char) (caractere ^ chaveChar);
            resultado.append(cifrado);
        }
        return resultado.toString();
    }

    private String cifrarAES(String textoCifradoVernam) {
        byte[] bytesMensagemCifrada;
        Cipher cifrador;
        try {
            cifrador = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cifrador.init(Cipher.ENCRYPT_MODE, chave);
            bytesMensagemCifrada = cifrador.doFinal(textoCifradoVernam.getBytes());
            String mensagemCifrada = Base64.getEncoder().encodeToString(bytesMensagemCifrada);
            return mensagemCifrada;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String decifrarAES(String textoCifradoAES) {
        try {
            Cipher decriptador = Cipher.getInstance("AES/ECB/PKCS5Padding");
            decriptador.init(Cipher.DECRYPT_MODE, chave);
            byte[] bytesMensagemDecifrada = decriptador.doFinal(Base64.getDecoder().decode(textoCifradoAES));
            String mensagemDecifrada = new String(bytesMensagemDecifrada);
            System.out.println("Mensagem cifrada = " + textoCifradoAES);
            System.out.println("---------------------------");
            return mensagemDecifrada;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String decifrarVernam(String mensagemCifradaVernam) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < mensagemCifradaVernam.length(); i++) {
            char caractere = mensagemCifradaVernam.charAt(i);
            char chaveChar = chaveVernam.charAt(i % chaveVernam.length());
            // XOR para decifrar com a cifra de Vernam
            char decifrado = (char) (caractere ^ chaveChar);
            resultado.append(decifrado);
        }
        return resultado.toString();
    }

    public String getChaveVernam() {
        return this.chaveVernam;
    }    
}