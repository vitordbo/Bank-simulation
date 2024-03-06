package servidor;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import banco.Banco;
import banco.BancoInterface;

public class ServidorRMIBanco {
    public static void main(String[] args) {
        try {
            ServidorChaves servidorChaves = new ServidorChaves();

            // Cria o registro do RMI
            Registry registry = LocateRegistry.createRegistry(1099);

            // Cria e vincula o objeto remoto ao registro
            BancoInterface banco = new Banco(servidorChaves);

            registry.rebind("BancoService", banco);

            System.out.println("Servidor RMI do Banco pronto para receber conexões.");
        } catch (Exception e) {
            System.err.println("Erro no servidor: " + e.toString());
            e.printStackTrace();
        }
    }
}