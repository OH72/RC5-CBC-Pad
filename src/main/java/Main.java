import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;

/**
 * @author Oleh Hembarovskyi
 * @since 21/10/2023
 **/
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Input string to MD5 hashing:");
        String userInputResult = MD5(scanner.next());

        String testCaseResult = testCases();
        System.out.println(testCaseResult);

        writeToFile("testCaseResult.txt", testCaseResult + userInputResult);

        System.out.println(testFile("C:\\Users\\admin\\Documents\\save\\NULP\\4 course\\my\\ProgramDataSecurity\\lab2\\PDS-Lab2\\testFile.txt", "1DA3BCF1F98A6394E4E87242C7FB1308"));
    }

    public static String testCases() {
        return testMD5String("", "D41D8CD98F00B204E9800998ECF8427E") +
                '\n' +
                testMD5String("a", "0CC175B9C0F1B6A831C399E269772661") +
                '\n' +
                testMD5String("abc", "900150983CD24FB0D6963F7D28E17F72") +
                '\n' +
                testMD5String("message digest", "F96B697D7CB7938D525A2F31AAF161D0") +
                '\n' +
                testMD5String("abcdefghijklmnopqrstuvwxyz", "C3FCD3D76192E4007DFB496CCA67E13B") +
                '\n' +
                testMD5String("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", "D174AB98D277D9F5A5611C2C9F419D9F") +
                '\n' +
                testMD5String("12345678901234567890123456789012345678901234567890123456789012345678901234567890", "57EDF4A22BE3C955AC49DA2E2107B67A") +
                '\n';
    }

    public static String testMD5String(String source, String expectedResult) {
        StringBuilder print = new StringBuilder();
        String result = Hex.encodeHexString(MD5Utils.md5(source.getBytes())).toUpperCase();
        print.append("---------------------------------\n");
        print.append("input = '%s'\nexpected = '%s'\n".formatted(source, expectedResult));
        print.append("result = '%s'\n".formatted(result));
        print.append(Objects.equals(expectedResult, result));

        return print.toString();
    }

    public static void writeToFile(String filePath, String text) {
        Path path = Paths.get(filePath);
        byte[] strToBytes = text.getBytes();

        try {
            Files.write(path, strToBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String MD5(String source) {
        StringBuilder print = new StringBuilder();
        String result = Hex.encodeHexString(MD5Utils.md5(source.getBytes())).toUpperCase();
        print.append("---------------------------------\n");
        print.append("input = '%s'\n".formatted(source));
        print.append("result = '%s'\n".formatted(result));

        return print.toString();
    }

    public static String testFile(String filePath, String expectedHash) {
        Path path = Paths.get(filePath);

        try {
            String result = Hex.encodeHexString(MD5Utils.md5(Files.readAllBytes(path))).toUpperCase();

            return "Check file by path = " + filePath + '\n'
                    + "Expected hash: " + expectedHash + '\n'
                    + "Result hash: " + result + '\n'
                    + "Has the file been modified? - "
                    + (Objects.equals(expectedHash, result) ? "no" : "yes");

        } catch (IOException e) {
            e.printStackTrace();

            return "error";
        }
    }
}
