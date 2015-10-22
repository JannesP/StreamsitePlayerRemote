package com.nourl.streamsiteplayerremote.networking;

/**
 * Created by janne on 10/16/2015.
 */
public class UByte {
    private short value;
    public UByte(int value) {
        setValue(value);
    }

    public UByte(byte value) {
        setValue(value);
    }

    public UByte(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }

    public byte getByte() {
        return (byte)value;
    }

    public void setValue(int value) {
        if (value > 255 || value < 0) throw new IllegalArgumentException("The value was not valid for a UByte!");
        this.value = (short)(value & 0xFF);
    }

    public void setValue(byte value) {
        this.value = (short)(value & 0xFF);
    }

    public void setValue(short value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return (int)getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != UByte.class) return false;
        return ((UByte)o).getValue() == getValue();
    }
}
