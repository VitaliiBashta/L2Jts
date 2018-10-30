package org.mmocore.gameserver.object.components.player;

import org.mmocore.commons.collections.FixedQueue;
import org.mmocore.gameserver.configuration.config.clientCustoms.LostDreamCustom;
import org.mmocore.gameserver.object.Player;

/**
 * Created by Hack
 * Date: 01.09.2016 3:37
 */
public class PvpComponent {
    private Player player;
    private FixedQueue<String> victimHwids = new FixedQueue<>(LostDreamCustom.pvpHolderSize);
    private FixedQueue<String> killerHwids = new FixedQueue<>(LostDreamCustom.pvpHolderSize);

    public PvpComponent(Player player) {
        this.player = player;
    }

    public void addVictim(String hwid) {
        victimHwids.add(hwid);
    }

    public void addKiller(String hwid) {
        killerHwids.add(hwid);
    }

    public boolean containsVictim(String hwid) {
        return victimHwids.contains(hwid);
    }

    public boolean containsKiller(String hwid) {
        return killerHwids.contains(hwid);
    }

    public Player getPlayer() {
        return player;
    }
}
