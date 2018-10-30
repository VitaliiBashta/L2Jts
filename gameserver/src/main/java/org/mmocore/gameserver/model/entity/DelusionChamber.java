package org.mmocore.gameserver.model.entity;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.InstantZoneHolder;
import org.mmocore.gameserver.manager.DimensionalRiftManager;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.templates.InstantZone;
import org.mmocore.gameserver.utils.Location;

import java.util.concurrent.Future;

public class DelusionChamber extends DimensionalRift {
    private volatile Future<?> killRiftTask;

    public DelusionChamber(final Party party, final int type, final int room) {
        super(party, type, room);
    }

    @Override
    public void createNewKillRiftTimer() {
        if (killRiftTask != null) {
            killRiftTask.cancel(false);
            killRiftTask = null;
        }

        killRiftTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                if (getParty() != null && !getParty().getPartyMembers().isEmpty()) {
                    for (final Player p : getParty().getPartyMembers()) {
                        if (p.getReflection() == DelusionChamber.this) {
                            String var = p.getPlayerVariables().get(PlayerVariables.BACK_COORDINATES);
                            if (var == null || var.isEmpty()) {
                                continue;
                            }
                            p.teleToLocation(Location.parseLoc(var), ReflectionManager.DEFAULT);
                            p.getPlayerVariables().remove(PlayerVariables.BACK_COORDINATES);
                        }
                    }
                }
                collapse();
            }
        }, 100L);
    }

    @Override
    public void partyMemberExited(final Player player) {
        if (getPlayersInside(false) < 2 || getPlayersInside(true) == 0) {
            createNewKillRiftTimer();
        }
    }

    @Override
    public void manualExitRift(final Player player, final NpcInstance npc) {
        if (!player.isInParty() || player.getParty().getReflection() != this) {
            return;
        }

        if (!player.getParty().isLeader(player)) {
            DimensionalRiftManager.getInstance().showHtmlFile(player, "rift/NotPartyLeader.htm", npc);
            return;
        }

        createNewKillRiftTimer();
    }

    @Override
    public String getName() {
        final InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(roomType + 120);
        return iz.getName();
    }

    @Override
    protected int getManagerId() {
        return 32664;
    }
}