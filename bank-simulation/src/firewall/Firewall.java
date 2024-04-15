package firewall;

import java.util.Calendar;

// autenticação já é verificada 
// Firewall para permitir ou bloquear operações com base em algumas condições
public class Firewall {
    public static boolean firewallPermiteSaque(double quantidade) {
        // Verifica o horário (das 9h às 18h)
        Calendar calendar = Calendar.getInstance();
        int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
        if (horaAtual < 9 || horaAtual >= 18) {
            return false; // Bloqueia saques fora do horário 
        }

        // Verifica se a quantidade do saque é maior que um limite 
        if (quantidade > 1000) {
            return false; // Bloqueia saques acima do limite
        }        

        // a operação é permitida
        return true;
    }

    public static boolean firewallPermiteDeposito(double quantidade) {
        // Verifica o horário (das 9h às 18h)
        Calendar calendar = Calendar.getInstance();
        int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
        if (horaAtual < 9 || horaAtual >= 16) {
            return false; // Bloqueia depsoitos fora do horário 
        }

        // Verifica se a quantidade do deposito é maior que um limite 
        if (quantidade > 5000) {
            return false; // Bloqueia depositos acima do limite
        }        

        // a operação é permitida
        return true;
    }

    public static boolean firewallPermiteTrasnferir(double quantidade) {
        // Verifica o horário (das 9h às 18h)
        Calendar calendar = Calendar.getInstance();
        int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
        if (horaAtual < 9 || horaAtual >= 15) {
            return false; // Bloqueia transf fora do horário 
        }

        // Verifica se a quantidade da transf é maior que um limite 
        if (quantidade > 10000) {
            return false; // Bloqueia transf acima do limite
        }        

        // a operação é permitida
        return true;
    }

    public static boolean firewallPermiteSaldo() {
        // Verifica o horário (das 9h às 18h)
        Calendar calendar = Calendar.getInstance();
        int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
        if (horaAtual < 9 || horaAtual >= 15) {
            return false; 
        }

        // a operação é permitida
        return true;
    }

    public static boolean firewallPermiteInvestirPoup(double quantidade) {
        // Verifica o horário (das 9h às 18h)
        Calendar calendar = Calendar.getInstance();
        int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
        if (horaAtual < 9 || horaAtual >= 20) {
            return false; // Bloqueia 
        }

        // Verifica se a quantidade é maior que um limite 
        if (quantidade > 50000) {
            return false; // Bloqueia 
        }        

        // a operação é permitida
        return true;
    }

    public static boolean firewallPermiteInvestirRendaFixa(double quantidade) {
        // Verifica o horário (das 9h às 18h)
        Calendar calendar = Calendar.getInstance();
        int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
        if (horaAtual < 9 || horaAtual >= 20) {
            return false; // Bloqueia 
        }

        // Verifica se a quantidade é maior que um limite 
        if (quantidade > 25000) {
            return false; // Bloqueia 
        }        

        // a operação é permitida
        return true;
    }

}