package com.nourl.streamsiteplayerremote.networking;

import android.app.Activity;

import com.nourl.streamsiteplayerremote.networking.events.AnswerEventArgs;
import com.nourl.streamsiteplayerremote.networking.events.ErrorEventArgs;
import com.nourl.streamsiteplayerremote.networking.events.MessageEventArgs;
import com.nourl.streamsiteplayerremote.networking.events.RequestEventArgs;

/**
 * Created by Jannes Peters on 18.10.2015.
 */
public interface INetworkReceiver {
    Activity getActivity();
    void onNetworkError(ErrorEventArgs eventArgs);
    void onNetworkRequest(RequestEventArgs eventArgs);
    void onNetworkMessage(MessageEventArgs eventArgs);
    void onNetworkAnswer(AnswerEventArgs eventArgs);
}
