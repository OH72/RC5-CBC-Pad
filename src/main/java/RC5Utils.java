import java.util.Arrays;

/**
 * @author Oleh Hembarovskyi
 * @since 29/10/2023
 **/
public class RC5Utils {
    private final int wordLengthInBytes;
    private final int wordLengthInBits;
    private final long wordBytesUsage;
    private final long arrP;
    private final long arrQ;
    private final int numberOfRounds;
    private final int secretKeyLengthInBytes;
    private final long[] s;
    private final long ivA;
    private final long ivB;

    public RC5Utils(WordLength wordLength, int numberOfRounds, int secretKeyLengthInBytes, String password) {
        this.wordLengthInBits = wordLength.getLength();
        this.wordLengthInBytes = wordLength.getLength() / 8;
        this.wordBytesUsage = wordLength.bytesUsage;
        this.arrP = wordLength.getP();
        this.arrQ = wordLength.getQ();
        this.numberOfRounds = numberOfRounds;
        this.secretKeyLengthInBytes = secretKeyLengthInBytes;
        this.s = generateArrayS(password);
        this.ivA = 99000090;
        this.ivB = 32423433;
    }

    public long[] generateArrayS(String password) {
        byte[] arrK = generateSecretKey(password);
        long[] arrL = splitArrayToWords(arrK);
        long[] arrS = initArrayS();

        int i = 0;
        int j = 0;
        long a = 0;
        long b = 0;
        int t = Math.max(arrL.length, 2 * numberOfRounds + 2);

        for (int s = 1; s < t * 3; s++) {
            arrS[i] = ((arrS[i] + a + b) << 3) & wordBytesUsage;
            a = arrS[i];
            i = (i + 1) % t;

            arrL[j] = ((arrL[j] + a + b) << (a + b)) & wordBytesUsage;
            b = arrL[j];
            j = (j + 1) % arrL.length;
        }

        return arrS;
    }

    private byte[] generateSecretKey(String password) {
        byte[] hash = MD5Utils.md5(password.getBytes());

        if (hash.length > secretKeyLengthInBytes) {
            byte[] result = new byte[secretKeyLengthInBytes];
            System.arraycopy(hash, hash.length - secretKeyLengthInBytes, result, 0, secretKeyLengthInBytes);

            return result;
        }

        if (hash.length < secretKeyLengthInBytes) {
            byte[] result = new byte[secretKeyLengthInBytes];

            for (int i = 0; i < secretKeyLengthInBytes / hash.length + secretKeyLengthInBytes % hash.length; i++) {
                System.arraycopy(hash, 0, result, secretKeyLengthInBytes - (i + 1) * hash.length, hash.length);
                hash = MD5Utils.md5(hash);
            }

            return result;
        }

        return hash;
    }

    private long[] splitArrayToWords(byte[] byteArray) {
        int numberOfWords = byteArray.length / wordLengthInBytes + byteArray.length % wordLengthInBytes;
        long[] wordList = new long[numberOfWords];

        for (int i = 0; i < numberOfWords; i++) {
            int offset = i * wordLengthInBytes;
            int numberOfBytes = Math.min(wordLengthInBytes, byteArray.length - offset);

            byte[] value = new byte[wordLengthInBytes];
            System.arraycopy(byteArray, offset, value, 0, numberOfBytes);

            wordList[i] = byteArrayToLong(value);
        }

        return wordList;
    }

    private long byteArrayToLong(byte[] byteArray) {
        long value = 0L;

        int offset = 0;

        for (byte b : byteArray) {
            value = value + (((long) (b & 0xFF)) << offset);
            offset += 8;
        }

        return value;
    }

