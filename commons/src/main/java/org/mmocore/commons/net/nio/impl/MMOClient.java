package org.mmocore.commons.net.nio.impl;

import java.nio.ByteBuffer;

public abstract class MMOClient<T extends MMOConnection> {
    private T _connection;
    private boolean isAuthed;

    public MMOClient(final T con) {
        _connection = con;
    }

    public T getConnection() {
        return _connection;
    }

    protected void setConnection(final T con) {
        _connection = con;
    }

    public boolean isAuthed() {
        return isAuthed;
    }

    public void setAuthed(final boolean isAuthed) {
        this.isAuthed = isAuthed;
    }

    public void closeNow(final boolean error) {
        if (isConnected()) {
            _connection.closeNow();
        }
    }

    public void closeLater() {
        if (isConnected()) {
            _connection.closeLater();
        }
    }

    public boolean isConnected() {
        return _connection != null && !_connection.isClosed();
    }

    public abstract boolean decrypt(ByteBuffer buf, int size);

    public abstract boolean encrypt(ByteBuffer buf, int size);

    protected void onDisconnection() {
    }

    protected void onForcedDisconnection() {
    }
}