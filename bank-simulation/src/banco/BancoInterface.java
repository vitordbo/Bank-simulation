package banco;

import java.rmi.Remote;
import java.rmi.RemoteException;

import servidor.ServidorChaves;

public interface BancoInterface extends Remote {
    public String sacar(String conta, double valor, String mensagemCifrada, String mac) throws RemoteException;
    public String depositar(String conta, double valor, String mensagemCifrada, String mac) throws RemoteException;
    public String transferir(String conta, String contaDestino, double valor, String mensagemCifrada, String mac) throws RemoteException;
    public String verSaldo(String conta, String mensagemCifrada, String mac) throws RemoteException;

    // alterar para string depois
    public String investirPoupanca(ContaCorrente conta, double valor, String mensagemCifrada, String mac) throws RemoteException;
    public String investirRendaFixa(ContaCorrente conta, double valor, String mensagemCifrada, String mac) throws RemoteException;
    public String simularInvestimento(ContaCorrente conta, double valor, int meses, String mensagemCifrada, String mac) throws RemoteException;

    boolean existeConta(String numeroConta) throws RemoteException;
    ContaCorrente obterConta(String numeroConta) throws RemoteException;
    ServidorChaves getServidorChaves() throws RemoteException;

    public String cifrarComChaveAES(String texto) throws RemoteException;
    public String decifrarComChaveAES(String textoCifrado) throws RemoteException;
    public String gerarMACComChaveAES(String mensagem) throws RemoteException;
    public boolean verificarMACComChaveAES(String mensagem, String macRecebido) throws RemoteException;
}