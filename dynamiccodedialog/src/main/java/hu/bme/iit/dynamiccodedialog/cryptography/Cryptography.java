package hu.bme.iit.dynamiccodedialog.cryptography;

public interface Cryptography {
    Object encrypt(Object input);
    Object decrypt (Object encryptedArray);
    boolean equals (Object code, Object input);
}
