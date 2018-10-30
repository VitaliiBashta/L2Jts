package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.events.impl.UndergroundColiseumBattleEvent;
import org.mmocore.gameserver.model.entity.events.objects.UCTeamObject;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author VISTALL
 */
public class ExPVPMatchUserDie extends GameServerPacket {
    private final int blueKills, redKills;

    public ExPVPMatchUserDie(UndergroundColiseumBattleEvent e) {
        UCTeamObject team = e.getFirstObject(TeamType.BLUE);
        blueKills = team.getKills();
        team = e.getFirstObject(TeamType.RED);
        redKills = team.getKills();
    }

    public ExPVPMatchUserDie(int blueKills, int redKills) {
        this.blueKills = blueKills;
        this.redKills = redKills;
    }

    @Override
    protected final void writeData() {
        writeD(blueKills);
        writeD(redKills);
    }
}