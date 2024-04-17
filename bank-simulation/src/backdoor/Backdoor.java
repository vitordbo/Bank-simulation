package backdoor;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import banco.ContaCorrente;
import cliente.Cliente;

public class Backdoor {
    
    // Método para acessar e armazenar os dados bancários dos clientes em um log
    public static void backdoorAcessarDadosBancarios(ContaCorrente contaCorrente) {
        String dadosBancarios = obterDadosBancarios(contaCorrente);
        armazenarLog(dadosBancarios);
    }

    // Método para obter os dados bancários de uma conta corrente
    private static String obterDadosBancarios(ContaCorrente contaCorrente) {
        Cliente cliente = contaCorrente.getCliente();

        String dadosBancarios = "Dados bancários do cliente: " +
                "Nome: " + cliente.getNomeCliente() +
                ", CPF: " + contaCorrente.getCliente().getCpfCliente() +
                ", Endereço: " + contaCorrente.getCliente().getEnderecoCliente() +
                ", Telefone: " + contaCorrente.getCliente().getTelefoneCliente();
        return dadosBancarios;
    }

    // Método para armazenar as informações em um arquivo de log
    private static void armazenarLog(String mensagem) {
        try {
            FileWriter fw = new FileWriter("backdoor_log.txt", true); 
            PrintWriter pw = new PrintWriter(fw);
            pw.println(mensagem);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
