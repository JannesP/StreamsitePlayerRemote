package com.nourl.streamsiteplayerremote.networking.messages;

import android.app.admin.DeviceAdminInfo;

import com.nourl.streamsiteplayerremote.networking.UByte;

/**
 * Created by Jannes Peters on 18.10.2015.
 */
public abstract class NetworkMessage {

    protected final NetworkMessageType TYPE;
    protected final UByte SPECIFIC_TYPE;
    protected final byte[] DATA;
    protected final UByte ID;

    protected NetworkMessage(NetworkMessageType type, UByte specificType, UByte id, byte[] data) {
        TYPE = type;
        SPECIFIC_TYPE = specificType;
        if (data != null) {
            DATA = data;
        } else {
            DATA = new byte[0];
        }
        ID = id;
    }

    public NetworkMessageType getType() {
        return TYPE;
    }

    public UByte getSpecificType() {
        return SPECIFIC_TYPE;
    }

    public byte[] getData() {
        return DATA;
    }

    public UByte getID() {
        return ID;
    }

    public byte[] getFullBytes() {
        int length = 3 + DATA.length;
        byte[] bytes = new byte[length];
        bytes[0] = TYPE.getValue().getByte();   //TODO check if correct
        bytes[1] = SPECIFIC_TYPE.getByte();
        bytes[2] = ID.getByte();
        System.arraycopy(DATA, 0, bytes, 3, DATA.length);
        return bytes;
    }
}
