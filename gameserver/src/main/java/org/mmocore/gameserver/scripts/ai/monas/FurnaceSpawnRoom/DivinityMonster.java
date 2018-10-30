package org.mmocore.gameserver.scripts.ai.monas.FurnaceSpawnRoom;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.MonasteryFurnaceEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;

/**
 * @author PaInKiLlEr
 * @author KilRoy
 * AI для монстров 22798, 22799, 22800.
 * АИ для спавна жаровень комнате.
 * Есть шанс 5% что заспавнят при смерти 4 жаровни вряд.
 * AI проверен и работает.
 */
public class DivinityMonster extends Fighter {
    public DivinityMonster(final NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(final Creature killer) {
        final NpcInstance actor = getActor();

        if (actor.getEvent(MonasteryFurnaceEvent.class) != null) {
            final int eventId = actor.getEvent(MonasteryFurnaceEvent.class).getId();
            final MonasteryFurnaceEvent furnace = EventHolder.getInstance().getEvent(EventType.FUN_EVENT, eventId);

            if (furnace != null && Rnd.chance(5) && furnace.isInProgress()) {
                furnace.setProgress(false);
                furnace.spawnAction(MonasteryFurnaceEvent.FURNACE_ROOM, true);
                for (final NpcInstance npcs : actor.getAroundNpc(2000, 300)) {
                    if (npcs.getEvent(MonasteryFurnaceEvent.class) == null && npcs.getNpcId() == MonasteryFurnaceEvent.furnaceDefault) {
                        npcs.addEvent(furnace);
                    }
                }
            }
        }

        super.onEvtDead(killer);
    }
}