package com.andy.spring.util.encrypt;

/**
 * Rc4加密解密工具
 *
 * @author 庞先海 2019-11-14
 */

public class Rc4Util {

    public static byte[] rc4(byte[] input, String pass) {
        if (input == null || pass == null) {
            return null;
        }

        byte[] output = new byte[input.length];
        byte[] mBox = getKey(pass.getBytes(), 256);

        // 加密
        int i = 0;
        int j = 0;

        for (int offset = 0; offset < input.length; offset++) {
            i = (i + 1) % mBox.length;
            j = (j + (int)((mBox[i] + 256) % 256)) % mBox.length;

            byte temp = mBox[i];
            mBox[i] = mBox[j];
            mBox[j] = temp;
            byte a = input[offset];

            // byte b = mBox[(mBox[i] + mBox[j] % mBox.Length) % mBox.Length];
            // mBox[j] 一定比 mBox.Length 小，不需要在取模
            byte b = mBox[(toInt(mBox[i]) + toInt(mBox[j])) % mBox.length];

            output[offset] = (byte)((int)a ^ (int)toInt(b));
        }

        return output;
    }

    /**
     * 处理rc4加密key
     */
    private static byte[] getKey(byte[] pass, int kLen) {
        byte[] mBox = new byte[kLen];
        for (int i = 0; i < kLen; i++) {
            mBox[i] = (byte)i;
        }
        int j = 0;
        for (int i = 0; i < kLen; i++) {
            j = (j + ((mBox[i] + 256) % 256) + pass[i % pass.length]) % kLen;
            byte temp = mBox[i];
            mBox[i] = mBox[j];
            mBox[j] = temp;
        }
        return mBox;
    }

    public static int toInt(byte b) {
        return (b + 256) % 256;
    }

    public static void main(String... args) {
        String rightPassword = HmacHashUtil.hmacSha256Hash("123456", "pxh");
        System.out.println(rightPassword);
    }
}
