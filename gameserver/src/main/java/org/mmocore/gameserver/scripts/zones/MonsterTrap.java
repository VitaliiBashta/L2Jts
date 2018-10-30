package org.mmocore.gameserver.scripts.zones;

import org.mmocore.commons.math.random.RndSelector;
import org.mmocore.commons.math.random.RndSelector.RndNode;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.ReflectionUtils;

import java.util.stream.Stream;

public class MonsterTrap implements OnInitScriptListener {
    private static final String[] zones = {
            "[hellbound_trap1]",
            "[hellbound_trap2]",
            "[hellbound_trap3]",
            "[hellbound_trap4]",
            "[hellbound_trap5]",
            "[SoD_trap_center]",
            "[SoD_trap_left]",
            "[SoD_trap_right]",
            "[SoD_trap_left_back]",
            "[SoD_trap_right_back]"
    };

    @Override
    public void onInit() {
        ZoneListener _zoneListener = new ZoneListener();

        for (String s : zones) {
            Zone zone = ReflectionUtils.getZone(s);
            zone.addListener(_zoneListener);
        }
    }

    public static class UnSpawnTask extends RunnableImpl {
        private final SimpleSpawner spawn;

        public UnSpawnTask(SimpleSpawner spawn) {
            this.spawn = spawn;
        }

        @Override
        public void runImpl() {
            spawn.deleteAll();
        }
    }

    private class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
            Player player = cha.getPlayer();
            if (player == null || zone.getParams() == null) {
                return;
            }

            int reuse = zone.getParams().getInteger("reuse"); // В секундах
            int despawn = zone.getParams().getInteger("despawn", 5 * 60); // В секундах
            boolean attackOnSpawn = zone.getParams().getBool("attackOnSpawn", true);
            long currentMillis = System.currentTimeMillis();
            long nextReuse = zone.getParams().getLong("nextReuse", currentMillis);
            if (nextReuse > currentMillis) {
                return;
            }
            zone.getParams().set("nextReuse", currentMillis + reuse * 1000L);

            //Структура: chance1:id11,id12...;chance2:id21,id22...
            String[] groups = zone.getParams().getString("monsters").split(";");

            final RndNode<int[]>[] nodes = Stream.of(groups).map(group -> {
                //Структура: chance:id1,id2,idN
                String[] params = group.split(":");
                int chance = Integer.parseInt(params[0]);
                params = params[1].split(",");
                int[] mobs = new int[params.length];
                for (int j = 0; j < params.length; j++)
                    mobs[j] = Integer.parseInt(params[j]);
                return RndNode.of(mobs, chance);
            }).toArray(RndNode[]::new);
            final RndSelector<int[]> rndSelector = RndSelector.of(nodes);

            int[] mobs = rndSelector.select();

            for (int npcId : mobs) {
                try {
                    SimpleSpawner spawn = new SimpleSpawner(npcId);
                    spawn.setTerritory(zone.getTerritory());
                    spawn.setAmount(1);
                    spawn.setReflection(player.getReflection());
                    spawn.stopRespawn();
                    NpcInstance mob = spawn.doSpawn(true);
                    if (mob != null) {
                        ThreadPoolManager.getInstance().schedule(new UnSpawnTask(spawn), despawn * 1000L);
                        if (mob.isAggressive() && attackOnSpawn) {
                            mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 100);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
        }
    }
}
