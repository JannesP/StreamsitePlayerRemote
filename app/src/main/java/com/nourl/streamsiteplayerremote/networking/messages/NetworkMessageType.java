package com.nourl.streamsiteplayerremote.networking.messages;

import com.nourl.streamsiteplayerremote.networking.UByte;

/**
 * Created by Jannes Peters on 18.10.2015.
 */
public enum NetworkMessageType {
    REQUEST(0), CONTROL(1), ANSWER(2), INFO(3);

    private UByte id;
    NetworkMessageType(int val) {
        id = new UByte(val);
    }

    public static NetworkMessageType get(UByte id) {
        for (NetworkMessageType type : values()) {
            if (type.getValue().getValue() == id.getValue()) return type;
        }
        return null;
    }

    public UByte getValue() {
        return id;
    }
}
