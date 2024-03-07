package banco;

import java.rmi.Remote;
import java.rmi.RemoteException;

import cliente.Cliente;
import servidor.ServidorChaves;

public interface BancoInterface extends Remote {
    // Operações
    public String sacar(String conta, double valor, String mensagemCifrada, String mac) throws RemoteException;
    public String depositar(String conta, double valor, String mensagemCifrada, String mac) throws RemoteException;
    public String transferir(String conta, String contaDestino, double valor, String mensagemCifrada, String mac) throws RemoteException;
    public String verSaldo(String conta, String mensagemCifrada, String mac) throws RemoteException;
    boolean existeConta(String numeroConta) throws RemoteException;
    ContaCorrente obterConta(String numeroConta) throws RemoteException;
    public void criarConta(String numeroConta, String nomeCliente, Cliente cliente) throws RemoteException;
    boolean autenticarMensagem(String mensagem, String macRecebido) throws RemoteException;

    // Investimentos
    public String investirPoupanca(String numeroConta, double valor, String mensagemCifrada, String mac) throws RemoteException;
    public String investirRendaFixa(String conta, double valor, String mensagemCifrada, String mac) throws RemoteException;
    public String simularInvestimento(String conta, double valor, int meses, String mensagemCifrada, String mac) throws RemoteException;

    // Cifras e chaves
    ServidorChaves getServidorChaves() throws RemoteException;
    public String cifrarComChaveAES(String texto) throws RemoteException;
    public String decifrarComChaveAES(String textoCifrado) throws RemoteException;
    public String gerarMACComChaveAES(String mensagem) throws RemoteException;
}