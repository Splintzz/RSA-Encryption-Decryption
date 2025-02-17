import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class RSAUserInterface extends JFrame {
    private RSAGenKey keyGenerator;
    private RSAEncrypt encryptor;
    private RSADecrypt decryptor;
    private FileHandler fileHandler;

    private JRadioButton encryptSetting, decryptSetting;
    private JButton encryptOrDecryptButton, generateKeyButton;

    private JButton enterSourceFilePathButton, enterDestinationFilePathButton, enterKeyFilePathButton;
    private JLabel sourceFilePath, destinationFilePath, keyFilePath;

    private boolean sourceFilePathChosen = false, destinationFilePathChosen = false, keyFilePathChosen = false;
    private boolean keyPairsGenerated = false;
    private boolean encryptionPerformed = false;

    private String fileExtensionOfEncryptedFile = null;

    public RSAUserInterface() {
        super();
        initializeRSAHelpers();
        setUpRadioButtons();
        setUpButtons();
        setUpFrame();
        setUpDestinationView();
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initializeRSAHelpers() {
        keyGenerator = new RSAGenKey();
        encryptor = new RSAEncrypt();
        decryptor = new RSADecrypt();
    }

    private void setUpRadioButtons() {
        decryptSetting = new JRadioButton("Decrypt");
        encryptSetting = new JRadioButton("Encrypt", true);

        decryptSetting.addActionListener(e -> {
            performRadioButtonSelectedAction(decryptSetting);
        });
        encryptSetting.addActionListener(e -> {
            performRadioButtonSelectedAction(encryptSetting);
        });
    }

    private void performRadioButtonSelectedAction(JRadioButton radioButton) {
        radioButton.setSelected(true);

        if (decryptSetting.equals(radioButton)) {
            encryptSetting.setSelected(false);
            encryptOrDecryptButton.setText("Decrypt");
        }else {
            decryptSetting.setSelected(false);
            encryptOrDecryptButton.setText("Encrypt");
        }
    }

    private void setUpButtons() {
        setUpRSAButtons();
        setUpFileChoosingButtons();

        setUpLayout();
    }

    private void setUpRSAButtons() {
        encryptOrDecryptButton = new JButton("Encrypt");
        generateKeyButton = new JButton("Generate Key Pairs");

        encryptOrDecryptButton.setEnabled(false);
        generateKeyButton.setEnabled(false);

        encryptOrDecryptButton.addActionListener(e -> {
            handleEncryptDecryptButton(encryptSetting.isSelected());
        });

        generateKeyButton.addActionListener(e -> {
            performKeyGeneration();
        });
    }

    private void handleEncryptDecryptButton(boolean encrypting) {
        String readSourceFilePath = getFilePathFromLabel(sourceFilePath);

        try {
            byte[] bytes = fileHandler.getFileBytes(readSourceFilePath);
            BigInteger fileBytes = new BigInteger(bytes);

            String writeSourceFilePath = getFilePathFromLabel(destinationFilePath);

            if (encrypting) {
                performEncrypt(fileBytes, writeSourceFilePath, readSourceFilePath);
            }else {
                performDecrypt(fileBytes, writeSourceFilePath);
            }
        } catch (FileTooBigError fileTooBigError) {
            JOptionPane.showMessageDialog(null, "The selected file is too big to encrypt. Must be 128 bytes or below.", "ERROR", JOptionPane.CANCEL_OPTION);
            return;
        }finally {
            resetEncryptionAndDecryptionState();
        }
    }

    private void performEncrypt(BigInteger fileBytes, String writeSourceFilePath, String readSourceFilePath) {
        createDestinationFile(writeSourceFilePath, getDestinationFileExtension(true));
        writeSourceFilePath = getFilePathFromLabel(destinationFilePath);

        KeyPair publicKeyPair = fileHandler.getPublicKeyPair();
        BigInteger publicExponent = publicKeyPair.getExponent();
        BigInteger modulus = publicKeyPair.getModulus();

        encryptor.encrypt(fileBytes, publicExponent, modulus);

        fileHandler.writeToFile(writeSourceFilePath, encryptor.getEncryptedMessage());

        updateFileExtensionOfEncryptedFile(readSourceFilePath);
        encryptionPerformed = true;
    }

    private void performDecrypt(BigInteger fileBytes, String writeSourceFilePath) {
        if (!encryptionPerformed) {
            JOptionPane.showMessageDialog(null, "Encryption was never performed, or was reset.", "ERROR", JOptionPane.CANCEL_OPTION);
            return;
        }
        createDestinationFile(writeSourceFilePath, getDestinationFileExtension(false));
        writeSourceFilePath = getFilePathFromLabel(destinationFilePath);

        KeyPair privateKeyPair = fileHandler.getPrivateKeyPair();
        BigInteger privateExponent = privateKeyPair.getExponent();
        BigInteger modulus = privateKeyPair.getModulus();

        decryptor.decrypt(fileBytes, privateExponent, modulus);

        fileHandler.writeToFile(writeSourceFilePath, decryptor.getDecryptedMessage());

        encryptionPerformed = false;
    }

    private void resetEncryptionAndDecryptionState() {
        sourceFilePath.setText(UIConstants.SOURCE_FILE_PATH_LABEL + UIConstants.EMPTY_PATH_LABEL);
        destinationFilePath.setText(UIConstants.DESTINATION_FILE_PATH_LABEL + UIConstants.EMPTY_PATH_LABEL);

        sourceFilePathChosen = false;
        destinationFilePathChosen = false;

        refreshRSAButtons();
    }

    private void createDestinationFile(String filePath, String fileExtension) {
        File destinationFile = new File(filePath + UIConstants.DESTINATION_FILE_NAME + fileExtension);

        try {
            destinationFile.createNewFile();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to create destination file.", "ERROR", JOptionPane.CANCEL_OPTION);
            return;
        }
        destinationFilePath.setText(UIConstants.DESTINATION_FILE_PATH_LABEL + destinationFile.getAbsolutePath());
    }

    private String getDestinationFileExtension(boolean encrypting) {
        if (encrypting) {
            return UIConstants.ENCRYPTION_FILE_EXTENSION;
        }

        return fileExtensionOfEncryptedFile;
    }

    private void updateFileExtensionOfEncryptedFile(String filePath) {
        int startOfFileExtension = filePath.indexOf(".");

        fileExtensionOfEncryptedFile = filePath.substring(startOfFileExtension);
    }

    private void performKeyGeneration() {
        keyGenerator.generateKeyPairs();

        List<KeyPair> keyPair = keyGenerator.getKeyPairs();

        List<String> keyPairData = new ArrayList<>();
        keyPairData.add(keyPair.get(RSAGenKey.PUBLIC_KEY_PAIR_INDEX).toString());
        keyPairData.add(keyPair.get(RSAGenKey.PRIVATE_KEY_PAIR_INDEX).toString());

        String filePath = getFilePathFromLabel(keyFilePath);

        if (fileIsADirectory(filePath)) {
            filePath += UIConstants.KEY_FILE;
            createKeysStorageFile(filePath);
            keyFilePath.setText(UIConstants.KEY_FILE_PATH_LABEL + filePath);
        }

        fileHandler = new FileHandler(getFilePathFromLabel(keyFilePath));
        fileHandler.writeToFile(filePath, keyPairData);
        keyPairsGenerated = true;

        refreshRSAButtons();
    }

    private boolean fileIsADirectory(String filePath) {
        File file = new File(filePath);

        return file.isDirectory();
    }

    private void createKeysStorageFile(String filePath) {
        File file = new File(filePath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to create key storage file.", "ERROR", JOptionPane.CANCEL_OPTION);
            return;
        }
    }

    private void setUpFileChoosingButtons() {
        enterSourceFilePathButton = new JButton("Choose Source File Path");
        enterDestinationFilePathButton = new JButton("Choose Destination File Path");
        enterKeyFilePathButton = new JButton("Choose Key File Path");

        enterSourceFilePathButton.addActionListener(e -> {
            performSourceButtonAction();
        });
        enterDestinationFilePathButton.addActionListener(e -> {
            performDestinationButtonAction();
        });
        enterKeyFilePathButton.addActionListener(e -> {
            performKeyButtonAction();
        });
    }

    private void performSourceButtonAction() {
        try {
            String filePath = chooseFilePath(JFileChooser.FILES_ONLY);
            sourceFilePath.setText(UIConstants.SOURCE_FILE_PATH_LABEL + filePath);
            sourceFilePathChosen = true;

            refreshRSAButtons();
        }catch (PathNotChosenException exc) {
            return;
        }
    }

    private void performDestinationButtonAction() {
        try {
            String filePath = chooseFilePath(JFileChooser.DIRECTORIES_ONLY);
            destinationFilePath.setText(UIConstants.DESTINATION_FILE_PATH_LABEL + filePath);
            destinationFilePathChosen = true;

            refreshRSAButtons();
        }catch (PathNotChosenException exc) {
            return;
        }
    }

    private void performKeyButtonAction() {
        try {
            String filePath = chooseFilePath(JFileChooser.FILES_AND_DIRECTORIES);
            keyFilePath.setText(UIConstants.KEY_FILE_PATH_LABEL + filePath);
            keyFilePathChosen = true;

            refreshRSAButtons();
        }catch (PathNotChosenException exc) {
            return;
        }
    }

    private String chooseFilePath(int fileSelectionMode) throws PathNotChosenException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(fileSelectionMode);

        int chosenOption = fileChooser.showOpenDialog(null);
        File directoryPath = fileChooser.getSelectedFile();

        if (chosenOption == JFileChooser.CANCEL_OPTION || directoryPath == null) {
            throw new PathNotChosenException();
        }

        return (directoryPath.getAbsolutePath());
    }

    private void setUpLayout() {
        JPanel keyButtonPanel = new JPanel();
        keyButtonPanel.setVisible(true);
        keyButtonPanel.setLayout(new GridLayout(2,1));
        keyButtonPanel.add(enterKeyFilePathButton);
        keyButtonPanel.add(generateKeyButton);

        JPanel cryptPanel = new JPanel();
        keyButtonPanel.setVisible(true);
        keyButtonPanel.setLayout(new GridLayout(1,3));
        cryptPanel.add(enterSourceFilePathButton);
        cryptPanel.add(enterDestinationFilePathButton);
        cryptPanel.add(encryptOrDecryptButton);
        cryptPanel.add(decryptSetting);
        cryptPanel.add(encryptSetting);

        add(keyButtonPanel, BorderLayout.NORTH);
        add(cryptPanel, BorderLayout.CENTER);
    }

    private void setUpFrame() {
        setVisible(true);
        setEnabled(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(UIConstants.FRAME_DIMENSION);
        setResizable(false);
    }

    private void setUpDestinationView() {
        sourceFilePath = new JLabel(UIConstants.SOURCE_FILE_PATH_LABEL + UIConstants.EMPTY_PATH_LABEL);
        destinationFilePath = new JLabel(UIConstants.DESTINATION_FILE_PATH_LABEL + UIConstants.EMPTY_PATH_LABEL);
        keyFilePath = new JLabel(UIConstants.KEY_FILE_PATH_LABEL + UIConstants.EMPTY_PATH_LABEL);

        JPanel pathPanel = new JPanel();
        pathPanel.setVisible(true);
        pathPanel.setLayout(new GridLayout(3, 1));
        pathPanel.add(sourceFilePath);
        pathPanel.add(destinationFilePath);
        pathPanel.add(keyFilePath);

        add(pathPanel, BorderLayout.SOUTH);
    }

    private String getFilePathFromLabel(JLabel filePathLabel) {
        String filePath;

        if (filePathLabel.equals(keyFilePath)) {
            filePath = filePathLabel.getText().substring(UIConstants.KEY_FILE_PATH_LABEL.length());
        }else if (filePathLabel.equals(destinationFilePath)) {
            filePath = filePathLabel.getText().substring(UIConstants.DESTINATION_FILE_PATH_LABEL.length());
        }else {
            filePath = filePathLabel.getText().substring(UIConstants.SOURCE_FILE_PATH_LABEL.length());
        }

        return filePath;
    }

    private void refreshRSAButtons() {
        if (keyFilePathChosen) {
            generateKeyButton.setEnabled(true);

            if (keyPairsGenerated && sourceFilePathChosen && destinationFilePathChosen) {
                encryptOrDecryptButton.setEnabled(true);
            }else {
                encryptOrDecryptButton.setEnabled(false);
            }
        }else {
            encryptOrDecryptButton.setEnabled(false);
        }
    }
}