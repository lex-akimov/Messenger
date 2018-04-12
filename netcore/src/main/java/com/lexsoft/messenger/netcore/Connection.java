package com.lexsoft.messenger.netcore;

public interface Connection {
    void onNewConnection();

    void receiveMsg(String msg);

    void transmitMsg(String msg);

    void disconnect();
}
