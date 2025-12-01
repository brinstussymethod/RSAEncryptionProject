import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class RSADecrypt {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java RSADecrypt <encrypted file> <private key file>");
            return;
        }

        // Read private key (d, n)
        BigInteger d = null, n = null;
        try (Scanner keyScanner = new Scanner(new File(args[1]))) {
            while (keyScanner.hasNextLine()) {
                String line = keyScanner.nextLine().trim();
                if (line.startsWith("d")) {
                    d = new BigInteger(line.split("=")[1].trim());
                } else if (line.startsWith("n")) {
                    n = new BigInteger(line.split("=")[1].trim());
                }
            }
        }

        if (d == null || n == null) {
            System.out.println("Error: Could not read private key file.");
            return;
        }

        // Read encrypted numbers from file
        List<BigInteger> ciphertextBlocks = new ArrayList<>();
        try (Scanner encScanner = new Scanner(new File(args[0]))) {
            while (encScanner.hasNext()) {
                ciphertextBlocks.add(new BigInteger(encScanner.next()));
            }
        }

        // Decrypt each block
        StringBuilder plaintext = new StringBuilder();
        for (BigInteger c : ciphertextBlocks) {
            BigInteger m = c.modPow(d, n); // RSA decryption: m = c^d mod n
            plaintext.append(decodeBlock(m));
        }

        // Write output file (.dec)
        String outputFile = args[0].replace(".enc", ".dec");
        try (PrintWriter out = new PrintWriter(outputFile)) {
            out.print(plaintext.toString());
        }

        System.out.println("Decryption complete. Output written to " + outputFile);
    }

    // decode integer block back to text
    private static String decodeBlock(BigInteger m) {
        String block = m.toString(); // full precision, no truncation
        // Pad to 6 digits if needed
        while (block.length() < 6) {
            block = "0" + block;
        }
        StringBuilder decoded = new StringBuilder();
        for (int i = 0; i < block.length(); i += 2) {
            int code = Integer.parseInt(block.substring(i, i + 2));
            decoded.append(codeToChar(code));
        }
        return decoded.toString();
    }

    // map numeric code back to character
    private static char codeToChar(int code) {
        if (code == 26) return ' ';
        if (code >= 0 && code <= 25) return (char) ('a' + code);
        if (code == 27) return ',';
        if (code == 28) return '.';
        if (code == 29) return '\n';
        return 'a'; // fallback
    }
}
