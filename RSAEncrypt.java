import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class RSAEncrypt {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java RSAEncrypt <plaintext file> <public key file>");
            return;
        }

        // Read public key (e, n)
        BigInteger e = null, n = null;
        try (Scanner keyScanner = new Scanner(new File(args[1]))) {
            while (keyScanner.hasNextLine()) {
                String line = keyScanner.nextLine().trim();
                if (line.startsWith("e")) {
                    e = new BigInteger(line.split("=")[1].trim());
                } else if (line.startsWith("n")) {
                    n = new BigInteger(line.split("=")[1].trim());
                }
            }
        }

        if (e == null || n == null) {
            System.out.println("Error: Could not read public key file.");
            return;
        }

        // Read plaintext file
        StringBuilder plaintext = new StringBuilder();
        try (Scanner plainScanner = new Scanner(new File(args[0]))) {
            while (plainScanner.hasNextLine()) {
                plaintext.append(plainScanner.nextLine());
                if (plainScanner.hasNextLine()) plaintext.append("\n");
            }
        }

        // Break plaintext into 3-character blocks
        List<String> blocks = new ArrayList<>();
        for (int i = 0; i < plaintext.length(); i += 3) {
            int end = Math.min(i + 3, plaintext.length());
            blocks.add(plaintext.substring(i, end));
        }

        // Encode each block into integer using 00–26 scheme
        List<BigInteger> ciphertextBlocks = new ArrayList<>();
        for (String block : blocks) {
            String encoded = encodeBlock(block);
            BigInteger m = new BigInteger(encoded);
            BigInteger c = m.modPow(e, n); // RSA encryption: c = m^e mod n
            ciphertextBlocks.add(c);
        }

        // Write encrypted output file (.enc)
        String outputFile = args[0].replace(".txt", ".enc");
        try (PrintWriter out = new PrintWriter(outputFile)) {
            for (BigInteger c : ciphertextBlocks) {
                out.print(c.toString() + " ");
            }
        }

        System.out.println("Encryption complete. Output written to " + outputFile);
    }

    // encode a block of up to 3 characters into a 6-digit string
    private static String encodeBlock(String block) {
        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < block.length(); i++) {
            encoded.append(charToCode(block.charAt(i)));
        }
        // Pad with "00" if block < 3 chars
        while (encoded.length() < 6) {
            encoded.append("00");
        }
        return encoded.toString();
    }

    // map character to numeric code
    private static String charToCode(char ch) {
        if (ch == ' ') return "26";
        if (ch >= 'a' && ch <= 'z') {
            int code = ch - 'a';
            return String.format("%02d", code);
        }
        // For simplicity, treat unknown chars as "00" (a)
        return "00";
    }
}
