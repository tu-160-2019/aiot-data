/*
 * Copyright 2023 http://gcpaas.gccloud.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gccloud.dataset.utils;

import com.gccloud.common.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Description: DES加解密
 *
 * @author pan.shun
 * @since 2021/9/7 09:54
 */
@Slf4j
public class DESUtils {

    /**
     * 密钥
     */
    private final static String KEY_STR = "GC_REPORT";

    /**
     * 编码
     */
    private static final String CHARSET = "UTF8";

    private static final Key key;

    static {
        try {
            KeyGenerator _generator = KeyGenerator.getInstance("DES");
            //防止linux下 随机生成key
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(KEY_STR.getBytes());

            _generator.init(56, secureRandom);
            key = _generator.generateKey();
            _generator = null;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GlobalException(e.getMessage());
        }
    }


    /**
     * 加密
     */
    public static String getEncryptString(String strMing) {
        byte[] byteMi;
        byte[] byteMing;
        String strMi;
//        BASE64Encoder base64en = new BASE64Encoder();
        Base64.Encoder encoder = Base64.getEncoder();
        try {
            byteMing = strMing.getBytes(CHARSET);
            byteMi = encryptByte(byteMing);
//            strMi = base64en.encode(byteMi);
            strMi = encoder.encodeToString(byteMi);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GlobalException(e.getMessage());
        } finally {
            encoder = null;
            byteMing = null;
            byteMi = null;
        }
        return strMi;
    }

    /**
     * 解密
     */
    public static String getDecryptString(String strMi) {
//        BASE64Decoder base64De = new BASE64Decoder();
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] byteMing;
        byte[] byteMi;
        String strMing;
        try {
//            byteMi = base64De.decodeBuffer(strMi);
            byteMi = decoder.decode(strMi);
            byteMing = decryptByte(byteMi);
            strMing = new String(byteMing, CHARSET);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GlobalException(e.getMessage());
        } finally {
            decoder = null;
            byteMing = null;
            byteMi = null;
        }
        return strMing;
    }

    /**
     * 加密以 byte[] 明文输入 ,byte[] 密文输出
     */
    private static byte[] encryptByte(byte[] byteS) {
        byte[] byteFina;
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GlobalException(e.getMessage());
        } finally {
            cipher = null;
        }
        return byteFina;
    }

    /**
     * 解密以 byte[] 密文输入 , 以 byte[] 明文输出
     */
    private static byte[] decryptByte(byte[] byteD) {
        Cipher cipher;
        byte[] byteFina = null;
        try {
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byteFina = cipher.doFinal(byteD);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GlobalException(e.getMessage());
        } finally {
            cipher = null;
        }
        return byteFina;
    }
}