import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;

/**
 * @author Oleh Hembarovskyi
 * @since 21/10/2023
 **/
public class Dispatcher {
    private final Scanner scanner = new Scanner(System.in);
    private RC5Utils rc5Utils;

    public void start() {
        updateRc5Password();

        System.out.println("""
                                
                Options:
                  1 - encrypt file
                  2 - decrypt file
                  3 - encrypt message
                  4 - decrypt message
                  5 - change password
                  0 - exit
                """);

        while (true) {
            System.out.print("<---->\nInput option: ");

            switch (scanner.next()) {
                case "1" -> encryptFile();
                case "2" -> decryptFile();
                case "3" -> encryptMessage();
                case "4" -> decryptMessage();
                case "5" -> updateRc5Password();
                case "0" -> {
                    return;
                }
            }
        }
    }

    private void encryptFile() {
        try {
            System.out.print("File to encrypt: ");
            String sourceFilePath = scanner.next();
            System.out.print("Encrypted file destination: ");
            String destinationFilePath = scanner.next();

            Path sourcePath = Paths.get(sourceFilePath);
            Path destinationPath = Paths.get(destinationFilePath);

            byte[] sourceBytes = Files.readAllBytes(sourcePath);
            byte[] destinationBytes = rc5Utils.encryptCbc(sourceBytes);

            Files.write(destinationPath, destinationBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void decryptFile() {
        try {
            System.out.print("File to decrypt: ");
            String sourceFilePath = scanner.next();
            System.out.print("Decrypted file destination: ");
            String destinationFilePath = scanner.next();

            Path sourcePath = Paths.get(sourceFilePath);
            Path destinationPath = Paths.get(destinationFilePath);

            byte[] sourceBytes = Files.readAllBytes(sourcePath);
            byte[] destinationBytes = rc5Utils.encryptCbc(sourceBytes);

            Files.write(destinationPath, destinationBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateRc5Password() {
        System.out.print("Input RC5 password: ");
        this.rc5Utils = new RC5Utils(RC5Utils.WordLength._32, 12, 16, scanner.next());
    }

    private void encryptMessage() {
        System.out.print("Message to encrypt: ");
        String sourceMessage = scanner.next();

        byte[] resultBytes = rc5Utils.encryptCbc(sourceMessage.getBytes());

        System.out.println("Encrypted message:");
        System.out.println(new String(Base64.getEncoder().encode(resultBytes)));
    }

    private void decryptMessage() {
        System.out.print("64Based message to decrypt: ");
        String sourceMessage = scanner.next();

        byte[] resultBytes = rc5Utils.decryptCbc(Base64.getDecoder().decode(sourceMessage.getBytes()));

        System.out.println("Decrypted message:");
        System.out.println(new String(resultBytes));
    }

    public static void main(String[] args) {
        new Dispatcher().start();
    }
}
