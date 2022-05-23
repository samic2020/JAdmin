/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancosDigital.Inter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.net.URL;
import java.security.KeyStore;
import java.util.Base64;
import java.util.stream.Collectors;

/**
 *
 * @author desenvolvimento-pc
 */
public class Inter {
    /**
    / Retono em caso de erro
    / Retorna na função insertBoleta
    **/
    private String codErro;
    private String msgErro;

    public String getCodErro() { return codErro; }
    public String getMsgErro() { return msgErro; }

    bancos bco = new bancos("077");

    public static void main(String[] args) throws Exception {
        //Inter c = new Inter();
      
        //Object[] msg = c.pdfBoleta("https://apis.bancointer.com.br/openbanking/v1/certificado/boletos/00705612432/pdf", "c:\\cert\\Inter_API_Certificado.crt", "c:\\cert\\Inter_API_Chave.key");
        //System.out.println(msg[0] + "\n" + msg[1]);
    }
    
    public Object[] insertBoleta(String url_ws, String path_crt, String path_key, String json_message) throws Exception{
        if (!new File(path_crt).exists()) return new Object[] {-1, new String[] {"Não achei o Certificado."}};
        if (!new File(path_key).exists()) return new Object[] {-1, new String[] {"Não achei a Chave Privada."}};
        
        File crtFile = new File(path_crt);
        File keyFile = new File(path_key);
        
        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samicsistemas");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samicsistemas");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        
        
        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setRequestMethod("POST");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("Accept", "application/json");
        uc.setRequestProperty("x-inter-conta-corrente", bco.getBanco_CCORRENTE());

        OutputStream wr = uc.getOutputStream();
        wr.write(json_message.getBytes());
        wr.flush();
        wr.close();

