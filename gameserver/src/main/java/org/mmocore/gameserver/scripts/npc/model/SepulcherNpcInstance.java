package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.NpcSay;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.scripts.bosses.FourSepulchersManager;
import org.mmocore.gameserver.scripts.bosses.FourSepulchersSpawn;
import org.mmocore.gameserver.scripts.bosses.FourSepulchersSpawn.GateKeeper;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.PositionUtils;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class SepulcherNpcInstance extends NpcInstance {
    private final static String HTML_FILE_PATH = "SepulcherNpc/";
    private final static int HALLS_KEY = 7260;
    protected static Map<Integer, Integer> _hallGateKeepers = new HashMap<Integer, Integer>();
    protected Future<?> _closeTask = null, _spawnMonsterTask = null;

    public SepulcherNpcInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onDelete() {
        if (_closeTask != null) {
            _closeTask.cancel(false);
            _closeTask = null;
        }
        if (_spawnMonsterTask != null) {
            _spawnMonsterTask.cancel(false);
            _spawnMonsterTask = null;
        }
        super.onDelete();
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        if (isDead()) {
            player.sendActionFailed();
            return;
        }

        switch (getNpcId()) {
            case 31468:
            case 31469:
            case 31470:
            case 31471:
            case 31472:
            case 31473:
            case 31474:
            case 31475:
            case 31476:
            case 31477:
            case 31478:
            case 31479:
            case 31480:
            case 31481:
            case 31482:
            case 31483:
            case 31484:
            case 31485:
            case 31486:
            case 31487:
                doDie(player);
                if (_spawnMonsterTask != null) {
                    _spawnMonsterTask.cancel(false);
                }
                _spawnMonsterTask = ThreadPoolManager.getInstance().schedule(new SpawnMonster(getNpcId()), 3500);
                return;

            case 31455:
            case 31456:
            case 31457:
            case 31458:
            case 31459:
            case 31460:
            case 31461:
            case 31462:
            case 31463:
            case 31464:
            case 31465:
            case 31466:
            case 31467:
                if (player.isInParty() && !hasPartyAKey(player) && player.getParty().getGroupLeader() == player) {
                    ItemFunctions.addItem(player.getParty().getGroupLeader(), HALLS_KEY, 1);
                    doDie(player);
                }
                return;
        }
        super.showChatWindow(player, val);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String pom;
        if (val == 0) {
            pom = String.valueOf(npcId);
        } else {
            pom = npcId + "-" + val;
        }
        return HTML_FILE_PATH + pom + ".htm";
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (command.startsWith("open_gate")) {
            ItemInstance hallsKey = player.getInventory().getItemByItemId(HALLS_KEY);
            if (hallsKey == null) {
                showHtmlFile(player, "Gatekeeper-no.htm");
            } else if (FourSepulchersManager.isAttackTime()) {
                switch (getNpcId()) {
                    case 31929:
                    case 31934:
                    case 31939:
                    case 31944:
                        if (!FourSepulchersSpawn.isShadowAlive(getNpcId())) {
                            FourSepulchersSpawn.spawnShadow(getNpcId());
                        }
                }

                // Moved here from switch-default
                openNextDoor(getNpcId());
                if (player.getParty() != null) {
                    for (Player mem : player.getParty().getPartyMembers()) {
                        hallsKey = mem.getInventory().getItemByItemId(HALLS_KEY);
                        if (hallsKey != null) {
                            ItemFunctions.removeItem(mem, HALLS_KEY, hallsKey.getCount());
                        }
                    }
                } else {
                    ItemFunctions.removeItem(player, HALLS_KEY, hallsKey.getCount());
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    public void openNextDoor(int npcId) {
        GateKeeper gk = FourSepulchersManager.getHallGateKeeper(npcId);
        gk.door.openMe();

        if (_closeTask != null) {
            _closeTask.cancel(false);
        }
        _closeTask = ThreadPoolManager.getInstance().schedule(new CloseNextDoor(gk), 10000);
    }

    public void sayInShout(String msg) {
        if (msg == null || msg.isEmpty()) {
            return; //wrong usage
        }

        NpcSay sm = new NpcSay(this, ChatType.NPC_ALL, NpcString.NONE);

        for (Player player : GameObjectsStorage.getPlayers()) {
            if (player == null) {
                continue;
            }
            if (PositionUtils.checkIfInRange(15000, player, this, true)) {
                player.sendPacket(sm);
            }
        }
    }

    public void showHtmlFile(Player player, String file) {
        HtmlMessage html = new HtmlMessage(this);
        html.setFile("SepulcherNpc/" + file);
        html.replace("%npcname%", getName());
        player.sendPacket(html);
    }

    private boolean hasPartyAKey(Player player) {
        for (Player m : player.getParty().getPartyMembers()) {
            if (ItemFunctions.getItemCount(m, HALLS_KEY) > 0) {
                return true;
            }
        }
        return false;
    }

    private static class SpawnMonster extends RunnableImpl {
        private final int _NpcId;

        public SpawnMonster(int npcId) {
            _NpcId = npcId;
        }

        @Override
        public void runImpl() throws Exception {
            FourSepulchersSpawn.spawnMonster(_NpcId);
        }
    }

    private class CloseNextDoor extends RunnableImpl {
        private final GateKeeper _gk;
        private int state = 0;

        public CloseNextDoor(GateKeeper gk) {
            _gk = gk;
        }

        @Override
        public void runImpl() throws Exception {
            if (state == 0) {
                try {
                    _gk.door.closeMe();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                state++;
                _closeTask = ThreadPoolManager.getInstance().schedule(this, 10000);
            } else if (state == 1) {
                FourSepulchersSpawn.spawnMysteriousBox(_gk.template.npcId);
                _closeTask = null;
            }
        }
    }
}