package org.mmocore.gameserver.scripts.ai.hellbound;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * @Оффлайк АИ для нпц Тулли, доработка АИ - VAVAN.
 */
public class Tully extends Fighter {

    // 32371
    private static final Location[] locSD = {
            new Location(-10831, 273890, -9040, 81895),
            new Location(-10817, 273986, -9040, -16452),
            new Location(-13773, 275119, -9040, 8428),
            new Location(-11547, 271772, -9040, -19124),
    };
    //22392
    private static final Location[] locFTT = {
            new Location(-10832, 273808, -9040, 0),
            new Location(-10816, 274096, -9040, 14964),
            new Location(-13824, 275072, -9040, -24644),
            new Location(-11504, 271952, -9040, 9328),
    };
    private static Zone _zone = ReflectionUtils.getZone("[tully5_damage]");
    private static NpcInstance removable_ghost = null;
    private boolean s = false;

    public Tully(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        for (int i = 0; i < locSD.length; i++) {
            try {
                SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(32371));
                sp.setLoc(locSD[i]);
                sp.doSpawn(true);
                if (!s) {
                    Functions.npcShout(sp.getLastSpawn(), "Self Destruction mechanism launched: 10 minutes to explosion");
                    s = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < locFTT.length; i++) {
            try {
                SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(22392));
                sp.setLoc(locFTT[i]);
                sp.doSpawn(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(32370));
            sp.setLoc(new Location(-11984, 272928, -9040, 23644));
            sp.doSpawn(true);
            removable_ghost = sp.getLastSpawn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ThreadPoolManager.getInstance().schedule(new UnspawnAndExplode(), 600 * 1000L); // 10 mins
        ReflectionUtils.getDoor(19260051).openMe();
        ReflectionUtils.getDoor(19260052).openMe();
        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtSpawn() {
        GameObjectsStorage.getAllByNpcId(32370, true).forEach(NpcInstance::deleteMe);
    }

    private static class UnspawnAndExplode extends RunnableImpl {
        public UnspawnAndExplode() {
        }

        @Override
        public void runImpl() {
            ThreadPoolManager.getInstance().schedule(new setZoneInActive(), 600 * 1000L); // 10 mins

            _zone.setActive(true);

            ReflectionUtils.getDoor(19260051).closeMe();
            ReflectionUtils.getDoor(19260052).closeMe();

            GameObjectsStorage.getAllByNpcId(32371, true).forEach(NpcInstance::deleteMe);

            GameObjectsStorage.getAllByNpcId(22392, true).forEach(NpcInstance::deleteMe);

            if (removable_ghost != null) {
                removable_ghost.deleteMe();
            }
        }
    }

    private static class setZoneInActive extends RunnableImpl {
        public setZoneInActive() {
        }

        @Override
        public void runImpl() {
            _zone.setActive(false);
            NpcUtils.spawnSingle(32370, new Location(-14643, 274588, -9040, 49152));
        }
    }
}