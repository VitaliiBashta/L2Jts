package org.mmocore.gameserver.scripts.ai.monas.FurnaceSpawnRoom;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.entity.events.impl.MonasteryFurnaceEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * @author PaInKiLlEr
 * @author KilRoy
 * AI для Furnace (18914).
 * При спавне имеет собственный ник.
 * При атаке загорается, сбрасывает у игрока таргет, включает неактивность таргета.
 * Кричит в чат при ударе.
 * Через 15 секунд исчезают все жаровни.
 * Запускается евент и комната респавнится с монстрами воинами.
 * AI проверен и работает.
 */
public class FurnaceWill extends DefaultAI {
    private boolean firstTimeAttacked = true;

    public FurnaceWill(NpcInstance actor) {
        super(actor);
        actor.setIsInvul(true);
        actor.setNameNpcString(NpcString.FURNACE_OF_WILL);
        actor.broadcastCharInfo();
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (firstTimeAttacked) {
            boolean spawned = true;
            if (actor.getEvent(MonasteryFurnaceEvent.class) != null)
                spawned = actor.getEvent(MonasteryFurnaceEvent.class).isFuranceSpawned();
            if (!spawned) {
                actor.getEvent(MonasteryFurnaceEvent.class).setFuranceSpawn(true);
                firstTimeAttacked = false;
                attacker.setTarget(null);
                actor.setTargetable(false);
                actor.setNpcState(1);
                ChatUtils.say(actor, NpcString.DIVINE_ENERGY_IS_BEGINNING_TO_ENCIRCLE);
                ThreadPoolManager.getInstance().schedule(new ScheduleTimerTask(), 15000);
            }
        }

        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        firstTimeAttacked = true;
        super.onEvtDead(killer);
    }

    @Override
    protected boolean checkAggression(final Creature target) {
        return false;
    }

    @Override
    protected void onEvtAggression(final Creature attacker, final int aggro) {
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    private class ScheduleTimerTask extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            NpcInstance actor = getActor();
            Spawner spawn = actor.getSpawn();
            if (spawn != null) {
                MonasteryFurnaceEvent furnace = actor.getEvent(MonasteryFurnaceEvent.class);
                furnace.spawnAction(MonasteryFurnaceEvent.DIVINITY_ROOM, false);
                furnace.spawnAction(MonasteryFurnaceEvent.FIGHTER_ROOM, true);
                for (final NpcInstance monster : actor.getAroundNpc(2000, 300)) {
                    if (monster.getEvent(MonasteryFurnaceEvent.class) == null && ArrayUtils.contains(MonasteryFurnaceEvent.MONSTER_ID, monster.getNpcId()))
                        monster.addEvent(furnace);
                }
                furnace.spawnAction(MonasteryFurnaceEvent.FURNACE_ROOM, false);
            }
        }
    }
}