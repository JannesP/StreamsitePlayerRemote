package com.nourl.streamsiteplayerremote.networking.messages;

import com.nourl.streamsiteplayerremote.networking.UByte;

/**
 * Created by Jannes Peters on 18.10.2015.
 */
public enum NetworkMessageType {
    REQUEST(0), CONTROL(1);

    private UByte id;
    NetworkMessageType(int val) {
        id = new UByte(val);
    }

    public UByte getValue() {
        return id;
    }
}
