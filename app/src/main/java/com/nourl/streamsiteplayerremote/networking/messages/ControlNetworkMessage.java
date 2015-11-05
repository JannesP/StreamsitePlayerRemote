package com.nourl.streamsiteplayerremote.networking.messages;

import com.nourl.streamsiteplayerremote.networking.UByte;

/**
 * Created by Jannes Peters on 20.10.2015.
 */
public class ControlNetworkMessage extends NetworkMessage {

    public enum ControlNetworkMessageType {
        PLAY_PAUSE(0), CLOSE(1), NEXT(2), PREVIOUS(3), PLAY_EPISODE(4), SEEK_TO(5), SKIP_START(6), SKIP_END(7), TOGGLE_FULLSCREEN(8), VOLUME(9);

        private UByte id;
        ControlNetworkMessageType(int val) {
            id = new UByte(val);
        }

        public static ControlNetworkMessageType get(UByte id) {
            for (ControlNetworkMessageType type : values()) {
                if (type.getValue().equals(id)) return type;
            }
            return null;
        }

        public UByte getValue() {
            return id;
        }
    }

    public ControlNetworkMessage(UByte type, UByte id) {
        super(NetworkMessageType.CONTROL, type, id, null);
    }

    public ControlNetworkMessage(ControlNetworkMessageType type, UByte id) {
        super(NetworkMessageType.CONTROL, type.getValue(), id, null);
    }

    public ControlNetworkMessage(ControlNetworkMessageType type, UByte id, byte[] data) {
        super(NetworkMessageType.CONTROL, type.getValue(), id, data);
    }
}
