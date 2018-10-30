package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.database.dao.impl.SiegeClanDAO;
import org.mmocore.gameserver.database.dao.impl.SiegePlayerDAO;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.mmocore.gameserver.model.pledge.Privilege;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExReplyRegisterDominion;
import org.mmocore.gameserver.object.Player;

import java.time.ZonedDateTime;

/**
 * @author VISTALL
 * Скорее всего, мессаджи о том, что клан регнулся на доминион, шлются овнерам кастла, но никак не активному клану. Судя по РПГ клабу.
 */
public class RequestExJoinDominionWar extends L2GameClientPacket {
    private int _dominionId;
    private boolean _clanRegistration;
    private boolean _isRegistration;

    @Override
    protected void readImpl() {
        _dominionId = readD();
        _clanRegistration = readD() == 1;
        _isRegistration = readD() == 1;
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final Dominion dominion = ResidenceHolder.getInstance().getResidence(Dominion.class, _dominionId);
        if (dominion == null) {
            return;
        }

        final DominionSiegeEvent siegeEvent = dominion.getSiegeEvent();
        if (player.isGM() && dominion.getSiegeDate().isBefore(ZonedDateTime.now())) {
            player.sendMessage("RegTime: " + dominion.getSiegeDate().toString());
        } else if (player.isGM() && siegeEvent.isRegistrationOver()) {
            player.sendMessage("RegisterOver: " + dominion.getSiegeDate().toString());
        }

        if (siegeEvent.isRegistrationOver() || dominion.getSiegeDate().isBefore(ZonedDateTime.now())) {
            player.sendPacket(SystemMsg.IT_IS_NOT_A_TERRITORY_WAR_REGISTRATION_PERIOD_SO_A_REQUEST_CANNOT_BE_MADE_AT_THIS_TIME);
            return;
        }

        if (player.getClan() != null && player.getClan().getCastle() > 0) {
            player.sendPacket(SystemMsg.THE_CLAN_WHO_OWNS_THE_TERRITORY_CANNOT_PARTICIPATE_IN_THE_TERRITORY_WAR_AS_MERCENARIES);
            return;
        }

        if (player.getLevel() < 40 || player.getPlayerClassComponent().getClassId().getLevel().ordinal() <= 2) {
            player.sendPacket(SystemMsg.ONLY_CHARACTERS_WHO_ARE_LEVEL_40_OR_ABOVE_WHO_HAVE_COMPLETED_THEIR_SECOND_CLASS_TRANSFER_CAN_REGISTER_IN_A_TERRITORY_WAR);
            return;
        }

        int playerReg = 0;
        int clanReg = 0;
        for (final Dominion d : ResidenceHolder.getInstance().getResidenceList(Dominion.class)) {
            final DominionSiegeEvent dominionSiegeEvent = d.getSiegeEvent();
            if (dominionSiegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).contains(player.getObjectId())) {
                playerReg = d.getId();
            }

            if (dominionSiegeEvent.getSiegeClan(DominionSiegeEvent.DEFENDERS, player.getClan()) != null) {
                clanReg = d.getId();
            }
        }

        if (_isRegistration) {
            // если клан уже где то зареган
            if (clanReg > 0) {
                player.sendPacket(SystemMsg.YOUVE_ALREADY_REQUESTED_A_TERRITORY_WAR_IN_ANOTHER_TERRITORY_ELSEWHERE);
                return;
            }

            // если регаемся как наемник, по кланова/одиночнача рега в наличии
            if (!_clanRegistration && (clanReg > 0 || playerReg > 0)) {
                player.sendPacket(SystemMsg.YOUVE_ALREADY_REQUESTED_A_TERRITORY_WAR_IN_ANOTHER_TERRITORY_ELSEWHERE);
                return;
            }

            if (_clanRegistration) {
                if (!player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR)) {
                    player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                    return;
                }

                final SiegeClanObject object = new SiegeClanObject(DominionSiegeEvent.DEFENDERS, player.getClan(), 0);
                siegeEvent.addObject(DominionSiegeEvent.DEFENDERS, object);
                SiegeClanDAO.getInstance().insert(dominion, object);
            } else {
                siegeEvent.addObject(DominionSiegeEvent.DEFENDER_PLAYERS, player.getObjectId());
                SiegePlayerDAO.getInstance().insert(dominion, 0, player.getObjectId());
            }
        } else {
            if (_clanRegistration && clanReg != dominion.getId()) {
                return;
            }
            if (!_clanRegistration && playerReg != dominion.getId()) {
                return;
            }

            if (_clanRegistration) {
                if (!player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR)) {
                    player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                    return;
                }

                final SiegeClanObject clanObject = siegeEvent.getSiegeClan(DominionSiegeEvent.DEFENDERS, player.getClan());
                siegeEvent.removeObject(DominionSiegeEvent.DEFENDERS, clanObject);
                SiegeClanDAO.getInstance().delete(dominion, clanObject);
            } else {
                siegeEvent.removeObject(DominionSiegeEvent.DEFENDER_PLAYERS, player.getObjectId());
                SiegePlayerDAO.getInstance().delete(dominion, 0, player.getObjectId());
            }
        }

        player.sendPacket(new ExReplyRegisterDominion(dominion, true, _isRegistration, _clanRegistration));
    }
}