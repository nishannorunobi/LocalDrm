package com.google.android.exoplayer2.imademo;

import com.google.android.exoplayer2.drm.ExoMediaDrm;
import com.google.android.exoplayer2.drm.MediaDrmCallback;
import com.google.android.exoplayer2.util.Assertions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Common on 2/15/2018.
 */

public class MyDRMCallBack implements MediaDrmCallback {
    private final Map<String,String> keyRequestProperties = new HashMap<>();
    String keyId = "abba271e8bcf552bbd2e86a434a9a5d9";
    String keyValue = "69eaa802a6763af979e8d1940fb88392";
    private String keyString = "{\"keys\":[{\"kty\":\"oct\",\"k\":\""+keyValue+"\",\"kid\":\""+keyId+"\"}],\"type\":\"temporary\"}";
    @Override
    public byte[] executeProvisionRequest(UUID uuid, ExoMediaDrm.ProvisionRequest request) throws Exception {
        return new byte[0];
    }

    @Override
    public byte[] executeKeyRequest(UUID uuid, ExoMediaDrm.KeyRequest request) throws Exception {
        return keyString.getBytes();
    }

    public void setKeyRequestProperty(String name, String value) {
        Assertions.checkNotNull(name);
        Assertions.checkNotNull(value);
        synchronized (keyRequestProperties) {
            keyRequestProperties.put(name, value);
        }
    }


    public void clearKeyRequestProperty(String name) {
        Assertions.checkNotNull(name);
        synchronized (keyRequestProperties) {
            keyRequestProperties.remove(name);
        }
    }

    public void clearAllKeyRequestProperties() {
        synchronized (keyRequestProperties) {
            keyRequestProperties.clear();
        }
    }
}
