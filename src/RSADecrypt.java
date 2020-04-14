import java.math.BigInteger;

public class RSADecrypt {
    private BigInteger decryptedMessage;

    public RSADecrypt() {
        decryptedMessage = null;
    }

    public void decrypt(BigInteger cipher, BigInteger privateExponent, BigInteger modulus) {
        decryptedMessage = cipher.modPow(privateExponent, modulus);
    }

    public BigInteger getDecryptedMessage() {
        return decryptedMessage;
    }
}
