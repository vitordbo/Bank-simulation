package rsa;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

public class RSA  {
    private BigInteger privateKey;
    private BigInteger publicKey;
    private BigInteger modulus;
    private KeyFactory keyFactory;
    private PublicKey rsaPublicKey;

    public RSA(int bitLength) {
        // Gera números primos p e q
        SecureRandom random = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, random);

        // Calcula n = p * q
        modulus = p.multiply(q);

        // Calcula a função totiente de Euler phi(n)
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // Escolhe um inteiro e tal que 1 < e < phi(n) e e e phi(n) são primos entre si
        publicKey = BigInteger.probablePrime(bitLength / 4, random);
        while (phi.gcd(publicKey).compareTo(BigInteger.ONE) > 0 && publicKey.compareTo(phi) < 0) {
            publicKey = publicKey.add(BigInteger.ONE);
        }

        // Calcula a chave privada d tal que d * e ≡ 1 (mod phi(n))
        privateKey = publicKey.modInverse(phi);

        try {
            // Inicializa o KeyFactory
            keyFactory = KeyFactory.getInstance("RSA");

            // Constrói a chave pública com a especificação RSAPublicKeySpec
            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicKey);
            rsaPublicKey = keyFactory.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public PublicKey getChavePublica() {
        return rsaPublicKey;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public BigInteger getModulus() {
        return modulus;
    }

}