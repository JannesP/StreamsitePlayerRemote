package com.nourl.streamsiteplayerremote.networking.messages;

import com.nourl.streamsiteplayerremote.networking.UByte;

/**
 * Created by Jannes Peters on 20.10.2015.
 */
public class ControlNetworkMessage extends NetworkMessage {

    public enum ControlNetworkMessageType {
        PLAY_PAUSE(0), CLOSE(1), NEXT(2), PREVIOUS(3), PLAY_EPISODE(4);

        private UByte id;
        ControlNetworkMessageType(int val) {
            id = new UByte(val);
        }

        public UByte getValue() {
            return id;
        }
    }

    protected ControlNetworkMessage(ControlNetworkMessageType type, int id) {
        super(NetworkMessageType.CONTROL, type.getValue(), id, null);
    }

    protected ControlNetworkMessage(ControlNetworkMessageType type, int id, byte[] data) {
        super(NetworkMessageType.CONTROL, type.getValue(), id, data);
    }
}
