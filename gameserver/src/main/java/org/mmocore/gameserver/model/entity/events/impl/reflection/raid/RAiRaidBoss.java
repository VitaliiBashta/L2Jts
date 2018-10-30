package org.mmocore.gameserver.model.entity.events.impl.reflection.raid;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.entity.events.impl.reflection.RBaseController;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.reward.RewardItem;
import org.mmocore.gameserver.model.reward.RewardList;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.funcs.Func;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ItemFunctions;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Mangol
 * @since 08.10.2016
 */
public class RAiRaidBoss extends Fighter {
    private RBaseController controller;
    private Future<?> task;

    public RAiRaidBoss(NpcInstance actor) {
        super(actor);
        actor.addStatFunc(new FuncPMDefMul(Stats.MAGIC_DEFENCE, actor, false));
        actor.addStatFunc(new FuncPMDefMul(Stats.POWER_DEFENCE, actor, false));
        actor.addStatFunc(new FuncPMAtakMul(Stats.MAGIC_ATTACK, actor, false));
        actor.addStatFunc(new FuncPMAtakMul(Stats.POWER_ATTACK, actor, false));
    }

    @Override
    protected void onEvtSpawn() {
        ThreadPoolManager.getInstance().schedule(() -> {
            if (getActor() == null)
                return;
            List<NpcInstance> list = getActor().getPrivatesList().getAlivePrivates();
            if (list != null && !list.isEmpty()) {
                list.forEach(n -> {
                    n.getParameters().replace("respawn_minion", 0);
                    n.addStatFunc(new FuncPMDefMul(Stats.MAGIC_DEFENCE, n, true));
                    n.addStatFunc(new FuncPMDefMul(Stats.POWER_DEFENCE, n, true));
                    n.addStatFunc(new FuncPMAtakMul(Stats.MAGIC_ATTACK, n, true));
                    n.addStatFunc(new FuncPMAtakMul(Stats.POWER_ATTACK, n, true));
                    n.setName("EventMinion");
                    n.setTitle("");
                    n.setLevel(Rnd.get(controller.getEvent().getMinLevel(), controller.getEvent().getMaxLevel()));
                });
            }
        }, 3000L);

    }

    public void setController(final RBaseController controller) {
        this.controller = controller;
    }

    @Override
    protected void onEvtDead(Creature killer) {
        final Player player = killer.getPlayer();
        if (controller != null) {
            final boolean c = controller.getPlayers().parallelStream().anyMatch(p -> p != null && p.getPlayer().getObjectId() == player.getObjectId());
            if (c) {
                controller.getPlayers().stream().filter(p -> p != null && p.getPlayer() != null).forEach(p -> p.getPlayer().sendPacket(new ExShowScreenMessage("Поздравляем! Вы успешно окончили евент! Вы будете телепортированы через 30 секунд.", 6000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false)));
                List<Serializable> list = controller.getEvent().getObjects("rewardBoss");
                if (list != null && !list.isEmpty()) {
                    list.stream().filter(o -> o instanceof RewardList).forEach(l -> {
                        RewardList rewardGroups = (RewardList) l;
                        List<RewardItem> rewardItems = rewardGroups.rollEvent();
                        rewardItems.forEach(i -> {
                            ItemInstance instance = ItemFunctions.createItem(i.itemId);
                            instance.setCount(i.count);
                            instance.setDropTime(0);
                            instance.dropToTheGround(getActor(), getActor().getLoc());
                        });
                    });
                }
                if (task != null) {
                    return;
                }
                task = ThreadPoolManager.getInstance().schedule(() -> controller.getEvent().stopEvent(), 30000);
            }
        }
        if (getActor().hasPrivates()) {
            getActor().getPrivatesList().deletePrivates();
        }
        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtAttacked(Creature attacker, SkillEntry skill, int damage) {
        super.onEvtAttacked(attacker, skill, damage);
        if (attacker.isPlayer()) {
            int chance = Rnd.get(1, 100);
            if (chance < 10)
                getActor().doCast(SkillTable.getInstance().getSkillEntry(1101, 2), attacker, true);
            else if (chance < 20)
                getActor().doCast(SkillTable.getInstance().getSkillEntry(23320, 1), attacker, true);
            else if (chance < 25)
                getActor().doCast(SkillTable.getInstance().getSkillEntry(23322, 1), attacker, true);
            else if (chance < 30)
                getActor().doCast(SkillTable.getInstance().getSkillEntry(23323, 1), attacker, true);
        }
    }

    private final class FuncPMDefMul extends Func {
        boolean minion;

        private FuncPMDefMul(Stats stat, Object owner, final boolean minion) {
            super(stat, 0x30, owner);
            this.minion = minion;
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            if (minion)
                return initialValue * Math.max(1.7, controller.getPlayers().size() * (Rnd.nextBoolean() ? (Rnd.get(1, 20) + creature.getLevel() / 100) : 1.0));
            return initialValue * Math.max(1.7, (controller.getPlayers().size() + (Rnd.nextBoolean() ? 1.0 + creature.getLevel() / 100 : 0)));
        }
    }

    private final class FuncPMAtakMul extends Func {
        boolean minion;

        private FuncPMAtakMul(Stats stat, Object owner, final boolean minion) {
            super(stat, 0x30, owner);
            this.minion = minion;
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            if (minion)
                return initialValue * Math.max(1.7, controller.getPlayers().size() * (Rnd.nextBoolean() ? (Rnd.get(1, 20) + creature.getLevel() / 100) : 1.0));
            return initialValue * Math.max(1.7, controller.getPlayers().size() / (Rnd.nextBoolean() ? 1.2 + creature.getLevel() / 100 : 1.0));
        }
    }
}

