package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.BossInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.NpcUtils;


public class QueenAntInstance extends BossInstance {
    private static final int Queen_Ant_Larva = 29002;
    private NpcInstance _minionLarva = null;

    public QueenAntInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    public synchronized NpcInstance getMinionLarva() {
        if (_minionLarva == null) {
            _minionLarva = NpcUtils.spawnSingle(Queen_Ant_Larva, new Location(-21600, 179482, -5846, Rnd.get(0, 0xFFFF)));
        }
        return _minionLarva;
    }

    @Override
    protected void onDeath(Creature killer) {
        broadcastPacketToOthers(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, 0, getLoc()));
        _minionLarva.deleteMe();
        _minionLarva = null;
        Log.add("Queen Ant died", "org/mmocore/gameserver/scripts/bosses");
        super.onDeath(killer);
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        if (this.hasPrivates()) {
            this.getPrivatesList().useSpawnPrivates();
        } else {
            String privates = "29003:2:10;29003:2:10;29003:2:10;29005:1:20;29005:1:20;29005:1:20;29005:1:20";
            this.getPrivatesList().createPrivates(privates, true);
        }
        getMinionLarva();
        broadcastPacketToOthers(new PlaySound(PlaySound.Type.MUSIC, "BS01_A", 1, 0, getLoc()));
    }

    @Override
    protected int getMinChannelSizeForLock() {
        return 36;
    }

    @Override
    protected void onChannelLock(String leaderName) {
        broadcastPacket(new ExShowScreenMessage(NpcString.QUEEN_ANT_S1_COMMAND_CHANNEL_HAS_LOOTING_RIGHTS, 4000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, false, leaderName));
    }

    @Override
    protected void onChannelUnlock() {
        broadcastPacket(new ExShowScreenMessage(NpcString.QUEEN_ANT_LOOTING_RULES_ARE_NO_LONGER_ACTIVE, 4000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, false));
    }
}