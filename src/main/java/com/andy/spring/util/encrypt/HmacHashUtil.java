package com.andy.spring.util.encrypt;

import com.andy.spring.constant.Constant;
import com.andy.spring.util.StringUtil;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * hmac hash算法
 *
 * @author 庞先海 2019-11-14
 */
public class HmacHashUtil {

    public static String hmacShaHash(String data, String key) {
        return hmacShaHash(data, key, Constant.DEFAULT_CHARSET);
    }

    public static String hmacShaHash(String data, String key, Charset charset) {
        return hmacShaHash(data.getBytes(charset), key.getBytes(charset));
    }

    public static String hmacShaHash(byte[] data, byte[] key) {
        return hash(data, key, "HmacSHA1");
    }

    public static String hmacSha224Hash(String data, String key) {
        return hmacSha224Hash(data, key, Constant.DEFAULT_CHARSET);
    }

    public static String hmacSha224Hash(String data, String key, Charset charset) {
        return hmacSha224Hash(data.getBytes(charset), key.getBytes(charset));
    }

    public static String hmacSha224Hash(byte[] data, byte[] key) {
        return hash(data, key, "HmacSHA224");
    }

    public static String hmacSha256Hash(String data, String key) {
        return hmacSha256Hash(data, key, Constant.DEFAULT_CHARSET);
    }

    public static String hmacSha256Hash(String data, String key, Charset charset) {
        return hmacSha256Hash(data.getBytes(charset), key.getBytes(charset));
    }

    public static String hmacSha256Hash(byte[] data, byte[] key) {
        return hash(data, key, "HmacSHA256");
    }


    public static String hmacSha384Hash(String data, String key) {
        return hmacSha384Hash(data, key, Constant.DEFAULT_CHARSET);
    }

    public static String hmacSha384Hash(String data, String key, Charset charset) {
        return hmacSha384Hash(data.getBytes(charset), key.getBytes(charset));
    }

    public static String hmacSha384Hash(byte[] data, byte[] key) {
        return hash(data, key, "HmacSHA384");
    }

    public static String hmacSha512Hash(String data, String key) {
        return hmacSha512Hash(data, key, Constant.DEFAULT_CHARSET);
    }

    public static String hmacSha512Hash(String data, String key, Charset charset) {
        return hmacSha512Hash(data.getBytes(charset), key.getBytes(charset));
    }

    public static String hmacSha512Hash(byte[] data, byte[] key) {
        return hash(data, key, "HmacSHA512");
    }

    private static String hash(byte[] inputBytes, byte[] keyBytes, String hashType) {
        try {
            Mac hmacMac = Mac.getInstance(hashType);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, hashType);
            hmacMac.init(secretKey);
            return StringUtil.byteToHexString(hmacMac.doFinal(inputBytes)).toUpperCase();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("NoSuchAlgorithmException  or InvalidKeyException", e);
        }
    }
}
