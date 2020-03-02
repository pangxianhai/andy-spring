package com.andy.spring.util.encrypt;

import com.andy.spring.constant.Constant;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringUtils;

/**
 * AES加密解密工具-ECB模式
 *
 * @author 庞先海 2019-11-14
 */
public class AESUtil {

    public static String buildEncryptKey() {
        return EncodeUtil.base64Encode(buildEncryptKeyByte());
    }

    public static byte[] buildEncryptKeyByte() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            return keyGenerator.generateKey().getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        }
    }

    /**
     * 字符串加密
     *
     * @param text 密文字符串
     */
    public static String encrypt(String text, String encryptKey) {
        return encrypt(text, EncodeUtil.base64DecodeByte(encryptKey), Constant.DEFAULT_CHARSET);
    }

    /**
     * AES加密
     *
     * @param text       明文字符串
     * @param encryptKey 密钥
     * @param charset    字符串编码
     */
    public static String encrypt(String text, byte[] encryptKey, Charset charset) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        byte[] result = aesSecret(text.getBytes(charset), encryptKey, Cipher.ENCRYPT_MODE);
        String encryptText = EncodeUtil.base64Encode(result, charset);
        return EncodeUtil.base64Encode(encryptText, charset);
    }

    /**
     * AES加密
     *
     * @param encryptData 明文数据
     * @param encryptKey  key数据
     * @return 密文数据
     */
    public static byte[] encrypt(byte[] encryptData, byte[] encryptKey) {
        return aesSecret(encryptData, encryptKey, Cipher.ENCRYPT_MODE);
    }


    /**
     * 字符串解密
     *
     * @param text 密文字符串
     */
    public static String decrypt(String text, String encryptKey) {
        return decrypt(text, EncodeUtil.base64DecodeByte(encryptKey), Constant.DEFAULT_CHARSET);
    }

    /**
     * AES 解密
     *
     * @param encryptText 密文
     * @param encryptKey  密钥
     * @param charset     字符串编码
     */
    public static String decrypt(String encryptText, byte[] encryptKey, Charset charset) {
        if (StringUtils.isEmpty(encryptText)) {
            return encryptText;
        }
        encryptText = EncodeUtil.base64Decode(encryptText, charset);
        byte[] encryptByte = EncodeUtil.base64DecodeByte(encryptText);
        return new String(aesSecret(encryptByte, encryptKey, Cipher.DECRYPT_MODE), charset);
    }

    /**
     * AES解密
     *
     * @param encryptData 密文数据
     * @param encryptKey  key数据
     * @return 明文数据
     */
    public static byte[] decrypt(byte[] encryptData, byte[] encryptKey) {
        return aesSecret(encryptData, encryptKey, Cipher.DECRYPT_MODE);
    }

    /**
     * AES加密解密
     */
    private static byte[] aesSecret(byte[] data, byte[] key, int mode) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(mode, secretKey);
            byte[] decrypted = cipher.doFinal(data);
            return decrypted;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("NoSuchPaddingException", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("InvalidKeyException", e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException("IllegalBlockSizeException", e);
        } catch (BadPaddingException e) {
            throw new RuntimeException("BadPaddingException", e);
        }

    }
}
