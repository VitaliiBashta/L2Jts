package org.mmocore.gameserver.network.authcomm.as2gs;


import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServerFail extends ReceivablePacket {
    private static final Logger _log = LoggerFactory.getLogger(LoginServerFail.class);

    private static final String[] reasons = {
            "none",
            "IP banned",
            "IP reserved",
            "wrong hexid",
            "ID reserved",
            "no free ID",
            "not authed",
            "already logged in"
    };
    private int _reason;

    public String getReason() {
        return reasons[_reason];
    }

    @Override
    protected void readImpl() {
        _reason = readC();
    }

    protected void runImpl() {
        _log.warn("Authserver registration failed! Reason: " + getReason());
        AuthServerCommunication.getInstance().restart();
    }
}