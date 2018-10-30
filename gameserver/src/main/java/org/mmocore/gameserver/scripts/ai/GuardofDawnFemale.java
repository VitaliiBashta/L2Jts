package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.PositionUtils;
import org.mmocore.gameserver.world.World;

/**
 * AI NPC для SSQ Dungeon
 * - Если находят чара в радиусе 300, то кричат в чат и отправляют на точку старта
 * - Не видят, кто находится в хайде
 * - Никогда и никого не атакуют
 *
 * @author pchayka, n0nam3
 */
public class GuardofDawnFemale extends DefaultAI {
    private static final int _aggrorange = 300;
    private static final SkillEntry _skill = SkillTable.getInstance().getSkillEntry(5978, 1);
    private Location _locTele = null;
    private boolean noCheckPlayers = false;

    public GuardofDawnFemale(NpcInstance actor, Location telePoint) {
        super(actor);
        AI_TASK_ATTACK_DELAY = 200;
        setTelePoint(telePoint);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();

        // проверяем игроков вокруг
        if (!noCheckPlayers) {
            checkAroundPlayers(actor);
        }

        return true;
    }

    private boolean checkAroundPlayers(NpcInstance actor) {
        for (Playable target : World.getAroundPlayables(actor, _aggrorange, _aggrorange)) {
            if (target != null && target.isPlayer() && !target.isSilentMoving() && !target.isInvul() && GeoEngine.canSeeTarget(actor, target, false) && PositionUtils.isFacing(actor, target, 150)) {
                actor.doCast(_skill, target, true);
                ChatUtils.say(actor, NpcString.WHO_ARE_YOU_A_NEW_FACE_LIKE_YOU_CANT_APPROACH_THIS_PLACE);
                noCheckPlayers = true;
                ThreadPoolManager.getInstance().schedule(new Teleportation(getTelePoint(), target), 3000);
                return true;
            }
        }
        return false;
    }

    private Location getTelePoint() {
        return _locTele;
    }

    private void setTelePoint(Location loc) {
        _locTele = loc;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected void thinkAttack() {
    }

    @Override
    protected void onIntentionAttack(Creature target) {
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
    }

    @Override
    protected void onEvtAggression(Creature attacker, int aggro) {
    }

    @Override
    protected void onEvtClanAttacked(Creature attacked_member, Creature attacker, int damage) {
    }

    public class Teleportation extends RunnableImpl {

        Location _telePoint = null;
        Playable _target = null;

        public Teleportation(Location telePoint, Playable target) {
            _telePoint = telePoint;
            _target = target;
        }

        @Override
        public void runImpl() {
            _target.teleToLocation(_telePoint);
            noCheckPlayers = false;
        }
    }

}