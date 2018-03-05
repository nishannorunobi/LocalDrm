package com.google.android.exoplayer2.imademo;

/**
 * Created by Nishan on 3/5/2018.
 */

public class KeyConverter {
    String key = "0a247b0751cbf1a827e2fedfb87479a2";
    long hexKeyPrefix = 0x0a247b0751cbf1a8L;
    long hexKeySuffix = 0x27e2fedfb87479a2L;

    String keyId = "91341951696b5e1ba232439ecec1f12a";
    long hexKeyIdPrefix = 0x91341951696b5e1bL;
    long hexKeyIdSuffix = 0xa232439ecec1f12aL;

    String encodedKey;
    String encodedKeId;

    public KeyConverter() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getEncodedKey(){
        return DemoUtil.getBase64Encoded(hexKeyPrefix,hexKeySuffix);
    }

    public String getEncodedKid(){
        return DemoUtil.getBase64Encoded(hexKeyIdPrefix,hexKeyIdSuffix);
    }
}