        int statusCode = uc.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode == 200) ? uc.getInputStream() : uc.getErrorStream()
        ));
        String message = br.readLine();
        
        //String out;
        //while ((out = br.readLine()) != null) {
        //   System.out.println(out);
        //}

        String[] infoMessage = null;
        if (statusCode != 200) {
            if (message == null) {
                infoMessage = new String[] {"Erro desconhecido."};
            } else {
                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {                    
                    JSONArray arrJson = null;
                    try {
                        JSONObject jsonOb = new JSONObject(message);      
                        arrJson = jsonOb.getJSONArray("message");
                    } catch (JSONException jex) {} finally {
                        if (arrJson != null) infoMessage = new String[] {arrJson.getString(0)};                    
                    }
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            }
            codErro = String.valueOf(statusCode); msgErro = infoMessage != null ? infoMessage[0].toString() : null;
        } else {
            JSONObject jsonOb = new JSONObject(message);
            String nossoNumero = myfunction(jsonOb,"nossoNumero").toString();
            String codigoBarras = myfunction(jsonOb,"codigoBarras").toString();
            String linhaDigitavel = myfunction(jsonOb,"linhaDigitavel").toString();
            infoMessage = new String[] {nossoNumero, codigoBarras, linhaDigitavel};
            
            codErro = null; msgErro = null;
        }       
       
       uc.disconnect();
       
       return new Object[] {statusCode, infoMessage};
    }

    public Object[] selectBoleta(String url_ws, String path_crt, String path_key) throws Exception{
        if (!(new File(path_crt)).exists()) return new Object[] {-1, new String[] {"Não achei o Certificado."}};
        if (!(new File(path_key)).exists()) return new Object[] {-1, new String[] {"Não achei a Chave Privada."}};

        File crtFile = new File(path_crt);
        File keyFile = new File(path_key);

        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samicsistemas");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samicsistemas");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        

        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setRequestMethod("GET");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("Accept", "application/json");
        uc.setRequestProperty("x-inter-conta-corrente", bco.getBanco_CCORRENTE());
        uc.setRequestProperty("data-raw", "");

        int statusCode = uc.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
               (statusCode == 200) ? uc.getInputStream() : uc.getErrorStream()
        ));

        String message = br.readLine();
        Object[] infoMessage = null;
        if (statusCode != 200) {
            if (message == null) {
                infoMessage = new String[] {"Erro desconhecido."};
            } else {
                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {
                    JSONObject jsonOb = new JSONObject(message);      
                    JSONArray arrJson=jsonOb.getJSONArray("message");
                    infoMessage = new String[] {arrJson.getString(0)};
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            }
        } else {
            JSONObject jsonOb = new JSONObject(message);
            infoMessage = new Object[] {jsonOb};
        }       
       
       uc.disconnect();
       return new Object[] {statusCode, infoMessage};       
    }

    public Object[] baixaBoleta(String url_ws, String path_crt, String path_key, String codBaixa) throws Exception{
        if (!(new File(path_crt)).exists()) return new Object[] {-1, new String[] {"Não achei o Certificado."}};
        if (!(new File(path_key)).exists()) return new Object[] {-1, new String[] {"Não achei a Chave Privada."}};

        File crtFile = new File(path_crt);
        File keyFile = new File(path_key);

        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samicsistemas");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samicsistemas");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        

        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setRequestMethod("POST");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("Accept", "application/json");
        uc.setRequestProperty("x-inter-conta-corrente", bco.getBanco_CCORRENTE());
        //uc.setRequestProperty("data-raw", codBaixa);

        OutputStream wr = uc.getOutputStream();
        wr.write(codBaixa.getBytes());
        wr.flush();
        wr.close();
        
        int statusCode = uc.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode == 204) ? uc.getInputStream() : uc.getErrorStream()
        ));
        String message = br.readLine();
        
        String out;
        while ((out = br.readLine()) != null) {
           System.out.println(out);
       }
        
        String[] infoMessage = null;
        if (statusCode != 204) {
            if (message == null) {
                infoMessage = new String[] {"Erro desconhecido."};
            } else {
                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {
                    JSONObject jsonOb = new JSONObject(message);      
                    JSONArray arrJson=jsonOb.getJSONArray("message");
                    infoMessage = new String[] {arrJson.getString(0)};
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            }
        } else {
            infoMessage = new String[] {"Sucesso"};
        }       
       
       uc.disconnect();
       return new Object[] {statusCode, infoMessage};  
    }

    public Object[] pdfBoleta(String url_ws, String path_crt, String path_key) throws Exception{
        if (!(new File(path_crt)).exists()) return new Object[] {-1, new String[] {"Não achei o Certificado."}};
        if (!(new File(path_key)).exists()) return new Object[] {-1, new String[] {"Não achei a Chave Privada."}};

        File crtFile = new File(path_crt);
        File keyFile = new File(path_key);

        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samicsistemas");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samicsistemas");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        
        
        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        //uc.setHostnameVerifier(hv);
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setRequestMethod("GET");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("Content-Type", "application/base64");
        uc.setRequestProperty("x-inter-conta-corrente", bco.getBanco_CCORRENTE());

        int statusCode = uc.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode == 200) ? uc.getInputStream() : uc.getErrorStream()
        ));
       
        String message = br.readLine();
        Object[] infoMessage = null;
        if (statusCode != 200) {
            if (message == null) {
                infoMessage = new String[] {"Erro desconhecido."};
            } else {
                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {
                    JSONObject jsonOb = new JSONObject(message);      
                    JSONArray arrJson=jsonOb.getJSONArray("message");
                    infoMessage = new String[] {arrJson.getString(0)};
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            }
        } else {
            InputStream inputStream = uc.getInputStream();
             String result = new BufferedReader(new InputStreamReader(inputStream))
            .lines().collect(Collectors.joining("\n"));
            String saveFilePath = "/cert/interBoleta.pdf";
            if (new File(saveFilePath).exists()) new File(saveFilePath).delete();

            //byte[] data = Base64.getDecoder().decode(result);
            byte[] data = Base64.getDecoder().decode(message);
            OutputStream stream = null;
            try { 
                stream = new FileOutputStream(saveFilePath);
                stream.write(data);
            } catch (Exception e) {
               System.err.println("Couldn't write to file...");
            }

            stream.close();
            inputStream.close();
        }
        
        uc.disconnect();

        return new Object[] {statusCode, infoMessage};
    }    
    
    public Object myfunction(JSONObject x,String y) throws JSONException {
        Object finalresult = null;    
        JSONArray keys =  x.names();
        for(int i=0;i<keys.length();i++) {
            if(finalresult!=null) {
                return finalresult;                     //To kill the recursion
            }

            String current_key = keys.get(i).toString();
            if(current_key.equals(y)) {
                finalresult = x.get(current_key);
                return finalresult;
            }

            if(x.get(current_key).getClass().getName().equals("org.json.JSONObject")) {
                myfunction((JSONObject) x.get(current_key),y);
            }
            else if(x.get(current_key).getClass().getName().equals("org.json.JSONArray")) {
                for(int j=0;j<((JSONArray) x.get(current_key)).length();j++) {
                    if(((JSONArray) x.get(current_key)).get(j).getClass().getName().equals("org.json.JSONObject")) {
                        myfunction((JSONObject)((JSONArray) x.get(current_key)).get(j),y);
                    }
                }
            }
        }
        return null;
    }    
}

