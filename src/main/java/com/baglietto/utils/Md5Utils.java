package com.baglietto.utils;

import org.springframework.util.DigestUtils;

//TODO cambiar a sha256
public class Md5Utils {

    public String digest(String plainText) {
        return bytesToHex(DigestUtils.md5Digest(plainText.getBytes()));
    }

    public String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
