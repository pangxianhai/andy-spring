package com.andy.spring.util.encrypt;

import com.andy.spring.constant.Constant;
import com.andy.spring.util.StringUtil;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * has算法工具
 *
 * @author 庞先海 2019-11-14
 */
public class HashUtil {

    /**
     * md5 hash
     */
    public static String md5Hash(byte[] inputBytes) {
        return hash(inputBytes, "MD5");
    }

    /**
     * md5 hash
     */
    public static String md5Hash(String text, Charset charset) {
        return md5Hash(text.getBytes(charset));
    }

    public static String md5Hash(String text) {
        return md5Hash(text, Constant.DEFAULT_CHARSET);
    }

    /**
     * sha-1 hash
     */
    public static String shaHash(byte[] inputBytes) {
        return hash(inputBytes, "SHA-1");
    }

    /**
     * sha-1 hash
     */
    public static String shaHash(String text, Charset charset) {
        return shaHash(text.getBytes(charset));
    }

    /**
     * sha-1 hash
     */
    public static String shaHash(String text) {
        return shaHash(text, Constant.DEFAULT_CHARSET);
    }

    /**
     * sha-256 hash
     */
    public static String sha256Hash(byte[] inputBytes) {
        return hash(inputBytes, "SHA-256");
    }

    /**
     * sha-256 hash
     */
    public static String sha256Hash(String text, Charset charset) {
        return sha256Hash(text.getBytes(charset));
    }

    public static String sha256Hash(String text) {
        return sha256Hash(text, Constant.DEFAULT_CHARSET);
    }

    /**
     * 哈希算法
     *
     * @param hashType md5 or sha
     * @return 字符串
     */
    private static String hash(byte[] inputBytes, String hashType) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(hashType);
            messageDigest.update(inputBytes);
            return StringUtil.byteToHexString(messageDigest.digest()).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException find", e);
        }

    }

}