/*
{
    "totalPages": 1,
    "totalElements": 6,
    "numberOfElements": 6,
    "last": true,
    "first": true,
    "size": 10,
    "summary": {
        "recebidos": {
            "quantidade": 0,
            "valor": 0
        },
        "previstos": {
            "quantidade": 6,
            "valor": 6.00
        },
        "baixados": {
            "quantidade": 0,
            "valor": 0
        },
        "expirados": {
            "quantidade": 0,
            "valor": 0
        }
    },
    "content": [
        {
            "nossoNumero": "00704389586",
            "seuNumero": "00001",
            "cnpjCpfSacado": "01903033799",
            "nomeSacado": "WELLINGTON DE SOUZA PINTO",
            "situacao": "EMABERTO",
            "dataVencimento": "30/07/2021",
            "valorNominal": 1.00,
            "telefone": "",
            "email": "",
            "dataEmissao": "25/07/2021",
            "dataLimite": "29/08/2021",
            "linhaDigitavel": "07790001161205101210707043895866486970000000100",
            "valorJuros": 0.00,
            "valorMulta": 0.00,
            "desconto1": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "desconto2": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "desconto3": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "multa": {
                "codigo": "PERCENTUAL",
                "data": "2021-08-01",
                "taxa": 10.00,
                "valor": 0.00
            },
            "mora": {
                "codigo": "TAXAMENSAL",
                "data": "2021-08-01",
                "taxa": 1.00,
                "valor": 0.00
            },
            "valorAbatimento": 0.00
        },
        {
            "nossoNumero": "00704389578",
            "seuNumero": "00001",
            "cnpjCpfSacado": "01903033799",
            "nomeSacado": "WELLINGTON DE SOUZA PINTO",
            "situacao": "EMABERTO",
            "dataVencimento": "30/07/2021",
            "valorNominal": 1.00,
            "telefone": "",
            "email": "",
            "dataEmissao": "25/07/2021",
            "dataLimite": "29/08/2021",
            "linhaDigitavel": "07790001161205101210707043895783386970000000100",
            "valorJuros": 0.00,
            "valorMulta": 0.00,
            "desconto1": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "desconto2": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "desconto3": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "multa": {
                "codigo": "PERCENTUAL",
                "data": "2021-08-01",
                "taxa": 10.00,
                "valor": 0.00
            },
            "mora": {
                "codigo": "TAXAMENSAL",
                "data": "2021-08-01",
                "taxa": 1.00,
                "valor": 0.00
            },
            "valorAbatimento": 0.00
        },
        {
            "nossoNumero": "00704389552",
            "seuNumero": "00001",
            "cnpjCpfSacado": "01903033799",
            "nomeSacado": "WELLINGTON DE SOUZA PINTO",
            "situacao": "EMABERTO",
            "dataVencimento": "30/07/2021",
            "valorNominal": 1.00,
            "telefone": "",
            "email": "",
            "dataEmissao": "25/07/2021",
            "dataLimite": "29/08/2021",
            "linhaDigitavel": "07790001161205101210707043895528186970000000100",
            "valorJuros": 0.00,
            "valorMulta": 0.00,
            "desconto1": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "desconto2": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "desconto3": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "multa": {
                "codigo": "PERCENTUAL",
                "data": "2021-08-01",
                "taxa": 10.00,
                "valor": 0.00
            },
            "mora": {
                "codigo": "TAXAMENSAL",
                "data": "2021-08-01",
                "taxa": 1.00,
                "valor": 0.00
            },
            "valorAbatimento": 0.00
        },
        {
            "nossoNumero": "00704389529",
            "seuNumero": "00001",
            "cnpjCpfSacado": "01903033799",
            "nomeSacado": "WELLINGTON DE SOUZA PINTO",
            "situacao": "EMABERTO",
            "dataVencimento": "30/07/2021",
            "valorNominal": 1.00,
            "telefone": "",
            "email": "",
            "dataEmissao": "25/07/2021",
            "dataLimite": "29/08/2021",
            "linhaDigitavel": "07790001161205101210707043895296586970000000100",
            "valorJuros": 0.00,
            "valorMulta": 0.00,
            "desconto1": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "desconto2": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "desconto3": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "multa": {
                "codigo": "PERCENTUAL",
                "data": "2021-08-01",
                "taxa": 10.00,
                "valor": 0.00
            },
            "mora": {
                "codigo": "TAXAMENSAL",
                "data": "2021-08-01",
                "taxa": 1.00,
                "valor": 0.00
            },
            "valorAbatimento": 0.00
        },
        {
            "nossoNumero": "00704389404",
            "seuNumero": "00001",
            "cnpjCpfSacado": "01903033799",
            "nomeSacado": "WELLINGTON DE SOUZA PINTO",
            "situacao": "EMABERTO",
            "dataVencimento": "30/07/2021",
            "valorNominal": 1.00,
            "telefone": "",
            "email": "",
            "dataEmissao": "25/07/2021",
            "dataLimite": "29/08/2021",
            "linhaDigitavel": "07790001161205101210707043894042386970000000100",
            "valorJuros": 0.00,
            "valorMulta": 0.00,
            "desconto1": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "desconto2": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "desconto3": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "multa": {
                "codigo": "PERCENTUAL",
                "data": "2021-08-01",
                "taxa": 10.00,
                "valor": 0.00
            },
            "mora": {
                "codigo": "TAXAMENSAL",
                "data": "2021-08-01",
                "taxa": 1.00,
                "valor": 0.00
            },
            "valorAbatimento": 0.00
        },
        {
            "nossoNumero": "00704389396",
            "seuNumero": "00001",
            "cnpjCpfSacado": "01903033799",
            "nomeSacado": "WELLINGTON DE SOUZA PINTO",
            "situacao": "EMABERTO",
            "dataVencimento": "30/07/2021",
            "valorNominal": 1.00,
            "telefone": "",
            "email": "",
            "dataEmissao": "25/07/2021",
            "dataLimite": "29/08/2021",
            "linhaDigitavel": "07790001161205101210707043893960986970000000100",
            "valorJuros": 0.00,
            "valorMulta": 0.00,
            "desconto1": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "desconto2": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "desconto3": {
                "codigo": "NAOTEMDESCONTO",
                "taxa": 0.00,
                "valor": 0.00
            },
            "multa": {
                "codigo": "PERCENTUAL",
                "data": "2021-08-01",
                "taxa": 10.00,
                "valor": 0.00
            },
            "mora": {
                "codigo": "TAXAMENSAL",
                "data": "2021-08-01",
                "taxa": 1.00,
                "valor": 0.00
            },
            "valorAbatimento": 0.00
        }
    ]
}
*/