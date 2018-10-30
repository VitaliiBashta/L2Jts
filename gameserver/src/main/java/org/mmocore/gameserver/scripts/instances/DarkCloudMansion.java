package org.mmocore.gameserver.scripts.instances;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CharacterAI;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.listener.actor.OnAttackListener;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.funcs.FuncMul;
import org.mmocore.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KilRoy
 * @author Unknow(Old owner quest script)
 */
public class DarkCloudMansion extends Reflection {
    private static final int[] BM = new int[]{22272, 22273, 22274};
    private static final int[] CCG = new int[]{18369, 18370};
    private static final int[] BS = new int[]{18371, 18372, 18373, 18374, 18375, 18376, 18377}; // real_or_sham01-07  AI:D_room_real_and_imi

    private static final int chastalia = 22264;
    private static final int dark_stonehenge = 32324;
    private static final int road_maker_object = 22402;

    private static final int DOOR_1 = 24230001;
    private static final int DOOR_2 = 24230002;
    private static final int DOOR_3 = 24230005;
    private static final int DOOR_4 = 24230003;
    private static final int DOOR_5 = 24230004;
    private static final int DOOR_6 = 24230006;
    private static final int WALL = 24230007;

    private static final String firstGroup = "belet_minion_group";
    private static final String hallGroup = "hall_mob_group";
    private static final String firstRoomGroup = "first_room_group";
    private static final String firstRoomGobletGroup = "goblet_first_room_group";
    private static final String secondRoomMonolithGroup = "second_room_group";
    private static final String secondRoomGobletGroup = "goblet_second_room_group";
    private static final String thirdRoomGroup = "third_room_group";
    private static final String thirdRoomGobletGroup = "goblet_third_room_group";
    private static final String beletSampleGroup = "belet_sample_group";
    // Second room - random monolith order
    private static final int[][] order = new int[][]{
            {1, 2, 3, 4, 5, 6},
            {6, 5, 4, 3, 2, 1},
            {4, 5, 6, 3, 2, 1},
            {2, 6, 3, 5, 1, 4},
            {4, 1, 5, 6, 2, 3},
            {3, 5, 1, 6, 2, 4},
            {6, 1, 3, 4, 5, 2},
            {5, 6, 1, 2, 4, 3},
            {5, 2, 6, 3, 4, 1},
            {1, 5, 2, 6, 3, 4},
            {1, 2, 3, 6, 5, 4},
            {6, 4, 3, 1, 5, 2},
            {3, 5, 2, 4, 1, 6},
            {3, 2, 4, 5, 1, 6},
            {5, 4, 3, 1, 6, 2}
    };
    // Second room - golem spawn locatons - random
    private static final int[][] golems = new int[][]{
            {CCG[0], 148060, 181389},
            {CCG[1], 147910, 181173},
            {CCG[0], 147810, 181334},
            {CCG[1], 147713, 181179},
            {CCG[0], 147569, 181410},
            {CCG[1], 147810, 181517},
            {CCG[0], 147805, 181281}
    };
    // forth room - random shadow column
    private static final int[][] rows = new int[][]{
            {1, 1, 0, 1, 0},
            {0, 1, 1, 0, 1},
            {1, 0, 1, 1, 0},
            {0, 1, 0, 1, 1},
            {1, 0, 1, 0, 1}
    };
    public final List<int[]> npcList = new ArrayList<int[]>();
    private final TIntSet rewarded = new TIntHashSet();
    private final DeathListener deathListener = new DeathListener();
    private final AttackListener attackListener = new AttackListener();
    public int[] monolithOrder = new int[]{1, 0, 0, 0, 0, 0, 0};
    private int currentStage = 0;
    private int currentStageCountDeath = 0;

