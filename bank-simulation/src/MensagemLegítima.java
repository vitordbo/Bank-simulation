import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import servidor.ServidorChaves;

public class MensagemLegítima {
    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException {
        ServidorChaves servidor = new ServidorChaves();

        // Mensagem legítima enviada por um usuário
        String mensagem = "Mensagem legítima";
        
        // Gerar um MAC verdadeiro para a mensagem
        String macVerdadeiro = servidor.gerarMAC(mensagem);

        // Tentativa de autenticar a mensagem com o MAC verdadeiro
        boolean autenticado = servidor.autenticarMensagem(mensagem, macVerdadeiro);

        if (autenticado) {
            System.out.println("Mensagem autenticada: " + mensagem);
        } else {
            System.out.println("Mensagem não autenticada.");
        }
    }
}
