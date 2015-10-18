package com.nourl.streamsiteplayerremote.networking;

import com.nourl.streamsiteplayerremote.networking.messages.NetworkMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janne on 10/16/2015.
 */
public abstract class NetworkInterface {

    protected List<INetworkReceiver> receivers = new ArrayList<>();

    public enum NETWORK_STATUS {
        OK, FAILED
    }

    public void addNetworkReceiver(INetworkReceiver receiver) {
        receivers.add(receiver);
    }

    public void removeNetworkReceiver(INetworkReceiver receiver) {
        receivers.remove(receiver);
    }

    public abstract void sendMessage(NetworkMessage message);
}
