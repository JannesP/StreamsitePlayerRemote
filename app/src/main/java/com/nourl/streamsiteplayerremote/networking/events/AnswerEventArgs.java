package com.nourl.streamsiteplayerremote.networking.events;

import com.nourl.streamsiteplayerremote.networking.messages.AnswerNetworkMessage;

/**
 * Created by Jannes Peters on 20.10.2015.
 */
public class AnswerEventArgs {
    private final AnswerNetworkMessage MSG;

    public AnswerEventArgs(AnswerNetworkMessage MSG) {
        this.MSG = MSG;
    }

    public AnswerNetworkMessage getMessage() {
        return MSG;
    }
}
