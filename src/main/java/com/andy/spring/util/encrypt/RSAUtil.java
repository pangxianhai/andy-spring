package com.andy.spring.util.encrypt;

import com.andy.spring.constant.Constant;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

/**
 * rsa加密/解密工具
 *
 * @author 庞先海 2019-11-14
 */
public class RSAUtil {

    private static final String RSA = "RSA";

    /**
     * key的默认长度
     */
    private static int KEY_LEN = 1024;

    /**
     * 加密长度 最大只能是 KEY_LEN/8-11
     */
    private static int ENCRYPT_LEN = KEY_LEN / 8 - 11;

    /**
     * 解密长度 加密后的长度统一是 KEY_LEN / 8
     */
    private static int DECRYPT_LEN = KEY_LEN / 8;

    /**
     * 设置默认key长度时 同时要设置加密长度 和解密长度  ENCRYPT_LEN，和 DECRYPT_LEN都是为了分段加解密使用
     * 该方法适用与一个系统使用统一的长度 可以少传分段参数
     *
     * @param keyLen key长度
     */
    public static void setKeyLen(int keyLen) {
        KEY_LEN = keyLen;
        ENCRYPT_LEN = keyLen / 8 - 11;
        DECRYPT_LEN = keyLen / 8;
    }

    public static int getKeyLen() {
        return KEY_LEN;
    }

    public static int getEncryptLen() {
        return ENCRYPT_LEN;
    }

    public static int getDecryptLen() {
        return DECRYPT_LEN;
    }

