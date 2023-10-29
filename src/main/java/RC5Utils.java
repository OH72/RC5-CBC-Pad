import java.util.Random;

/**
 * @author Oleh Hembarovskyi
 * @since 29/10/2023
 **/
public class RC5Utils {
    private final int wordLengthInBits;
    private final int wordLengthInBytes;
    private final byte[] arrP;
    private final byte[] arrQ;
    private final int numberOfRounds;
    private final int secretKeyLengthInBytes;

    public RC5Utils(WordLength wordLength, int numberOfRounds, int secretKeyLengthInBytes) {
        this.wordLengthInBits = wordLength.getLength();
        this.wordLengthInBytes = wordLengthInBits / 8 + wordLengthInBits % 8;
        this.arrP = wordLength.getP();
        this.arrQ = wordLength.getQ();
        this.numberOfRounds = numberOfRounds;
        this.secretKeyLengthInBytes = secretKeyLengthInBytes;
    }

    public byte[] generateArrayS() {
        byte[] arrK = generateSecretKey();
        byte[][] arrL = splitArrayToWords(arrK);
        byte[][] arrS = initArrayS();

        int i = 0;
        int j = 0;
        byte[] arrA = new byte[wordLengthInBytes];
        byte[] arrB = new byte[wordLengthInBytes];
        int t = Math.max(arrL.length, 2 * numberOfRounds + 1);

        for (int s = 1; s < t * 3; s++) {
            s[i] = byteArraysSum(s[i] +)
        }
    }

    private byte[] generateSecretKey() {
        // todo : replace
        byte[] secretKey = new byte[secretKeyLengthInBytes];

        Random random = new Random();
        random.nextBytes(secretKey);

        return secretKey;
    }

    private byte[][] splitArrayToWords(byte[] byteArray) {
        int numberOfWords = byteArray.length / wordLengthInBytes + byteArray.length % wordLengthInBytes;
        byte[][] wordList = new byte[numberOfWords][wordLengthInBytes];

        for (int i = 0; i < numberOfWords; i++) {
            int offset = i * wordLengthInBytes;
            int numberOfBytes = Math.min(wordLengthInBytes, byteArray.length - offset * i);

            System.arraycopy(byteArray, offset, wordList[i], 0, numberOfBytes);
        }

        return wordList;
    }

    private byte[][] initArrayS() {
        byte[][] arrS = new byte[2 * numberOfRounds + 1][wordLengthInBytes];
        arrS[0] = arrP;

        for (int i = 1; i < arrS.length; i++) {
            arrS[i] = byteArraysSum(arrS[i - 1], arrQ);
        }

        return arrS;
    }

    private static byte[] byteArraysSum(byte[] arrA, byte[] arrB) {
        if (arrA.length != arrB.length) {
            throw new RuntimeException("byteArraysSum(): arrA.size != arrB.size");
        }

        byte[] result = new byte[arrA.length];
        byte carry = 0;

        for (int i = arrA.length - 1; i >= 0; i--) {
            int sum = (arrA[i] & 0xFF) + (arrB[i] & 0xFF) + (carry & 0xFF);
            result[i] = (byte) sum;
            carry = (byte) (sum >> 8);
        }

        return result;
    }

    private static byte[] byteArraysDiff(byte[] arrA, byte[] arrB) {
        if (arrA.length != arrB.length) {
            throw new RuntimeException("byteArraysDiff(): arrA.size != arrB.size");
        }

        byte[] result = new byte[arrA.length];
        byte borrow = 0;

        for (int i = arrA.length - 1; i >= 0; i--) {
            int diff = (arrA[i] & 0xFF) - (arrB[i] & 0xFF) - (borrow & 0xFF);

            if (diff < 0) {
                borrow = 1;
                diff += 256;
            } else {
                borrow = 0;
            }

            result[i] = (byte) diff;
        }
        return result;
    }


    public static byte[] encrypt(byte[] byteArray) {
        byte[] result = new byte[byteArray.length];
        int offset = 0;

        for (byte[] word : splitArrayToWords(byteArray)) {
            System.arraycopy(encryptWord(word), 0, result, offset, word.length);
            offset += word.length;
        }

        return result;
    }


    private static byte[] encryptWord(byte[] word, byte[] secretKey) {

    }

    public enum WordLength {
        _16(
                16,
                new byte[]{(byte) 0xB7, (byte) 0xE1},
                new byte[]{(byte) 0x9E, (byte) 0x37}),
        _32(32,
                new byte[]{(byte) 0xB7, (byte) 0xE1, (byte) 0x51, (byte) 0x63},
                new byte[]{(byte) 0x9E, (byte) 0x37, (byte) 0x79, (byte) 0xB9}),
        _64(64,
                new byte[]{(byte) 0xB7, (byte) 0xE1, (byte) 0x51, (byte) 0x62, (byte) 0x8A, (byte) 0xD2, (byte) 0xA6, (byte) 0x6B},
                new byte[]{(byte) 0x9E, (byte) 0x37, (byte) 0x79, (byte) 0xB9, (byte) 0x7F, (byte) 0x4A, (byte) 0x7C, (byte) 0x15});

        private final int length;
        private final byte[] p;
        private final byte[] q;

        WordLength(int length, byte[] p, byte[] q) {
            this.length = length;
            this.p = p;
            this.q = q;
        }

        public int getLength() {
            return length;
        }

        public byte[] getP() {
            return p;
        }

        public byte[] getQ() {
            return q;
        }
    }
}
