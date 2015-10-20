package com.nourl.streamsiteplayerremote.networking;

import com.nourl.streamsiteplayerremote.networking.events.AnswerEventArgs;
import com.nourl.streamsiteplayerremote.networking.events.ErrorEventArgs;
import com.nourl.streamsiteplayerremote.networking.events.MessageEventArgs;
import com.nourl.streamsiteplayerremote.networking.events.RequestEventArgs;
import com.nourl.streamsiteplayerremote.networking.messages.NetworkMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janne on 10/16/2015.
 */
public abstract class NetworkInterface {

    protected List<INetworkReceiver> receivers = new ArrayList<>();

    public void addNetworkReceiver(INetworkReceiver receiver) {
        receivers.add(receiver);
    }

    public void removeNetworkReceiver(INetworkReceiver receiver) {
        receivers.remove(receiver);
    }

    protected void onNetworkError(final ErrorEventArgs eventArgs) {
        for (final INetworkReceiver receiver : receivers) {
            receiver.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    receiver.onNetworkError(eventArgs);
                }
            });
        }
    }
    protected void onNetworkRequest(final RequestEventArgs eventArgs) {
        for (final INetworkReceiver receiver : receivers) {
            receiver.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    receiver.onNetworkRequest(eventArgs);
                }
            });
        }
    }

    protected void onNetworkMessage(final MessageEventArgs eventArgs) {
        for (final INetworkReceiver receiver : receivers) {
            receiver.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    receiver.onNetworkMessage(eventArgs);
                }
            });
        }
    }

    protected void onNetworkAnswer(final AnswerEventArgs eventArgs) {
        for (final INetworkReceiver receiver : receivers) {
            receiver.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    receiver.onNetworkAnswer(eventArgs);
                }
            });
        }
    }

    /**
     * Sends the specified NetworkMessage to the server. Make sure to call isReady before.
     * @param message The network message to send.
     */
    public abstract void sendMessage(NetworkMessage message);

    /**
     * Checks if the interface is ready. If not, try to call initialize if false.
     * @return true if interface is ready to send/receive data
     */
    public abstract boolean isReady();

    public void initialize() { } //Override this if you need to initialize

}
