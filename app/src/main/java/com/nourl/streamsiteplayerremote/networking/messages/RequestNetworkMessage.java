package com.nourl.streamsiteplayerremote.networking.messages;

import com.nourl.streamsiteplayerremote.networking.UByte;

/**
 * Created by Jannes Peters on 20.10.2015.
 */
public class RequestNetworkMessage extends NetworkMessage {

    public enum NetworkMessageRequestType {
        SERIES(0), SEASON(1), EPISODE(2), PLAYER_STATUS(3);

        private UByte id;
        NetworkMessageRequestType(int val) {
            id = new UByte(val);
        }

        public static NetworkMessageRequestType get(UByte id) {
            for (NetworkMessageRequestType type : values()) {
                if (type.getValue().equals(id)) return type;
            }
            return null;
        }

        public UByte getValue() {
            return id;
        }
    }

    public RequestNetworkMessage(UByte type, UByte id) {
        super(NetworkMessageType.REQUEST, type, id, null);
    }

    public RequestNetworkMessage(NetworkMessageRequestType type, UByte id) {
        super(NetworkMessageType.REQUEST, type.getValue(), id, null);
    }

    public RequestNetworkMessage(UByte type, UByte id, byte[] data) {
        super(NetworkMessageType.REQUEST, type, id, data);
    }

    public RequestNetworkMessage(NetworkMessageRequestType type, UByte id, byte[] data) {
        super(NetworkMessageType.REQUEST, type.getValue(), id, data);
    }
}
