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
import org.mmocore.gameserver.world.World;

/**
 * AI NPC для SSQ Dungeon
 * - Если находят чара в радиусе 120, то кричат в чат и отправляют на точку старта
 * - Видят и цепляют тех, кто находится в хайде
 * - Никогда и никого не атакуют
 *
 * @author n0nam3
 * @date 20/09/2010 19:03
 */
public class GuardofDawn extends DefaultAI {
    private static final int _aggrorange = 150;
    private static final SkillEntry _skill = SkillTable.getInstance().getSkillEntry(5978, 1);
    private Location _locStart = null;
    private Location _locEnd = null;
    private Location _locTele = null;
    private boolean moveToEnd = true;
    private boolean noCheckPlayers = false;

    public GuardofDawn(NpcInstance actor, Location locationEnd, Location telePoint) {
        super(actor);
        AI_TASK_ATTACK_DELAY = 200;
        setStartPoint(((NpcInstance) actor).getSpawnedLoc()); // точка старта, по сути место спавна.
        setEndPoint(locationEnd);
        setTelePoint(telePoint);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();

        // проверяем игроков вокруг
        if (!noCheckPlayers) {
            checkAroundPlayers(actor);
        }

        // если есть задания - делаем их
        if (_def_think) {
            doTask();
            return true;
        }

        // заданий нет, значит можно давать новое, для этого ставим moveToEnd обратное значение
        moveToEnd = !moveToEnd;

        // добавляем задачу на движение
        if (!moveToEnd) {
            addTaskMove(getEndPoint(), true);
        } else {
            addTaskMove(getStartPoint(), true);
        }
        doTask();

        return true;
    }

    private boolean checkAroundPlayers(NpcInstance actor) {
        for (Playable target : World.getAroundPlayables(actor, _aggrorange, _aggrorange)) {
            if (target != null && target.isPlayer() && !target.isInvul() && GeoEngine.canSeeTarget(actor, target, false)) {
                actor.doCast(_skill, target, true);
                ChatUtils.say(actor, NpcString.INTRUDER_PROTECT_THE_PRIESTS_OF_DAWN);
                noCheckPlayers = true;
                ThreadPoolManager.getInstance().schedule(new Teleportation(getTelePoint(), target), 3000);
                return true;
            }
        }
        return false;
    }

    private Location getStartPoint() {
        return _locStart;
    }

    private void setStartPoint(Location loc) {
        _locStart = loc;
    }

    private Location getEndPoint() {
        return _locEnd;
    }

    private void setEndPoint(Location loc) {
        _locEnd = loc;
    }

    private Location getTelePoint() {
        return _locTele;
    }

    private void setTelePoint(Location loc) {
        _locTele = loc;
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