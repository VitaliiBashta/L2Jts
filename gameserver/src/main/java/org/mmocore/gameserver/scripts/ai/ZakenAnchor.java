package org.mmocore.gameserver.scripts.ai;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;

public class ZakenAnchor extends DefaultAI {
    private static final int DayZaken = 29176;
    private static final int UltraDayZaken = 29181;
    private static final int Candle = 32705;
    private int i = 0;

    public ZakenAnchor(final NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        final NpcInstance actor = getActor();
        for (final NpcInstance npc : actor.getAroundNpc(1200, 100)) {
            if (npc.getNpcId() == Candle && npc.getRightHandItem() == 15302) {
                i++;
            }
        }

        if (i >= 4) {
            if (actor.getReflection().getInstancedZoneId() == 133) {
                actor.getReflection().addSpawnWithoutRespawn(DayZaken, actor.getLoc(), 0);
                for (int i = 0; i < 4; i++) {
                    actor.getReflection().addSpawnWithoutRespawn(20845, actor.getLoc(), 200);
                    actor.getReflection().addSpawnWithoutRespawn(20847, actor.getLoc(), 200);
                }
                actor.deleteMe();
                return true;
            } else if (actor.getReflection().getInstancedZoneId() == 135) {
                actor.getReflection().addSpawnWithoutRespawn(UltraDayZaken, actor.getLoc(), 0);
                for (int i = 0; i < 4; i++) {
                    actor.getReflection().addSpawnWithoutRespawn(29184, actor.getLoc(), 300);
                }
                actor.deleteMe();
                return true;
            }
        } else {
            i = 0;
        }

        return false;
    }
}