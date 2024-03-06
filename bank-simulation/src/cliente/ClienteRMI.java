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
                        System.out.println("4. Saldo");
                        System.out.println("5. Investimento Poupança");
                        System.out.println("6. Investimento Renda Fixa");
                        System.out.println("7. Simular Investimentos");
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
                            case 4:
                                String msgSaldo = "Quero ver meu saldo";

                                // Criptografa a mensagem e gera o MAC
                                String mensagemCifradaSaldo = banco.cifrarComChaveAES(msgSaldo);
                                String macSaldo = banco.gerarMACComChaveAES(msgSaldo);

                                // Chama o método remoto no servidor para ver o saldo
                                System.out.println(banco.verSaldo(contaCorrente.getNumeroConta(), mensagemCifradaSaldo, macSaldo));
                                break;
                            case 5:
                                System.out.println("Digite o valor que deseja investir na poupança: ");
                                double valorPoupan = scannerSwitch.nextDouble();

                                String msgPoupanca = "Quero investir money na poupanca";

                                // Criptografa a mensagem e gera o MAC
                                String mensagemCifradaPoupan = banco.cifrarComChaveAES(msgPoupanca);
                                String macPoupan = banco.gerarMACComChaveAES(msgPoupanca);

                                // Chama o método remoto no servidor para investir
                                System.out.println(banco.investirPoupanca(contaCorrente.getNumeroConta(), valorPoupan, mensagemCifradaPoupan, macPoupan));
                                break;
                            case 6:
                                System.out.println("Digite o valor que deseja investir na renda fixa: ");
                                double valorRendaFixa = scannerSwitch.nextDouble();

                                String msgRendaFixa = "Quero investir money na renda fixa";

                                // Criptografa a mensagem e gera o MAC
                                String mensagemCifradaRendaFixa = banco.cifrarComChaveAES(msgRendaFixa);
                                String macRendaFixa = banco.gerarMACComChaveAES(msgRendaFixa);

                                // Chama o método remoto no servidor para investir
                                System.out.println(banco.investirRendaFixa(contaCorrente.getNumeroConta(), valorRendaFixa, mensagemCifradaRendaFixa, macRendaFixa));
                                break;
                            case 7:
                                System.out.println("Digite o valor para simular o investimento: ");
                                double valorInves = scannerSwitch.nextDouble();

                                System.out.println("Digite a quantidade de meses para simular o investimento: ");
                                int meses = scannerSwitch.nextInt();

                                String msgSimular = "Quero simular meu money";

                                // Criptografa a mensagem e gera o MAC
                                String mensagemCifradaSimula = banco.cifrarComChaveAES(msgSimular);
                                String macSimular = banco.gerarMACComChaveAES(msgSimular);

                                // Chama o método remoto no servidor para transferir
                                System.out.println(banco.simularInvestimento(contaCorrente.getNumeroConta(), valorInves, meses, mensagemCifradaSimula, macSimular));
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
