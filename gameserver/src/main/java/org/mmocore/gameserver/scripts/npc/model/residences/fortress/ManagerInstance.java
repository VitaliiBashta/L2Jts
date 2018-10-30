package org.mmocore.gameserver.scripts.npc.model.residences.fortress;

import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.npc.model.residences.ResidenceManager;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.HtmlUtils;

import java.time.Duration;
import java.time.ZonedDateTime;

public class ManagerInstance extends ResidenceManager {
    private static final long REWARD_CYCLE = 6 * 60 * 60; // каждых 6 часов

    public ManagerInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void setDialogs() {
        _mainDialog = "residence2/fortress/fortress_steward001.htm";
        _failDialog = "residence2/fortress/fortress_steward002.htm";
        _siegeDialog = "residence2/fortress/fortress_steward018.htm";
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if ("receive_report".equalsIgnoreCase(command)) {
            final Duration ownedTime = Duration.between(ZonedDateTime.now(), getFortress().getOwnDate());

            final HtmlMessage html = new HtmlMessage(this);

            final Fortress fortress = getFortress();
            if (fortress.getContractState() == Fortress.CONTRACT_WITH_CASTLE) {
                html.setFile("residence2/fortress/fortress_steward022.htm");
                html.replace("%castle_name%", HtmlUtils.htmlResidenceName(getFortress().getCastleId()));
                html.replace("%contract%", NpcString.CONTRACT_STATE);

                final long leftTime = (REWARD_CYCLE - (3600 - fortress.getCycleDelay()) - fortress.getPaidCycle() * 3600) / 60;

                html.replace("%rent_cost%", String.valueOf(Fortress.CASTLE_FEE));
                html.replace("%next_hour%", String.valueOf(leftTime / 60));
                html.replace("%next_min%", String.valueOf(leftTime % 60));
            } else {
                html.setFile("residence2/fortress/fortress_steward023.htm");
            }

            html.replace("%time_remained%", NpcString.S1HOUR_S2MINUTE, ownedTime.toHours(), ownedTime.toMinutes());

            player.sendPacket(html);
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    protected int getCond(final Player player) {
        final DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);

        final Residence residence = getResidence();
        final Clan residenceOwner = residence.getOwner();
        if (residenceOwner != null && player.getClan() == residenceOwner) {
            if (residence.getSiegeEvent().isInProgress() || runnerEvent.isInProgress()) {
                return COND_SIEGE;
            } else {
                return COND_OWNER;
            }
        } else {
            return COND_FAIL;
        }
    }

    @Override
    protected Residence getResidence() {
        return getFortress();
    }

    @Override
    public L2GameServerPacket decoPacket() {
        return null;
    }

    @Override
    protected int getPrivUseFunctions() {
        return Clan.CP_CS_USE_FUNCTIONS;
    }

    @Override
    protected int getPrivSetFunctions() {
        return Clan.CP_CS_SET_FUNCTIONS;
    }

    @Override
    protected int getPrivDismiss() {
        return Clan.CP_CS_DISMISS;
    }

    @Override
    protected int getPrivDoors() {
        return Clan.CP_CS_ENTRY_EXIT;
    }
}