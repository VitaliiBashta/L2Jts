package org.mmocore.gameserver.scripts.ai.monas;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * @author PaInKiLlEr
 * AI для моба Divine Furnace (18915).
 * Если РБ Анаис был атакован, спавнит шарики (18929) каждую минуту.
 * AI проверен и работает.
 */
public class DivineFurnace extends DefaultAI {
    private static final int Sharik = 18929;

    private static final Location spawn1 = new Location(113610, -77318, 56);
    private static final Location spawn2 = new Location(111971, -77366, 56);
    private static final Location spawn3 = new Location(111957, -75691, 56);
    private static final Location spawn4 = new Location(113552, -75715, 56);

    private long _wait_timeout = System.currentTimeMillis() + 60000;
    private int _offCount = 0;

    public DivineFurnace(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null || actor.isDead())
            return true;

        NpcInstance npc = GameObjectsStorage.getByNpcId(25701);
        if (npc != null && npc.isAttackingNow()) {
            if (_wait_timeout < System.currentTimeMillis()) {
                // При каждом окончании таймера приписываем +1 для разновидности
                // локаций для спавна
                _offCount++;
                // Загораемся огнём
                actor.setNpcState(1);
                // Запускаем таск что бы потушить огонь
                ThreadPoolManager.getInstance().schedule(new ScheduleTimerTask(), 10000);
                // Ставим новый таймер
                _wait_timeout = (System.currentTimeMillis() + 90000);

                switch (_offCount) {
                    case 1:
                        NpcUtils.spawnSingle(Sharik, spawn1);
                    case 2:
                        NpcUtils.spawnSingle(Sharik, spawn2);
                    case 3:
                        NpcUtils.spawnSingle(Sharik, spawn3);
                    case 4:
                        NpcUtils.spawnSingle(Sharik, spawn4);
                        _offCount = 0; // Сбрасываем память
                }
            }
        } else if (npc != null && npc.isDead()) {
            if (_offCount > 0)
                _offCount = 0; // Сбрасываем память
        }

        return super.thinkActive();
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    private class ScheduleTimerTask implements Runnable {
        @Override
        public void run() {
            NpcInstance actor = getActor();
            actor.setNpcState(2);
        }
    }
}