package org.mmocore.gameserver.scripts.ai;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.CommandChannel;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillList;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;

/**
 * Created by Hack
 * Date: 28.08.2016 5:49
 * This custom AI grants noble status to killer and all his members in party or command channel
 */
public class Barakiel extends Fighter {
    public Barakiel(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        setNoblesse(killer);
        super.onEvtDead(killer);
    }

    private void setNoblesse(Creature killer) {
        if (killer == null)
            return;
        Player player = killer.getPlayer();
        if (player == null)
            return;
        Party party = player.getParty();
        if (party != null && party.getCommandChannel() != null)
            setNobleCommandChannel(party.getCommandChannel());
        else if (party != null)
            setNobleParty(party);
        else
            setNoblePlayer(player);
    }

    private void setNoblePlayer(Player player) {
        if (player.isNoble())
            return;
        Olympiad.addNoble(player);
        player.setNoble(true);
        player.updatePledgeClass();
        player.updateNobleSkills();
        player.sendPacket(new SkillList(player));
        player.broadcastUserInfo(true);
    }

    private void setNobleParty(Party party) {
        for (Player member : party.getPartyMembers())
            setNoblePlayer(member);
    }

    private void setNobleCommandChannel(CommandChannel channel) {
        for (Party party : channel.getParties())
            setNobleParty(party);
    }
}
