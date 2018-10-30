package org.mmocore.gameserver.scripts.ai.pagan_temple;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author PaInKiLlEr - AI для нпц Altar Gatekeeper (32051). - Контроллеры дверей. - AI проверен и работает.
 */
public class AltarGatekeeper extends DefaultAI {
    private static final int[] door = {19160014, 19160015, 19160016, 19160017};
    private boolean firstTime = true;

    public AltarGatekeeper(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null)
            return true;

        if (firstTime) {
            // Двери на балкон
            DoorInstance door1 = ReflectionUtils.getDoor(door[0]);
            DoorInstance door2 = ReflectionUtils.getDoor(door[1]);
            // Двери к алтарю
            DoorInstance door3 = ReflectionUtils.getDoor(door[2]);
            DoorInstance door4 = ReflectionUtils.getDoor(door[3]);

            // Кричим 4 раза (т.к. актор заспавнен в 4х местах) как на оффе о том что двери открылись
            if (!door1.isOpen() && !door2.isOpen() && door3.isOpen() && door4.isOpen()) {
                firstTime = false;
                ChatUtils.shout(actor, NpcString.THE_DOOR_TO_THE_3RD_FLOOR_OF_THE_ALTAR_IS_NOW_OPEN);
            }
        }

        return true;
    }
}