package org.mmocore.authserver.network.gamecomm;

import org.mmocore.authserver.configuration.config.ServerNamesConfig;
import org.mmocore.commons.net.utils.NetInfo;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class GameServer {

    public Map<Integer, Entry> entries = new LinkedHashMap<>();
    private int _serverType;
    private int _ageLimit;
    private int _protocol;
    private boolean _isOnline;
    private boolean _isPvp;
    private boolean _isShowingBrackets;
    private boolean _isGmOnly;

    private int _maxPlayers;

    private GameServerConnection _conn;
    private boolean _isAuthed;

    private Set<String> _accounts = new CopyOnWriteArraySet<>();
    public GameServer() {
    }

    public GameServer(GameServerConnection conn) {
        _conn = conn;
    }

    public boolean isAuthed() {
        return _isAuthed;
    }

    public void setAuthed(boolean isAuthed) {
        _isAuthed = isAuthed;
    }

    public GameServerConnection getConnection() {
        return _conn;
    }

    public void setConnection(GameServerConnection conn) {
        _conn = conn;
    }

    public int getMaxPlayers() {
        return _maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        _maxPlayers = maxPlayers;
    }

    public int getOnline() {
        return _accounts.size();
    }

    public Set<String> getAccounts() {
        return _accounts;
    }

    public void addAccount(String account) {
        _accounts.add(account);
    }

    public void removeAccount(String account) {
        _accounts.remove(account);
    }

    public void setDown() {
        setAuthed(false);
        setConnection(null);
        setOnline(false);

        _accounts.clear();
    }

    public void sendPacket(SendablePacket packet) {
        GameServerConnection conn = getConnection();
        if (conn != null) {
            conn.sendPacket(packet);
        }
    }

    public int getServerType() {
        return _serverType;
    }

    public void setServerType(int serverType) {
        _serverType = serverType;
    }

    public boolean isOnline() {
        return _isOnline;
    }

    public void setOnline(boolean online) {
        _isOnline = online;
    }

    public boolean isPvp() {
        return _isPvp;
    }

    public void setPvp(boolean pvp) {
        _isPvp = pvp;
    }

    public boolean isShowingBrackets() {
        return _isShowingBrackets;
    }

    public void setShowingBrackets(boolean showingBrackets) {
        _isShowingBrackets = showingBrackets;
    }

    public boolean isGmOnly() {
        return _isGmOnly;
    }

    public void setGmOnly(boolean gmOnly) {
        _isGmOnly = gmOnly;
    }

    public int getAgeLimit() {
        return _ageLimit;
    }

    public void setAgeLimit(int ageLimit) {
        _ageLimit = ageLimit;
    }

    public int getProtocol() {
        return _protocol;
    }

    public void setProtocol(int protocol) {
        _protocol = protocol;
    }

    public static class Entry {
        public final int id;
        public List<NetInfo> infos = new ArrayList<>(5);

        public Entry(int id) {
            this.id = id;
        }

        public String getName() {
            return ServerNamesConfig.SERVER_NAMES.get(id);
        }

        public int getId() {
            return id;
        }
    }
}