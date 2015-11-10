package com.nourl.streamsiteplayerremote.networking.tcp;

import java.io.IOException;

/**
 * Created by Jannes Peters on 10.11.2015.
 */
public class EndOfStreamException extends IOException {
    public EndOfStreamException() {
        super("The end of stream was reached. (InputStream.read() returned -1)");
    }
}
