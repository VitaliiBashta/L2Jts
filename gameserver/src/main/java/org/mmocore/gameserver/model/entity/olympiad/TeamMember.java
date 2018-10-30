package org.mmocore.gameserver.model.entity.olympiad;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.mmocore.gameserver.configuration.config.clientCustoms.LostDreamCustom;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.impl.DuelEvent;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.SkillEntryType;
import org.mmocore.gameserver.skills.TimeStamp;
import org.mmocore.gameserver.templates.InstantZone;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Log;

import java.util.Calendar;
import java.util.Set;

public class TeamMember {
    private static final int[] CLEAR_CUBIC_SLOT = new int[]{1, 5, 7};
    private final int _objId;
    private final OlympiadGame _game;
    private final CompType _type;
    private final int _side;
    private String _name = StringUtils.EMPTY;
    private String _clanName = StringUtils.EMPTY;
    private int _classId;
    private double _damage;
    private boolean _isDead;
    private Player _player;
    private Location _returnLoc = null;

    public TeamMember(final int obj_id, final String name, final Player player, final OlympiadGame game, final int side) {
        _objId = obj_id;
        _name = name;
        _game = game;
        _type = game.getType();
        _side = side;

        _player = player;
        if (_player == null) {
            return;
        }

        _clanName = player.getClan() == null ? StringUtils.EMPTY : player.getClan().getName();
        _classId = player.getPlayerClassComponent().getActiveClassId();

        player.setOlympiadSide(side);
        player.setOlympiadGame(game);
    }

    public boolean isDead() {
        return _isDead;
    }

    public void doDie() {
        _isDead = true;
    }

    public StatsSet getStat() {
        return Olympiad.nobles.get(_objId);
    }

    public void incGameCount() {
        final StatsSet set = getStat();
        switch (_type) {
            case TEAM:
                set.set(Olympiad.GAME_TEAM_COUNT, set.getInteger(Olympiad.GAME_TEAM_COUNT) + 1);
                break;
            case CLASSED:
                set.set(Olympiad.GAME_CLASSES_COUNT, set.getInteger(Olympiad.GAME_CLASSES_COUNT) + 1);
                break;
            case NON_CLASSED:
                set.set(Olympiad.GAME_NOCLASSES_COUNT, set.getInteger(Olympiad.GAME_NOCLASSES_COUNT) + 1);
                break;
        }
    }

    public void takePointsForCrash() {
        if (!checkPlayer()) {
            if (OlympiadConfig.OLYMPIAD_POINT_TRANSFER) {
                Calendar calendar = Calendar.getInstance();
                long nowTime = calendar.getTimeInMillis();
                calendar.set(Calendar.HOUR_OF_DAY, OlympiadConfig.OLYMPIAD_POINT_TRANSFER_TIMES[0]);
                long startPointTransferTime = calendar.getTimeInMillis();
                calendar.set(Calendar.HOUR_OF_DAY, OlympiadConfig.OLYMPIAD_POINT_TRANSFER_TIMES[1]);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                long stopPointTransferTime = calendar.getTimeInMillis();
                if (nowTime >= startPointTransferTime && nowTime < stopPointTransferTime) {
                    final StatsSet stat = getStat();
                    final int points = stat.getInteger(Olympiad.POINTS);
                    stat.set(Olympiad.POINTS, points - _game.getPointLoose());
                }
            } else {
                final StatsSet stat = getStat();
                final int points = stat.getInteger(Olympiad.POINTS);
                stat.set(Olympiad.POINTS, points - _game.getPointLoose());
            }

            Log.add("Olympiad Result: " + _name + " lost " + _game.getPointLoose() + " points for crash", "olympiad");

            // TODO: Снести подробный лог после исправления беспричинного отъёма очков.
            final Player player = _player;
            if (player == null) {
                Log.add("Olympiad info: " + _name + " crashed coz player == null", "olympiad");
            } else {
                if (player.isLogoutStarted()) {
                    Log.add("Olympiad info: " + _name + " crashed coz player.isLogoutStarted()", "olympiad");
                }
                if (!player.isConnected()) {
                    Log.add("Olympiad info: " + _name + " crashed coz !player.isOnline()", "olympiad");
                }
                if (player.getOlympiadGame() == null) {
                    Log.add("Olympiad info: " + _name + " crashed coz player.getOlympiadGame() == null", "olympiad");
                }
                if (player.getOlympiadObserveGame() != null) {
                    Log.add("Olympiad info: " + _name + " crashed coz player.getOlympiadObserveGame() != null", "olympiad");
                }
            }
        }
    }

