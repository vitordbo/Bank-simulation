package banco;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BancoInterface extends Remote {
    public String sacar(String conta, double valor, String mensagemCifrada, String mac) throws RemoteException;
    public String depositar(String conta, double valor, String mensagemCifrada, String mac) throws RemoteException;
    public String transferir(String conta, String contaDestino, double valor, String mensagemCifrada, String mac) throws RemoteException;

    // alterar para string depois
    public String investirPoupanca(ContaCorrente conta, double valor, String mensagemCifrada, String mac) throws RemoteException;
    public String investirRendaFixa(ContaCorrente conta, double valor, String mensagemCifrada, String mac) throws RemoteException;
    public String simularInvestimento(ContaCorrente conta, double valor, int meses, String mensagemCifrada, String mac) throws RemoteException;

    boolean existeConta(String numeroConta) throws RemoteException;
    ContaCorrente obterConta(String numeroConta) throws RemoteException;

}