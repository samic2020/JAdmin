package Funcoes;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CriptografiaUtil {
//private static SecretKey skey;
    /**
     * CHAVE DEVE CONTER ATE 24 CARACTERES
     */
    public static final String ALGORITMO_TRIPLE_DES = "DESede";
    /**
     * CHAVE DEVE CONTER ATE 8 CARACTERES
     */
    public static final String ALGORITMO_DES = "DES";
    /**
     * CHAVE DEVE CONTER ATE 16 CARACTERES
     */
    public static final String ALGORITMO_BLOWFISH = "Blowfish";
    /**
     * CHAVE DEVE CONTER ATE 16 CARACTERES
     */
    public static final String ALGORITMO_AES = "AES";

    private static Map tamanhosChaves = new HashMap();

    private static BASE64Encoder enc = new BASE64Encoder();
    private static BASE64Decoder dec = new BASE64Decoder();

    static {
        tamanhosChaves.put(ALGORITMO_TRIPLE_DES, new Long(24));
        tamanhosChaves.put(ALGORITMO_DES, new Long(8));
        tamanhosChaves.put(ALGORITMO_BLOWFISH, new Long(16));
        tamanhosChaves.put(ALGORITMO_AES, new Long(16));

    }
    public static String encrypt(String text, String chave, String algoritmo) throws BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, UnsupportedEncodingException, InvalidKeySpecException {
        SecretKey skey = getSecretKey(chave,algoritmo);
        Cipher cipher = Cipher.getInstance(algoritmo);
        cipher.init(Cipher.ENCRYPT_MODE, skey);
        return enc.encode (cipher.doFinal(text.getBytes()));
    }

    public static String decrypt(String text, String chave, String algoritmo) {
        StringBuffer ret = new StringBuffer();
        try{
            SecretKey skey = getSecretKey(chave, algoritmo);
            Cipher cipher = Cipher.getInstance(algoritmo);
            cipher.init(Cipher.DECRYPT_MODE, skey);

            byte[] b = dec.decodeBuffer (text);
            ret.append(new String(cipher.doFinal(b)));
        }catch(Exception e){
            return "Chave Incorreta";
        }
        return ret.toString();
    }
    public static void encryptFile(String pathAqruivoOriginal, String pathArquivoDestino, String chave, String algoritmo) throws Exception{
        FileInputStream fis;
        FileOutputStream fos;
        CipherInputStream cis;
        SecretKey skey = getSecretKey(chave,algoritmo);
        Cipher cipher = Cipher.getInstance(algoritmo);
        cipher.init(Cipher.ENCRYPT_MODE, skey);

        fis = new FileInputStream(pathAqruivoOriginal);
        cis = new CipherInputStream(fis, cipher);
        fos = new FileOutputStream(pathArquivoDestino);
        int tam = new Long(tamanhosChaves.get(algoritmo).toString()).intValue();
        byte[] b = new byte[tam];
        int i = cis.read(b);
        while (i != -1) {
            fos.write(b, 0, i);
            i = cis.read(b);
        }
        fos.close();
        cis.close();
        fis.close();
    }
    public static void decryptFile(String pathAqruivoOriginal, String pathArquivoDestino, String chave, String algoritmo) throws Exception{
        FileInputStream fis;
        FileOutputStream fos;
        CipherInputStream cis;
        SecretKey skey = getSecretKey(chave,algoritmo);
        Cipher cipher = Cipher.getInstance(algoritmo);
        cipher.init(Cipher.DECRYPT_MODE, skey);

        fis = new FileInputStream(pathAqruivoOriginal);
        cis = new CipherInputStream(fis, cipher);
        fos = new FileOutputStream(pathArquivoDestino);
        int tam = new Long(tamanhosChaves.get(algoritmo).toString()).intValue();
        byte[] b = new byte[tam];
        int i = cis.read(b);
        while (i != -1) {
            fos.write(b, 0, i);
            i = cis.read(b);
        }
        fos.close();
        cis.close();
        fis.close();
    }
    /**
     * Utiliza SHA1, este metodo de criptografia nao pode serdesfeito,
     * uma vez a senha criptografada, ela nao pode ser recuperada
     * @param password
     * @return senha criptografada com SHA1
     */
    public static String encriptaSenha(String password){
        MessageDigest md5=null;
        try {
            md5 = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.reset();
        md5.update(password.getBytes());
        return new String(md5.digest());
    }
    /**
     * verifica se uma senha em texto claro � igual a uma senha criptografada com SHA1.
     * @return true se for iigual , false se for diferente
     */
    public static boolean comparaSenhaCriptografada(String passwordClear, String passwordEncriptado){
        MessageDigest md5=null;
        try {
            md5 = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
        md5.reset();
        md5.update(passwordClear.getBytes());
        String senha1 = new String(md5.digest());
        return MessageDigest.isEqual(senha1.getBytes(), passwordEncriptado.getBytes());
    }
    public static SecretKey getSecretKey(String chave, String algoritmo) {
        String keyString =chave;

        int tam = new Long(tamanhosChaves.get(algoritmo).toString()).intValue();
        byte[] keyB = new byte[tam];
        for (int i = 0; i < keyString.length() && i < keyB.length; i++) {
            keyB[i] = (byte) keyString.charAt(i);
        }

        SecretKey skey = new SecretKeySpec(keyB, algoritmo);
        return skey;
    }
}
