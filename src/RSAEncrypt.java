import java.math.BigInteger;

public class RSAEncrypt {
    private BigInteger encryptedMessage;

    public RSAEncrypt() {
        encryptedMessage = null;
    }

    public void encrypt(BigInteger message, BigInteger publicExponent, BigInteger modulus) {
        encryptedMessage = message.modPow(publicExponent, modulus);

        System.out.println("Encrypted Message: " + encryptedMessage);
    }

    public BigInteger getEncryptedMessage() {
        return encryptedMessage;
    }
}
