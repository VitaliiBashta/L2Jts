package ru.akumu.smartguard.wrappers;

import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.RawPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.ServerClose;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import ru.akumu.smartguard.core.manager.session.HWID;
import ru.akumu.smartguard.core.wrappers.ISmartClient;
import ru.akumu.smartguard.core.wrappers.ISmartPlayer;

import java.nio.ByteBuffer;
import java.security.InvalidParameterException;

/**
 * @author Akumu
 * @date 27.03.2016 12:25
 */
public class SmartClient extends ISmartClient {
    GameClient client;

    public SmartClient(GameClient client) {
        if (client == null)
            throw new InvalidParameterException();

        this.client = client;
    }

    @Override
    public NetworkStatus getConnectionStatus() {
        if (client.getConnection() == null || client.getConnection().getSocket() == null)
            return NetworkStatus.DISCONNECTED;

        switch (client.getState()) {
            case CONNECTED:
            case AUTHED:
                return NetworkStatus.CONNECTED;

            case IN_GAME:
                return NetworkStatus.IN_GAME;

            default:
                return NetworkStatus.DISCONNECTED;
        }
    }

    @Override
    public void setHWID(HWID hwid) {
        client.setHWID(hwid.plain);
    }

    @Override
    public void closeConnection(boolean b) {
        if (b)
            client.close(new ServerClose());
        else
            client.closeNow(true);
    }

    @Override
    public void sendRawPacket(ByteBuffer byteBuffer) {
        client.sendPacket(new RawPacket(byteBuffer));
    }

    @Override
    public void closeWithRawPacket(ByteBuffer byteBuffer) {
        client.close(new RawPacket(byteBuffer));
    }

    @Override
    public void sendHtml(String s) {
        HtmlMessage html = new HtmlMessage(5);
        html.setHtml(s);
        client.getActiveChar().sendPacket(html);
    }

    @Override
    public void sendMessage(String s) {
        client.sendPacket(new SystemMessage(SystemMsg.S1).addString(s));
    }

    @Override
    public String getAccountName() {
        return client.getLogin();
    }

    @Override
    public String getIpAddr() {
        try {
            return client.getConnection().getSocket().getInetAddress().getHostAddress();
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public ISmartPlayer getPlayer() {
        if (client.getActiveChar() == null)
            return null;

        return new SmartPlayer(this, client.getActiveChar());
    }
}
