package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;

/**
 * AI боса Core:
 * <br> - Бубнит при атаке и смерти.
 * <br> - При смерти играет музыку и спаунит обратные порталы, которые удаляются через 15 минут
 *
 * @author SYS
 */
public class Core extends Fighter {
    private static final int TELEPORTATION_CUBIC_ID = 31842;
    private static final Location CUBIC_1_POSITION = new Location(16502, 110165, -6394, 0);
    private static final Location CUBIC_2_POSITION = new Location(18948, 110165, -6394, 0);
    private static final int CUBIC_DESPAWN_TIME = 15 * 60 * 1000; // 15 min
    private boolean _firstTimeAttacked = true;

    public Core(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (_firstTimeAttacked) {
            ChatUtils.say(actor, NpcString.A_NONPERMITTED_TARGET_HAS_BEEN_DISCOVERED);
            ChatUtils.say(actor, NpcString.INTRUDER_REMOVAL_SYSTEM_INITIATED);
            _firstTimeAttacked = false;
        } else if (Rnd.chance(1)) {
            ChatUtils.say(actor, NpcString.REMOVING_INTRUDERS);
        }
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        actor.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, 0, actor.getLoc()));
        ChatUtils.say(actor, NpcString.A_FATAL_ERROR_HAS_OCCURRED);
        ChatUtils.say(actor, NpcString.SYSTEM_IS_BEING_SHUT_DOWN);
        ChatUtils.say(actor, NpcString.DOT_DOT_DOT_DOT_DOT_DOT_DOT);

        try {
            NpcInstance cubic1 = NpcHolder.getInstance().getTemplate(TELEPORTATION_CUBIC_ID).getNewInstance();
            cubic1.setReflection(actor.getReflection());
            cubic1.setCurrentHpMp(cubic1.getMaxHp(), cubic1.getMaxMp(), true);
            cubic1.spawnMe(CUBIC_1_POSITION);

            NpcInstance cubic2 = NpcHolder.getInstance().getTemplate(TELEPORTATION_CUBIC_ID).getNewInstance();
            cubic2.setReflection(actor.getReflection());
            cubic2.setCurrentHpMp(cubic1.getMaxHp(), cubic1.getMaxMp(), true);
            cubic2.spawnMe(CUBIC_2_POSITION);

            ThreadPoolManager.getInstance().schedule(new DeSpawnScheduleTimerTask(cubic1, cubic2), CUBIC_DESPAWN_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        _firstTimeAttacked = true;
        super.onEvtDead(killer);
    }

    static class DeSpawnScheduleTimerTask extends RunnableImpl {
        final NpcInstance cubic1;
        final NpcInstance cubic2;

        public DeSpawnScheduleTimerTask(NpcInstance cubic1, NpcInstance cubic2) {
            this.cubic1 = cubic1;
            this.cubic2 = cubic2;
        }

        @Override
        public void runImpl() {
            cubic1.deleteMe();
            cubic2.deleteMe();
        }
    }
}