    public boolean checkPlayer() {
        final Player player = _player;
        return !(player == null || player.isLogoutStarted() || player.getOlympiadGame() == null || player.isInObserverMode());
    }

    public void portPlayerToArena() {
        final Player player = _player;
        if (!checkPlayer() || player.isTeleporting()) {
            _player = null;
            return;
        }

        final DuelEvent duel = player.getEvent(DuelEvent.class);
        if (duel != null) {
            duel.abortDuel(player);
        }

        _returnLoc = player._stablePoint == null ? player.getReflection().getReturnLoc() == null ? player.getLoc() : player.getReflection().getReturnLoc() : player._stablePoint;

        if (player.isDead()) {
            player.setPendingRevive(true);
        }
        if (player.isSitting()) {
            player.standUp();
        }
        if (player.isRiding() || player.isFlying()) {
            player.dismount();
        }

        player.setTarget(null);
        player.setIsInOlympiadMode(true);

        player.leaveParty();

        final Reflection ref = _game.getReflection();
        final InstantZone instantZone = ref.getInstancedZone();

        final Location tele = Location.findPointToStay(instantZone.getTeleportCoords().get(_side - 1), 50, 50, ref.getGeoIndex());

        player._stablePoint = _returnLoc;
        player.teleToLocation(tele, ref);

        if (_type == CompType.TEAM) {
            player.setTeam(_side == 1 ? TeamType.BLUE : TeamType.RED);
        }

        player.sendPacket(new ExOlympiadMode(_side));
    }

    public void portPlayerBack() {
        final Player player = _player;
        if (player == null) {
            return;
        }

        if (_returnLoc == null) // игрока не портнуло на стадион
        {
            return;
        }
        player.setOlympiadEnchant(false);

        player.setIsInOlympiadMode(false);
        player.setOlympiadSide(-1);
        player.setOlympiadGame(null);

        if (_type == CompType.TEAM) {
            player.setTeam(TeamType.NONE);
        }

        removeBuffs(true);

        // Возвращаем клановые скиллы если репутация положительная.
        if (player.getClan() != null && player.getClan().getReputationScore() >= 0) {
            player.enableSkillsByEntryType(SkillEntryType.CLAN);
        }

        // Add Hero Skills
        if (player.isHero() || player.getCustomPlayerComponent().isTemporalHero()) {
            Hero.addSkills(player);
        }

        if (player.isDead()) {
            player.setCurrentHp(player.getMaxHp(), true);
            player.broadcastPacket(new Revive(player));
        } else {
            player.setCurrentHp(player.getMaxHp(), false);
        }

        player.setCurrentCp(player.getMaxCp());
        player.setCurrentMp(player.getMaxMp());

        // Обновляем скилл лист, после добавления скилов
        player.sendPacket(new SkillList(player));
        player.sendPacket(new ExOlympiadMode(0));
        player.sendPacket(new ExOlympiadMatchEnd());

        player._stablePoint = null;
        player.teleToLocation(_returnLoc, ReflectionManager.DEFAULT);
    }

