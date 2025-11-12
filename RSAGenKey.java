import java.math.BigInteger;
import java.util.Random;
import java.io.*;

/**
 * RSAGenKey - Generates RSA public and private key pairs
 * Usage: java RSAGenKey <keysize> OR java RSAGenKey <p> <q> <e>
 */
public class RSAGenKey {
    public static void main(String[] args) {
        // RSA key components: p, q (primes), e (public exp), n (modulus), d (private exp), phi (Euler's totient)
        BigInteger p, q, e, n, d, phi;
        // Scenario A: Generate random primes based on key size
        if(args.length == 1) {
            int bitLength = Integer.parseInt(args[0]);
            Random random = new Random();
            // Generate two random k-bit prime numbers
            p = BigInteger.probablePrime(bitLength, random);
            q = BigInteger.probablePrime(bitLength, random);
            e = new BigInteger("65537");  // Use standard public exponent

            // Calculate RSA parameters
            n = p.multiply(q);  // Modulus n = p * q
            phi = p.subtract(BigInteger.ONE).multiply(q.subtract((BigInteger.ONE)));  // Euler's totient calculation

            // Validate that e and phi are coprime (gcd must equal 1)
            BigInteger gcd = e.gcd(phi);
            if (!gcd.equals(BigInteger.ONE)) {
                System.out.println("Error: e and phi(n) are not coprime.");
                return;
            }

            d = e.modInverse(phi);  // Finding the modular multiplicative inverse of e and euler's totient
        // Scenario B: Use provided p, q, and e values
        } else if (args.length == 3) {
            // Parse input parameters
            p = new BigInteger(args[0]);
            q = new BigInteger(args[1]);
            e = new BigInteger(args[2]);

            // Calculate RSA parameters
            n = p.multiply(q);  // Modulus n = p * q
            phi = p.subtract(BigInteger.ONE).multiply(q.subtract((BigInteger.ONE)));  // Euler's totient calculation

            // Validate that e and phi are coprime (gcd must equal 1)
            BigInteger gcd = e.gcd(phi);
            if (!gcd.equals(BigInteger.ONE)) {
                System.out.println("Error: e and phi(n) are not coprime.");
                return;
            }

            d = e.modInverse(phi);  // Finding the modular multiplicative inverse of e and euler's totient
        } else {
            System.out.println("Usage: java RSAGenKey <keysize> OR java RSAGenKey <p> <q> <e>");
            return;
        }

        // Write public and private keys to files
        try {
            // Create pub_key.txt with public key (e, n)
            PrintWriter pubOut = new PrintWriter(new FileWriter("pub_key.txt"));
            PrintWriter privOut = new PrintWriter(new FileWriter("pri_key.txt"));

            // Write public key
            pubOut.println("e = " + e.toString());
            pubOut.println("n = " + n.toString());

            // Write private key (d, n)
            privOut.println("d = " + d.toString());
            privOut.println("n = " + n.toString());

            pubOut.close();
            privOut.close();
        } catch (IOException ex) {
            System.out.println("Error writing file: " + ex.getMessage());
        }
    }
}