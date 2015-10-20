package com.nourl.streamsiteplayerremote.networking;

import android.view.View;
import android.widget.MediaController;

/**
 * Created by Jannes Peters on 20.10.2015.
 */
public class NetworkMediaPlayerControl implements MediaController.MediaPlayerControl {
    private MediaController mediaController;

    public NetworkMediaPlayerControl(MediaController mediaController) {
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
        mediaController.show();
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
        return true;
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
        //TODO write method
    }

    private void previous() {
        //TODO write method
    }
}
