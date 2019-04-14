package hu.bme.iit.dynamiccodedialog.cryptography;

public interface Cryptography {
    byte[] encrypt(String text);
    String decrypt (byte[] array);
}
