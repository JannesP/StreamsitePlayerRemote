package com.nourl.streamsiteplayerremote.networking.tcp;

import com.nourl.streamsiteplayerremote.networking.NetworkInterface;
import com.nourl.streamsiteplayerremote.networking.messages.NetworkMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Jannes Peters on 20.10.2015.
 */
public class TcpNetworkInterface extends NetworkInterface {
    public final int MSG_MAX_SIZE = 512;

    protected InetSocketAddress inetSocketAddress = null;
    private final Object socketLock = new Object();
    protected Socket socket;
    protected int timeout;

    public TcpNetworkInterface(String ip, int port) {
        inetSocketAddress = new InetSocketAddress(ip, port);
    }

    //TODO add receiving code

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
                            e.printStackTrace();
                            //TODO handle
                        }
                    }
                }
                if (outputStream != null) {
                    byte[] finalMsg = message.getFullBytes();
                    try {
                        outputStream.write(finalMsg);
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        sendThread.run();
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
        if (socket != null) try {
            socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();
            //TODO handle
        }
    }

    @Override
    public boolean isReady() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            socket.setKeepAlive(true);
            socket.setReceiveBufferSize(MSG_MAX_SIZE);
            socket.setSendBufferSize(MSG_MAX_SIZE);
            socket.connect(inetSocketAddress, timeout);
        } catch (IOException e) {
            e.printStackTrace();
            //TODO handle
        }
    }
}
