package com.nourl.streamsiteplayerremote.networking.messages;

import com.nourl.streamsiteplayerremote.networking.UByte;

/**
 * Created by Jannes Peters on 21.10.2015.
 */
public class AnswerNetworkMessage extends NetworkMessage {



    public AnswerNetworkMessage(UByte specificType, UByte id, byte[] data) {
        super(NetworkMessageType.ANSWER, specificType, id, data);
    }
}
