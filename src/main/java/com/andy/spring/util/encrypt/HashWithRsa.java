package com.andy.spring.util.encrypt;

import com.andy.spring.constant.Constant;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

/**
 * 基于rsa的hash
 *
 * @author 庞先海 2019-11-14
 */
public class HashWithRsa {

    private static final String SHA1_RSA = "SHA1WithRSA";

    private static final String SHA256_RSA = "SHA256WithRSA";

    public static String sha1RsaHash(String content, String privateKeyStr) {
        PrivateKey privateKey = RSAUtil.generatePrivateKey(privateKeyStr);
        return sha1RsaHash(content, privateKey);
    }

    public static String sha1RsaHash(String content, PrivateKey privateKey) {
        return sha1RsaHash(content.getBytes(Constant.DEFAULT_CHARSET), privateKey);
    }

    public static String sha1RsaHash(byte[] content, PrivateKey privateKey) {
        byte[] hash = hash(content, SHA1_RSA, privateKey);
        return EncodeUtil.base64Encode(hash, Constant.DEFAULT_CHARSET);
    }

    public static String sha256RsaHash(String content, String privateKeyStr) {
        PrivateKey privateKey = RSAUtil.generatePrivateKey(privateKeyStr);
        return sha256RsaHash(content, privateKey);
    }

    public static String sha256RsaHash(String content, PrivateKey privateKey) {
        return sha256RsaHash(content.getBytes(Constant.DEFAULT_CHARSET), privateKey);
    }

    public static String sha256RsaHash(byte[] content, PrivateKey privateKey) {
        byte[] hash = hash(content, SHA256_RSA, privateKey);
        return EncodeUtil.base64Encode(hash, Constant.DEFAULT_CHARSET);
    }

    private static byte[] hash(byte[] content, String algorithm, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(privateKey);
            signature.update(content);
            return signature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean sha1RsaVerify(String content, String publicKeyStr, String signature) {
        PublicKey publicKey = RSAUtil.generatePublicKey(publicKeyStr);
        return sha1RsaVerify(content, publicKey, signature);
    }

    public static boolean sha1RsaVerify(String content, PublicKey publicKey, String signature) {
        return sha1RsaVerify(content.getBytes(Constant.DEFAULT_CHARSET), publicKey,
            EncodeUtil.base64DecodeByte(signature));
    }

    public static boolean sha1RsaVerify(byte[] content, PublicKey publicKey, byte[] signatureContent) {
        return verify(content, SHA1_RSA, publicKey, signatureContent);
    }

    public static boolean sha256RsaVerify(String content, String publicKeyStr, String signature) {
        PublicKey publicKey = RSAUtil.generatePublicKey(publicKeyStr);
        return sha256RsaVerify(content, publicKey, signature);
    }

    public static boolean sha256RsaVerify(String content, PublicKey publicKey, String signature) {
        return sha256RsaVerify(content.getBytes(Constant.DEFAULT_CHARSET), publicKey,
            EncodeUtil.base64DecodeByte(signature));

    }

    public static boolean sha256RsaVerify(byte[] content, PublicKey publicKey, byte[] signature) {
        return verify(content, SHA256_RSA, publicKey, signature);
    }

    private static boolean verify(byte[] content, String algorithm, PublicKey publicKey, byte[] signatureContent) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(publicKey);
            signature.update(content);
            return signature.verify(signatureContent);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }
}
