package org.mmocore.gameserver.ai;

import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class Ranger extends DefaultAI {
    public Ranger(final NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        return super.thinkActive() || defaultThinkBuff();
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        super.onEvtAttacked(attacker, skill, damage);
        final NpcInstance actor = getActor();
        if (actor.isDead() || attacker == null || actor.getDistance(attacker) > 200) {
            return;
        }

        if (actor.isMoving) {
            return;
        }

        int posX = actor.getX();
        int posY = actor.getY();
        int posZ = actor.getZ();

        final int old_posX = posX;
        final int old_posY = posY;
        final int old_posZ = posZ;

        final int signx = posX < attacker.getX() ? -1 : 1;
        final int signy = posY < attacker.getY() ? -1 : 1;

        // int range = (int) ((actor.calculateAttackSpeed()  /1000 * actor.getWalkSpeed() )* 0.71); // was "actor.getPhysicalAttackRange()"    0.71 = sqrt(2) / 2

        final int range = (int) (0.71 * actor.calculateAttackDelay() / 1000 * actor.getMoveSpeed());

        posX += signx * range;
        posY += signy * range;
        posZ = GeoEngine.getHeight(posX, posY, posZ, actor.getGeoIndex());

        if (GeoEngine.canMoveToCoord(old_posX, old_posY, old_posZ, posX, posY, posZ, actor.getGeoIndex())) {
            addTaskMove(posX, posY, posZ);
            addTaskAttack(attacker);
        }
    }

    @Override
    protected boolean createNewTask() {
        return defaultFightTask();
    }

    @Override
    public int getRatePHYS() {
        return 10;
    }

    @Override
    public int getRateDOT() {
        return 15;
    }

    @Override
    public int getRateDEBUFF() {
        return 8;
    }

    @Override
    public int getRateDAM() {
        return 20;
    }

    @Override
    public int getRateSTUN() {
        return 15;
    }

    @Override
    public int getRateBUFF() {
        return 3;
    }

    @Override
    public int getRateHEAL() {
        return 20;
    }
}