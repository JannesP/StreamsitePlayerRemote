package com.nourl.streamsiteplayerremote.networking.events;

import com.nourl.streamsiteplayerremote.networking.messages.RequestNetworkMessage;

/**
 * Created by Jannes Peters on 20.10.2015.
 */
public class RequestEventArgs {
    private final RequestNetworkMessage MSG;

    public RequestEventArgs(RequestNetworkMessage MSG) {
        this.MSG = MSG;
    }

    public RequestNetworkMessage getMessage() {
        return MSG;
    }
}