    private byte[] longToByteArray(long value) {
        byte[] byteArray = new byte[wordLengthInBytes];

        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte) (value & 0xFF);
            value >>= 8;
        }

        return byteArray;
    }

    private long[] initArrayS() {
        long[] arrS = new long[2 * numberOfRounds + 2];
        arrS[0] = arrP;

        for (int i = 1; i < arrS.length; i++) {
            arrS[i] = (arrS[i - 1] + arrQ) & wordBytesUsage;
        }

        return arrS;
    }

    private byte[] messagePadding(byte[] message) {
        if (message.length % (wordLengthInBytes * 2) == 0) {
            return Arrays.copyOf(message, message.length);
        }

        int bytesToAdd = wordLengthInBytes * 2 - message.length % (wordLengthInBytes * 2);
        byte[] result = new byte[message.length + bytesToAdd];
        System.arraycopy(message, 0, result, 0, message.length);

        for (int i = 0; i < bytesToAdd; i++) {
            result[message.length + i] = (byte) bytesToAdd;
        }

        return result;
    }

    private long loopLeftShift(long value, long bits) {
        bits = bits % wordLengthInBits;

        long copyValue = value;

        value <<= bits;
        value &= wordBytesUsage;

        copyValue >>= (wordLengthInBits - bits);
        copyValue &= wordBytesUsage;

        return value | copyValue;
    }

    private long loopRightShift(long value, long bits) {
        bits = bits % wordLengthInBits;

        long copyValue = value;

        value >>= bits;
        value &= wordBytesUsage;

        copyValue <<= (wordLengthInBits - bits);
        copyValue &= wordBytesUsage;

        return (value | copyValue);
    }

    private long[] encryptTwoWords(long a, long b) {
        a = (a + s[0]) & wordBytesUsage;
        b = (b + s[1]) & wordBytesUsage;

        for (int i = 1; i <= numberOfRounds; i++) {
            a = a ^ b;
            a = loopLeftShift(a, b);
            a = (a + s[2 * i]) & wordBytesUsage;

            b = b ^ a;
            b = loopLeftShift(b, a);
            b = (b + s[2 * i + 1]) & wordBytesUsage;
        }

        return new long[]{a, b};
    }

    private long[] decryptTwoWords(long a, long b) {
        for (int i = numberOfRounds; i >= 1; i--) {
            b = (b - s[2 * i + 1]) & wordBytesUsage;
            b = loopRightShift(b, a);
            b = b ^ a;

            a = (a - s[2 * i]) & wordBytesUsage;
            a = loopRightShift(a, b);
            a = a ^ b;
        }

        b = (b - s[1]) & wordBytesUsage;
        a = (a - s[0]) & wordBytesUsage;

        return new long[]{a, b};
    }

    public byte[] encrypt(byte[] message) {
        byte[] extendedMessage = messagePadding(message);
        long[] words = splitArrayToWords(extendedMessage);
        byte[] result = new byte[extendedMessage.length];

        long preA = ivA;
        long preB = ivB;

        for (int i = 0; i < words.length; i += 2) {
            long wordA = words[i] ^ preA;
            long wordB = words[i + 1] ^ preB;

            long[] twoWordsEncrypted = encryptTwoWords(wordA, wordB);

            System.arraycopy(longToByteArray(twoWordsEncrypted[0]), 0, result, i * wordLengthInBytes, wordLengthInBytes);
            System.arraycopy(longToByteArray(twoWordsEncrypted[1]), 0, result, (i + 1) * wordLengthInBytes, wordLengthInBytes);

            preA = twoWordsEncrypted[0];
            preB = twoWordsEncrypted[1];
        }

        return result;
    }

    public byte[] decrypt(byte[] message) {
        int extendedMessageLength = (message.length / wordLengthInBytes + message.length % wordLengthInBytes);
        extendedMessageLength += extendedMessageLength % 2;
        extendedMessageLength *= wordLengthInBytes;

        byte[] extendedMessage = new byte[extendedMessageLength];
        System.arraycopy(message, 0, extendedMessage, 0, message.length);

        long[] words = splitArrayToWords(extendedMessage);
        byte[] result = new byte[extendedMessage.length];

        long preA = ivA;
        long preB = ivB;

        for (int i = 0; i < words.length; i += 2) {
            long[] twoWordsDecrypted = decryptTwoWords(words[i], words[i + 1]);

            twoWordsDecrypted[0] = twoWordsDecrypted[0] ^ preA;
            twoWordsDecrypted[1] = twoWordsDecrypted[1] ^ preB;

            System.arraycopy(longToByteArray(twoWordsDecrypted[0]), 0, result, i * wordLengthInBytes, wordLengthInBytes);
            System.arraycopy(longToByteArray(twoWordsDecrypted[1]), 0, result, (i + 1) * wordLengthInBytes, wordLengthInBytes);

            preA = words[i];
            preB = words[i + 1];
        }

        return result;
    }

    public enum WordLength {
        _16(
                16,
                0x000000000000FFFFL,
                0x000000000000B7E1L,
                0x0000000000009E37L),
        _32(
                32,
                0x00000000FFFFFFFFL,
                0x00000000B7E15163L,
                0x000000009E3779B9L),
        _64(
                64,
                0xFFFFFFFFFFFFFFFFL,
                0xB7E151628AED2A6BL,
                0x9E3779B97F4A7C15L);

        private final int length;
        private final long bytesUsage;
        private final long p;
        private final long q;

        WordLength(int length, long bytesUsage, long p, long q) {
            this.length = length;
            this.bytesUsage = bytesUsage;
            this.p = p;
            this.q = q;
        }

        public int getLength() {
            return length;
        }

        public long getBytesUsage() {
            return bytesUsage;
        }

        public long getP() {
            return p;
        }

        public long getQ() {
            return q;
        }
    }
}
