package com.nourl.streamsiteplayerremote.networking.events;

import com.nourl.streamsiteplayerremote.networking.messages.InfoNetworkMessage;

/**
 * Created by Jannes Peters on 20.10.2015.
 */
public class InfoEventArgs {
    private final InfoNetworkMessage MSG;

    public InfoEventArgs(InfoNetworkMessage MSG) {
        this.MSG = MSG;
    }

    public InfoNetworkMessage getMessage() {
        return MSG;
    }
}
