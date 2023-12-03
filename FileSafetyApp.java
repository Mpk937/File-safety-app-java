import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Scanner;

public class FileSafetyApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            displayWelcomeMessage();
            while (true) {
                displayMainMenu();
                int choice = getUserChoice(scanner);

                switch (choice) {
                    case 1:
                        handleEncryption(scanner);
                        break;
                    case 2:
                        handleDecryption(scanner);
                        break;
                    case 3:
                        System.out.println("Exiting File Safety App. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void displayWelcomeMessage() {
        System.out.println("==============================================================");
        System.out.println("        Welcome to File Encryption/Decryption");
        System.out.println("==============================================================");
    }

    private static void displayMainMenu() {
        System.out.println("\n1. Encrypt a File");
        System.out.println("2. Decrypt a File");
        System.out.println("3. Exit");
        System.out.println("\n--------------------------------------------------------------");
        System.out.print("Please choose an option (1/2/3): ");
    }

    private static int getUserChoice(Scanner scanner) {
        int choice;
        while (true) {
            try {
                choice = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return choice;
    }

    private static void handleEncryption(Scanner scanner) {
        System.out.print("\nEnter the path of the file to encrypt: ");
        String filePath = scanner.nextLine();

        System.out.print("Enter the encryption key: ");
        String encryptionKey = scanner.nextLine();

        try {
            byte[] key = getKey(encryptionKey);
            encryptFile(filePath, key);
            System.out.println("\nFile encrypted successfully!");
        } catch (Exception e) {
            System.out.println("\nEncryption failed. Please check your input and try again.");
        }
    }

    private static void handleDecryption(Scanner scanner) {
        System.out.print("\nEnter the path of the file to decrypt: ");
        String filePath = scanner.nextLine();

        System.out.print("Enter the decryption key: ");
        String decryptionKey = scanner.nextLine();

        try {
            byte[] key = getKey(decryptionKey);
            if (isKeyValid(filePath, key)) {
                decryptFile(filePath, key);
                System.out.println("\nFile decrypted successfully!");
            } else {
                System.out.println("\nDecryption key is incorrect. File cannot be decrypted.");
            }
        } catch (Exception e) {
            System.out.println("\nDecryption failed. Please check your input and try again.");
        }
    }

    private static byte[] getKey(String keyString) {
        byte[] key = new byte[16];
        byte[] inputKey = keyString.getBytes();
        System.arraycopy(inputKey, 0, key, 0, Math.min(inputKey.length, key.length));
        return key;
    }

    private static void encryptFile(String filePath, byte[] key) throws Exception {
        FileInputStream fis = new FileInputStream(filePath);
        byte[] fileContent = new byte[fis.available()];
        fis.read(fileContent);
        fis.close();

        byte[] encryptedContent = encryptAES(fileContent, key);

        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(encryptedContent);
        fos.close();
    }

    private static byte[] encryptAES(byte[] content, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        byte[] ivBytes = new byte[cipher.getBlockSize()];
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
        return cipher.doFinal(content);
    }

    private static void decryptFile(String filePath, byte[] key) throws Exception {
        FileInputStream fis = new FileInputStream(filePath);
        byte[] fileContent = new byte[fis.available()];
        fis.read(fileContent);
        fis.close();

        byte[] decryptedContent = decryptAES(fileContent, key);

        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(decryptedContent);
        fos.close();
    }

    private static byte[] decryptAES(byte[] content, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        byte[] ivBytes = new byte[cipher.getBlockSize()];
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
        return cipher.doFinal(content);
    }

    private static boolean isKeyValid(String filePath, byte[] enteredKey) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        byte[] firstBlock = new byte[16];
        fis.read(firstBlock);
        fis.close();

        try {
            decryptAES(firstBlock, enteredKey);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
