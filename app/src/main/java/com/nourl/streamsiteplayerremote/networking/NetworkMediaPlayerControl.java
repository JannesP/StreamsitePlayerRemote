package com.nourl.streamsiteplayerremote.networking;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;

import com.nourl.streamsiteplayerremote.networking.events.AnswerEventArgs;
import com.nourl.streamsiteplayerremote.networking.events.ErrorEventArgs;
import com.nourl.streamsiteplayerremote.networking.events.InfoEventArgs;
import com.nourl.streamsiteplayerremote.networking.events.RequestEventArgs;
import com.nourl.streamsiteplayerremote.networking.messages.AnswerNetworkMessage;
import com.nourl.streamsiteplayerremote.networking.messages.RequestNetworkMessage;

import java.util.Arrays;

/**
 * Created by Jannes Peters on 20.10.2015.
 */
public class NetworkMediaPlayerControl implements MediaController.MediaPlayerControl, INetworkReceiver {
    protected MediaController mediaController;
    protected NetworkInterface networkInterface;
    protected Activity activity;

    protected boolean isPlaying = false;

    public NetworkMediaPlayerControl(MediaController mediaController, NetworkInterface networkInterface, Activity activity) {
        this.activity = activity;
        this.networkInterface = networkInterface;
        this.networkInterface.addNetworkReceiver(this);
        this.mediaController = mediaController;
        this.mediaController.setMediaPlayer(this);
        this.mediaController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });
    }

    public void show() {
        if (!mediaController.isShowing()) mediaController.show();
    }

    @Override
    public void start() {
        //TODO write method
    }

    @Override
    public void pause() {
        //TODO write method
    }

    @Override
    public int getDuration() {
        //TODO write method
        return 10000;
    }

    @Override
    public int getCurrentPosition() {
        //TODO write method
        return 1000;
    }

    @Override
    public void seekTo(int pos) {
        //TODO write method
    }

    @Override
    public boolean isPlaying() {
        //TODO write method
        return isPlaying;
    }

    @Override
    public int getBufferPercentage() {
        //TODO write method
        return 5000;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    private void next() {
        networkInterface.sendMessage(new RequestNetworkMessage(RequestNetworkMessage.NetworkMessageRequestType.PLAYER_STATUS, new UByte(130)));
        //TODO write method
    }

    private void previous() {
        networkInterface.stop();
        networkInterface.start();
        //TODO write method
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public void onNetworkError(ErrorEventArgs eventArgs) {

    }

    @Override
    public void onNetworkRequest(RequestEventArgs eventArgs) {

    }

    @Override
    public void onNetworkInfoMessage(InfoEventArgs eventArgs) {

    }

    @Override
    public void onNetworkAnswer(AnswerEventArgs eventArgs) {
        AnswerNetworkMessage answer = eventArgs.getMessage();
        isPlaying = answer.getData()[0] == 1;
        mediaController.show();
        Log.d("ON_NETWORK_ANSWER", "Playing: " + answer.getData()[0]);
        Log.d("DEBUG", Arrays.toString(answer.getData()));
    }
}
