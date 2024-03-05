import servidor.ServidorChaves;

public class MensagemFalsa {
    public static void main(String[] args) {
        ServidorChaves servidor = new ServidorChaves();

        // Simulação de uma mensagem falsa enviada pelo atacante
        String mensagemAtaque = "Mensagem falsa";
        String macFalso = "MACFalso"; // Este seria o MAC enviado pelo atacante
        // Um MAC verdadeiro seria gerado a partir da mensagem e da chave secreta 
        
        // Tentativa de autenticar a mensagem falsa
        boolean autenticado = servidor.autenticarMensagem(mensagemAtaque, macFalso);

        if (autenticado) {
            System.out.println("Mensagem autenticada: " + mensagemAtaque);
        } else {
            System.out.println("Mensagem não autenticada. Recusando...");
        }
    }
}
