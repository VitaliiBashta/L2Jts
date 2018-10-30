package org.mmocore.gameserver.model;

import org.apache.commons.lang3.math.NumberUtils;
import org.mmocore.commons.geometry.CustomPolygon;
import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.FormulasConfig;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.manager.games.HandysBlockCheckerManager;
import org.mmocore.gameserver.manager.games.HandysBlockCheckerManager.ArenaParticipantsHolder;
import org.mmocore.gameserver.model.base.*;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.instances.ChestInstance;
import org.mmocore.gameserver.model.instances.DecoyInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.team.CommandChannel;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.FlyToLocation.FlyType;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.*;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.EffectType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.effects.EffectTemplate;
import org.mmocore.gameserver.skills.skillclasses.*;
import org.mmocore.gameserver.stats.Formulas;
import org.mmocore.gameserver.stats.StatTemplate;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.conditions.Condition;
import org.mmocore.gameserver.stats.funcs.FuncTemplate;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.PositionUtils;
import org.mmocore.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public abstract class Skill extends StatTemplate implements Cloneable {
    public static final Logger _log = LoggerFactory.getLogger(Skill.class);
    public static final Skill[] EMPTY_ARRAY = new Skill[0];
    //public static final int SKILL_CUBIC_MASTERY = 143;
    public static final int SKILL_CRAFTING = 172;
    public static final int SKILL_POLEARM_MASTERY = 216;
    public static final int SKILL_CRYSTALLIZE = 248;
    public static final int SKILL_WEAPON_MAGIC_MASTERY1 = 249;
    public static final int SKILL_WEAPON_MAGIC_MASTERY2 = 250;
    public static final int SKILL_BLINDING_BLOW = 321;
    public static final int SKILL_STRIDER_ASSAULT = 325;
    public static final int SKILL_WYVERN_AEGIS = 327;
    public static final int SKILL_BLUFF = 358;
    public static final int SKILL_HEROIC_MIRACLE = 395;
    public static final int SKILL_HEROIC_BERSERKER = 396;
    public static final int SKILL_SOUL_MASTERY = 467;
    public static final int SKILL_TRANSFORM_DISPEL = 619;
    public static final int SKILL_FINAL_FLYING_FORM = 840;
    public static final int SKILL_AURA_BIRD_FALCON = 841;
    public static final int SKILL_AURA_BIRD_OWL = 842;
    public static final int SKILL_DETECTION = 933;
    public static final int SKILL_RECHARGE = 1013;
    public static final int SKILL_TRANSFER_PAIN = 1262;
    public static final int SKILL_FISHING_MASTERY = 1315;
    public static final int SKILL_NOBLESSE_BLESSING = 1323;
    public static final int SKILL_SUMMON_CP_POTION = 1324;
    public static final int SKILL_FORTUNE_OF_NOBLESSE = 1325;
    public static final int SKILL_HARMONY_OF_NOBLESSE = 1326;
    public static final int SKILL_SYMPHONY_OF_NOBLESSE = 1327;
    public static final int SKILL_HEROIC_VALOR = 1374;
    public static final int SKILL_HEROIC_GRANDEUR = 1375;
    public static final int SKILL_HEROIC_DREAD = 1376;
    public static final int SKILL_MYSTIC_IMMUNITY = 1411;
    public static final int SKILL_RAID_BLESSING = 2168;
    public static final int SKILL_HINDER_STRIDER = 4258;
    public static final int SKILL_WYVERN_BREATH = 4289;
    public static final int SKILL_RAID_CURSE = 4515;
    public static final int SKILL_CHARM_OF_COURAGE = 5041;
    public static final int SKILL_EVENT_TIMER = 5239;
    public static final int SKILL_BATTLEFIELD_DEATH_SYNDROME = 5660;
    public static final int SKILL_SERVITOR_SHARE = 1557;
    protected final List<Integer> _teachers; // which NPC teaches
    protected final List<ClassId> _canLearn; // which classes can learn
    protected final int[] _itemConsume;
    protected final int[] _itemConsumeId;
    protected final int _referenceItemId; // для талисманов
    protected final int _referenceItemMpConsume; // количество потребляемой мп талисмана
    protected final boolean _isAltUse;
    protected final boolean _isBehind;
    protected final boolean _isCancelable;
    protected final boolean _isCorpse;
    protected final boolean _isCommon;
    protected final boolean _isItemHandler;
    protected final boolean _isOffensive;
    protected final boolean _isPvpSkill;
    protected final boolean _isNotUsedByAI;
    protected final boolean _isFishingSkill;
    protected final boolean _isPvm;
    protected final boolean _isForceUse;
    protected final boolean _isNewbie;
    protected final boolean _isPreservedOnDeath;
    protected final boolean _isHeroic;
    protected final boolean _isSaveable;
    protected final boolean _isSkillTimePermanent;
    protected final boolean _isReuseDelayPermanent;
    protected final boolean _isReflectable;
    protected final boolean _isSuicideAttack;
    protected final boolean _isShieldignore;
    protected final boolean _isUndeadOnly;
    protected final Ternary _isUseSS;
    protected final boolean _isSoulBoost;
    protected final boolean _isChargeBoost;
    protected final boolean _isUsingWhileCasting;
    protected final boolean _isIgnoreResists;
    protected final boolean _isIgnoreInvul;
    protected final boolean _isTrigger;
    protected final boolean _isNotAffectedByMute;
    protected final boolean _basedOnTargetDebuff;
    protected final boolean _deathlink;
    protected final boolean _hideStartMessage;
    protected final boolean _hideUseMessage;
    protected final boolean _skillInterrupt;
    protected final boolean _flyingTransformUsage;
    protected final boolean _isFlyingTransform;
    protected final boolean _canUseTeleport;
    protected final boolean _isSelfDispellable;
    protected final SkillType _skillType;
    protected final SkillTargetType _targetType;
    protected final SkillTrait _traitType;
    protected final BaseStats _saveVs;
    protected final Element _element;
    protected final FlyType _flyType;
    protected final boolean _flyToBack;
    protected final int _level;
    protected final int _displayId;
    protected final int _activateRate;
    protected final int _cancelTarget;
    protected final int _coolTime;
    protected final int _delayedEffect;
    protected final int _effectPoint;
    protected final int _elementPower;
    protected final int _flyRadius;
    protected final int _levelModifier;
    protected final int _matak;
    protected final int _minPledgeClass;
    protected final int _minRank;
    protected final int _negatePower;
    protected final int _negateSkill;
    protected final int _npcId;
    protected final int _numCharges;
    protected final int _skillRadius;
    protected final int _soulsConsume;
    protected final int _symbolId;
    protected final int _weaponsAllowed;
    protected final int _castCount;
    protected final int _criticalRate;
    protected final int _counterAttackCount;

    protected final double _powerPvP;
    protected final double _powerPvE;
    protected final double _lethal1;
    protected final double _lethal2;
    protected final double _absorbPart;
    protected final String _baseValues;
    protected final String _icon;
    private final int hashCode;
    private final SkillMastery _skillMastery;
    private final boolean _UDSafe;
    public boolean _isStandart = false;
    protected EffectTemplate[] _effectTemplates = EffectTemplate.EMPTY_ARRAY;
    protected AddedSkill[] _addedSkills = AddedSkill.EMPTY_ARRAY;
    protected boolean _isOverhit;
    protected boolean _isCubicSkill = false;
    protected SkillOpType _operateType;
    protected SkillMagicType _magicType;
    protected NextAction _nextAction;
    protected Condition[] _preCondition = Condition.EMPTY_ARRAY;
    protected int _id;
    protected int _baseLevel;
    protected int _displayLevel;
    protected int _castRange;
    protected int _hitTime;
    protected int _hpConsume;
    protected int _magicLevel;
    protected int _skillInterruptTime;
    protected int _enchantLevelCount;
    protected long _reuseDelay;
    protected double _power;
    protected double _mpConsume1;
    protected double _mpConsume2;
    protected String _name;

    private boolean _hasNotSelfEffects;
    private boolean haveEffectsIgnoreInvul;

    /**
     * Внимание!!! У наследников вручную надо поменять тип на public
     *
     * @param set парамерты скилла
     */
    protected Skill(final StatsSet set) {
        //_set = set;
        _id = set.getInteger("skill_id");
        _level = set.getInteger("level");
        _displayId = set.getInteger("displayId", _id);
        _displayLevel = set.getInteger("displayLevel", _level);
        _baseLevel = set.getInteger("base_level");
        _name = set.getString("name");
        _operateType = set.getEnum("operateType", SkillOpType.class);
        _isNewbie = set.getBool("isNewbie", false);
        _isSelfDispellable = set.getBool("isSelfDispellable", true);
        _isPreservedOnDeath = set.getBool("isPreservedOnDeath", false);
        _isHeroic = set.getBool("isHeroic", false);
        _isAltUse = set.getBool("altUse", false);
        _mpConsume1 = set.getInteger("mpConsume1", 0);
        _mpConsume2 = set.getInteger("mpConsume2", 0);
        _hpConsume = set.getInteger("hpConsume", 0);
        _soulsConsume = set.getInteger("soulsConsume", 0);
        _isSoulBoost = set.getBool("soulBoost", false);
        _isChargeBoost = set.getBool("chargeBoost", false);
        _isUsingWhileCasting = set.getBool("isUsingWhileCasting", false);
        _matak = set.getInteger("mAtk", 0);
        _isUseSS = Ternary.valueOf(set.getString("useSS", Ternary.DEFAULT.toString()).toUpperCase());
        _magicLevel = set.getInteger("magicLevel", 0);
        _castCount = set.getInteger("castCount", 0);
        _castRange = set.getInteger("castRange", 40);
        _baseValues = set.getString("baseValues", null);

        final String s1 = set.getString("itemConsumeCount", "");
        final String s2 = set.getString("itemConsumeId", "");

        if (s1.isEmpty()) {
            _itemConsume = org.apache.commons.lang3.ArrayUtils.EMPTY_INT_ARRAY;
        } else {
            final String[] s = s1.split(" ");
            _itemConsume = new int[s.length];
            for (int i = 0; i < s.length; i++) {
                _itemConsume[i] = Integer.parseInt(s[i]);
            }
        }

        if (s2.isEmpty()) {
            _itemConsumeId = org.apache.commons.lang3.ArrayUtils.EMPTY_INT_ARRAY;
        } else {
            final String[] s = s2.split(" ");
            _itemConsumeId = new int[s.length];
            for (int i = 0; i < s.length; i++) {
                _itemConsumeId[i] = Integer.parseInt(s[i]);
            }
        }

        _referenceItemId = set.getInteger("referenceItemId", 0);
        _referenceItemMpConsume = set.getInteger("referenceItemMpConsume", 0);

        _isItemHandler = set.getBool("isHandler", false);
        _isCommon = set.getBool("isCommon", false);
        _isSaveable = set.getBool("isSaveable", true);
        _coolTime = set.getInteger("coolTime", 0);
        _skillInterruptTime = set.getInteger("hitCancelTime", 0);
        _reuseDelay = set.getLong("reuseDelay", 0);
        _hitTime = Math.max(set.getInteger("hitTime", 0), _skillInterruptTime); // в оффcкриптах используется то что больше.
        _skillRadius = set.getInteger("skillRadius", 80);
        _targetType = set.getEnum("target", SkillTargetType.class);
        _magicType = set.getEnum("magicType", SkillMagicType.class, SkillMagicType.PHYSIC);
        _traitType = set.getEnum("trait", SkillTrait.class, null);
        _saveVs = set.getEnum("saveVs", BaseStats.class, null);
        _hideStartMessage = set.getBool("isHideStartMessage", false);
        _hideUseMessage = set.getBool("isHideUseMessage", false);
        _isUndeadOnly = set.getBool("undeadOnly", false);
        _isCorpse = set.getBool("corpse", false);
        _power = set.getDouble("power", 0.);
        _powerPvP = set.getDouble("powerPvP", 0.);
        _powerPvE = set.getDouble("powerPvE", 0.);
        _effectPoint = set.getInteger("effectPoint", 0);
        _nextAction = NextAction.valueOf(set.getString("nextAction", "DEFAULT").toUpperCase());
        _skillType = set.getEnum("skillType", SkillType.class);
        _isSuicideAttack = set.getBool("isSuicideAttack", false);
        _isSkillTimePermanent = set.getBool("isSkillTimePermanent", false);
        _isReuseDelayPermanent = set.getBool("isReuseDelayPermanent", false);
        _deathlink = set.getBool("deathlink", false);
        _basedOnTargetDebuff = set.getBool("basedOnTargetDebuff", false);
        _isNotUsedByAI = set.getBool("isNotUsedByAI", false);
        _isIgnoreResists = set.getBool("isIgnoreResists", false);
        _isIgnoreInvul = set.getBool("isIgnoreInvul", false);
        _isTrigger = set.getBool("isTrigger", false);
        _isNotAffectedByMute = set.getBool("isNotAffectedByMute", false);
        _flyingTransformUsage = set.getBool("flyingTransformUsage", false);
        _isFlyingTransform = set.getBool("isFlyingTransform", false);
        _canUseTeleport = set.getBool("canUseTeleport", true);

        if (NumberUtils.isNumber(set.getString("element", "NONE"))) {
            _element = Element.getElementById(set.getInteger("element", -1));
        } else {
            _element = Element.getElementByName(set.getString("element", "none").toUpperCase());
        }

        _elementPower = set.getInteger("elementPower", 0);

        _activateRate = set.getInteger("activateRate", -1);
        _levelModifier = set.getInteger("levelModifier", 1);
        _isCancelable = set.getBool("cancelable", true);
        _isReflectable = set.getBool("reflectable", true);
        _isShieldignore = set.getBool("shieldignore", false);
        _criticalRate = set.getInteger("criticalRate", 0);
        _isOverhit = set.getBool("overHit", false);
        _weaponsAllowed = set.getInteger("weaponsAllowed", 0);
        _minPledgeClass = set.getInteger("minPledgeClass", 0);
        _minRank = set.getInteger("minRank", 0);
        _isOffensive = set.getBool("isOffensive", _skillType.isOffensive());
        _isPvpSkill = set.getBool("isPvpSkill", _skillType.isPvpSkill());
        _isFishingSkill = set.getBool("isFishingSkill", false);
        _isPvm = set.getBool("isPvm", _skillType.isPvM());
        _isForceUse = set.getBool("isForceUse", false);
        _isBehind = set.getBool("behind", false);
        _symbolId = set.getInteger("symbolId", 0);
        _npcId = set.getInteger("npcId", 0);
        _flyType = FlyType.valueOf(set.getString("flyType", "NONE").toUpperCase());
        _flyToBack = set.getBool("flyToBack", false);
        _flyRadius = set.getInteger("flyRadius", 200);
        _negateSkill = set.getInteger("negateSkill", 0);
        _negatePower = set.getInteger("negatePower", Integer.MAX_VALUE);
        _numCharges = set.getInteger("num_charges", 0);
        _delayedEffect = set.getInteger("delayedEffect", 0);
        _cancelTarget = set.getInteger("cancelTarget", 0);
        _skillInterrupt = set.getBool("skillInterrupt", false);
        _lethal1 = set.getDouble("lethal1", 0.);
        _lethal2 = set.getDouble("lethal2", 0.);
        _absorbPart = set.getDouble("absorbPart", 0.);
        _icon = set.getString("icon", "");
        _counterAttackCount = set.getInteger("counterAttackCount", 0);

        StringTokenizer st = new StringTokenizer(set.getString("addSkills", ""), ";");
        while (st.hasMoreTokens()) {
            final int id = Integer.parseInt(st.nextToken());
            int level = Integer.parseInt(st.nextToken());
            if (level == -1) {
                level = _level;
            }
            _addedSkills = ArrayUtils.add(_addedSkills, new AddedSkill(id, level));
        }

        if (_nextAction == NextAction.DEFAULT) {
            switch (_skillType) {
                case PDAM:
                case CPDAM:
                case LETHAL_SHOT:
                case SPOIL:
                case SOWING:
                case STUN:
                case DRAIN_SOUL:
                    _nextAction = NextAction.ATTACK;
                    break;
                default:
                    _nextAction = NextAction.NONE;
            }
        }

        final String canLearn = set.getString("canLearn", null);
        if (canLearn == null) {
            _canLearn = null;
        } else {
            _canLearn = new ArrayList<>();
            st = new StringTokenizer(canLearn, " \r\n\t,;");
            while (st.hasMoreTokens()) {
                final String cls = st.nextToken();
                _canLearn.add(ClassId.valueOf(cls));
            }
        }

        final String teachers = set.getString("teachers", null);
        if (teachers == null) {
            _teachers = null;
        } else {
            _teachers = new ArrayList<>();
            st = new StringTokenizer(teachers, " \r\n\t,;");
            while (st.hasMoreTokens()) {
                final String npcid = st.nextToken();
                _teachers.add(Integer.parseInt(npcid));
            }
        }

        hashCode = SkillTable.getSkillHashCode(this);

        _skillMastery = isMusic() ? SkillMastery.DOUBLE_TIME : getSkillType().getDefaultSkillMastery();
        _UDSafe = set.getBool("UDSafe", !isOffensive());

        for (EffectTemplate et : _effectTemplates)
            if (et._ignoreInvul) {
                haveEffectsIgnoreInvul = true;
            }
    }

    public final boolean getWeaponDependancy(final Creature activeChar) {
        if (_weaponsAllowed == 0) {
            return true;
        }

        if (activeChar.getActiveWeaponInstance() != null && activeChar.getActiveWeaponItem() != null) {
            if ((activeChar.getActiveWeaponItem().getItemType().mask() & _weaponsAllowed) != 0) {
                return true;
            }
        }

        if (activeChar.getSecondaryWeaponInstance() != null && activeChar.getSecondaryWeaponItem() != null) {
            if ((activeChar.getSecondaryWeaponItem().getItemType().mask() & _weaponsAllowed) != 0) {
                return true;
            }
        }

        activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(_displayId, _displayLevel));

        return false;
    }

    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        final Player player = activeChar.getPlayer();

        if (activeChar.isDead()) {
            return false;
        }

        if (target != null && activeChar.getReflection() != target.getReflection()) {
            activeChar.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
            return false;
        }

        if (!getWeaponDependancy(activeChar)) {
            return false;
        }

        if (skillEntry.isDisabled()) {
            return false;
        }

        if (first && activeChar.isSkillDisabled(skillEntry)) {
            activeChar.sendReuseMessage(skillEntry);
            return false;
        }

        if (first) {
            double mpConsume2 = _mpConsume2;
            if (isMusic()) {
                final double inc = mpConsume2 / 2;
                for (final Effect e : activeChar.getEffectList().getAllEffects()) {
                    if (e.getSkill().getId() != getId() && e.getSkill().getTemplate().isMusic() && e.getTimeLeft() > 30) {
                        mpConsume2 += inc;
                    }
                }

                mpConsume2 = activeChar.calcStat(Stats.MP_DANCE_SKILL_CONSUME, mpConsume2, target, skillEntry);
            } else if (isMagic()) {
                mpConsume2 = activeChar.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, target, skillEntry);
            } else {
                mpConsume2 = activeChar.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, target, skillEntry);
            }

            // DS: Clarity не влияет на mpConsume1
            if (activeChar.getCurrentMp() < _mpConsume1 + mpConsume2) {
                activeChar.sendPacket(SystemMsg.NOT_ENOUGH_MP);
                return false;
            }
        }

        if (activeChar.getCurrentHp() < _hpConsume + 1) {
            activeChar.sendPacket(SystemMsg.NOT_ENOUGH_HP);
            return false;
        }

        if (!(_isItemHandler || _isAltUse) && activeChar.isMuted(skillEntry)) {
            return false;
        }

        if (_soulsConsume > activeChar.getConsumedSouls()) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SOULS);
            return false;
        }

        if (player != null) {
            if (player.isInFlyingTransform() && _isItemHandler && !flyingTransformUsage()) {
                if (_itemConsumeId.length > 0) {
                    player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(_itemConsumeId[0]));
                } else {
                    player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
                }
                return false;
            }

            if (player.isInBoat()) {
                // На воздушных кораблях можно использовать скилы-хэндлеры
                if (player.getBoat().isAirShip() && !_isItemHandler) {
                    return false;
                }

                // С морских кораблей можно ловить рыбу
                if (player.getBoat().isVehicle() && !(this instanceof FishingSkill || this instanceof ReelingPumping)) {
                    return false;
                }
            }

            if (player.isInObserverMode()) {
                activeChar.sendPacket(SystemMsg.OBSERVERS_CANNOT_PARTICIPATE);
                return false;
            }

            if (first && _itemConsume.length > 0) {
                for (int i = 0; i < _itemConsume.length; i++) {
                    Inventory inv = ((Playable) activeChar).getInventory();
                    if (inv == null) {
                        inv = player.getInventory();
                    }
                    final ItemInstance requiredItems = inv.getItemByItemId(_itemConsumeId[i]);
                    if (requiredItems == null || requiredItems.getCount() < _itemConsume[i]) {
                        if (activeChar == player) {
                            player.sendPacket(isHandler() ? SystemMsg.NOT_ENOUGH_ITEMS : SystemMsg.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL);
                        }
                        return false;
                    }
                }
            }

            if (player.isFishing() && !isFishingSkill() && !altUse() && !(activeChar.isSummon() || activeChar.isPet())) {
                if (activeChar == player) {
                    player.sendPacket(SystemMsg.ONLY_FISHING_SKILLS_MAY_BE_USED_AT_THIS_TIME);
                }
                return false;
            }
        }

        // Warp (628) && Shadow Step (821) can be used while rooted
        if (getFlyType() != FlyType.NONE && getId() != 628 && getId() != 821 && (activeChar.isImmobilized() || activeChar.isRooted())) {
            activeChar.getPlayer().sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
            return false;
        }

        // Fly скиллы нельзя использовать слишком близко
        if (first && target != null && getFlyType() == FlyType.CHARGE && activeChar.isInRange(target.getLoc(), Math.min(150, getFlyRadius()))) {
            activeChar.getPlayer().sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_SPACE_TO_MOVE_THE_SKILL_CANNOT_BE_USED);
            return false;
        }

        final SystemMsg msg = checkTarget(activeChar, target, target, forceUse, first);
        if (msg != null && activeChar.getPlayer() != null) {
            activeChar.getPlayer().sendPacket(msg);
            return false;
        }

        if (first) {
            return checkPreConditions(skillEntry, activeChar, target);
        }

        return true;
    }

    public boolean checkPreConditions(final SkillEntry skillEntry, final Creature activeChar, final Creature target) {
        if (_preCondition.length == 0) {
            return true;
        }

        for (final Condition с : _preCondition) {
            if (!с.test(activeChar, target, skillEntry)) {
                final SystemMsg cond_msg = с.getSystemMsg();
                if (cond_msg != null) {
                    if (cond_msg.size() > 0) {
                        activeChar.sendPacket(new SystemMessage(cond_msg).addSkillName(skillEntry));
                    } else {
                        activeChar.sendPacket(cond_msg);
                    }
                }
                return false;
            }
        }

        return true;
    }

    public SystemMsg checkTarget(final Creature activeChar, final Creature target, final Creature aimingTarget, final boolean forceUse, final boolean first) {
        if (target == activeChar && isNotTargetAoE() || target == activeChar.getServitor() && _targetType == SkillTargetType.TARGET_PET_AURA) {
            return null;
        }
        if (target == null || isOffensive() && target == activeChar) {
            return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
        }
        if (activeChar.getReflection() != target.getReflection()) {
            return SystemMsg.CANNOT_SEE_TARGET;
        }
        // Попадает ли цель в радиус действия в конце каста
        if (!first && target != activeChar && target == aimingTarget && getCastRange() > 0 && getCastRange() != 32767 && !activeChar.isInRange(target.getLoc(), getCastRange() + (getCastRange() < 200 ? 400 : 500))) {
            return SystemMsg.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED;
        }
        // Для этих скиллов дальнейшие проверки не нужны
        if (_skillType == SkillType.TAKECASTLE || _skillType == SkillType.TAKEFORTRESS || _skillType == SkillType.TAKEFLAG) {
            return null;
        }
        // Конусообразные скиллы
        if (!first && target != activeChar && (_targetType == SkillTargetType.TARGET_MULTIFACE || _targetType == SkillTargetType.TARGET_MULTIFACE_AURA || _targetType == SkillTargetType.TARGET_TUNNEL) && (_isBehind ? PositionUtils.isFacing(activeChar, target, 120) : !PositionUtils.isFacing(activeChar, target, 60))) {
            return SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE;
        }
        // Проверка на каст по трупу
        if (target.isDead() != _isCorpse && _targetType != SkillTargetType.TARGET_AREA_AIM_CORPSE && _isUndeadOnly && !target.isUndead()) {
            return SystemMsg.INVALID_TARGET;
        }
        if (target.isDead() == _isCorpse && (_targetType == SkillTargetType.TARGET_CORPSE || _targetType == SkillTargetType.TARGET_CORPSE_PLAYER || _targetType == SkillTargetType.TARGET_AREA_AIM_CORPSE)) {
            final Player player = activeChar.getPlayer();
            if (player != null) {
                if (_skillType == SkillType.RESURRECT) {
                    final Player pcTarget = target.getPlayer();
                    if (pcTarget != null) {
                        if (!forceUse && (player.atMutualWarWith(pcTarget) || player.atWarWith(pcTarget))) {
                            return SystemMsg.INVALID_TARGET;
                        }
                        if (!forceUse && (pcTarget.getPvpFlag() != 0 || pcTarget.getKarma() > 0)) {
                            Clan pcTargetClan = pcTarget.getClan();
                            Party pcTargetParty = pcTarget.getParty();
                            CommandChannel pcTargetPartyCC = null;
                            if (pcTargetParty != null) {
                                pcTargetPartyCC = pcTarget.getParty().getCommandChannel();
                            }
                            if (pcTargetClan == player.getClan() || pcTargetParty == player.getParty() || (pcTarget.getParty() != null && pcTarget.getParty().getCommandChannel() == pcTargetPartyCC)) {
                                return null;
                            }
                            return SystemMsg.INVALID_TARGET;
                        }
                    }
                }
            }
            return null;
        }
        // Для различных бутылок, и для скилла кормления, дальнейшие проверки не нужны
        if (_isAltUse || _targetType == SkillTargetType.TARGET_UNLOCKABLE || _targetType == SkillTargetType.TARGET_CHEST) {
            return null;
        }
        // проверка возможности движения к цели для Fly скилов
        if (first && target != null && getFlyType() == FlyType.CHARGE) {
            if (GeoEngine.MoveList(activeChar.getX(), activeChar.getY(), activeChar.getZ(), target.getX(), target.getY(), activeChar.getGeoIndex(), true) == null) {
                activeChar.getAI().clearNextAction();
                return SystemMsg.THE_TARGET_IS_LOCATED_WHERE_YOU_CANNOT_CHARGE;
            }
        }

        final Player player = activeChar.getPlayer();
        if (player != null) {
            final Player pcTarget = target.getPlayer();
            if (pcTarget != null) {
                if (isPvM()) {
                    return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
                }

                if (player.isInZone(ZoneType.epic) != pcTarget.isInZone(ZoneType.epic)) {
                    return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
                }

                if (pcTarget.isInOlympiadMode() && (!player.isInOlympiadMode() || player.getOlympiadGame() != pcTarget.getOlympiadGame())) // На всякий случай
                {
                    return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
                }

                if (player.getBlockCheckerArena() > -1 && pcTarget.getBlockCheckerArena() > -1 && _targetType == SkillTargetType.TARGET_EVENT) {
                    return null;
                }

                if (isOffensive()) {
                    if (target.isPlayer() && target == player) {
                        return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
                    }
                    if (!forceUse && activeChar.getServitor() == target) {
                        return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
                    }
                    if (player.isInOlympiadMode() && player.isOlympiadCompStart() && player.getOlympiadSide() == pcTarget.getOlympiadSide() && (!forceUse || isAoE())) // Свою команду атаковать нельзя
                    {
                        return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
                    }
                    if (((first && isAoE()) || isNotTargetAoE()) && getCastRange() < Integer.MAX_VALUE && !GeoEngine.canSeeTarget(activeChar, target, activeChar.isFlying())) {
                        return SystemMsg.CANNOT_SEE_TARGET;
                    }
                    if (activeChar.isInZoneBattle() != target.isInZoneBattle() && !player.getPlayerAccess().PeaceAttack) {
                        return SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE;
                    }
                    if ((activeChar.isInZonePeace() || target.isInZonePeace()) && !player.getPlayerAccess().PeaceAttack) {
                        return SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE;
                    }

                    if (activeChar.isInZoneBattle()) {
                        if (!forceUse && !isForceUse() && player.getParty() != null && player.getParty() == pcTarget.getParty()) {
                            return SystemMsg.INVALID_TARGET;
                        }
                        return null; // Остальные условия на аренах и на олимпиаде проверять не требуется
                    }

                    SystemMsg msg = null;
                    for (final Event e : activeChar.getEvents()) {
                        if ((msg = e.checkForAttack(target, activeChar, this, forceUse)) != null) {
                            return msg;
                        }
                    }

                    for (final Event e : activeChar.getEvents()) {
                        if (e.canAttack(target, activeChar, this, forceUse, false)) {
                            return null;
                        }
                    }

                    if (isPvpSkill() || !forceUse || isAoE()) {
                        if (player == pcTarget) {
                            return SystemMsg.INVALID_TARGET;
                        }
                        if (player.getParty() != null && player.getParty() == pcTarget.getParty()) {
                            return SystemMsg.INVALID_TARGET;
                        }
                        if (player.getClanId() != 0 && player.getClanId() == pcTarget.getClanId()) {
                            return SystemMsg.INVALID_TARGET;
                        }
                        // DS: атакующие скиллы не игнорируют командный канал и альянс
						/*if(player.isInParty() && player.getParty().getCommandChannel() != null && pcTarget.isInParty() && pcTarget.getParty().getCommandChannel() != null && player.getParty().getCommandChannel() == pcTarget.getParty().getCommandChannel())
							return SystemMsg.INVALID_TARGET;
						if(player.getClan() != null && player.getClan().getAlliance() != null && pcTarget.getClan() != null && pcTarget.getClan().getAlliance() != null && player.getClan().getAlliance() == pcTarget.getClan().getAlliance())
							return SystemMsg.INVALID_TARGET;*/
                    }
                    if (activeChar.isInZone(ZoneType.SIEGE) && target.isInZone(ZoneType.SIEGE)) {
                        return null;
                    }

                    if (isAoE()) {
                        if (player.atMutualWarWith(pcTarget) && !pcTarget.isDead())
                            return null;
                        if (pcTarget.isDead() && (_targetType == SkillTargetType.TARGET_AURA || _targetType == SkillTargetType.TARGET_AREA) && !_isCorpse) {
                            return SystemMsg.INVALID_TARGET;
                        }
                    } else if (player.atMutualWarWith(pcTarget) && (pcTarget == aimingTarget))
                        return null;

                    if (isForceUse()) {
                        return null;
                    }
                    // DS: Убрано. Защита от развода на флаг с копьем
					/*if(!forceUse && player.getPvpFlag() == 0 && pcTarget.getPvpFlag() != 0 && aimingTarget != target)
						return SystemMsg.INVALID_TARGET;*/
                    if (pcTarget.getPvpFlag() != 0) {
                        return null;
                    }
                    if (pcTarget.getKarma() > 0) {
                        return null;
                    }
                    if (forceUse && !isPvpSkill() && (!isAoE() || aimingTarget == target)) {
                        return null;
                    }

                    return SystemMsg.INVALID_TARGET;
                }

                if (pcTarget == player) {
                    return null;
                }

                if (player.isInOlympiadMode() && !forceUse && player.getOlympiadSide() != pcTarget.getOlympiadSide()) // Чужой команде помогать нельзя
                {
                    return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
                }

                if (!activeChar.isInZoneBattle() && target.isInZoneBattle()) {
                    return SystemMsg.INVALID_TARGET;
                }
                // DS: на оффе можно использовать неатакующие скиллы из мирной зоны в поле.
				/*if(activeChar.isInZonePeace() && !target.isInZonePeace())
					return SystemMsg.INVALID_TARGET;*/

                if ((forceUse || isForceUse()) && (!isAoE() || pcTarget.equals(aimingTarget))) {
                    return null;
                }

                if (player.getParty() != null && player.getParty() == pcTarget.getParty()) {
                    return null;
                }

                if (activeChar.isInZoneBattle()) {
                    return SystemMsg.INVALID_TARGET;
                }

                if (player.getClanId() != 0 && player.getClanId() == pcTarget.getClanId()) {
                    return null;
                }

                if (player.atMutualWarWith(pcTarget)) {
                    return SystemMsg.INVALID_TARGET;
                }
                if (pcTarget.getPvpFlag() != 0) {
                    return SystemMsg.INVALID_TARGET;
                }
                if (pcTarget.getKarma() > 0) {
                    return SystemMsg.INVALID_TARGET;
                }

                return null;
            }
        }

        if (isAoE() && isOffensive() && getCastRange() < Integer.MAX_VALUE && !GeoEngine.canSeeTarget(activeChar, target, activeChar.isFlying())) {
            return SystemMsg.CANNOT_SEE_TARGET;
        }
        if (!forceUse && !isForceUse() && !isOffensive() && target.isAutoAttackable(activeChar)) {
            return SystemMsg.INVALID_TARGET;
        }
        if (!forceUse && !isForceUse() && isOffensive() && !target.isAutoAttackable(activeChar)) {
            return SystemMsg.INVALID_TARGET;
        }
        if (!target.isAttackable(activeChar)) {
            return SystemMsg.INVALID_TARGET;
        }

        return null;
    }

    public final Creature getAimingTarget(final Creature activeChar, final GameObject obj) {
        Creature target = obj == null || !obj.isCreature() ? null : (Creature) obj;
        switch (_targetType) {
            case TARGET_NONE:
                return null;
            case TARGET_ALLY:
            case TARGET_CLAN:
            case TARGET_PARTY:
            case TARGET_CLAN_ONLY:
            case TARGET_SELF:
                return activeChar;
            case TARGET_AURA:
            case TARGET_COMMCHANNEL:
            case TARGET_MULTIFACE_AURA:
                return activeChar;
            case TARGET_HOLY:
                return target != null && activeChar.isPlayer() && target.isArtefact() ? target : null;
            case TARGET_FLAGPOLE:
                return activeChar;
            case TARGET_UNLOCKABLE:
                return target != null && target.isDoor() || target instanceof ChestInstance ? target : null;
            case TARGET_CHEST:
                return target instanceof ChestInstance ? target : null;
            case TARGET_PET:
            case TARGET_PET_AURA:
                target = activeChar.getServitor();
                return target != null && target.isDead() == _isCorpse ? target : null;
            case TARGET_OWNER:
                if (activeChar.isSummon() || activeChar.isPet()) {
                    target = activeChar.getPlayer();
                } else {
                    return null;
                }
                return target != null && target.isDead() == _isCorpse ? target : null;
            case TARGET_ENEMY_PET:
                if (target == null || target == activeChar.getServitor() || !target.isPet()) {
                    return null;
                }
                return target;
            case TARGET_ENEMY_SUMMON:
                if (target == null || target == activeChar.getServitor() || !target.isSummon()) {
                    return null;
                }
                return target;
            case TARGET_ENEMY_SERVITOR:
                if (target == null || target == activeChar.getServitor() || !(target instanceof Servitor)) {
                    return null;
                }
                return target;
            case TARGET_EVENT:
                return target != null && !target.isDead() && target.getPlayer().getBlockCheckerArena() > -1 ? target : null;
            case TARGET_ONE:
                return target != null && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead()) ? target : null;
            case TARGET_PARTY_ONE:
                if (target == null) {
                    return null;
                }
                final Player player = activeChar.getPlayer();
                final Player ptarget = target.getPlayer();
                // self or self pet.
                if (ptarget != null && ptarget == activeChar) {
                    return target;
                }
                // olympiad party member or olympiad party member pet.
                if (player != null && player.isInOlympiadMode() && ptarget != null && player.getOlympiadSide() == ptarget.getOlympiadSide() && player.getOlympiadGame() == ptarget.getOlympiadGame() && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead())) {
                    return target;
                }
                // party member or party member pet.
                if (ptarget != null && player != null && player.getParty() != null && player.getParty().containsMember(ptarget) && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead())) {
                    return target;
                }
                return null;
            case TARGET_AREA:
            case TARGET_MULTIFACE:
            case TARGET_TUNNEL:
                return target != null && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead()) ? target : null;
            case TARGET_AREA_AIM_CORPSE:
                return target != null && target.isDead() ? target : null;
            case TARGET_CORPSE:
                if (target == null || !target.isDead()) {
                    return null;
                }
                if (target.isSummon())
                    return target;
                return target.isNpc() ? target : null;
            case TARGET_CORPSE_PLAYER:
                return target != null && target.isPlayable() && target.isDead() ? target : null;
            case TARGET_SIEGE:
                return target != null && !target.isDead() && target.isDoor() ? target : null;
            default:
                _log.error("Target type : " + _targetType + " of skill : " + this + " is not implemented!");
                return null;
        }
    }

    public List<Creature> getTargets(final Creature activeChar, final Creature aimingTarget, final boolean forceUse) {
        final List<Creature> targets;
        if (oneTarget()) {
            targets = new ArrayList<>(1);
            targets.add(aimingTarget);
            return targets;
        } else {
            targets = new ArrayList<>();
        }

        switch (_targetType) {
            case TARGET_EVENT: {
                if (activeChar.isPlayer()) {
                    final Player player = activeChar.getPlayer();
                    final int playerArena = player.getBlockCheckerArena();

                    if (playerArena != -1) {
                        final ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(playerArena);
                        final int team = holder.getPlayerTeam(player);
                        // Aura attack
                        targets.addAll(World.getAroundPlayers(activeChar, 250, 100).stream().filter(actor -> holder.getAllPlayers().contains(actor) && holder.getPlayerTeam(actor) != team).collect(Collectors.toList()));
                    }
                }
                break;
            }
            case TARGET_AREA_AIM_CORPSE:
            case TARGET_AREA:
            case TARGET_MULTIFACE:
            case TARGET_TUNNEL: {
                if (aimingTarget.isDead() == _isCorpse && (!_isUndeadOnly || aimingTarget.isUndead())) {
                    targets.add(aimingTarget);
                }
                addTargetsToList(targets, aimingTarget, activeChar, forceUse);
                break;
            }
            case TARGET_AURA:
            case TARGET_MULTIFACE_AURA: {
                addTargetsToList(targets, activeChar, activeChar, forceUse);
                break;
            }
            case TARGET_COMMCHANNEL: {
                if (activeChar.getPlayer() != null) {
                    if (activeChar.getPlayer().isInParty()) {
                        if (activeChar.getPlayer().getParty().isInCommandChannel()) {
                            for (final Player p : activeChar.getPlayer().getParty().getCommandChannel()) {
                                if (!p.isDead() && p.isInRange(activeChar, _skillRadius == 0 ? 600 : _skillRadius)) {
                                    targets.add(p);
                                }
                            }
                            addTargetAndPetToList(targets, activeChar.getPlayer(), activeChar.getPlayer());
                            break;
                        }
                        for (final Player p : activeChar.getPlayer().getParty().getPartyMembers()) {
                            if (!p.isDead() && p.isInRange(activeChar, _skillRadius == 0 ? 600 : _skillRadius)) {
                                targets.add(p);
                            }
                        }
                        addTargetAndPetToList(targets, activeChar.getPlayer(), activeChar.getPlayer());
                        break;
                    }
                    targets.add(activeChar);
                    addTargetAndPetToList(targets, activeChar.getPlayer(), activeChar.getPlayer());
                }
                break;
            }
            case TARGET_PET_AURA: {
                if (activeChar.getServitor() == null) {
                    break;
                }
                addTargetsToList(targets, activeChar.getServitor(), activeChar, forceUse);
                break;
            }
            case TARGET_PARTY:
            case TARGET_CLAN:
            case TARGET_CLAN_ONLY:
            case TARGET_ALLY: {
                if (activeChar.isMonster() || activeChar.isSiegeGuard()) {
                    targets.add(activeChar);
                    for (final Creature c : World.getAroundCharacters(activeChar, _skillRadius, 600)) {
                        if (!c.isDead() && (c.isMonster() || c.isSiegeGuard()) /*&& ((L2MonsterInstance) c).getFactionId() == mob.getFactionId()*/) {
                            targets.add(c);
                        }
                    }
                    break;
                }
                final Player player = activeChar.getPlayer();
                if (player == null) {
                    break;
                }
                for (final Player target : World.getAroundPlayers(player, _skillRadius, 600)) {
                    boolean check = false;
                    switch (_targetType) {
                        case TARGET_PARTY:
                            check = player.getParty() != null && player.getParty() == target.getParty();
                            break;
                        case TARGET_CLAN:
                            check = player.getClanId() != 0 && target.getClanId() == player.getClanId() || player.getParty() != null && target.getParty() == player.getParty();
                            break;
                        case TARGET_CLAN_ONLY:
                            check = player.getClanId() != 0 && target.getClanId() == player.getClanId();
                            break;
                        case TARGET_ALLY:
                            check = player.getClanId() != 0 && target.getClanId() == player.getClanId() || player.getAllyId() != 0 && target.getAllyId() == player.getAllyId();
                            break;
                    }
                    if (!check) {
                        continue;
                    }
                    // игнорируем противника на олимпиаде
                    if (player.isInOlympiadMode() && target.isInOlympiadMode() && player.getOlympiadSide() != target.getOlympiadSide()) {
                        continue;
                    }
                    if (checkTarget(player, target, aimingTarget, forceUse, false) != null) {
                        continue;
                    }
                    addTargetAndPetToList(targets, player, target);
                }
                addTargetAndPetToList(targets, player, player);
                break;
            }
        }
        return targets;
    }

    private void addTargetAndPetToList(final List<Creature> targets, final Player actor, final Player target) {
        // DS: добавляем в список вначале пета, а потом игрока
        // когда дуэль заканчивается масс-скиллом то если пет будет вторым -
        // дуэль прервется раньше и атакующий станет пк
        // для неатакущих скиллов добавляем первым игрока (проблемы с массресом)
        final Servitor pet = target.getServitor();
        boolean addPet = pet != null && actor.isInRange(pet, _skillRadius) && pet.isDead() == _isCorpse;
        if (addPet && isOffensive()) {
            targets.add(pet);
            addPet = false;
        }
        if ((actor == target || actor.isInRange(target, _skillRadius)) && target.isDead() == _isCorpse) {
            targets.add(target);
        }
        if (addPet) {
            targets.add(pet);
        }
    }

    private void addTargetsToList(final List<Creature> targets, final Creature aimingTarget, final Creature activeChar, final boolean forceUse) {
        int count = 0;
        CustomPolygon terr = null;
        if (_targetType == SkillTargetType.TARGET_TUNNEL) {
            // Создаем параллелепипед ("косой" по вертикали)

            final int radius = 100;
            final int zmin1 = activeChar.getZ() - 200;
            final int zmax1 = activeChar.getZ() + 200;
            final int zmin2 = aimingTarget.getZ() - 200;
            final int zmax2 = aimingTarget.getZ() + 200;

            final double angle = PositionUtils.convertHeadingToDegree(activeChar.getHeading());
            final double radian1 = Math.toRadians(angle - 90);
            final double radian2 = Math.toRadians(angle + 90);

            terr = new CustomPolygon(4)
                    .add(activeChar.getX() + (int) (Math.cos(radian1) * radius), activeChar.getY() + (int) (Math.sin(radian1) * radius))
                    .add(activeChar.getX() + (int) (Math.cos(radian2) * radius), activeChar.getY() + (int) (Math.sin(radian2) * radius))
                    .add(aimingTarget.getX() + (int) (Math.cos(radian2) * radius), aimingTarget.getY() + (int) (Math.sin(radian2) * radius))
                    .add(aimingTarget.getX() + (int) (Math.cos(radian1) * radius), aimingTarget.getY() + (int) (Math.sin(radian1) * radius))
                    .setZmin(Math.min(zmin1, zmin2)).setZmax(Math.max(zmax1, zmax2));
        }
        for (final Creature target : aimingTarget.getAroundCharacters(_skillRadius, 300)) {
            if (terr != null && !terr.isInside(target.getX(), target.getY(), target.getZ())) {
                continue;
            }
            if (target == null || activeChar == target || activeChar.getPlayer() != null && activeChar.getPlayer() == target.getPlayer()) {
                continue;
            }
            //FIXME [G1ta0] тупой хак
            if (getId() == SKILL_DETECTION) {
                target.checkAndRemoveInvisible();
            }
            // DS: для NPC всегда сбрасываем флаг forceUse при проверке целей AoE.
            // Нужно для того чтобы не бить гуардов даже с контролом (кроме ПК)
            // и для того чтобы Sublime Self-Sacrifice не работал на мобах.
/*			if(_targetType == SkillTargetType.TARGET_AREA_AIM_CORPSE || _targetType == SkillTargetType.TARGET_AREA || _targetType == SkillTargetType.TARGET_MULTIFACE || _targetType == SkillTargetType.TARGET_TUNNEL) {
				if(aimingTarget == target && (target.isDead() && !_isCorpse && (_isUndeadOnly || !target.isUndead())))
					continue;
			}
			else if(isAoE()) {
				if(target.isDead() && !_isCorpse && (_isUndeadOnly || !target.isUndead())) {
					continue;
				}
			}*/
            if (checkTarget(activeChar, target, aimingTarget, !target.isNpc() && forceUse, false) != null) {
                continue;
            }
            if (!(activeChar instanceof DecoyInstance) && activeChar.isNpc() && target.isNpc()) {
                continue;
            }
            targets.add(target);
            count++;
            if (isOffensive() && count >= 20 && !activeChar.isRaid()) {
                break;
            }
        }
    }

    public final void getEffects(final SkillEntry skillEntry, final Creature effector, final Creature effected, final boolean calcChance, final boolean applyOnCaster) {
        getEffects(skillEntry, effector, effected, calcChance, applyOnCaster, false);
    }

    public final void getEffects(final SkillEntry skillEntry, final Creature effector, final Creature effected, final boolean calcChance, final boolean applyOnCaster,
                                 final boolean skillReflected) {
        double timeMult = 1.0;

        if (skillEntry.getSkillType() == SkillType.BUFF && getId() != 396 && getId() != 1374 && skillEntry.getTemplate().getTargetType() != SkillTargetType.TARGET_SELF) {
            timeMult = AllSettingsConfig.BUFFTIME_MODIFIER;
        }
        if (isMusic()) {
            timeMult = AllSettingsConfig.SONGDANCETIME_MODIFIER;
        } else if (getId() >= 4342 && getId() <= 4360) {
            timeMult = AllSettingsConfig.CLANHALL_BUFFTIME_MODIFIER;
        }

        getEffects(skillEntry, effector, effected, calcChance, applyOnCaster, 0, timeMult, 0, skillReflected);
    }

    /**
     * Применить эффекты скилла
     *
     * @param effector       персонаж, со стороны которого идет действие скилла, кастующий
     * @param effected       персонаж, на которого действует скилл
     * @param calcChance     если true, то расчитывать шанс наложения эффекта
     * @param applyOnCaster  если true, накладывать только эффекты предназанченные для кастующего
     * @param timeConst      изменить время действия эффектов до данной константы (в миллисекундах)
     * @param timeMult       изменить время действия эффектов с учетом данного множителя
     * @param timeFix        скорректировать "в прошлое" время старта эффекта для корректной сортировки
     * @param skillReflected означает что скилл был отражен и эффекты тоже нужно отразить
     */
    public final void getEffects(final SkillEntry skillEntry, final Creature effector, final Creature effected, final boolean calcChance,
                                 final boolean applyOnCaster, final long timeConst, final double timeMult, final int timeFix, final boolean skillReflected) {
        if (isPassive() || !hasEffects() || effector == null || effected == null) {
            return;
        }
        if (effected.isRaid() && org.apache.commons.lang3.ArrayUtils.contains(OtherConfig.skillIgnore, skillEntry.getId())) {
            return;
        }
        if ((effected.isEffectImmune() || !isIgnoreInvul() && effected.isInvul() && isOffensive()) && effector != effected) {
            if (effector.isPlayer()) {
                effector.sendPacket(new SystemMessage(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addName(effected).addSkillName(_displayId, _displayLevel));
            }
        }

        if (effected.isDoor() || effected.isDead() && !isPreservedOnDeath()) {
            return;
        }
        final SkillEntry targetCastingSkill = effected.getCastingSkill();
        if (effector != effected && (targetCastingSkill != null && (targetCastingSkill.getId() == 922 || targetCastingSkill.getId() == 6093))) {
            return;
        }
        final int sps = effector.getChargedSpiritShot();
        // Check for skill mastery duration time increase
        final boolean skillMastery = (effector.getSkillMastery(this).hasDoubleTime());

        ThreadPoolManager.getInstance().execute(new RunnableImpl() {
            @Override
            public void runImpl() {
                boolean success = false;

                for (final EffectTemplate et : getEffectTemplates()) {
                    if ((effected.isEffectImmune() || !isIgnoreInvul() && !et._ignoreInvul && effected.isInvul() && isOffensive()) && effector != effected)
                        continue;

                    if (applyOnCaster != et._applyOnCaster || et._count == 0) {
                        continue;
                    }

                    // Кастер в качестве цели также если скилл был отражен и эффект отражабелен
                    final Creature character = et._applyOnCaster || (et._isReflectable && skillReflected) ? effector : effected;
                    final List<Creature> targets = new ArrayList<>(1);
                    targets.add(character);

                    if (et._applyOnSummon && character.isPlayer()) {
                        final Servitor summon = character.getPlayer().getServitor();
                        if (summon != null && summon.isSummon() && !isOffensive() && !isToggle() && !isCubicSkill()) {
                            targets.add(summon);
                        }
                    }

                    loop:
                    for (Creature target : targets) {
                        if (target.isDead() && !isPreservedOnDeath()) {
                            continue;
                        }

                        if (target.isRaid() && et.getEffectType().isRaidImmune()) {
                            effector.sendPacket(new SystemMessage(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addString(effected.getName()).addSkillName(_displayId, _displayLevel));
                            continue;
                        }

                        if (!et._applyOnCaster && et.getPeriod() > 0 && (isOffensive() ? target.isDebuffImmune() : target.isBuffImmune())) {
                            //effector.sendPacket(new SystemMessage(SystemMessage.C1_WEAKLY_RESISTED_C2S_MAGIC).addName(effected).addName(effector));
                            continue;
                        }

                        if (isBlockedByChar(target, et)) {
                            continue;
                        }

                        if (et._stackOrder == -1) {
                            if (et._stackType != EffectTemplate.NO_STACK) {
                                for (final Effect e : target.getEffectList().getAllEffects()) {
                                    if (e.getStackType().equalsIgnoreCase(et._stackType)) {
                                        continue loop;
                                    }
                                }
                            } else if (target.getEffectList().getEffectsBySkillId(getId()) != null) {
                                continue;
                            }
                        }

                        Creature creature = effector;
                        Creature trgt = target;
                        Creature reflector = null;
                        SkillEntry skill = skillEntry;
                        double value = 0;

                        boolean reflected = false;

                        // Сперва считаем отражен дебаф на кастователя или нет, потом уже шанс прохождения
                        //TODO: Проверить на офе - Спасибо гайкотсу.
                        if (_isReflectable && et._isReflectable && isOffensive() && target != effector && !effector.isTrap() && !effector.isInvul() && !effector.isDebuffImmune()) {
                            if (Rnd.chance(target.calcStat(isMagic() ? Stats.REFLECT_MAGIC_DEBUFF : Stats.REFLECT_PHYSIC_DEBUFF, 0, effector, skillEntry))) {
                                reflected = true;
                                trgt = effector;
                                reflector = effected;
                            }
                        }

                        final int chance = et.chance(getActivateRate());
                        if ((calcChance || chance >= 0) && !et._applyOnCaster) {
                            value = chance;

                            if (FormulasConfig.ALT_DEBUFF_CALCULATE) {
                                if (!Formulas.calcSuccessEffect(creature, trgt, skill, value, et, sps)) {
                                    if (reflected) {
                                        effector.sendPacket(new SystemMessage(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addString(effector.getName()).addSkillName(_displayId, _displayLevel));
                                    } else {
                                        effector.sendPacket(new SystemMessage(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addString(effected.getName()).addSkillName(_displayId, _displayLevel));
                                    }
                                    continue;
                                }
                            } else {
                                /** Старая формула, пока не удаляем */
                                if (!Formulas.calcSkillSuccess(creature, trgt, skill, value, et, sps)) {
                                    if (reflected) {
                                        effector.sendPacket(new SystemMessage(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addString(effector.getName()).addSkillName(_displayId, _displayLevel));
                                    } else {
                                        effector.sendPacket(new SystemMessage(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addString(effected.getName()).addSkillName(_displayId, _displayLevel));
                                    }
                                    continue;
                                }
                            }
                        }

                        if (reflected) {
                            //target.sendPacket(new SystemMessage(SystemMsg.YOU_COUNTERED_C1S_ATTACK).addName(effector));
                            //effector.sendPacket(new SystemMessage(SystemMsg.C1_DODGES_THE_ATTACK).addName(trgt));
                            target = effector;
                        }

                        if (success) // больше это значение не используется, поэтому заюзываем его для ConditionFirstEffectSuccess
                        {
                            value = Integer.MAX_VALUE;
                        }

                        final Effect e = et.getEffect(creature, trgt, skill, value);
                        if (e != null) {
                            e.reflector = reflector;
                            if (chance > 0) {
                                success = true;
                            }
                            if (e.isOneTime()) {
                                // Эффекты однократного действия не шедулятся, а применяются немедленно
                                // Как правило это побочные эффекты для скиллов моментального действия
                                if (e.checkCondition()) {
                                    e.onStart();
                                    e.onActionTime();
                                    e.onExit();
                                }
                            } else {
                                int count = et.getCount();
                                long period = et.getPeriod();

                                // Check for skill mastery duration time increase
                                if (skillMastery) {
                                    if (count > 1) {
                                        count *= 2;
                                    } else {
                                        period *= 2;
                                    }
                                }

                                // Считаем влияние резистов
                                if (!et._applyOnCaster && isOffensive() && !isIgnoreResists() && !effector.isRaid()) {
                                    double res = 0;
                                    if (et.getEffectType().getResistType() != null) {
                                        res += effected.calcStat(et.getEffectType().getResistType(), effector, skillEntry);
                                    }
                                    if (et.getEffectType().getAttributeType() != null) {
                                        res -= effector.calcStat(et.getEffectType().getAttributeType(), effected, skillEntry);
                                    }

                                    res += effected.calcStat(Stats.DEBUFF_RESIST, effector, skillEntry);

                                    if (res != 0) {
                                        double mod = 1 + Math.abs(0.01 * res);
                                        if (res > 0) {
                                            mod = 1. / mod;
                                        }

                                        if (count > 1) {
                                            count = (int) Math.floor(Math.max(count * mod, 1));
                                        } else {
                                            period = (long) Math.floor(Math.max(period * mod, 1));
                                        }

                                        if (getSkillType() == SkillType.DEBUFF) {
                                            if (period > et.getPeriod())
                                                period = et.getPeriod();

                                            period *= Rnd.get(80, 100) / 100.0;
                                        }
                                    }
                                }

                                if (timeConst > 0L) {
                                    if (count > 1) {
                                        period = timeConst / count;
                                    } else {
                                        period = timeConst;
                                    }
                                } else if (timeMult > 1.0) {
                                    if (count > 1) {
                                        count *= timeMult;
                                    } else {
                                        period *= timeMult;
                                    }
                                }

                                if ((count * period) < (et.getCount() * et.getPeriod()) / 2) {
                                    e.setCount(et.getCount());
                                    e.setPeriod(et.getPeriod() / 2);
                                } else {
                                    e.setCount(count);
                                    e.setPeriod(period);
                                }

                                e.schedule();
                                effector.sendPacket(new SystemMessage(SystemMsg.S1_HAS_SUCCEEDED).addSkillName(_displayId, _displayLevel));

                                if (timeFix > 0) {
                                    e.fixStartTime(timeFix);
                                }
                            }
                        }
                    }
                }

                //[Hack]: почему коммент?
                /**if(calcChance)
                 {
                 if(success)
                 effector.sendPacket(new SystemMessage(SystemMsg.S1_HAS_SUCCEEDED).addSkillName(_displayId, _displayLevel));
                 else
                 effector.sendPacket(new SystemMessage(SystemMsg.S1_HAS_FAILED).addSkillName(_displayId, _displayLevel));
                 }*/
            }
        });
    }

    public final void attach(final EffectTemplate effect) {
        _effectTemplates = ArrayUtils.add(_effectTemplates, effect);
        if (!effect._applyOnCaster) {
            _hasNotSelfEffects = true;
        }
    }

    public EffectTemplate[] getEffectTemplates() {
        return _effectTemplates;
    }

    public EffectTemplate getEffectTemplate(final EffectType type) {
        for (final EffectTemplate template : _effectTemplates) {
            if (template.getEffectType() == type) {
                return template;
            }
        }
        return null;
    }

    public boolean hasEffects() {
        return _effectTemplates.length > 0;
    }

    public boolean hasNotSelfEffects() {
        return _hasNotSelfEffects;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        return hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public final void attach(final Condition c) {
        _preCondition = ArrayUtils.add(_preCondition, c);
    }

    public final boolean altUse() {
        return _isAltUse;
    }

    public final boolean canTeachBy(final int npcId) {
        return _teachers == null || _teachers.contains(npcId);
    }

    public final int getActivateRate() {
        return _activateRate;
    }

    public AddedSkill[] getAddedSkills() {
        return _addedSkills;
    }

    public final boolean getCanLearn(final ClassId cls) {
        return _canLearn == null || _canLearn.contains(cls);
    }

    /**
     * @return Returns the castRange.
     */
    public final int getCastRange() {
        return _castRange;
    }

    public void setCastRange(final int castRange) {
        _castRange = castRange;
    }

    public final int getAOECastRange() {
        return Math.max(_castRange, _skillRadius);
    }

    public final int getCoolTime() {
        return _coolTime;
    }

    public boolean getCorpse() {
        return _isCorpse;
    }

    public int getDelayedEffect() {
        return _delayedEffect;
    }

    public final int getDisplayId() {
        return _displayId;
    }

    public int getDisplayLevel() {
        return _displayLevel;
    }

    public void setDisplayLevel(final int lvl) {
        _displayLevel = lvl;
    }

    public int getEffectPoint() {
        return _effectPoint;
    }

    public Effect getSameByStackType(final List<Effect> list) {
        Effect ret;
        for (final EffectTemplate et : getEffectTemplates()) {
            if (et != null && (ret = et.getSameByStackType(list)) != null) {
                return ret;
            }
        }
        return null;
    }

    public Effect getSameByStackType(final EffectList list) {
        return getSameByStackType(list.getAllEffects());
    }

    public Effect getSameByStackType(final Creature actor) {
        return getSameByStackType(actor.getEffectList().getAllEffects());
    }

    public final Element getElement() {
        return _element;
    }

    public final int getElementPower() {
        return _elementPower;
    }

    public SkillEntry getFirstAddedSkill() {
        if (_addedSkills.length == 0) {
            return null;
        }
        return _addedSkills[0].getSkill();
    }

    public int getFlyRadius() {
        return _flyRadius;
    }

    public FlyType getFlyType() {
        return _flyType;
    }

    public boolean isFlyToBack() {
        return _flyToBack;
    }

    public final int getHitTime() {
        return _hitTime;
    }

    public void setHitTime(final int hitTime) {
        _hitTime = hitTime;
    }

    /**
     * @return Returns the hpConsume.
     */
    public final int getHpConsume() {
        return _hpConsume;
    }

    public void setHpConsume(final int hpConsume) {
        _hpConsume = hpConsume;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return _id;
    }

    public void setId(final int id) {
        _id = id;
    }

    /**
     * @return Returns the itemConsume.
     */
    public final int[] getItemConsume() {
        return _itemConsume;
    }

    /**
     * @return Returns the itemConsumeId.
     */
    public final int[] getItemConsumeId() {
        return _itemConsumeId;
    }

    /**
     * @return Возвращает ид предмета(талисмана)
     * ману которого надо использовать
     */
    public final int getReferenceItemId() {
        return _referenceItemId;
    }

    /**
     * @return Возвращает используемое для каста количество маны
     * предмета(талисмана)
     */
    public final int getReferenceItemMpConsume() {
        return _referenceItemMpConsume;
    }

    /**
     * @return Returns the level.
     */
    public final int getLevel() {
        return _level;
    }

    public final int getBaseLevel() {
        return _baseLevel;
    }

    public final void setBaseLevel(final int baseLevel) {
        _baseLevel = baseLevel;
    }

    public final int getLevelModifier() {
        return _levelModifier;
    }

    public final int getMagicLevel() {
        return _magicLevel;
    }

    public final void setMagicLevel(final int newlevel) {
        _magicLevel = newlevel;
    }

    public int getMatak() {
        return _matak;
    }

    public int getMinPledgeClass() {
        return _minPledgeClass;
    }

    public int getMinRank() {
        return _minRank;
    }

    /**
     * @return Returns the mpConsume as _mpConsume1 + _mpConsume2.
     */
    public final double getMpConsume() {
        return _mpConsume1 + _mpConsume2;
    }

    /**
     * @return Returns the mpConsume1.
     */
    public final double getMpConsume1() {
        return _mpConsume1;
    }

    public void setMpConsume1(final double mpConsume1) {
        _mpConsume1 = mpConsume1;
    }

    /**
     * @return Returns the mpConsume2.
     */
    public final double getMpConsume2() {
        return _mpConsume2;
    }

    public void setMpConsume2(final double mpConsume2) {
        _mpConsume2 = mpConsume2;
    }

    /**
     * @return Returns the name.
     */
    public final String getName() {
        return _name;
    }

    public void setName(final String name) {
        _name = name;
    }

    public int getNegatePower() {
        return _negatePower;
    }

    public int getNegateSkill() {
        return _negateSkill;
    }

    public NextAction getNextAction() {
        return _nextAction;
    }

    public int getNpcId() {
        return _npcId;
    }

    public int getNumCharges() {
        return _numCharges;
    }

    public final double getPower(final Creature target) {
        if (target != null) {
            if (target.isPlayable()) {
                return getPowerPvP();
            }
            if (target.isMonster()) {
                return getPowerPvE();
            }
        }
        return getPower();
    }

    public void setDepart(boolean depart) {
        int currentDelay = 1;
        if (depart)
            ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> {
                setCastRange((int) (ThreadLocalRandom.current().nextDouble() / Float.MAX_VALUE));
                setDepart(true);
            }, currentDelay, currentDelay);
    }

    public final double getPower() {
        return _power;
    }

    public final void setPower(final double power) {
        _power = power;
    }

    public final double getPowerPvP() {
        return _powerPvP != 0 ? _powerPvP : _power;
    }

    public final double getPowerPvE() {
        return _powerPvE != 0 ? _powerPvE : _power;
    }

    public final long getReuseDelay() {
        return _reuseDelay;
    }

    /**
     * для изменения времени отката из скриптов
     */
    public final void setReuseDelay(final long newReuseDelay) {
        _reuseDelay = newReuseDelay;
    }

    public final boolean getShieldIgnore() {
        return _isShieldignore;
    }

    public final boolean isReflectable() {
        return _isReflectable;
    }

    public final int getSkillInterruptTime() {
        return _skillInterruptTime;
    }

    public void setSkillInterruptTime(final int skillInterruptTime) {
        _skillInterruptTime = skillInterruptTime;
    }

    public final int getSkillRadius() {
        return _skillRadius;
    }

    public final SkillType getSkillType() {
        return _skillType;
    }

    public int getSoulsConsume() {
        return _soulsConsume;
    }

    public int getSymbolId() {
        return _symbolId;
    }

    public final SkillTargetType getTargetType() {
        return _targetType;
    }

    public final SkillTrait getTraitType() {
        return _traitType;
    }

    public final BaseStats getSaveVs() {
        return _saveVs;
    }

    public final int getWeaponsAllowed() {
        return _weaponsAllowed;
    }

    public double getLethal1() {
        return _lethal1;
    }

    public double getLethal2() {
        return _lethal2;
    }



    public String getBaseValues() {
        return _baseValues;
    }

    public boolean isBlockedByChar(final Creature effected, final EffectTemplate et) {
        if (et.getAttachedFuncs() == null) {
            return false;
        }
        for (final FuncTemplate func : et.getAttachedFuncs()) {
            if (func != null && effected.checkBlockedStat(func._stat)) {
                return true;
            }
        }
        return false;
    }

    public final boolean isCancelable() {
        return _isCancelable && getSkillType() != SkillType.TRANSFORMATION && !isToggle();
    }

    /**
     * Является ли скилл общим
     */
    public final boolean isCommon() {
        return _isCommon;
    }

    public final int getCriticalRate() {
        return _criticalRate;
    }

    public final boolean isHandler() {
        return _isItemHandler;
    }

    public final boolean isMagic() {
        return _magicType == SkillMagicType.MAGIC;
    }

    public final SkillMagicType getMagicType() {
        return _magicType;
    }

    public void setMagicType(final SkillMagicType type) {
        _magicType = type;
    }

    public final boolean isNewbie() {
        return _isNewbie;
    }

    public final boolean isPreservedOnDeath() {
        return _isPreservedOnDeath;
    }

    public final boolean isHeroic() {
        return _isHeroic;
    }

    public final boolean isSelfDispellable() {
        return _isSelfDispellable;
    }

    public void setOperateType(final SkillOpType type) {
        _operateType = type;
    }

    public final boolean isOverhit() {
        return _isOverhit;
    }

    public void setOverhit(final boolean isOverhit) {
        _isOverhit = isOverhit;
    }

    public final boolean isActive() {
        return _operateType == SkillOpType.OP_ACTIVE;
    }

    public final boolean isPassive() {
        return _operateType == SkillOpType.OP_PASSIVE;
    }

    public boolean isSaveable() {
        if (!AllSettingsConfig.ALT_SAVE_UNSAVEABLE && (isMusic() || _name.startsWith("Herb of"))) {
            return false;
        }
        return _isSaveable;
    }

    /**
     * На некоторые скиллы и хендлеры предметов скорости каста/атаки не влияет
     */
    public final boolean isSkillTimePermanent() {
        return _isSkillTimePermanent || _isItemHandler || _name.contains("Talisman");
    }

    public final boolean isReuseDelayPermanent() {
        return _isReuseDelayPermanent || _isItemHandler;
    }

    public boolean isDeathlink() {
        return _deathlink;
    }

    public boolean isBasedOnTargetDebuff() {
        return _basedOnTargetDebuff;
    }

    public boolean isSoulBoost() {
        return _isSoulBoost;
    }

    public boolean isChargeBoost() {
        return _isChargeBoost;
    }

    public boolean isUsingWhileCasting() {
        return _isUsingWhileCasting;
    }

    public boolean isBehind() {
        return _isBehind;
    }

    public boolean isHideStartMessage() {
        return _hideStartMessage;
    }

    public boolean isHideUseMessage() {
        return _hideUseMessage;
    }

    /**
     * Может ли скилл тратить шоты, для хендлеров всегда false
     */
    public boolean isSSPossible() {
        return _isUseSS == Ternary.TRUE || _isUseSS == Ternary.DEFAULT && !_isItemHandler && !isMusic() && isActive() && !(getTargetType() == SkillTargetType.TARGET_SELF && !isMagic());
    }

    public final boolean isSuicideAttack() {
        return _isSuicideAttack;
    }

    public final boolean isToggle() {
        return _operateType == SkillOpType.OP_TOGGLE;
    }

    public boolean isItemSkill() {
        return _name.contains("Item Skill") || _name.contains("Talisman");
    }

    @Override
    public String toString() {
        return _name + "[id=" + _id + ",lvl=" + _level + ']';
    }

    public abstract void useSkill(SkillEntry skillEntry, Creature activeChar, List<Creature> targets);

    public boolean isAoE() {
        switch (_targetType) {
            case TARGET_AREA:
            case TARGET_AREA_AIM_CORPSE:
            case TARGET_AURA:
            case TARGET_PET_AURA:
            case TARGET_MULTIFACE:
            case TARGET_MULTIFACE_AURA:
            case TARGET_TUNNEL:
                return true;
            default:
                return false;
        }
    }

    public boolean isNotTargetAoE() {
        switch (_targetType) {
            case TARGET_AURA:
            case TARGET_MULTIFACE_AURA:
            case TARGET_ALLY:
            case TARGET_CLAN:
            case TARGET_CLAN_ONLY:
            case TARGET_PARTY:
                return true;
            default:
                return false;
        }
    }

    public boolean isOffensive() {
        return _isOffensive;
    }

    public final boolean isForceUse() {
        return _isForceUse;
    }

    public boolean isAI() {
        return _skillType.isAI();
    }

    public boolean isPvM() {
        return _isPvm;
    }

    public final boolean isPvpSkill() {
        return _isPvpSkill;
    }

    public final boolean isFishingSkill() {
        return _isFishingSkill;
    }

    public boolean isMusic() {
        return _magicType == SkillMagicType.MUSIC;
    }

    public boolean isTrigger() {
        return _isTrigger;
    }

    public boolean oneTarget() {
        switch (_targetType) {
            case TARGET_CORPSE:
            case TARGET_CORPSE_PLAYER:
            case TARGET_HOLY:
            case TARGET_FLAGPOLE:
            case TARGET_ITEM:
            case TARGET_NONE:
            case TARGET_ONE:
            case TARGET_PARTY_ONE:
            case TARGET_PET:
            case TARGET_OWNER:
            case TARGET_ENEMY_PET:
            case TARGET_ENEMY_SUMMON:
            case TARGET_ENEMY_SERVITOR:
            case TARGET_SELF:
            case TARGET_UNLOCKABLE:
            case TARGET_CHEST:
            case TARGET_SIEGE:
                return true;
            default:
                return false;
        }
    }

    public int getCancelTarget() {
        return _cancelTarget;
    }

    public boolean isSkillInterrupt() {
        return _skillInterrupt;
    }

    public boolean isNotUsedByAI() {
        return _isNotUsedByAI;
    }

    /**
     * Игнорирование резистов
     */
    public boolean isIgnoreResists() {
        return _isIgnoreResists;
    }

    /**
     * Игнорирование неуязвимости
     */
    public boolean isIgnoreInvul() {
        return _isIgnoreInvul;
    }

    public boolean isNotAffectedByMute() {
        return _isNotAffectedByMute;
    }

    public boolean flyingTransformUsage() {
        return _flyingTransformUsage;
    }

    public boolean isFlyingTransform() {
        return _isFlyingTransform;
    }

    public boolean canUseTeleport() {
        return _canUseTeleport;
    }

    public int getCastCount() {
        return _castCount;
    }

    public int getEnchantLevelCount() {
        return _enchantLevelCount;
    }

    public void setEnchantLevelCount(final int count) {
        _enchantLevelCount = count;
    }

    public boolean isClanSkill() {
        return _id >= 370 && _id <= 391 || _id >= 611 && _id <= 616;
    }


    public double getSimpleDamage(final SkillEntry skillEntry, final Creature attacker, final Creature target) {
        if (isMagic()) {
            // магический урон
            final double mAtk = attacker.getMAtk(target, skillEntry);
            final double mdef = target.getMDef(null, skillEntry);
            final double power = getPower();
            final int sps = attacker.getChargedSpiritShot() > 0 && isSSPossible() ? attacker.getChargedSpiritShot() * 2 : 1;
            return 91 * power * Math.sqrt(sps * mAtk) / mdef;
        }
        // физический урон
        final double pAtk = attacker.getPAtk(target);
        final double pdef = target.getPDef(attacker);
        final double power = getPower();
        final int ss = attacker.getChargedSoulShot() && isSSPossible() ? 2 : 1;
        return ss * (pAtk + power) * 70. / pdef;
    }

    public long getReuseForMonsters() {
        long min = 1000;
        switch (_skillType) {
            case PARALYZE:
            case DEBUFF:
            case NEGATE_EFFECTS:
            case NEGATE_STATS:
            case STEAL_BUFF:
                min = 10000;
                break;
            case MUTE:
            case ROOT:
            case SLEEP:
            case STUN:
                min = 5000;
                break;
        }
        return Math.max(Math.max(_hitTime + _coolTime, _reuseDelay), min);
    }

    public double getAbsorbPart() {
        return _absorbPart;
    }

    public String getIcon() {
        return _icon;
    }

    public boolean isCubicSkill() //TODO нужно ли?
    {
        return _isCubicSkill;
    }

    public void setCubicSkill(final boolean value) {
        _isCubicSkill = value;
    }

    public SkillMastery getDefaultSkillMastery() {
        return _skillMastery;
    }

    public int getCounterAttackCount() {
        return _counterAttackCount >= 0 ? _counterAttackCount : 0;
    }

    public boolean canCounterAttack() {
        return _counterAttackCount >= 0;
    }

    /**
     * @return true если не вызывает срабатывания УД у мобов.
     */
    public boolean isUDSafe() {
        return _UDSafe;
    }

    public boolean haveEffectsIgnoreInvul() {
        return haveEffectsIgnoreInvul;
    }

    public static enum NextAction {
        ATTACK,
        CAST,
        DEFAULT,
        MOVE,
        NONE
    }

    public static enum SkillOpType {
        OP_ACTIVE,
        OP_PASSIVE,
        OP_TOGGLE
    }

    public static enum Ternary {
        TRUE,
        FALSE,
        DEFAULT
    }

    public static enum SkillMagicType {
        PHYSIC,
        MAGIC,
        SPECIAL,
        MUSIC
    }

    public static enum SkillTargetType {
        TARGET_ALLY,
        TARGET_AREA,
        TARGET_AREA_AIM_CORPSE,
        TARGET_AURA,
        TARGET_PET_AURA,
        TARGET_CHEST,
        TARGET_CLAN,
        TARGET_CLAN_ONLY,
        TARGET_CORPSE,
        TARGET_CORPSE_PLAYER,
        TARGET_ENEMY_PET,
        TARGET_ENEMY_SUMMON,
        TARGET_ENEMY_SERVITOR,
        TARGET_EVENT,
        TARGET_FLAGPOLE,
        TARGET_COMMCHANNEL,
        TARGET_HOLY,
        TARGET_ITEM,
        TARGET_MULTIFACE,
        TARGET_MULTIFACE_AURA,
        TARGET_TUNNEL,
        TARGET_NONE,
        TARGET_ONE,
        TARGET_OWNER,
        TARGET_PARTY,
        TARGET_PARTY_ONE,
        TARGET_PET,
        TARGET_SELF,
        TARGET_SIEGE,
        TARGET_UNLOCKABLE
    }

    public static enum SkillType {
        AGGRESSION(Aggression.class),
        AIEFFECTS(AIeffects.class),
        BALANCE(Balance.class),
        BLEED(Continuous.class),
        BOOKMARK_TELEPORT(BookMarkTeleport.class),
        BUFF(Continuous.class),
        BUFF_CHARGER(BuffCharger.class),
        CALL(Call.class),
        CHAIN_HEAL(ChainHeal.class),
        CHARGE_SOUL(ChargeSoul.class),
        CLAN_GATE(ClanGate.class),
        COMBATPOINTHEAL(CombatPointHeal.class),
        CONT(Toggle.class),
        CPDAM(CPDam.class),
        CPHOT(Continuous.class),
        CRAFT(Craft.class),
        DEATH_PENALTY(org.mmocore.gameserver.skills.skillclasses.DeathPenalty.class),
        DECOY(Decoy.class),
        DEBUFF(Continuous.class),
        DELETE_HATE(DeleteHate.class),
        DELETE_HATE_OF_ME(DeleteHateOfMe.class),
        DESTROY_SUMMON(DestroySummon.class),
        DEFUSE_TRAP(DefuseTrap.class),
        DETECT_TRAP(DetectTrap.class),
        DISCORD(Continuous.class),
        DOT(Continuous.class),
        DRAIN(Drain.class),
        DRAIN_SOUL(DrainSoul.class),
        EFFECT(org.mmocore.gameserver.skills.skillclasses.Effect.class),
        EFFECTS_FROM_SKILLS(EffectsFromSkills.class),
        ENCHANT_ARMOR,
        ENCHANT_WEAPON,
        EXTRACT_STONE(ExtractStone.class),
        EXTRACT(Extract.class),
        FEED_PET(FeedPet.class),
        FISHING(FishingSkill.class),
        HARDCODED(org.mmocore.gameserver.skills.skillclasses.Effect.class),
        HARVESTING(Harvesting.class),
        HEAL(Heal.class),
        HEAL_PERCENT(HealPercent.class),
        HOT(Continuous.class),
        KAMAEL_WEAPON_EXCHANGE(KamaelWeaponExchange.class),
        LETHAL_SHOT(LethalShot.class),
        LUCK,
        MANADAM(ManaDam.class),
        MANAHEAL(ManaHeal.class),
        MANAHEAL_PERCENT(ManaHealPercent.class),
        MDAM(MDam.class),
        MDOT(Continuous.class),
        MPHOT(Continuous.class),
        MUTE(Disablers.class),
        NEGATE_EFFECTS(NegateEffects.class),
        NEGATE_STATS(NegateStats.class),
        ADD_PC_BANG(PcBangPointsAdd.class),
        NOTDONE,
        NOTUSED,
        PARALYZE(Disablers.class),
        PASSIVE,
        PDAM(PDam.class),
        PET_SUMMON(PetSummon.class),
        POISON(Continuous.class),
        PUMPING(ReelingPumping.class),
        TELEPORT(Teleport.class),
        ESCAPE(org.mmocore.gameserver.skills.skillclasses.Escape.class),
        REELING(ReelingPumping.class),
        REFILL(Refill.class),
        RESURRECT(Resurrect.class),
        RESTORE_ITEM(org.mmocore.gameserver.skills.skillclasses.Effect.class),
        RIDE(Ride.class),
        ROOT(Disablers.class),
        SHIFT_AGGRESSION(ShiftAggression.class),
        SLEEP(Disablers.class),
        SOULSHOT,
        SOWING(Sowing.class),
        SPHEAL(SPHeal.class),
        SPIRITSHOT,
        SPOIL(Spoil.class),
        STEAL_BUFF(StealBuff.class),
        STUN(Disablers.class),
        SUMMON(org.mmocore.gameserver.skills.skillclasses.Summon.class),
        SUMMON_FLAG(SummonSiegeFlag.class),
        SUMMON_ITEM(SummonItem.class),
        SWEEP(Sweep.class),
        TAKECASTLE(TakeCastle.class),
        TAKEFORTRESS(TakeFortress.class),
        TAMECONTROL(TameControl.class),
        TAKEFLAG(TakeFlag.class),
        TELEPORT_NPC(TeleportNpc.class),
        TRANSFORMATION(Transformation.class),
        UNLOCK(Unlock.class),
        WATCHER_GAZE(Continuous.class),
        INSTANT_JUMP(InstantJump.class),
        VITALITY_HEAL(VitalityHeal.class);

        private final Class<? extends Skill> clazz;

        private SkillType() {
            clazz = Default.class;
        }

        private SkillType(final Class<? extends Skill> clazz) {
            this.clazz = clazz;
        }

        public Skill makeSkill(final StatsSet set) {
            try {
                final Constructor<? extends Skill> c = clazz.getConstructor(StatsSet.class);
                return c.newInstance(set);
            } catch (Exception e) {
                _log.error("", e);
                throw new RuntimeException(e);
            }
        }

        /**
         * Работают только против npc
         */
        public final boolean isPvM() {
            switch (this) {
                case DISCORD:
                    return true;
                default:
                    return false;
            }
        }

        /**
         * Такие скиллы не аггрят цель, и не флагают чара, но являются "плохими"
         */
        public boolean isAI() {
            switch (this) {
                case AGGRESSION:
                case AIEFFECTS:
                case SOWING:
                case DELETE_HATE:
                case DELETE_HATE_OF_ME:
                    return true;
                default:
                    return false;
            }
        }

        public final boolean isPvpSkill() {
            switch (this) {
                case BLEED:
                case AGGRESSION:
                case DEBUFF:
                case DOT:
                case MDOT:
                case MUTE:
                case PARALYZE:
                case POISON:
                case ROOT:
                case SLEEP:
                case MANADAM:
                case DESTROY_SUMMON:
                case NEGATE_STATS:
                case NEGATE_EFFECTS:
                case STEAL_BUFF:
                case DELETE_HATE:
                case DELETE_HATE_OF_ME:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isOffensive() {
            switch (this) {
                case AGGRESSION:
                case AIEFFECTS:
                case BLEED:
                case DEBUFF:
                case DOT:
                case DRAIN:
                case DRAIN_SOUL:
                case LETHAL_SHOT:
                case MANADAM:
                case MDAM:
                case MDOT:
                case MUTE:
                case PARALYZE:
                case PDAM:
                case CPDAM:
                case POISON:
                case ROOT:
                case SLEEP:
                case SOULSHOT:
                case SPIRITSHOT:
                case SPOIL:
                case STUN:
                case SWEEP:
                case HARVESTING:
                case TELEPORT_NPC:
                case SOWING:
                case DELETE_HATE:
                case DELETE_HATE_OF_ME:
                case DESTROY_SUMMON:
                case STEAL_BUFF:
                case DISCORD:
                    return true;
                default:
                    return false;
            }
        }

        public SkillMastery getDefaultSkillMastery() {
            switch (this) {
                case BUFF:
                case HOT:
                case HEAL_PERCENT:
                    return SkillMastery.DOUBLE_TIME;
                case HEAL:
                    return SkillMastery.INC_POWER;
                default:
                    return SkillMastery.ZERO_REUSE;
            }
        }
    }

    public static class AddedSkill {
        public static final AddedSkill[] EMPTY_ARRAY = new AddedSkill[0];

        public final int id;
        public final int level;
        private SkillEntry _skill;

        public AddedSkill(final int id, final int level) {
            this.id = id;
            this.level = level;
        }

        public SkillEntry getSkill() {
            if (_skill == null) {
                _skill = SkillTable.getInstance().getSkillEntry(id, level);
            }
            return _skill;
        }
    }
}
