package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.TimeStamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SkillCoolTime extends GameServerPacket {
    private List<Skill> list = Collections.emptyList();

    public SkillCoolTime(final Player player) {
        final Collection<TimeStamp> list = player.getSkillReuses();
        this.list = new ArrayList<>(list.size());
        int reuseCurrent;
        for (final TimeStamp stamp : list) {
            if (!stamp.hasNotPassed()) {
                continue;
            }

            reuseCurrent = (int) (stamp.getReuseCurrent() / 1000);
            if (reuseCurrent == 0) // DS: не допускаем нулевые значения, клиенту от них плохеет
            {
                continue;
            }

            final Skill sk = new Skill();
            sk.skillId = stamp.getId();
            sk.level = stamp.getLevel();
            sk.reuseBase = (int) (stamp.getReuseBasic() / 1000);
            sk.reuseCurrent = reuseCurrent;
            this.list.add(sk);
        }
    }

    @Override
    protected final void writeData() {
        writeD(list.size()); //Size of list
        for (final Skill sk : list) {
            writeD(sk.skillId); //Skill Id
            writeD(sk.level); //Skill Level
            writeD(sk.reuseBase); //Total reuse delay, seconds
            writeD(sk.reuseCurrent); //Time remaining, seconds
        }
    }

    private static class Skill {
        public int skillId;
        public int level;
        public int reuseBase;
        public int reuseCurrent;
    }
}