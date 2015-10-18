package com.nourl.streamsiteplayerremote.networking.messages;

import com.nourl.streamsiteplayerremote.networking.UByte;

/**
 * Created by Jannes Peters on 18.10.2015.
 */
public abstract class NetworkMessage {

    protected final NetworkMessageType TYPE;
    protected final UByte ID;

    protected NetworkMessage(NetworkMessageType type, UByte id) {
        TYPE = type;
        ID = id;
    }

    public NetworkMessageType getType() {
        return TYPE;
    }

    public UByte getId() {
        return ID;
    }

    public abstract byte[] getDataBytes();
}
