package hu.bme.iit.dynamiclayout_prototype;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import hu.bme.iit.dynamiccodedialog.cryptography.Cryptography;

public class CryptographyImplementation implements Cryptography {
    private String iv = "T3mPoR4ryIV3cTor";
    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;
    private String SecretKey = "TemPS3cr3TKEy$tR";

    public CryptographyImplementation() {
        ivspec = new IvParameterSpec(iv.getBytes());
        keyspec = new SecretKeySpec(SecretKey.getBytes(), "AES");

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(String text){
        if (text == null || text.length() == 0)
            try {
                throw new Exception("Empty string");
            } catch (Exception e) {
                e.printStackTrace();
            }

        byte[] encrypted = null;
        try {
// Cipher.ENCRYPT_MODE = Constant for encryption operation mode.
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

            encrypted = cipher.doFinal(padString(text).getBytes());
        } catch (Exception e) {
            try {
                throw new Exception("[encrypt] " + e.getMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return encrypted;
    }

    public String decrypt(byte[] array){
        if (array == null || array.length == 0)
            try {
                throw new Exception("Empty array");
            } catch (Exception e) {
                e.printStackTrace();
            }

        byte[] decrypted = null;
        try {
// Cipher.DECRYPT_MODE = Constant for decryption operation mode.
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            decrypted = cipher.doFinal(array);
        } catch (Exception e) {
            try {
                throw new Exception("[decrypt] " + e.getMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return new String(decrypted);
    }

    public String byteArrayToHexString(byte[] array){
        StringBuilder hexString = new StringBuilder();
        for (byte b : array) {
            int intVal = b & 0xff;
            if (intVal < 0x10)
                hexString.append("0");
            hexString.append(Integer.toHexString(intVal));
        }
        return hexString.toString();
    }

    public byte[] hexStringToBytes(String str){
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {

            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(
                        str.substring(i * 2, i * 2 + 2), 16);

            }
            return buffer;
        }
    }

    private String padString(String source) {
        String src = source;
        char paddingChar = 0;
        int size = 16;
        int x = src.length() % size;
        int padLength = size - x;
        for (int i = 0; i < padLength; i++) {
            src += paddingChar;
        }
        return src;
    }
}

