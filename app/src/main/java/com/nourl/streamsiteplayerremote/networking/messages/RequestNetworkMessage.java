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

        public UByte getValue() {
            return id;
        }
    }

    protected RequestNetworkMessage(NetworkMessageRequestType type, int id) {
        super(NetworkMessageType.REQUEST, type.getValue(), id, null);
    }

    protected RequestNetworkMessage(NetworkMessageRequestType type, int id, byte[] data) {
        super(NetworkMessageType.REQUEST, type.getValue(), id, data);
    }
}
