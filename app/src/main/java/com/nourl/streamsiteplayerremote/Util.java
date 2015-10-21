package com.nourl.streamsiteplayerremote;

import com.nourl.streamsiteplayerremote.networking.UByte;

import java.util.Arrays;

/**
 * Created by Jannes Peters on 21.10.2015.
 */
public class Util {

    private String byteArrayToString(byte[] array) {
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
