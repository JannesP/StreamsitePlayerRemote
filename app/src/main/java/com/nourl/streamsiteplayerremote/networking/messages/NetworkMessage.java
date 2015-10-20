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
    protected final int ID;

    protected NetworkMessage(NetworkMessageType type, UByte specificType, int id, byte[] data) {
        TYPE = type;
        SPECIFIC_TYPE = specificType;
        DATA = data;
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

    public int getID() {
        return ID;
    }

    public byte[] getFullBytes() {
        int length = 2;
        if (DATA != null) {
            length += DATA.length;
        }
        byte[] bytes = new byte[length];
        bytes[0] = TYPE.getValue().getByte();   //TODO check if correct
        bytes[1] = SPECIFIC_TYPE.getByte();
        System.arraycopy(DATA, 0, bytes, 2, DATA.length);
        return bytes;
    }
}
