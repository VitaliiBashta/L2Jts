package org.mmocore.authserver.network.gamecomm.gs2as;

import org.mmocore.authserver.manager.GameServerManager;
import org.mmocore.authserver.network.gamecomm.GameServer;
import org.mmocore.authserver.network.gamecomm.ReceivablePacket;
import org.mmocore.authserver.network.gamecomm.as2gs.AuthResponse;
import org.mmocore.authserver.network.gamecomm.as2gs.LoginServerFail;
import org.mmocore.commons.net.utils.Net;
import org.mmocore.commons.net.utils.NetInfo;
import org.mmocore.commons.net.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuthRequest extends ReceivablePacket {
    private final static Logger _log = LoggerFactory.getLogger(AuthRequest.class);
    private int _protocolVersion;
    private int requestId;
    private boolean acceptAlternateID;
    private int port;
    private int maxOnline;
    private int _serverType;
    private int _ageLimit;
    private boolean _gmOnly;
    private boolean _brackets;
    private boolean _pvp;
    private List<NetInfo> _netInfos = new ArrayList<>();

    @Override
    protected void readImpl() {
        _protocolVersion = readD();
        if (_protocolVersion == 2) {
            requestId = readC();
            acceptAlternateID = readC() == 1;
            _serverType = readD();
            _ageLimit = readD();
            _gmOnly = readC() == 1;
            _brackets = readC() == 1;
            _pvp = readC() == 1;
            String externalIp = readS();
            String internalIp = readS();
            // Количество портов, всегда 1.
            readH();
            port = readH();
            maxOnline = readD();
            _netInfos.addAll(NetUtils.PRIVATE.stream().map(net -> new NetInfo(net, internalIp, port)).collect(Collectors.toList()));
            _netInfos.add(new NetInfo(Net.valueOf("*.*.*.*"), externalIp, port));
        } else {
            requestId = readC();
            acceptAlternateID = readC() == 1;
            _serverType = readD();
            _ageLimit = readD();
            _gmOnly = readC() == 1;
            _brackets = readC() == 1;
            _pvp = readC() == 1;
            maxOnline = readD();
            int size = readD();
            for (int i = 0; i < size; i++) {
                int netmask = readD();
                int address = readD();
                String host = readS();
                // Количество портов, всегда 1.
                readH();
                port = readH();
                _netInfos.add(new NetInfo(new Net(address, netmask), host, port));
            }
        }
    }

    @Override
    protected void runImpl() {
        _log.info("Trying to register gameserver: " + requestId + " [" + getGameServer().getConnection().getIpAddress() + "]");

        int failReason = 0;

        GameServer gs = getGameServer();
        GameServer.Entry entry = GameServerManager.getInstance().registerGameServer(requestId, gs, _netInfos);
        if (entry != null) {
            gs.setMaxPlayers(maxOnline);
            gs.setPvp(_pvp);
            gs.setServerType(_serverType);
            gs.setShowingBrackets(_brackets);
            gs.setGmOnly(_gmOnly);
            gs.setAgeLimit(_ageLimit);
            gs.setProtocol(_protocolVersion);
            gs.setAuthed(true);

            gs.getConnection().stopPingTask();
            gs.getConnection().startPingTask();
        } else if (acceptAlternateID) {
            if ((entry = GameServerManager.getInstance().registerGameServer(gs = getGameServer(), _netInfos)) != null) {
                gs.setMaxPlayers(maxOnline);
                gs.setPvp(_pvp);
                gs.setServerType(_serverType);
                gs.setShowingBrackets(_brackets);
                gs.setGmOnly(_gmOnly);
                gs.setAgeLimit(_ageLimit);
                gs.setProtocol(_protocolVersion);
                gs.setAuthed(true);

                gs.getConnection().stopPingTask();
                gs.getConnection().startPingTask();
            } else {
                failReason = LoginServerFail.REASON_NO_FREE_ID;
            }
        } else {
            failReason = LoginServerFail.REASON_ID_RESERVED;
        }

        if (failReason != 0) {
            _log.info("Gameserver registration failed.");
            sendPacket(new LoginServerFail(failReason));
            return;
        }

        _log.info("Gameserver registration successful.");
        sendPacket(new AuthResponse(entry.getId(), entry.getName()));
    }
}
