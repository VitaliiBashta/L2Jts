package org.mmocore.gameserver.network.lineage;

import org.jts.protection.network.ProtectionGameCrypt;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.net.nio.impl.MMOClient;
import org.mmocore.commons.net.nio.impl.MMOConnection;
import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.client.holder.NpcNameLineHolder;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.model.CharSelectInfoPackage;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.SessionKey;
import org.mmocore.gameserver.network.authcomm.gs2as.PlayerLogout;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.premium.PremiumBonus;
import org.mmocore.gameserver.utils.AutoBan;
import org.mmocore.gameserver.utils.Util;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Represents a client connected on Game Server
 */
public final class GameClient extends MMOClient<MMOConnection<GameClient>> {
    private static final Logger _log = LoggerFactory.getLogger(GameClient.class);
    private static final String NO_IP = "?.?.?.?";

    public ProtectionGameCrypt _crypt = null;

    public GameClientState _state;
    /**
     * Данные аккаунта
     */
    private String _login;
    private String passwordHash;
    private String HWID;
    private PremiumBonus premiumBonus = new PremiumBonus();
    private int _accountId = -1;
    private Player _activeChar;
    private SessionKey _sessionKey;
    private String _ip = NO_IP;
    private int revision = 0;
    private Chronicle chronicle;
    private int _selectedIndex = -1;
    private int languageType = 0;
    private int lastPing = 0;
    private CharSelectInfoPackage[] _players = new CharSelectInfoPackage[7];
    private int _failedPackets = 0;
    private int _unknownPackets = 0;

    public GameClient(final MMOConnection<GameClient> con) {
        super(con);

        _state = GameClientState.CONNECTED;
        _crypt = new ProtectionGameCrypt();
        _ip = con.getSocket().getInetAddress().getHostAddress();
    }

    @Override
    protected void onDisconnection() {
        final Player player;
        setState(GameClientState.DISCONNECTED);
        player = getActiveChar();
        setActiveChar(null);

        if (player != null) {
            player.setNetConnection(null);
            player.scheduleDelete();
        }

        if (getSessionKey() != null) {
            if (isAuthed()) {
                AuthServerCommunication.getInstance().removeAuthedClient(getLogin());
                AuthServerCommunication.getInstance().sendPacket(new PlayerLogout(getLogin()));
            } else {
                AuthServerCommunication.getInstance().removeWaitingClient(getLogin());
            }
        }
    }

    @Override
    protected void onForcedDisconnection() {
        if (_activeChar != null)
            _activeChar.getListeners().onForcedDisconnect();
    }

