package com.example.network.encrypt;


import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

/**
 * @author： duchangchuang
 * @Description: 兼容前端实现 Aes 加解密
 * @date：2021-10-18
 */
public class AesEcbUtils {

    /**
     * 加密/解密算法名称
     */
    private static final String KEY_ALGORITHM = "AES";
    /**
     * 编码方式
     */
    private static final String CHAR_SET = "UTF-8";
    /**
     * AES的密钥长度
     */
    private static final Integer SECRET_KEY_LENGTH = 128;
    /**
     * 加解密算法/工作模式/填充方式  AES/ECB/PKCS5Padding
     */
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";


    /**
     * Aes 加密
     *
     * @param content 待加密内容
     * @param aesKey  加密的密钥
     * @return String
     */
    public static String encrypt(String content, String aesKey) {
        try {
            //创建密码器
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            byte[] byteContent = content.getBytes(CHAR_SET);
            //初始化为加密密码器
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey.getBytes(), KEY_ALGORITHM));
            byte[] encryptByte = cipher.doFinal(byteContent);
            return parseByte2HexStr(encryptByte);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AES加密模式加密异常，原因：{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Aes 解密
     *
     * @param content 待解密内容
     * @param aesKey  解密的密钥
     * @return String
     * @throws NoSuchProviderException
     */
    public static String decrypt(String content, String aesKey) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            //设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey.getBytes(), KEY_ALGORITHM));
            //执行解密操作
            byte[] result = cipher.doFinal(parseHexStr2Byte(content));
            return new String(result, CHAR_SET);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AES加密模式解密异常，原因：{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取密钥对象
     *
     * @param aesKey
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static SecretKeySpec getSecretKey(final String aesKey) throws NoSuchAlgorithmException {
        //生成指定算法密钥的生成器
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        keyGenerator.init(SECRET_KEY_LENGTH, new SecureRandom(aesKey.getBytes()));
        //生成密钥
        SecretKey secretKey = keyGenerator.generateKey();
        //转换成AES的密钥
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 随机生成秘钥
     *
     * @throws NoSuchAlgorithmException 的
     */
    public static String getAesKey() throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128);
        //要生成多少位，只需要修改这里即可128, 192或256
        SecretKey sk = kg.generateKey();
        byte[] b = sk.getEncoded();
        return byteToHexString(b);
    }

    public static String byteToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String strHex = Integer.toHexString(bytes[i]);
            if (strHex.length() > 3) {
                sb.append(strHex.substring(6));
            } else {
                if (strHex.length() < 2) {
                    sb.append("0" + strHex);
                } else {
                    sb.append(strHex);
                }
            }
        }
        return sb.toString().toUpperCase();
    }
}
