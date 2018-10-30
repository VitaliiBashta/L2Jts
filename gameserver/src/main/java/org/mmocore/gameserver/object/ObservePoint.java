package org.mmocore.gameserver.object;

import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.utils.Log;

import java.util.Collections;
import java.util.List;

/**
 * Псевдо-объект, представляющий собой точку просмотра игрока.
 *
 * @author G1ta0
 */
public class ObservePoint extends Creature {
    private final Player player;

    public ObservePoint(final Player player) {
        super(player.getObjectId(), null);
        this.player = player;
        setFlying(true);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public int getMoveSpeed() {
        return 400; // estimated
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();

        if (getReflection() == ReflectionManager.DEFAULT && !isMovementDisabled()) {
            Log.add("Movable observation point from player " + getPlayer() + " somehow appeared in main world !", "warning");
        }
    }

    @Override
    public ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public ItemInstance getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getActiveWeaponItem() {
        return null;
    }

    @Override
    public WeaponTemplate getSecondaryWeaponItem() {
        return null;
    }

    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return false;
    }

    @Override
    public boolean isAttackable(final Creature attacker) {
        return false;
    }

    @Override
    public List<L2GameServerPacket> deletePacketList() {
        return Collections.emptyList();
    }

    @Override
    public boolean isObservePoint() {
        return true;
    }

    @Override
    public boolean isInvul() {
        return true;
    }

    @Override
    public boolean isHealBlocked() {
        return true;
    }

    @Override
    public boolean isEffectImmune() {
        return true;
    }

    @Override
    public void sendPacket(final IBroadcastPacket p) {
        player.sendPacket(p);
    }

    @Override
    public void sendPacket(final IBroadcastPacket... packets) {
        player.sendPacket(packets);
    }

    @Override
    public void sendPacket(final List<? extends IBroadcastPacket> packets) {
        player.sendPacket(packets);
    }

    @Override
    public void validateLocation(final int broadcast) {
    }

    @Override
    public int getINT() {
        return getPlayer().getINT();
    }

    @Override
    public int getSTR() {
        return getPlayer().getSTR();
    }

    @Override
    public int getCON() {
        return getPlayer().getCON();
    }

    @Override
    public int getMEN() {
        return getPlayer().getMEN();
    }

    @Override
    public int getDEX() {
        return getPlayer().getDEX();
    }

    @Override
    public int getWIT() {
        return getPlayer().getWIT();
    }

    @Override
    public int getCriticalHit(Creature target, SkillEntry skill) {
        return getPlayer().getCriticalHit(target, skill);
    }

    @Override
    public int getMAtk(Creature target, SkillEntry skill) {
        return getPlayer().getMAtk(target, skill);
    }

    @Override
    public int getMAtkSpd() {
        return getPlayer().getMAtkSpd();
    }

    @Override
    public int getMaxCp() {
        return getPlayer().getMaxCp();
    }

    @Override
    public double getMaxHp() {
        return getPlayer().getMaxHp();
    }

    @Override
    public int getMaxMp() {
        return getPlayer().getMaxMp();
    }

    @Override
    public int getMDef(Creature target, SkillEntry skill) {
        return getPlayer().getMDef(target, skill);
    }

    @Override
    public double getMovementSpeedMultiplier() {
        return getPlayer().getMovementSpeedMultiplier();
    }

    @Override
    public int getPAtk(Creature target) {
        return getPlayer().getPAtk(target);
    }

    @Override
    public int getPAtkSpd() {
        return getPlayer().getPAtkSpd();
    }

    @Override
    public int getPDef(Creature target) {
        return getPlayer().getPDef(target);
    }

    @Override
    public int getRunSpeed() {
        return getPlayer().getRunSpeed();
    }

    @Override
    public int getWalkSpeed() {
        return getPlayer().getWalkSpeed();
    }

    @Override
    public int getSwimRunSpeed() {
        return getPlayer().getSwimRunSpeed();
    }

    @Override
    public int getSwimWalkSpeed() {
        return getPlayer().getSwimWalkSpeed();
    }

    @Override
    protected void refreshHpMpCp() {
    }

    @Override
    public void sendChanges() {
        getPlayer().getStatsRecorder().sendChanges();
    }
}
