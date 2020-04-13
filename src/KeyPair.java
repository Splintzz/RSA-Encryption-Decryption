import java.math.BigInteger;

public class KeyPair {
    private BigInteger exponent;
    private BigInteger modulus;

    public KeyPair() {
        this(null, null);
    }

    public KeyPair(BigInteger exponent, BigInteger modulus) {
        this.exponent = exponent;
        this.modulus = modulus;
    }

    public BigInteger getExponent() {
        return exponent;
    }

    public BigInteger getModulus() {
        return modulus;
    }

    @Override
    public String toString() {
        return "KeyPair{" +
                "exponent=" + exponent +
                ", modulus=" + modulus +
                '}';
    }
}
