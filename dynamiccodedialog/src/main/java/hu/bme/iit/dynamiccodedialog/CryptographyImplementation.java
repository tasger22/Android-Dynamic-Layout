package hu.bme.iit.dynamiccodedialog;

import java.lang.reflect.Array;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import hu.bme.iit.dynamiccodedialog.cryptography.Cryptography;

public class CryptographyImplementation implements Cryptography {
    private static final String iv = "T3mPoR4ryIV3cTor";
    private static final String SecretKey = "TemPS3cr3TKEy$tR";
    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;

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

    public Object encrypt(Object input){
        String text = (String) input;
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

    public Object decrypt(Object encryptedArray){
        byte[] array = (byte[]) encryptedArray;
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

    @Override
    public boolean equals(Object code, Object input) {
        byte[] codeBytes = (byte[]) code;
        byte[] inputBytes = (byte[]) input;

        return Arrays.equals(codeBytes, inputBytes);
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

