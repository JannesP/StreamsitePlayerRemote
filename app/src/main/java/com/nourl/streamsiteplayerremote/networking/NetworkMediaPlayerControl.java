package com.nourl.streamsiteplayerremote.networking;

import android.app.Activity;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.SeekBar;

import com.nourl.streamsiteplayerremote.R;
import com.nourl.streamsiteplayerremote.Util;
import com.nourl.streamsiteplayerremote.activities.MainActivity;
import com.nourl.streamsiteplayerremote.networking.events.AnswerEventArgs;
import com.nourl.streamsiteplayerremote.networking.events.ErrorEventArgs;
import com.nourl.streamsiteplayerremote.networking.events.InfoEventArgs;
import com.nourl.streamsiteplayerremote.networking.events.RequestEventArgs;
import com.nourl.streamsiteplayerremote.networking.messages.AnswerNetworkMessage;
import com.nourl.streamsiteplayerremote.networking.messages.ControlNetworkMessage;
import com.nourl.streamsiteplayerremote.networking.messages.RequestNetworkMessage;
import com.nourl.streamsiteplayerremote.objects.Episode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Jannes Peters on 20.10.2015.
 */
public class NetworkMediaPlayerControl implements MediaController.MediaPlayerControl, INetworkReceiver {
    protected MainActivity.MyMediaController mediaController;
    protected NetworkInterface networkInterface;
    protected Activity activity;
    protected Thread refreshThread;
    protected Thread reconnectThread;
    protected UByte currentRefreshId = new UByte(0);

    protected boolean isPlaying = false;
    protected int position = 0;
    protected int duration = 0;
    protected int bufferPercentage = 0;

    public NetworkMediaPlayerControl(MainActivity.MyMediaController mediaController, NetworkInterface networkInterface, Activity activity) {
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
        mediaController.setReadyToShow();
        startRefreshLoop();
    }

    @Override
    public void start() {
        networkInterface.sendMessage(new ControlNetworkMessage(ControlNetworkMessage.ControlNetworkMessageType.PLAY_PAUSE, new UByte(0)));
    }

    @Override
    public void pause() {
        //networkInterface.sendMessage(new RequestNetworkMessage(RequestNetworkMessage.NetworkMessageRequestType.EPISODE_LIST, new UByte(0)));
        networkInterface.sendMessage(new ControlNetworkMessage(ControlNetworkMessage.ControlNetworkMessageType.PLAY_PAUSE, new UByte(0)));
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getCurrentPosition() {
        return position;
    }

    private long lastSeek = 0;

    @Override
    public void seekTo(int pos) {
        if (lastSeek + 500 < SystemClock.elapsedRealtime()) {
            byte[] bytePos = Util.intToByteArray(pos);
            Log.d("SEEK", "Seeking to " + pos);
            Log.d("SEEK", "Seeking to " + Arrays.toString(bytePos));
            networkInterface.sendMessage(new ControlNetworkMessage(ControlNetworkMessage.ControlNetworkMessageType.SEEK_TO, new UByte(0), bytePos));
            lastSeek = SystemClock.elapsedRealtime();
        }
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public int getBufferPercentage() {
        return bufferPercentage;
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
        networkInterface.sendMessage(new ControlNetworkMessage(ControlNetworkMessage.ControlNetworkMessageType.NEXT, new UByte(0)));
    }

    private void previous() {
        networkInterface.sendMessage(new ControlNetworkMessage(ControlNetworkMessage.ControlNetworkMessageType.PREVIOUS, new UByte(0)));
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public void onNetworkError(ErrorEventArgs eventArgs) {
        Log.d("onNetworkError", "Network error received!");
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
        RequestNetworkMessage.NetworkMessageRequestType requestType = RequestNetworkMessage.NetworkMessageRequestType.get(answer.getSpecificType());
        if (requestType != null) {
            byte[] data = answer.getData();
            switch (requestType) {
                case PLAYER_STATUS:
                    if (data.length == 11) {
                        isPlaying = data[0] == 1;
                        position = Util.byteArrayToInt(data, 1);
                        duration = Util.byteArrayToInt(data, 5);
                        bufferPercentage = data[9] & 0xFF;
                        SeekBar sb = (SeekBar) activity.findViewById(R.id.seekBarVolume);
                        sb.setProgress(data[10] & 0xFF);
                        mediaController.show();
                    }
                    break;
            }
        } else {
            Log.d("ON_NETWORK_ANSWER", "Got invalid requestType in answer: " + answer.getSpecificType().getValue());
        }
    }

    private void reconnectLoop() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) activity).setLayoutState(false);
            }
        });
        if (reconnectThread == null || !reconnectThread.isAlive()) {
            reconnectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("TCP_RECONNECT", "Checking connection ...");
                    while (!networkInterface.isWorking() && !Thread.currentThread().isInterrupted()) {
                        Log.d("TCP_RECONNECT", "Disconnected from host. Reconnecting ...");
                        networkInterface.start();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    if (networkInterface.isWorking()) {
                        Log.d("TCP_RECONNECT", "Connected successfully!");
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((MainActivity) activity).setLayoutState(true);
                            }
                        });
                        refreshThread = null;
                        startRefreshLoop();
                    } else {
                        Log.d("TCP_RECONNECT", "Interrupted while reconnecting. Stopping networkInterface.");
                        networkInterface.stop();
                    }
                }
            });
            reconnectThread.start();
        }
    }

    public void startNetworkRefresh() {
        reconnectLoop();
    }

    public void stopNetworkRefresh() {
        if (refreshThread != null && refreshThread.isAlive()) {
            refreshThread.interrupt();
        }
        if (reconnectThread != null && reconnectThread.isAlive()) {
            reconnectThread.interrupt();
        }
    }

    private void startRefreshLoop() {
        if (refreshThread == null || ((refreshThread.getState() == Thread.State.TERMINATED) && (reconnectThread == null || !reconnectThread.isAlive()))) {
            refreshThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("TCP_REFRESH", "Refresh started!");
                    while (!Thread.currentThread().isInterrupted()) {
                        if (networkInterface.isWorking()) {
                            networkInterface.sendMessage(new RequestNetworkMessage(RequestNetworkMessage.NetworkMessageRequestType.PLAYER_STATUS, currentRefreshId));
                            currentRefreshId.setValue(currentRefreshId.getValue() % Byte.MAX_VALUE);
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                Log.d("TCP_REFRESH", "Refresh finished through interrupt!");
                                return;
                            }
                        } else {
                            reconnectLoop();
                            Log.d("TCP_REFRESH", "Network interface no longer working. Starting reconnectThread ...");
                            return;
                        }
                    }
                }
            });
            refreshThread.start();
        }
    }

}
