package org.mmocore.gameserver.object.components.player.template;

import org.jts.dataparser.data.holder.PCParameterHolder;
import org.jts.dataparser.data.holder.SettingHolder;
import org.jts.dataparser.data.holder.pcparameter.PCParameterUtils;
import org.jts.dataparser.data.holder.pcparameter.common.LevelParameter;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.jts.dataparser.data.holder.setting.common.PlayerSex;
import org.jts.dataparser.data.holder.setting.model.NewPlayerBaseStat;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
public class PlayerTemplateComponent {
    private final Player playerRef;
    private PlayerRace playerRace;
    private PlayerSex playerSex;

    public PlayerTemplateComponent(final Player player) {
        playerRef = player;
    }

    public Player getPlayer() {
        return playerRef;
    }

    public PlayerRace getPlayerRace() {
        return playerRace;
    }

    public void setPlayerRace(PlayerRace playerRace) {
        this.playerRace = playerRace;
    }

    public PlayerSex getPlayerSex() {
        return playerSex;
    }

    public void setPlayerSex(PlayerSex playerSex) {
        this.playerSex = playerSex;
    }

    public int getBasePhysicalAttack() {
        return PCParameterHolder.getInstance().getBasePhysicalAttack().getFor(getPlayerSex(), getPlayerRace(), getPlayer().isMageClass());
    }

    public int getBaseCriticalRate() {
        return PCParameterHolder.getInstance().getBaseCritical().getFor(getPlayerSex(), getPlayerRace(), getPlayer().isMageClass());
    }

    public int getBaseAttackSpeed() {
        return PCParameterHolder.getInstance().getBaseAttackSpeed().getFor(getPlayerSex(), getPlayerRace(), getPlayer().isMageClass());
    }

    public double[] getBaseDefend() {
        return PCParameterHolder.getInstance().getBaseDefend().getFor(getPlayerSex(), getPlayerRace(), getPlayer().isMageClass());
    }

    public int getBaseMagicAttack() {
        return PCParameterHolder.getInstance().getBaseMagicAttack().getFor(getPlayerSex(), getPlayerRace(), getPlayer().isMageClass());
    }

    public double[] getBaseMagicDefend() {
        return PCParameterHolder.getInstance().getBaseMagicDefend().getFor(getPlayerSex(), getPlayerRace(), getPlayer().isMageClass());
    }

    public int getBaseAttackRange() {
        return PCParameterHolder.getInstance().getBaseAttackRange().getFor(getPlayerSex(), getPlayerRace(), getPlayer().isMageClass());
    }

    public int getBaseRandomDamage() {
        return PCParameterHolder.getInstance().getBaseRandDam().getFor(getPlayerSex(), getPlayerRace(), getPlayer().isMageClass());
    }

    public double[] getMovingSpeed() {
        return PCParameterHolder.getInstance().getMovingSpeed().getFor(getPlayerSex(), getPlayerRace(), getPlayer().isMageClass());
    }

    public double getBaseRegen(LevelParameter levelParameter) {
        switch (levelParameter) {
            case hp:
                return PCParameterHolder.getInstance().getOrgHpRegen().getFor(getPlayerSex(), getPlayerRace(), getPlayer().isMageClass())[getPlayer().getLevel()];
            case mp:
                return PCParameterHolder.getInstance().getOrgMpRegen().getFor(getPlayerSex(), getPlayerRace(), getPlayer().isMageClass())[getPlayer().getLevel()];
            case cp:
                return PCParameterHolder.getInstance().getOrgCpRegen().getFor(getPlayerSex(), getPlayerRace(), getPlayer().isMageClass())[getPlayer().getLevel()];
        }
        return 0;
    }

    public int getPcBreathBonus() {
        return PCParameterHolder.getInstance().getPcBreathBonusTable().getFor(getPlayerSex(), getPlayerRace(), getPlayer().isMageClass());
    }

    public int getPcSafeFallHeight() {
        return PCParameterHolder.getInstance().getPcSafeFallHeightTable().getFor(getPlayerSex(), getPlayerRace(), getPlayer().isMageClass());
    }

    public double[] getPcCollisionBox() {
        return PCParameterHolder.getInstance().getPcCollisionBoxTable().getFor(getPlayerSex(), getPlayerRace(), getPlayer().isMageClass());
    }

    /**
     * @param level          - player level
     * @param levelParameter - see class LevelParameter
     * @return double value (hp, mp, cp)
     */
    public double getLevelParameter(int level, LevelParameter levelParameter) {
        switch (levelParameter) {
            case hp:
                return PCParameterUtils.getLevelParameter(level, PCParameterUtils.getClassDataInfoFor(getPlayer().getPlayerClassComponent().getClassId().getId()).getHp());
            case mp:
                return PCParameterUtils.getLevelParameter(level, PCParameterUtils.getClassDataInfoFor(getPlayer().getPlayerClassComponent().getClassId().getId()).getMp());
            case cp:
                return PCParameterUtils.getLevelParameter(level, PCParameterUtils.getClassDataInfoFor(getPlayer().getPlayerClassComponent().getClassId().getId()).getCp());
        }
        return 0;
    }

    /**
     * @return parsed parameter(INT, STR, DEX...etc)
     */
    public NewPlayerBaseStat getParameter() {
        return SettingHolder.getInstance().getNewPlayerBaseStats().get(getPlayer().getPlayerClassComponent().getClassId().getPlayerClasses().ordinal());
    }
}