    public void preparePlayer() {
        final Player player = _player;
        if (player == null) {
            return;
        }

        if (LostDreamCustom.customOlyEnchant)
            player.setOlympiadEnchant(true);

        if (player.isInObserverMode()) {
            if (player.getOlympiadObserveGame() != null) {
                player.leaveOlympiadObserverMode(true);
            } else {
                player.leaveObserverMode();
            }
        }

        // Un activate clan skills
        if (player.getClan() != null) {
            player.disableSkillsByEntryType(SkillEntryType.CLAN);
        }

        // Remove Hero Skills
        if (player.isHero() || player.getCustomPlayerComponent().isTemporalHero()) {
            Hero.removeSkills(player);
        }

        // Удаляем баффы
        removeBuffs(true);

        // cubicdata
        if (OlympiadConfig.OLYMPIAD_DELETE_CUBICS)
            player.deleteCubics();
        else {
            player.removeCubicsSlot(CLEAR_CUBIC_SLOT, true);
        }
        player.deleteAgathion();


        // Сброс кулдауна всех скилов, время отката которых меньше 15 минут
        for (final TimeStamp sts : player.getSkillReuses()) {
            if (sts == null) {
                continue;
            }
            final SkillEntry skill = player.getKnownSkill(sts.getId());
            if (skill == null || skill.getLevel() != sts.getLevel()) {
                continue;
            }
            if (skill.getTemplate().getReuseDelay() <= 15 * 60000L) {
                player.enableSkill(skill);
            }
        }

        // Обновляем скилл лист, после удаления скилов
        player.sendPacket(new SkillList(player));
        // Обновляем куллдаун, после сброса
        player.sendPacket(new SkillCoolTime(player));

        // Remove Hero weapons
        final ItemInstance wpn = player.getActiveWeaponInstance();
        if (wpn != null && wpn.isHeroWeapon()) {
            player.getInventory().unEquipItem(wpn);
        }

        // remove bsps/sps/ss automation
        final Set<Integer> activeSoulShots = player.getAutoSoulShot();
        for (final int itemId : activeSoulShots) {
            player.removeAutoSoulShot(itemId);
            player.sendPacket(new ExAutoSoulShot(itemId, false));
        }

        // Разряжаем заряженные соул и спирит шоты
        final ItemInstance weapon = player.getActiveWeaponInstance();
        if (weapon != null) {
            weapon.setChargedSpiritshot(ItemInstance.CHARGED_NONE);
            weapon.setChargedSoulshot(ItemInstance.CHARGED_NONE);
        }

        //player.getInventory().refreshEquip();
        heal();
    }

    public void heal() {
        final Player player = _player;
        if (player == null) {
            return;
        }

        player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
        player.setCurrentCp(player.getMaxCp());
        player.broadcastUserInfo(true);
    }

    public void revive() {
        final Player player = _player;
        if (player == null) {
            return;
        }

        if (player.isInOlympiadMode() && player.getCurrentHp() <= 1) {
            if (player.isDead())
                player.doRevive(100.0);

            player.setCurrentHp(2, true);
        }
    }

    public void removeBuffs(final boolean fromSummon) {
        final Player player = _player;
        if (player == null) {
            return;
        }

        player.abortAttack(true, false);
        if (player.isCastingNow()) {
            player.abortCast(true, true);
        }

        for (final Effect e : player.getEffectList().getAllEffects()) {
            if (e == null) {
                continue;
            }
            if (e.getSkill().getTemplate().isToggle()) {
                continue;
            }
            player.sendPacket(new SystemMessage(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill()));
            e.exit();
        }

        final Servitor summon = player.getServitor();
        if (summon != null) {
            summon.abortAttack(true, false);
            if (summon.isCastingNow()) {
                summon.abortCast(true, true);
            }

            if (fromSummon) {
                if (summon.isPet()) {
                    summon.unSummon(false, false);
                } else {
                    summon.getEffectList().stopAllEffects();
                }
            }
        }
    }

    public void saveNobleData() {
        OlympiadDatabase.saveNobleData(_objId);
    }

    public void logout() {
        _player = null;
    }

    public Player getPlayer() {
        return _player;
    }

    public String getName() {
        return _name;
    }

    public void addDamage(final double d) {
        _damage += d;
    }

    public double getDamage() {
        return _damage;
    }

    public String getClanName() {
        return _clanName;
    }

    public int getClassId() {
        return _classId;
    }

    public int getObjectId() {
        return _objId;
    }
}