    @Override
    protected void onCreate() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                currentStage++;
                spawnByGroup(firstGroup);
                invokeListener(false);
            }
        }, 10000L);
    }

    @Override
    protected void onCollapse() {
    }

    public void validateMonolithStone(final NpcInstance npc, final Player player) {
        for (int[] npcObj : npcList) {
            if (npcObj[0] == npc.getObjectId()) {
                checkStone(npc, player, monolithOrder, npcObj);
            }
        }
        if (allStonesDone()) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    currentStage++;
                    despawnByGroup(secondRoomMonolithGroup);
                    spawnByGroup(secondRoomGobletGroup);
                    spawnByGroup(hallGroup);
                    invokeListener(false);
                }
            }, 1500L);

        }
    }

    public boolean checkRewardedPlayer(final int objId) {
        if (!rewarded.contains(objId)) {
            rewarded.add(objId);
            return true;
        }
        return false;
    }

    private boolean allStonesDone() {
        for (int[] list : npcList) {
            if (list[2] != 1) {
                return false;
            }
        }
        return true;
    }

    private void checkStone(final NpcInstance npc, final Player player, int[] order, int[] npcObj) {
        if (getDoor(DOOR_3).isOpen())
            closeDoor(DOOR_3);
        for (int i = 1; i <= 6; i++) {
            if (order[i] == 0 && order[i - 1] != 0) {

                if (npcObj[1] == i && npcObj[2] == 0) {
                    order[i] = 1;
                    npcObj[2] = 1;
                    npc.broadcastPacket(new MagicSkillUse(npc, npc, 5441, 1, 1, 0));
                    return;
                }
            }
        }
        final int i = Rnd.get(golems.length);
        final int id = golems[i][0];
        final int x = golems[i][1];
        final int y = golems[i][2];
        final NpcInstance npcs = addSpawnWithoutRespawn(id, new Location(x, y, -6117), 0);
        npcs.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, Rnd.get(1, 100));
    }

    private void chkShadowColumn(final NpcInstance npc) {
        for (int[] mob : npcList) {
            if (mob[0] == npc.getObjectId()) {
                for (int i = 0; i <= 7; i++) {
                    if (mob[2] == i && currentStageCountDeath == i) {
                        openDoor(WALL + i);
                        currentStageCountDeath += 1;
                        if (currentStageCountDeath == 7) {
                            currentStageCountDeath = 0;
                            currentStage++;
                            for (final NpcInstance npcs : getNpcs()) {
                                if (ArrayUtils.contains(BM, npcs.getNpcId()))
                                    npcs.deleteMe();
                            }
                            spawnByGroup(thirdRoomGroup);
                            invokeListener(false);
                        }
                    }
                }
            }
        }
    }

    private void invokeListener(final boolean attack) {
        for (NpcInstance npc : getNpcs()) {
            npc.addListener(deathListener);
        }
        if (attack) {
            for (final Player player : getPlayers()) {
                player.addListener(attackListener);
            }
        }
    }

    private class AttackListener implements OnAttackListener {
        @Override
        public void onAttack(final Creature actor, final Creature target) {
            if (target.isCreature() && currentStage == 3 && target.getNpcId() == chastalia) {
                if (getDoor(DOOR_2).isOpen())
                    closeDoor(DOOR_2);
            }
            if (target.isCreature() && currentStage == 8 && target.getNpcId() == road_maker_object) {
                if (getDoor(DOOR_5).isOpen())
                    closeDoor(DOOR_5);
                if (((NpcInstance) target).isBusy() && Rnd.chance(10)) {
                    final NpcInstance beletMinion = addSpawnWithoutRespawn(BM[1], target.getLoc(), 10);
                    beletMinion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, actor, Rnd.get(1, 100));
                }
            }
        }
    }

    private class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(final Creature self, final Creature killer) {
            switch (currentStage) {
                case 1:
                    if (ArrayUtils.contains(BM, self.getNpcId())) {
                        currentStageCountDeath++;
                        if (currentStageCountDeath == 2) {
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    currentStageCountDeath = 0;
                                    currentStage++;
                                    openDoor(DOOR_1);
                                    spawnByGroup(hallGroup);
                                    invokeListener(false);
                                }
                            }, 8000L);
                        }
                    }
                    break;
                case 2:
                    if (ArrayUtils.contains(BM, self.getNpcId())) {
                        currentStageCountDeath++;
                        if (currentStageCountDeath == 8) {
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    currentStageCountDeath = 0;
                                    currentStage++;
                                    openDoor(DOOR_2);
                                    spawnByGroup(firstRoomGroup);
                                    invokeListener(true);
                                }
                            }, 8000L);
                        }
                    }
                    break;
                case 3:
                    if (self.getNpcId() == chastalia) {
                        currentStageCountDeath++;
                        if (currentStageCountDeath == 4) {
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    currentStageCountDeath = 0;
                                    currentStage++;
                                    spawnByGroup(firstRoomGobletGroup);
                                    spawnByGroup(hallGroup);
                                    invokeListener(false);
                                }
                            }, 10000L);
                        }
                    }
                    break;
                case 4:
                    if (ArrayUtils.contains(BM, self.getNpcId())) {
                        currentStageCountDeath++;
                        if (currentStageCountDeath == 8) {
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    currentStageCountDeath = 0;
                                    currentStage++;
                                    openDoor(DOOR_3);
                                    spawnByGroup(secondRoomMonolithGroup);
                                    int i = Rnd.get(order.length);
                                    int count = 0;
                                    for (final NpcInstance npcs : getNpcs()) {
                                        if (npcs.getNpcId() == dark_stonehenge) {
                                            npcList.add(new int[]{npcs.getObjectId(), order[i][count], 0});
                                            count++;
                                        }
                                    }
                                }
                            }, 8000L);
                        }
                    }
                    break;
                case 6:
                    if (ArrayUtils.contains(BM, self.getNpcId())) {
                        currentStageCountDeath++;
                        if (currentStageCountDeath == 8) {
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    currentStageCountDeath = 0;
                                    currentStage++;
                                    openDoor(DOOR_4);
                                    spawnByGroup(thirdRoomGroup);
                                    invokeListener(false);
                                }
                            }, 8000L);
                        }
                    }
                    break;
                case 7:
                    if (ArrayUtils.contains(BM, self.getNpcId())) {
                        currentStageCountDeath++;
                        if (currentStageCountDeath == 6) {
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    currentStageCountDeath = 0;
                                    currentStage++;
                                    openDoor(DOOR_5);
                                    final int[] temp = new int[7];
                                    final int[][] templist = new int[7][];

                                    for (int i = 0; i < temp.length; i++) {
                                        temp[i] = Rnd.get(rows.length);
                                    }

                                    for (int i = 0; i < temp.length; i++) {
                                        templist[i] = rows[temp[i]];
                                    }

                                    int xx = 0;
                                    int yy = 0;

                                    for (int x = 148660; x <= 149160; x += 125) {
                                        yy = 0;
                                        for (int y = 179280; y >= 178530; y -= 125) {
                                            NpcInstance newNpc = addSpawnWithoutRespawn(road_maker_object, new Location(x, y, -6115, 16215), 0);
                                            newNpc.setAI(new CharacterAI(newNpc));
                                            if (templist[yy][xx] == 0) {
                                                newNpc.setBusy(true); // Используется здесь для определения "ненастощих" статуй.
                                                newNpc.addStatFunc(new FuncMul(Stats.MAGIC_DEFENCE, 0x30, this, 1000));
                                                newNpc.addStatFunc(new FuncMul(Stats.POWER_DEFENCE, 0x30, this, 1000));
                                            }

                                            npcList.add(new int[]{newNpc.getObjectId(), templist[yy][xx], yy});
                                            yy += 1;
                                        }
                                        xx += 1;
                                    }
                                    spawnByGroup(thirdRoomGobletGroup);
                                    invokeListener(true);
                                }
                            }, 8000L);
                        }
                    }
                    break;
                case 8: {
                    if (self.getNpcId() == road_maker_object)
                        chkShadowColumn((NpcInstance) self);
                    break;
                }
                case 9: {
                    if (ArrayUtils.contains(BM, self.getNpcId())) {
                        currentStageCountDeath++;
                        if (currentStageCountDeath == 6) {
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    currentStageCountDeath = 0;
                                    currentStage++;
                                    openDoor(DOOR_6);
                                    spawnByGroup(beletSampleGroup);
                                    invokeListener(true);
                                }
                            }, 10000L);
                        }
                    }
                    break;
                }
                case 10: {

                }
            }
        }
    }
}