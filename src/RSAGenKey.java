import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RSAGenKey {
    private KeyPair publicKeyPair;
    private KeyPair privateKeyPair;
    private BigInteger modulus;
    private BigInteger modulusPhi;

    public final static int PUBLIC_KEY_PAIR_INDEX = 0;
    public final static int PRIVATE_KEY_PAIR_INDEX = 1;

    public RSAGenKey() {
        publicKeyPair = new KeyPair();
        privateKeyPair = new KeyPair();
    }

    public void generateKeyPairs() {
        generateModulus();

        generatePublicKeyPair();
        generatePrivateKeyPair();
    }

    private void generateModulus() {
        /*
        TODO: You will generate two large primes, p and q, that are larger than 2^511 and less than 2^513 be large d.
              For reasons of efficiency, d should be less than n but not by more than 10 or 15 bits
         */
        Random randomNumberOne = new Random();
        Random randomNumberTwo = new Random();

        BigInteger largePrimeOne = new BigInteger(RSAConstants.BITS_FOR_A_PRIME,
                                                        RSAConstants.CERTAINTY, randomNumberOne);
        BigInteger largePrimeTwo = new BigInteger(RSAConstants.BITS_FOR_A_PRIME,
                                                        RSAConstants.CERTAINTY, randomNumberTwo);

        System.out.println(largePrimeOne);
        System.out.println(largePrimeTwo);

        while (largePrimeOne.equals(largePrimeTwo)) {
            largePrimeOne = new BigInteger(RSAConstants.BITS_FOR_A_PRIME,
                    RSAConstants.CERTAINTY, randomNumberOne);
            largePrimeTwo = new BigInteger(RSAConstants.BITS_FOR_A_PRIME,
                    RSAConstants.CERTAINTY, randomNumberTwo);
        }

        modulusPhi = (largePrimeOne.subtract(BigInteger.ONE)).multiply((largePrimeTwo.subtract(BigInteger.ONE)));
        modulus = largePrimeOne.multiply(largePrimeTwo);

        System.out.println("Value of n is: " + modulus);
        System.out.println("Value of phi(n) is: " + modulusPhi);
    }

    private void generatePublicKeyPair() {
        BigInteger publicExponent = new BigInteger(Integer.toString(RSAConstants.PUBLIC_EXPONENT));

        publicKeyPair = new KeyPair(publicExponent, modulus);

        System.out.println("Public key pair is " + publicKeyPair);
    }

    private void generatePrivateKeyPair() {
        BigInteger privateExponent = publicKeyPair.getExponent().modInverse(modulusPhi);

        privateKeyPair = new KeyPair(privateExponent, modulus);

        System.out.println("Private key pair is " + privateKeyPair);
    }

    public List<KeyPair> getKeyPairs() {
        List<KeyPair> keyPairs = new ArrayList<>();
        keyPairs.add(publicKeyPair);
        keyPairs.add(privateKeyPair);

        return keyPairs;
    }
}