package org.mmocore.gameserver.scripts.ai;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

/**
 * @author Diamond
 */
public class CrystallineGolem extends Fighter {
    private static final int CORAL_GARDEN_SECRETGATE = 24220026; // Tears Door

    private static final int Crystal_Fragment = 9693;
    private static final String[] says = new String[]{"Ням, ням!!!", "Дай!!!", "Хочу!!!", "Моe!!!", "Еще!!!", "Еда!!!"};
    private static final String[] says2 = new String[]{
            "Отдай!!!",
            "Верни!!!",
            "Жадные вы, уйду я от вас...",
            "Куда оно подевалось?",
            "Наверное показалось..."
    };
    private static final TIntObjectHashMap<Info> instanceInfo = new TIntObjectHashMap<Info>();
    private ItemInstance itemToConsume = null;
    private Location lastPoint = null;

    public CrystallineGolem(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead()) {
            return true;
        }

        if (_def_think) {
            doTask();
            return true;
        }

        if (itemToConsume != null) {
            if (itemToConsume.isVisible()) {
                itemToConsume.deleteMe();
                itemToConsume = null;
            } else {
                itemToConsume = null;
                Functions.npcSay(actor, says2[Rnd.get(says2.length)]);
                actor.setWalking();
                addTaskMove(lastPoint, true);
                lastPoint = null;
                return true;
            }
        }

        Info info = instanceInfo.get(actor.getReflectionId());
        if (info == null) {
            info = new Info();
            instanceInfo.put(actor.getReflectionId(), info);
        }

        boolean opened = info.stage1 && info.stage2;

        if (!info.stage1) {
            int dx = actor.getX() - 142999;
            int dy = actor.getY() - 151671;
            if (dx * dx + dy * dy < 10000) {
                actor.broadcastPacket(new MagicSkillUse(actor, actor, 5441, 1, 1, 0));
                info.stage1 = true;
            }
        }

        if (!info.stage2) {
            int dx = actor.getX() - 139494;
            int dy = actor.getY() - 151668;
            if (dx * dx + dy * dy < 10000) {
                actor.broadcastPacket(new MagicSkillUse(actor, actor, 5441, 1, 1, 0));
                info.stage2 = true;
            }
        }

        if (!opened && info.stage1 && info.stage2) {
            actor.getReflection().openDoor(CORAL_GARDEN_SECRETGATE);
        }

        if (Rnd.chance(10)) {
            for (GameObject obj : World.getAroundObjects(actor, 300, 200)) {
                if (obj.isItem()) {
                    ItemInstance item = (ItemInstance) obj;
                    if (item.getItemId() == Crystal_Fragment) {
                        if (Rnd.chance(50)) {
                            Functions.npcSay(actor, says[Rnd.get(says.length)]);
                        }
                        itemToConsume = item;
                        lastPoint = actor.getLoc();
                        actor.setRunning();
                        addTaskMove(item.getLoc(), false);
                        return true;
                    }
                }
            }
        }

        if (randomAnimation()) {
            return true;
        }

        return false;
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    private static class Info {
        boolean stage1 = false;
        boolean stage2 = false;
    }
}