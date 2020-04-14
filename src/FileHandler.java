import java.io.*;
import java.math.BigInteger;
import java.util.List;

public class FileHandler {
    private KeyFileParser keyFileParser;

    public FileHandler(String filePathOfKeyFile) {
        keyFileParser = new KeyFileParser(filePathOfKeyFile);
    }

    public void writeToFile(String path, List<String> listOfData) {
        try {
            FileWriter fileWriter = new FileWriter(path, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);

            for (String data : listOfData) {
                printWriter.println(data);
            }

            printWriter.close();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            System.exit(-1);
        }
    }

    public void writeToFile(String filePath, BigInteger data) {
        File file = new File(filePath);

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            System.out.println("Big int before byte array = " + data);
            outputStream.write(data.toByteArray());
            System.out.print("Bytes read into file: ");
            for (byte b : data.toByteArray()) {
                System.out.print(b);
            }
            System.out.println();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            System.exit(-1);
        }
    }

    public byte[] getFileBytes(String filePath) throws FileTooBigError {
        byte[] fileBytes = null;

        File file = new File(filePath);
        int numberOfBytesToRead = (int)file.length();

        if (numberOfBytesToRead > RSAConstants.MAX_FILE_SIZE) {
            System.out.println("Too big");
            throw new FileTooBigError();
        }
        try {
            FileInputStream byteReader = new FileInputStream(file);
            fileBytes = new byte[numberOfBytesToRead];

            int read = byteReader.read(fileBytes);
            System.out.println("READ: " + read + " bytes");

            byteReader.close();
        } catch (Exception e) {
            System.exit(-1);
        }

        return fileBytes;
    }

    public KeyPair getPublicKeyPair() {
        BigInteger publicExponent = keyFileParser.getPublicExponent();
        BigInteger modulus = keyFileParser.getModulus();

        KeyPair publicKeyPair = new KeyPair(publicExponent, modulus);

        return publicKeyPair;
    }

    public KeyPair getPrivateKeyPair() {
        BigInteger privateExponent = keyFileParser.getPrivateExponent();
        BigInteger modulus = keyFileParser.getModulus();

        KeyPair privateKeyPair = new KeyPair(privateExponent, modulus);

        return privateKeyPair;
    }
}
