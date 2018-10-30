package org.mmocore.gameserver.network.authcomm.as2gs;

import org.mmocore.gameserver.configuration.config.community.CServiceConfig;
import org.mmocore.gameserver.database.dao.impl.AccountBonusDAO;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.ReceivablePacket;
import org.mmocore.gameserver.network.authcomm.SessionKey;
import org.mmocore.gameserver.network.authcomm.gs2as.PlayerInGame;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.CharacterSelectionInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.LoginFail;
import org.mmocore.gameserver.network.lineage.serverpackets.ServerClose;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.premium.PremiumBonus;

public class PlayerAuthResponse extends ReceivablePacket {
    private String account;
    private String passwordHash;
    private boolean authed;
    private int playOkId1;
    private int playOkId2;
    private int loginOkId1;
    private int loginOkId2;
    private double xp;
    private double sp;
    private double adena;
    private double drop;
    private double spoil;
    private double epaulette;

    private long bonusExpire;
    private int accountId;

    @Override
    public void readImpl() {
        account = readS();
        authed = readC() == 1;
        if (authed) {
            playOkId1 = readD();
            playOkId2 = readD();
            loginOkId1 = readD();
            loginOkId2 = readD();
            passwordHash = readS();
            xp = readF();
            sp = readF();
            adena = readF();
            drop = readF();
            spoil = readF();
            epaulette = readF();
            bonusExpire = readQ();
            accountId = readD();
        }
    }

    @Override
    protected void runImpl() {
        final SessionKey skey = new SessionKey(loginOkId1, loginOkId2, playOkId1, playOkId2);
        final GameClient client = AuthServerCommunication.getInstance().removeWaitingClient(account);
        if (client == null) {
            return;
        }

        if (authed && client.getSessionKey().equals(skey)) {
            client.setAuthed(true);
            client.setState(GameClient.GameClientState.AUTHED);
            final PremiumBonus premiumBonus;
            switch (CServiceConfig.rateBonusType) {
                case PremiumBonus.BONUS_GLOBAL_ON_AUTHSERVER: {
                    premiumBonus = new PremiumBonus();
                    premiumBonus.setRateXp(xp);
                    premiumBonus.setRateSp(sp);
                    premiumBonus.setDropAdena(adena);
                    premiumBonus.setDropItems(drop);
                    premiumBonus.setDropSpoil(spoil);
                    premiumBonus.setBonusEpaulette(epaulette);
                    premiumBonus.setBonusExpire(bonusExpire);
                    break;
                }
                case PremiumBonus.BONUS_GLOBAL_ON_GAMESERVER: {
                    premiumBonus = AccountBonusDAO.getInstance().select(account);
                    break;
                }
                default: {
                    premiumBonus = new PremiumBonus();
                    break;
                }
            }
            client.setPremiumBonus(premiumBonus);
            client.setAccountId(accountId);
            client.setPasswordHash(passwordHash);
            final GameClient oldClient = AuthServerCommunication.getInstance().addAuthedClient(client);
            if (oldClient != null) {
                oldClient.setAuthed(false);
                final Player activeChar = oldClient.getActiveChar();
                if (activeChar != null) {
                    //FIXME [G1ta0] сообщение чаще всего не показывается, т.к. при закрытии соединения очередь на отправку очищается
                    activeChar.sendPacket(SystemMsg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
                    activeChar.logout();
                } else {
                    oldClient.close(ServerClose.STATIC);
                }
            }

            sendPacket(new PlayerInGame(client.getLogin()));

            final CharacterSelectionInfo csi = new CharacterSelectionInfo(client.getLogin(), client.getSessionKey().playOkID1);
            client.sendPacket(csi);
            client.setCharSelection(csi.getCharInfo());
        } else {
            client.close(new LoginFail(LoginFail.ACCESS_FAILED_TRY_LATER));
        }
    }
}