    public static KeyPair generateRSAKeyPair(int keyLength) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
            kpg.initialize(keyLength);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        }
    }

    public static KeyPair generateRSAKeyPair() {
        return generateRSAKeyPair(KEY_LEN);
    }

    public static PublicKey generatePublicKey(String key) {
        StringReader reader = new StringReader(key);
        return generatePublicKey(reader);
    }

    public static PublicKey generatePublicKey(Reader reader) {
        try {
            PemReader pemReader = new PemReader(reader);
            PemObject pemObject = pemReader.readPemObject();
            byte[] bytes = pemObject.getContent();
            pemReader.close();
            return generatePublicKey(bytes);
        } catch (IOException e) {
            throw new RuntimeException("IOException", e);
        }
    }

    public static PublicKey generatePublicKey(byte[] key) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("InvalidKeySpecException", e);
        }
    }

    public static PrivateKey generatePrivateKey(String key) {
        StringReader reader = new StringReader(key);
        return generatePrivateKey(reader);
    }

    public static PrivateKey generatePrivateKey(Reader reader) {
        try {
            PemReader pemReader = new PemReader(reader);
            PemObject pemObject = pemReader.readPemObject();
            byte[] bytes = pemObject.getContent();
            pemReader.close();
            return generatePrivateKey(bytes);
        } catch (IOException e) {
            throw new RuntimeException("IOException", e);
        }
    }

    public static PrivateKey generatePrivateKey(byte[] key) {
        try {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return keyFactory.generatePrivate(spec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("InvalidKeySpecException", e);
        }
    }

    public static String generatePrivateStr(PrivateKey privateKey) {
        try {
            StringWriter stringWriter = new StringWriter();
            PemWriter pemWriter = new PemWriter(stringWriter);
            pemWriter.writeObject(new PemObject("PRIVATE KEY", privateKey.getEncoded()));
            pemWriter.close();
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException("IOException", e);
        }
    }

    public static String generatePublicStr(PublicKey publicKey) {
        try {
            StringWriter stringWriter = new StringWriter();
            PemWriter pemWriter = new PemWriter(stringWriter);
            pemWriter.writeObject(new PemObject("PUBLIC KEY", publicKey.getEncoded()));
            pemWriter.close();
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException("IOException", e);
        }
    }

    ////////公钥加密部分////////////

    /**
     * 公钥加密
     *
     * @param data      待加密的数据
     * @param publicKey 公钥
     */
    public static String encryptByPublic(String data, String publicKey) {
        return encryptByPublic(data, generatePublicKey(publicKey), ENCRYPT_LEN);
    }

    /**
     * 公钥加密
     *
     * @param data      待加密的数据
     * @param publicKey 公钥
     */
    public static String encryptByPublic(String data, String publicKey, int len) {
        return encryptByPublic(data, generatePublicKey(publicKey), len);
    }

    /**
     * 公钥加密
     *
     * @param data      待加密的数据
     * @param publicKey 公钥
     */
    public static String encryptByPublic(String data, PublicKey publicKey) {
        return encryptByPublic(data, publicKey, ENCRYPT_LEN);
    }

    /**
     * 公钥加密
     *
     * @param data      待加密的数据
     * @param publicKey 公钥
     */
    public static String encryptByPublic(String data, PublicKey publicKey, int len) {
        return EncodeUtil.base64Encode(
            rsaFinal(data.getBytes(Constant.DEFAULT_CHARSET), Cipher.ENCRYPT_MODE, publicKey, len));
    }

    /**
     * 公钥加密
     *
     * @param data      待加密的数据
     * @param publicKey 公钥
     */
    public static byte[] encryptByPublic(byte[] data, PublicKey publicKey, int len) {
        return rsaFinal(data, Cipher.ENCRYPT_MODE, publicKey, len);
    }

    ////////公钥解密部分////////////

    /**
     * 公钥解密
     *
     * @param data      待加解密的数据
     * @param publicKey 公钥
     */
    public static String decryptByPublic(String data, String publicKey) {
        return decryptByPublic(data, generatePublicKey(publicKey), DECRYPT_LEN);
    }

    /**
     * 公钥解密
     *
     * @param data      待加解密的数据
     * @param publicKey 公钥
     */
    public static String decryptByPublic(String data, String publicKey, int len) {
        return decryptByPublic(data, generatePublicKey(publicKey), len);
    }

    /**
     * 公钥解密
     *
     * @param data      待加解密的数据
     * @param publicKey 公钥
     */
    public static String decryptByPublic(String data, PublicKey publicKey, int len) {
        byte[] bytes = decryptByPublic(EncodeUtil.base64DecodeByte(data), publicKey, len);
        return new String(bytes, Constant.DEFAULT_CHARSET);
    }

    /**
     * 公钥解密
     *
     * @param data      待加解密的数据
     * @param publicKey 公钥
     */
    public static byte[] decryptByPublic(byte[] data, PublicKey publicKey, int len) {
        return rsaFinal(data, Cipher.DECRYPT_MODE, publicKey, len);
    }

    ////////私钥加密部分////////////


    /**
     * 私钥加密
     *
     * @param data       待加密的数据
     * @param privateKey 私钥
     */
    public static String encryptByPrivate(String data, String privateKey) {
        return encryptByPrivate(data, privateKey, ENCRYPT_LEN);
    }

    /**
     * 私钥加密
     *
     * @param data       待加密的数据
     * @param privateKey 私钥
     */
    public static String encryptByPrivate(String data, String privateKey, int len) {
        byte[] bytes = encryptByPrivate(data.getBytes(Constant.DEFAULT_CHARSET), generatePrivateKey(privateKey), len);
        return EncodeUtil.base64Encode(bytes);
    }

    /**
     * 私钥加密
     *
     * @param data       待加解密的数据
     * @param privateKey 私钥
     */
    public static byte[] encryptByPrivate(byte[] data, PrivateKey privateKey, int len) {
        return rsaFinal(data, Cipher.ENCRYPT_MODE, privateKey, len);
    }

    ////////私钥解密部分////////////


    /**
     * 私钥解密
     *
     * @param data       待加密的数据
     * @param privateKey 私钥
     */
    public static String decryptByPrivate(String data, String privateKey) {
        return decryptByPrivate(data, privateKey, DECRYPT_LEN);
    }

    /**
     * 私钥解密
     *
     * @param data       待加密的数据
     * @param privateKey 私钥
     */
    public static String decryptByPrivate(String data, String privateKey, int len) {
        byte[] bytes = decryptByPrivate(EncodeUtil.base64DecodeByte(data), generatePrivateKey(privateKey), len);
        return new String(bytes, Constant.DEFAULT_CHARSET);
    }

    /**
     * 私钥解密
     *
     * @param data       待加解密的数据
     * @param privateKey 私钥
     */
    public static byte[] decryptByPrivate(byte[] data, PrivateKey privateKey, int len) {
        return rsaFinal(data, Cipher.DECRYPT_MODE, privateKey, len);
    }


    /**
     * rsa 加密/解密
     *
     * @param data 待加密/解密的数据
     * @param mode Cipher.ENCRYPT_MODE:加密 Cipher.DECRYPT_MODE:解密
     * @param key  公钥或私钥
     * @param len  分段长度
     */
    public static byte[] rsaFinal(byte[] data, int mode, Key key, int len) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(mode, key);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int index = 0;
            while (data.length > index) {
                byte[] rsaData;
                if (data.length - index > len) {
                    rsaData = cipher.doFinal(data, index, len);
                } else {
                    rsaData = cipher.doFinal(data, index, data.length - index);
                }
                out.write(rsaData);
                index += len;
            }
            return out.toByteArray();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("NoSuchPaddingException", e);
        } catch (BadPaddingException e) {
            throw new RuntimeException("BadPaddingException", e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException("IllegalBlockSizeException", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("InvalidKeyException", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException", e);
        }
    }
}
