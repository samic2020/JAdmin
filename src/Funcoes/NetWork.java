package Funcoes;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by supervisor on 22/03/17.
 */
public class NetWork {
    public String NetWork(boolean IpDns) {
        String StringIpDns = null;
        try {
            if (!IpDns) {
                StringIpDns = InetAddress.getLocalHost().getHostName();
            } else {
                StringIpDns = InetAddress.getLocalHost().getHostAddress();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return StringIpDns;
    }

    public  String Ping(String ipAddress) {
        String Host = null;
        try {
            Host = InetAddress.getByName(ipAddress).getHostName();
        } catch (Exception ex) { }

        return !LerValor.isNumeric(Host.split("\\.")[0]) ? Host : "";
    }

}

/*
    String myDns = new NetWork().NetWork(false);
    String myIp = new NetWork().NetWork(true);
            System.out.println("myDns: " + myDns);
                    System.out.println("myIp: " + myIp);
                    String[] faixaIp = myIp.split("\\.");
                    for (int i = 1; i < 255; i++) {
        String ipConsulta = faixaIp[0] + "." + faixaIp[1] + "." + faixaIp[2] + "." + String.valueOf(i).replace(".","");
        if (!myIp.equalsIgnoreCase(ipConsulta)) {
        String sHost = new NetWork().Ping(ipConsulta);
        if (!sHost.equalsIgnoreCase("")) System.out.println(ipConsulta + " : " + new NetWork().Ping(ipConsulta));
        }
        }
*/
