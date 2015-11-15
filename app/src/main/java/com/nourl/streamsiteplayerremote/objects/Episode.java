package com.nourl.streamsiteplayerremote.objects;

import com.nourl.streamsiteplayerremote.Util;

import java.io.UnsupportedEncodingException;

/**
 * Created by Jannes Peters on 09.11.2015.
 */
public class Episode {
    private int season, number;
    private String name;

    public Episode(byte[] networkData) {
        season = Util.byteArrayToInt(networkData, 0);
        number = Util.byteArrayToInt(networkData, 4);
        try {
            name = new String(networkData, 8, networkData.length - 8, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            name = "!!ERROR WHILE DECODING DATA!!";
        }
    }

    @Override
    public String toString() {
        return "Episode " + number + " - " + this.name;
    }

    public int getSeason() {
        return season;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
}
