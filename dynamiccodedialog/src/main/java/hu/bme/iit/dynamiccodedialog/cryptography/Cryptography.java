package hu.bme.iit.dynamiccodedialog.cryptography;

public interface Cryptography <CodeType, InputType>{
    CodeType encrypt(InputType input);
    InputType decrypt (CodeType encryptedArray);
    boolean equals (CodeType code, CodeType input);
}
