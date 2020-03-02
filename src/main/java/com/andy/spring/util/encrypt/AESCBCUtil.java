package com.andy.spring.util.encrypt;

import com.andy.spring.constant.Constant;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密工具-CBC模式
 *
 * @author 庞先海 2019-11-14
 */
public class AESCBCUtil {

    /**
     * Aes cbc模式加密
     *
     * @param data    明文数据
     * @param sKey    Aes加密cbc模式 skey
     * @param ivParam 向量iv 可增强aes加密算法
     * @return 密文数据字符穿
     */
    public static String encrypt(String data, String sKey, String ivParam) {
        return encrypt(data, sKey.getBytes(Constant.DEFAULT_CHARSET), ivParam.getBytes(Constant.DEFAULT_CHARSET));
    }


    /**
     * Aes cbc模式加密
     *
     * @param data    明文数据
     * @param sKey    Aes加密cbc模式 skey
     * @param ivParam 向量iv 可增强aes加密算法
     * @return 密文数据二进制
     */
    public static byte[] encryptToByte(String data, String sKey, String ivParam) {

        return encrypt(data.getBytes(Constant.DEFAULT_CHARSET), sKey.getBytes(Constant.DEFAULT_CHARSET),
            ivParam.getBytes(Constant.DEFAULT_CHARSET));

    }

    /**
     * Aes cbc模式加密
     *
     * @param data    明文数据
     * @param sKey    Aes加密cbc模式 skey
     * @param ivParam 向量iv 可增强aes加密算法
     * @return 密文数据
     */
    public static String encrypt(String data, byte[] sKey, byte[] ivParam) {
        byte[] encrypt = encrypt(data.getBytes(Constant.DEFAULT_CHARSET), sKey, ivParam);
        return EncodeUtil.base64Encode(encrypt, Constant.DEFAULT_CHARSET);
    }

    /**
     * Aes cbc模式加密
     *
     * @param data    明文数据
     * @param sKey    Aes加密cbc模式 skey
     * @param ivParam 向量iv 可增强aes加密算法
     * @return 密文数据
     */
    public static byte[] encrypt(byte[] data, byte[] sKey, byte[] ivParam) {
        return aesCbcSecret(data, sKey, ivParam, Cipher.ENCRYPT_MODE);
    }


    public static String decrypt(String data, String sKey, String ivParam) {
        return decrypt(data, sKey.getBytes(Constant.DEFAULT_CHARSET), ivParam.getBytes(Constant.DEFAULT_CHARSET));
    }

    /**
     * Aes cbc模式解密
     *
     * @param data    密文数据
     * @param sKey    Aes加密cbc模式 skey
     * @param ivParam 向量iv 可增强aes加密算法
     * @return 密文数据
     */
    public static String decrypt(String data, byte[] sKey, byte[] ivParam) {
        byte[] byteData = EncodeUtil.base64DecodeByte(data);
        byte[] encrypt = decrypt(byteData, sKey, ivParam);
        return new String(encrypt, Constant.DEFAULT_CHARSET);
    }

    /**
     * Aes cbc模式解密
     *
     * @param data    明文数据
     * @param sKey    Aes加密cbc模式 skey
     * @param ivParam 向量iv 可增强aes加密算法
     * @return 密文数据
     */
    public static byte[] decrypt(byte[] data, byte[] sKey, byte[] ivParam) {
        return aesCbcSecret(data, sKey, ivParam, Cipher.DECRYPT_MODE);
    }

    /**
     * Aes cbc模式加密货解密
     *
     * @param data    明文数据
     * @param sKey    Aes加密cbc模式 skey
     * @param ivParam 向量iv 可增强aes加密算法
     * @param mode    Cipher.ENCRYPT_MODE加密 Cipher.DECRYPT_MODE解密
     * @return 密文数据
     */
    private static byte[] aesCbcSecret(byte[] data, byte[] sKey, byte[] ivParam, int mode) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec skeySpec = new SecretKeySpec(sKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivParam);
            cipher.init(mode, skeySpec, iv);
            return cipher.doFinal(data);
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
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException("InvalidAlgorithmParameterException", e);
        }
    }
}
