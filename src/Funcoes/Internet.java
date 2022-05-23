package Funcoes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.net.Socket;

public class Internet {
    public boolean ChecarNet() {
        boolean isOnLine = false;
        try{
            java.net.URL mandarMail = new java.net.URL(VariaveisGlobais.urlNET);
            java.net.URLConnection conn = mandarMail.openConnection();

            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) conn;
            httpConn.connect();
            int x = httpConn.getResponseCode();
            if(x == 200){
                System.out.println("Conectado");
                isOnLine = true;
            }
        }
        catch(java.net.MalformedURLException urlmal){

        }
        catch(java.io.IOException ioexcp){

        }
        return isOnLine;
    }

    public static boolean isInternetAvailable() {
        return isHostAvailable("google.com") || isHostAvailable("amazon.com")
                || isHostAvailable("facebook.com")|| isHostAvailable("apple.com");
    }

    private static boolean isHostAvailable(String hostName) {
        try {
            Socket socket = new Socket();
            int port = 80;
            InetSocketAddress socketAddress = new InetSocketAddress(hostName, port);
            socket.connect(socketAddress, 3000);

            return true;
        } catch(UnknownHostException unknownHost) {
            return false;
        } catch (IOException ioException) {
            return false;
        }
    }
}
