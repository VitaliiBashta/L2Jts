package org.mmocore.gameserver.scripts.ai.hellbound;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.world.World;

/**
 * @author pchayka
 */
public class NaiaLock extends Fighter {
    private static boolean _attacked = false;
    private static boolean _entranceactive = false;

    public NaiaLock(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    public static boolean isEntranceActive() {
        return _entranceactive;
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        _entranceactive = true;
        for (final NpcInstance npc : World.getAroundNpc(actor)) {
            if (npc.getNpcId() == 18492) {
                ChatUtils.say(npc, NpcString.EMERGENCY_EMERGENCY_THE_OTHER_WALL_IS_WEAKING_RAPIDLY);
                break;
            }
        }
        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        _entranceactive = false;
    }

    @Override
    public boolean checkAggression(Creature target) {
        return false;
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();

        if (!_attacked) {
            for (int i = 0; i < 4; i++) {
                try {
                    NpcUtils.spawnSingle(18493, Location.findPointToStay(actor, 150, 250), actor.getReflection());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            _attacked = true;
        }
    }
}