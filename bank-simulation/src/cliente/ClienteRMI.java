package cliente;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import banco.BancoInterface;
import banco.ContaCorrente;

public class ClienteRMI {
    public static void main(String[] args) {
        try {
            // Obtém o registro RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Obtém o objeto remoto do servidor
            BancoInterface banco = (BancoInterface) registry.lookup("BancoService");

            Scanner scanner = new Scanner(System.in);
            System.out.println("Bem-vindo ao banco NuVitor");
            System.out.println("--------------------------------");
            System.out.println("Por favor, digite sua conta e sua senha para ter acesso ao sistema");
            System.out.println("--------------------------------");

            System.out.println("Por favor, digite sua conta: ");
            String conta = scanner.next();

            System.out.println("Por favor, digite sua senha: ");
            String senha = scanner.next();

            // Verifica se a conta existe no banco
            if (banco.existeConta(conta)) {
                
                // Obtém a conta correspondente ao número fornecido
                ContaCorrente contaCorrente = banco.obterConta(conta);

                // Verifica se a senha está correta
                if (senha.equals(contaCorrente.getCliente().getSenha())) {
                    // Cliente autenticado
                    System.out.println("Cliente autenticado: " + contaCorrente.getCliente().getNomeCliente());

                    // Agora o cliente pode interagir com o banco
                    Scanner scannerSwitch = new Scanner(System.in);
                    boolean sair = false;
                    while (!sair) {
                        System.out.println("Escolha uma opção:");
                        System.out.println("1. Sacar");
                        System.out.println("2. Depositar");
                        System.out.println("3. Transferir");
                        System.out.println("0. Sair");
                        int opcao = scannerSwitch.nextInt();
                        scannerSwitch.nextLine(); // Limpa o buffer

                        switch (opcao) {
                            case 1:
                                System.out.println("Digite o valor que deseja sacar: ");
                                double valor = scannerSwitch.nextDouble();
                                String msg = "Quero sacar money";

                                // Criptografa a mensagem e gera o MAC
                                String mensagemCifrada = banco.cifrarComChaveAES(msg);
                                String mac = banco.gerarMACComChaveAES(msg);

                                // Chama o método remoto no servidor para sacar
                                System.out.println(banco.sacar(contaCorrente.getNumeroConta(), valor, mensagemCifrada, mac));
                                break;
                            case 2:
                                System.out.println("Digite o valor que deseja depositar: ");
                                double valorDepo = scannerSwitch.nextDouble();
                                String msgDepo = "Quero depositar money";

                                // Criptografa a mensagem e gera o MAC
                                String mensagemCifradaDepo = banco.cifrarComChaveAES(msgDepo);
                                String macDepo = banco.gerarMACComChaveAES(msgDepo);

                                // Chama o método remoto no servidor para depositar
                                System.out.println(banco.depositar(contaCorrente.getNumeroConta(), valorDepo, mensagemCifradaDepo, macDepo));
                                break;
                            case 3:
                                System.out.println("Digite o valor que deseja transferir: ");
                                double valorTrans = scannerSwitch.nextDouble();

                                System.out.println("Digite a conta para qual deseja transferir: ");
                                String contaTrans = scannerSwitch.next();
                                String transf = "Quero transferir money";

                                // Criptografa a mensagem e gera o MAC
                                String mensagemCifradaTrans = banco.cifrarComChaveAES(transf);
                                String macTrans = banco.gerarMACComChaveAES(transf);

                                // Chama o método remoto no servidor para transferir
                                System.out.println(banco.transferir(contaCorrente.getNumeroConta(), banco.obterConta(contaTrans).getNumeroConta(), valorTrans, mensagemCifradaTrans, macTrans));
                                break;
                            case 0:
                                sair = true;
                                break;
                            default:
                                System.out.println("Opção inválida.");
                        }
                    }
                    scannerSwitch.close();
                } else {
                    // Senha incorreta
                    System.out.println("Senha incorreta.");
                }
            } else {
                // Conta não encontrada
                System.out.println("Conta não encontrada.");
            }

            scanner.close();
        } catch (Exception e) {
            System.err.println("Erro no cliente: " + e.toString());
            e.printStackTrace();
        }
    }
}