    public void markRestoredChar(final int charslot) throws Exception {
        final int objid = getObjectIdByIndex(charslot);
        if (objid < 0) {
            return;
        }

        if (_activeChar != null && _activeChar.getObjectId() == objid) {
            _activeChar.setDeleteTimer(0);
        }

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE characters SET deletetime=0 WHERE obj_id=?");
            statement.setInt(1, objid);
            statement.execute();
        } catch (Exception e) {
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void markToDeleteChar(final int charslot) throws Exception {
        final int objid = getObjectIdByIndex(charslot);
        if (objid < 0) {
            return;
        }

        if (_activeChar != null && _activeChar.getObjectId() == objid) {
            _activeChar.setDeleteTimer((int) (System.currentTimeMillis() / 1000));
        }

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE characters SET deletetime=? WHERE obj_id=?");
            statement.setLong(1, (int) (System.currentTimeMillis() / 1000L));
            statement.setInt(2, objid);
            statement.execute();
        } catch (Exception e) {
            _log.error("data error on update deletime char:", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void deleteChar(final int charslot) throws Exception {
        //have to make sure active character must be nulled
        if (_activeChar != null) {
            return;
        }

        final int objid = getObjectIdByIndex(charslot);
        if (objid == -1) {
            return;
        }

        CharacterDAO.getInstance().deleteCharByObjId(objid);
    }

    public int getObjectIdByIndex(final int charslot) {
        if (charslot < 0 || charslot >= _players.length) {
            _log.warn(getLogin() + " tried to modify Character in slot " + charslot + " but no characters exits at that slot.");
            return -1;
        }
        final CharSelectInfoPackage p = _players[charslot];
        return p == null ? 0 : p.getObjectId();
    }

    public Player getActiveChar() {
        return _activeChar;
    }

    public void setActiveChar(final Player player) {
        _activeChar = player;
        if (player != null) {
            player.setNetConnection(this);
        }
    }

    /**
     * @return Returns the sessionId.
     */
    public SessionKey getSessionKey() {
        return _sessionKey;
    }

    public String getLogin() {
        return _login;
    }

    public void setLoginName(final String loginName) {
        _login = loginName;
    }
    //Protection section end

    //Protection section
    public String getHWID() {
        return HWID;
    }

    public void setHWID(final String HWID) {
        this.HWID = HWID;
    }

    public void setSessionId(final SessionKey sessionKey) {
        _sessionKey = sessionKey;
    }

    public void setCharSelection(final CharSelectInfoPackage[] chars) {
        _players = chars;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(final int revision) {
        this.revision = revision;
    }

    public Chronicle getChronicle() {
        return chronicle;
    }

    public void setChronicle(Chronicle chronicle) {
        this.chronicle = chronicle;
    }

    public void playerSelected(final int index) {
        final int objectIdByIndex = getObjectIdByIndex(index);
        if (objectIdByIndex <= 0 || getActiveChar() != null || AutoBan.isBanned(objectIdByIndex)) {
            sendPacket(ActionFail.STATIC);
            return;
        }

        _selectedIndex = index;
        final CharSelectInfoPackage info = _players[index];
        if (info == null) {
            return;
        }

        if (ExtConfig.EX_CHANGE_NAME_DIALOG && info.getAccessLevel() <= 0) {
            if (!Util.isMatchingRegexp(info.getName(), ServerConfig.CNAME_TEMPLATE) || NpcNameLineHolder.getInstance().isBlackListContainsName(info.getName())) {
                sendPacket(new ExNeedToChangeName(ExNeedToChangeName.TYPE_PLAYER_NAME, ExNeedToChangeName.REASON_INVALID, info.getName()));
                return;
            } else if (CharacterDAO.getInstance().getPlayersCountByName(info.getName()) > 1) {
                sendPacket(new ExNeedToChangeName(ExNeedToChangeName.TYPE_PLAYER_NAME, ExNeedToChangeName.REASON_EXISTS, info.getName()));
                return;
            }
        }

        if (ExtConfig.EX_2ND_PASSWORD_CHECK) {
            if (info.isPasswordEnable()) {
                if (!info.isPasswordChecked()) {
                    sendPacket(info.getPassword() == null ? Ex2ndPasswordCheck.PASSWORD_NEW : Ex2ndPasswordCheck.PASSWORD_PROMPT);
                    return;
                }
            }
        }

        final CharSelectInfoPackage[] array = getPlayers();
        Player inGamePlayer = null;
        for (int i = 0; i < array.length; i++) {
            final CharSelectInfoPackage p = array[i];
            final Player player = p != null ? GameObjectsStorage.getPlayer(p.getObjectId()) : null;
            if (player != null) {
                // если у нас чар в оффлайне, или выходит, и этот чар выбран - кикаем
                if (player.isInOfflineMode() || player.isLogoutStarted()) {
                    if (index == i) {
                        player.kick();
                    }
                } else {
                    player.sendPacket(SystemMsg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);

                    // если есть чар
                    // если это выбраный - обнуляем конект и используем, иначе кикаем)
                    if (index == i) {
                        final GameClient oldClient = player.getNetConnection();
                        if (oldClient != null) {
                            oldClient.setActiveChar(null);
                            oldClient.closeNow(false);
                        }
                        if (player.isNoCarrier() && player.getNoCarrierSchedule() != null && player.getNoCarrierSchedule().getDelay(TimeUnit.SECONDS) > 5) {
                            inGamePlayer = player;
                            inGamePlayer.setNetConnection(getConnection().getClient());
                            inGamePlayer.entering = true;
                        }
                    } else {
                        player.logout();
                    }
                }
            }
        }

        final Player selectedPlayer = inGamePlayer != null ? inGamePlayer : Player.restore(objectIdByIndex);
        if (selectedPlayer == null) {
            sendPacket(ActionFail.STATIC);
            return;
        }

        if (selectedPlayer.getAccessLevel() < 0) {
            selectedPlayer.setAccessLevel(0);
        }
        selectedPlayer.setPasswordHash(getPasswordHash());
        setActiveChar(selectedPlayer);
        setState(GameClientState.IN_GAME);

        sendPacket(new CharSelected(selectedPlayer, getSessionKey().playOkID1));
    }

    @Override
    public boolean encrypt(final ByteBuffer buf, final int size) {
        _crypt.encrypt(buf.array(), buf.position(), size);
        buf.position(buf.position() + size);
        return true;
    }

    @Override
    public boolean decrypt(final ByteBuffer buf, final int size) {
        return _crypt.decrypt(buf.array(), buf.position(), size);
    }

    public void sendPacket(final L2GameServerPacket gsp) {
        if (isConnected()) {
            getConnection().sendPacket(gsp);
        }
    }

    public void sendPacket(final L2GameServerPacket... gsp) {
        if (isConnected()) {
            getConnection().sendPacket(gsp);
        }
    }

    public void sendPackets(final List<L2GameServerPacket> gsp) {
        if (isConnected()) {
            getConnection().sendPackets(gsp);
        }
    }

    public void close(final L2GameServerPacket gsp) {
        if (isConnected()) {
            getConnection().close(gsp);
        }
    }

    public String getIpAddr() {
        return _ip;
    }

    public byte[] enableCrypt() {
        final byte[] key = BlowFishKeygen.getRandomKey();
        _crypt.setKey(key);
        return key;
    }

    public int getAccountId() {
        return _accountId;
    }

    public void setAccountId(final int id) {
        _accountId = id;
    }

    public PremiumBonus getPremiumBonus() {
        return this.premiumBonus;
    }

    public void setPremiumBonus(final PremiumBonus premiumBonus) {
        this.premiumBonus = premiumBonus;
    }

    public GameClientState getState() {
        return _state;
    }

    public void setState(final GameClientState state) {
        _state = state;
    }

    public void onPacketReadFail() {
        if (_failedPackets++ >= 10) {
            _log.warn("Too many client packet fails, connection closed : " + this);
            closeNow(true);
        }
    }

    public void onUnknownPacket() {
        if (_unknownPackets++ >= 10) {
            _log.warn("Too many client unknown packets, connection closed : " + this);
            closeNow(true);
        }
    }

    @Override
    public String toString() {
        return _state + " IP: " + getIpAddr() + (_login == null ? "" : " Account: " + _login) + (_activeChar == null ? "" : " Player : " + _activeChar);
    }

    public CharSelectInfoPackage[] getPlayers() {
        return _players;
    }

    public int getSelectedIndex() {
        return _selectedIndex;
    }

    public int getLanguageType() {
        return languageType;
    }

    public void setLanguageType(int languageType) {
        this.languageType = languageType;
    }

    public int getLastPing() {
        return lastPing;
    }

    public void setLastPing(final int lastPing) {
        this.lastPing = lastPing;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public enum GameClientState {
        CONNECTED,
        AUTHED,
        IN_GAME,
        DISCONNECTED
    }
}