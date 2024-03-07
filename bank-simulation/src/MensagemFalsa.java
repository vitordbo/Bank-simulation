import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MensagemFalsa {

    public static void main(String[] args) {
        String mensagem = "Mensagem secreta";
        String chaveSecreta = "Chave secreta";

        String mac = "MAC falso"; // mac só pode ser descoberto se tiver a chave secreta => um atacante não tem

        System.out.println("MAC: " + mac);

        boolean autenticado = verificarMAC(mensagem, mac, chaveSecreta);

        if (autenticado) {
            System.out.println("Mensagem autenticada");
        } else {
            System.out.println("Mensagem não autenticada");
        }
    }

    public static String gerarMAC(String mensagem, String chaveSecreta) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec chave = new SecretKeySpec(chaveSecreta.getBytes(), "HmacSHA256");
            mac.init(chave);
            byte[] resultado = mac.doFinal(mensagem.getBytes());
            return Base64.getEncoder().encodeToString(resultado);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verificarMAC(String mensagem, String mac, String chaveSecreta) {
        String novoMac = gerarMAC(mensagem, chaveSecreta);
        return mac.equals(novoMac);
    }
}
