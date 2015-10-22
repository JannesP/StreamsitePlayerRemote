package com.nourl.streamsiteplayerremote;

import com.nourl.streamsiteplayerremote.networking.UByte;

import java.util.Arrays;

/**
 * Created by Jannes Peters on 21.10.2015.
 */
public class Util {

    public static byte[] intToByteArray(int val) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)(val >> i * 8);
        }
        return bytes;
    }

    public static int byteArrayToInt(byte[] array, int pos) {
        if (array.length > (pos + 4)) {
            int val = 0;
            for (int i = 0; i < 4; i++) {
                int unsignedByte = array[pos + i] & 0xFF;
                val |= (unsignedByte << i * 8);
            }
            return val;
        } else {
            return 0;
        }
    }

    private String arrayDebugString(byte[] array) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(array.length * 6);
        sb.append('[');
        sb.append(new UByte(array[0]).getValue());
        for (int i = 1; i < array.length; i++) {
            sb.append(", ");
            sb.append(new UByte(array[i]).getValue());
        }
        sb.append(']');
        return sb.toString();
    }
}
