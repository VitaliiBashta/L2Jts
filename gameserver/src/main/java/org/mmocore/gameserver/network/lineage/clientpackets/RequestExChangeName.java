package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.client.holder.NpcNameLineHolder;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.model.CharSelectInfoPackage;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.serverpackets.ExNeedToChangeName;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeShowMemberListDeleteAll;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.utils.Util;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.List;

/**
 * @author VISTALL
 */
public class RequestExChangeName extends L2GameClientPacket {
    private int _type;
    private String _name;

    @Override
    protected void readImpl() {
        _type = readD();
        _name = readS();
        //readD();
    }

    @Override
    protected void runImpl() {
        if (!ExtConfig.EX_CHANGE_NAME_DIALOG) {
            return;
        }

        final GameClient client = getClient();
        boolean canReName = false;
        switch (_type) {
            case ExNeedToChangeName.TYPE_PLAYER_NAME:
                if (client.getActiveChar() != null) {
                    return;
                }

                if (!Util.isMatchingRegexp(_name, ServerConfig.CNAME_TEMPLATE)
                        || NpcNameLineHolder.getInstance().isBlackListContainsName(_name)
                        || !Util.checkIsAllowedName(_name)) {
                    sendPacket(new ExNeedToChangeName(ExNeedToChangeName.TYPE_PLAYER_NAME, ExNeedToChangeName.REASON_INVALID, _name));
                    return;
                } else if (CharacterDAO.getInstance().getPlayersCountByName(_name) >= 1) {
                    sendPacket(new ExNeedToChangeName(ExNeedToChangeName.TYPE_PLAYER_NAME, ExNeedToChangeName.REASON_EXISTS, _name));
                    return;
                }

                final CharSelectInfoPackage p = ArrayUtils.valid(client.getPlayers(), client.getSelectedIndex());
                if (p == null) {
                    return;
                }

                canReName = !Util.isMatchingRegexp(p.getName(), ServerConfig.CNAME_TEMPLATE) || CharacterDAO.getInstance().getPlayersCountByName(p.getName()) > 1 || NpcNameLineHolder.getInstance().isBlackListContainsName(p.getName());
                if (!canReName) {
                    return;
                }

                p.setName(_name);

                final Player pPlayer = GameObjectsStorage.getPlayer(p.getObjectId());
                if (pPlayer != null) {
                    pPlayer.reName(_name, false);
                }

                CharacterDAO.getInstance().updateName(p.getObjectId(), _name);

                client.playerSelected(client.getSelectedIndex());
                break;
            case ExNeedToChangeName.TYPE_CLAN_NAME:
                final Player player = client.getActiveChar();
                if (player == null) {
                    return;
                }
                final Clan clan = player.getClan();
                if (clan == null || clan.getLeaderId(Clan.SUBUNIT_MAIN_CLAN) != player.getObjectId()) {
                    return;
                }

                final String name = clan.getUnitName(Clan.SUBUNIT_MAIN_CLAN);
                if (!Util.isMatchingRegexp(_name, ServerConfig.CLAN_NAME_TEMPLATE)) {
                    sendPacket(new ExNeedToChangeName(ExNeedToChangeName.TYPE_CLAN_NAME, ExNeedToChangeName.REASON_INVALID, _name));
                    return;
                } else if (ClanTable.getInstance().getClanByName(_name) != null) {
                    sendPacket(new ExNeedToChangeName(ExNeedToChangeName.TYPE_CLAN_NAME, ExNeedToChangeName.REASON_EXISTS, _name));
                    return;
                }

                canReName = !Util.isMatchingRegexp(name, ServerConfig.CLAN_NAME_TEMPLATE) || ClanTable.getInstance().getClansSizeByName(name) > 1;
                if (!canReName) {
                    return;
                }

                final SubUnit subUnit = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
                subUnit.setName(_name, true);

                final List<L2GameServerPacket> packets = clan.listAll();
                for (final Player $player : clan.getOnlineMembers(0)) {
                    $player.sendPacket(PledgeShowMemberListDeleteAll.STATIC);
                    $player.sendPacket(packets);
                }
                break;
        }
    }
}