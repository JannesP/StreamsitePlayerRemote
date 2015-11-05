package com.nourl.streamsiteplayerremote.networking.tcp;

import android.util.Log;

import com.nourl.streamsiteplayerremote.networking.NetworkInterface;
import com.nourl.streamsiteplayerremote.networking.UByte;
import com.nourl.streamsiteplayerremote.networking.events.AnswerEventArgs;
import com.nourl.streamsiteplayerremote.networking.events.ErrorEventArgs;
import com.nourl.streamsiteplayerremote.networking.events.InfoEventArgs;
import com.nourl.streamsiteplayerremote.networking.events.RequestEventArgs;
import com.nourl.streamsiteplayerremote.networking.messages.AnswerNetworkMessage;
import com.nourl.streamsiteplayerremote.networking.messages.InfoNetworkMessage;
import com.nourl.streamsiteplayerremote.networking.messages.NetworkMessage;
import com.nourl.streamsiteplayerremote.networking.messages.NetworkMessageType;
import com.nourl.streamsiteplayerremote.networking.messages.RequestNetworkMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Created by Jannes Peters on 20.10.2015.
 */
public class TcpNetworkInterface extends NetworkInterface {
    public final int MSG_MAX_SIZE = 512;

    protected InetSocketAddress inetSocketAddress = null;
    protected Socket socket;
    protected int timeout = 1000;

    private final Object socketLock = new Object();
    private Thread receiveThread;

    public TcpNetworkInterface(String ip, int port) {
        inetSocketAddress = new InetSocketAddress(ip, port);
    }

    public TcpNetworkInterface(String ip, int port, int timeout) {
        inetSocketAddress = new InetSocketAddress(ip, port);
        setTimeout(timeout);
    }

    //TODO add receiving code
    private void startReceiveLoop() {
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                synchronized (socketLock) {
                    if (socket != null && !socket.isClosed() && socket.isConnected()) {
                        try {
                            inputStream = socket.getInputStream();
                        } catch (IOException e) {
                            onNetworkError(new ErrorEventArgs());
                            return;
                            //TODO handle
                        }
                    }
                }
                while (true) {
                    if (inputStream != null) {
                        byte[] receiveBuffer = new byte[MSG_MAX_SIZE];
                        int bytesReceived = -1;
                        try {
                            bytesReceived = inputStream.read(receiveBuffer);
                        } catch (InterruptedIOException e) {
                            return;
                        } catch (IOException e) {
                            onNetworkError(new ErrorEventArgs());
                            return;
                            //TODO handle
                        }
                        if (bytesReceived == -1) {
                            Log.d("ERROR", "The input stream couldn't be read. End of stream reached, server disconnected.");
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                        NetworkMessageType msgType = NetworkMessageType.get(new UByte(receiveBuffer[0]));
                        if (msgType != null) {
                            UByte specificMsgType = new UByte(receiveBuffer[1]);
                            UByte id = new UByte(receiveBuffer[2]);
                            byte[] data = new byte[bytesReceived - 3];
                            System.arraycopy(receiveBuffer, 3, data, 0, data.length);
                            NetworkMessage netMsg;
                            switch (msgType) {
                                case ANSWER:
                                    netMsg = new AnswerNetworkMessage(specificMsgType, id, data);
                                    onNetworkAnswer(new AnswerEventArgs((AnswerNetworkMessage) netMsg));
                                    return;
                                case REQUEST:
                                    netMsg = new RequestNetworkMessage(specificMsgType, id, data);
                                    onNetworkRequest(new RequestEventArgs((RequestNetworkMessage) netMsg));
                                    return;
                                case INFO:
                                    netMsg = new InfoNetworkMessage(specificMsgType, id, data);
                                    onNetworkInfoMessage(new InfoEventArgs((InfoNetworkMessage) netMsg));
                                    break;
                            }
                        } else {
                            Log.e("ERROR", "Got invalid msgType: " + receiveBuffer[0]);
                        }
                    }
                }
            }
        });
        receiveThread.start();
    }

    private void stopReceiveLoop() {
        if (receiveThread != null && receiveThread.isAlive()) receiveThread.interrupt();
    }

    @Override
    public void sendMessage(final NetworkMessage message) {
        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream outputStream = null;
                synchronized (socketLock) {
                    if (socket != null && !socket.isClosed() && socket.isConnected()) {
                        try {
                            outputStream = socket.getOutputStream();
                        } catch (IOException e) {
                            stop();
                            onNetworkError(new ErrorEventArgs());
                            return;
                            //TODO handle
                        }
                    }
                }
                if (outputStream != null) {
                    if (message.getData().length > 0)
                        Log.d("SEND", Arrays.toString(message.getFullBytes()));
                    byte[] finalMsg = message.getFullBytes();
                    try {
                        outputStream.write(finalMsg);
                        outputStream.flush();
                        //now interrupt reading to send the message
                        stopReceiveLoop();
                        //and restart it, obviously
                        startReceiveLoop();
                    } catch (IOException e) {
                        stop();
                        onNetworkError(new ErrorEventArgs());
                        e.printStackTrace();
                    }
                }
            }
        });
        sendThread.start();
    }

    @Override
    public boolean isWorking() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (socketLock) {
                        if (socket == null || !socket.isConnected()) socket = new Socket();
                        socket.setKeepAlive(true);
                        socket.setReceiveBufferSize(MSG_MAX_SIZE);
                        socket.setSendBufferSize(MSG_MAX_SIZE);
                        socket.connect(inetSocketAddress, timeout);
                    }
                    startReceiveLoop();
                } catch (IOException e) {
                    onNetworkError(new ErrorEventArgs());
                    //TODO handle
                }
            }
        }).start();
    }

    @Override
    public void stop() {
        if (receiveThread != null && receiveThread.isAlive()) receiveThread.interrupt();
        try {
            synchronized (socketLock) {
                socket.close();
            }
        } catch (IOException ignored) { }
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
        if (socket != null) try {
            synchronized (socketLock) {
                socket.setSoTimeout(timeout);
            }
        } catch (SocketException ignored) { }
    }
}
