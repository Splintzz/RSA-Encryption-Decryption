import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Scanner;

public class KeyFileParser {
    private String filePath;

    public KeyFileParser(String filePath) {
        this.filePath = filePath;
    }

    public BigInteger getPublicExponent() {
        final int START_OF_EXPONENT = 17;
        Scanner fileScanner = getFileScanner();

        String exponentLine = fileScanner.next();
        String publicExponent = exponentLine.substring(START_OF_EXPONENT, exponentLine.indexOf(','));

        fileScanner.close();

        return new BigInteger(publicExponent);
    }

    public BigInteger getModulus() {
        final int START_OF_MODULUS = 8;
        Scanner fileScanner = getFileScanner();

        fileScanner.next();
        String modulusLine = fileScanner.next();

        String modulus = modulusLine.substring(START_OF_MODULUS, modulusLine.indexOf('}'));

        fileScanner.close();

        return new BigInteger(modulus);
    }

    public BigInteger getPrivateExponent() {
        final int START_OF_EXPONENT = 17;
        Scanner fileScanner = getFileScanner();

        fileScanner.next();
        fileScanner.next();
        String exponentLine = fileScanner.next();

        String publicExponent = exponentLine.substring(START_OF_EXPONENT, exponentLine.indexOf(','));

        fileScanner.close();

        return new BigInteger(publicExponent);
    }

    private Scanner getFileScanner() {
        File fileToReadFrom = new File(filePath);
        Scanner fileScanner = null;

        try {
            fileScanner = new Scanner(fileToReadFrom);
        } catch (FileNotFoundException e) {
            System.exit(-1);
        }

        return fileScanner;
    }
}
