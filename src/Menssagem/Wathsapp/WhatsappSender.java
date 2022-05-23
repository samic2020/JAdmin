package Menssagem.Wathsapp;

import Funcoes.VariaveisGlobais;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WhatsappSender {
    /**
     * Test Entry Point
     */
    public static void main(String[] args) throws Exception {
        String number = "5521976659897";
        String message = "Mensagem Ok.";

        WhatsappSender.sendMessageTxt(number, message);
    }

    /**
     * Sends out a WhatsApp message.
     */
    public static void sendMessageTxt(String number, String message) throws Exception {
        final String WA_GATEWAY_URL = VariaveisGlobais.WA_GATEWAY_URL +
              "?api_key="+ VariaveisGlobais.CLIENT_ID + "&api_secret=" + VariaveisGlobais.CLIENT_SECRET;
        String jsonPayload = new StringBuilder()
                .append("{")
                .append("\"from\": {")
                .append("\"type\": \"whatsapp\", \"number\": \"")
                .append(VariaveisGlobais.INSTANCE_ID)
                .append("\" },")
                .append("\"to\": { \"type\": \"whatsapp\", \"number\": \"")
                .append(number)
                .append("\" }, ")
                .append("\"message\": {")
                .append("\"content\": {")
                .append("\"type\": \"text\", ")
                .append("\"text\": \"")
                .append(message)
                .append("\"")
                .append("}")
                .append("}")
                .append("}")
                .toString();

        // System.out.println(jsonPayload);

        URL url = new URL(WA_GATEWAY_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        OutputStream os = conn.getOutputStream();
        os.write(jsonPayload.getBytes());
        os.flush();
        os.close();

        int statusCode = conn.getResponseCode();
        System.out.println("Resposta do WA Gateway: \n");
        System.out.println("Código de status: " + statusCode);
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode == 202) ? conn.getInputStream() : conn.getErrorStream()
        ));
        String output;
        while ((output = br.readLine()) != null) {
            System.out.println(output);
        }
        conn.disconnect();
    }

    public static void sendMessageFile(String number, String file, String caption) throws Exception {
        final String WA_GATEWAY_URL = VariaveisGlobais.WA_GATEWAY_URL +
                "?api_key="+ VariaveisGlobais.CLIENT_ID + "&api_secret=" + VariaveisGlobais.CLIENT_SECRET;

        String jsonPayload = new StringBuilder()
                .append("{")
                .append("\"from\": {")
                .append("\"type\": \"whatsapp\", \"number\": \"")
                .append(VariaveisGlobais.INSTANCE_ID)
                .append("\" },")
                .append("\"to\": { \"type\": \"whatsapp\", \"number\": \"")
                .append(number)
                .append("\" }, ")
                .append("\"message\": {")
                .append("\"content\": {")
                .append("\"type\": \"file\", ")

                .append("\"file\": {")
                .append("\"url\": \"")
                .append(file)
                .append("\", ")
                .append("\"caption\": \"")
                .append(caption)
                .append("\"")
                .append("}")

                .append("}")
                .append("}")
                .append("}")
                .toString();

        // System.out.println(jsonPayload);

        URL url = new URL(WA_GATEWAY_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        OutputStream os = conn.getOutputStream();
        os.write(jsonPayload.getBytes());
        os.flush();
        os.close();

        int statusCode = conn.getResponseCode();
        System.out.println("Resposta do WA Gateway: \n");
        System.out.println("Código de status: " + statusCode);
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode == 202) ? conn.getInputStream() : conn.getErrorStream()
        ));
        String output;
        while ((output = br.readLine()) != null) {
            System.out.println(output);
        }
        conn.disconnect();
    }
}