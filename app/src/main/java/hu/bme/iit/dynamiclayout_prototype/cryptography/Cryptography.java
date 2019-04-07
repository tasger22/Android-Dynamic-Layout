package hu.bme.iit.dynamiclayout_prototype.cryptography;

public interface Cryptography {
    byte[] encrypt(String text);
    String decrypt (byte[] array);
}
