package org.mmocore.gameserver.handler.voicecommands.impl;

import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.stats.Formulas;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;

import java.text.NumberFormat;
import java.util.Locale;

public class WhoAmI implements IVoicedCommandHandler {
    private final String[] _commandList = {"whoami", "whoiam"};

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }

    @Override
    public boolean useVoicedCommand(final String command, final Player player, final String args) {
        final Creature target = null;

        //TODO [G1ta0] добавить рефлекты
        //TODO [G1ta0] возможно стоит показывать статы в зависимости от цели
        final double hpRegen = Formulas.calcHpRegen(player);
        final double cpRegen = Formulas.calcCpRegen(player);
        final double mpRegen = Formulas.calcMpRegen(player);
        final double hpDrain = player.calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0., target, null);
        final double mpDrain = player.calcStat(Stats.ABSORB_DAMAGEMP_PERCENT, 0., target, null);
        final double hpGain = player.calcStat(Stats.HEAL_EFFECTIVNESS, 100., target, null);
        final double mpGain = player.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., target, null);
        final double critPerc = 2 * player.calcStat(Stats.CRITICAL_DAMAGE, target, null);
        final double critStatic = player.calcStat(Stats.CRITICAL_DAMAGE_STATIC, target, null);
        final double mCritRate = player.calcStat(Stats.MCRITICAL_RATE, target, null);
        final double blowRate = player.calcStat(Stats.FATALBLOW_RATE, target, null);

        final ItemInstance shld = player.getSecondaryWeaponInstance();
        final boolean shield = shld != null && shld.getItemType() == WeaponType.NONE;

        final double shieldDef = shield ? player.calcStat(Stats.SHIELD_DEFENCE, 0, target, null) : 0.;
        final double shieldRate = shield ? player.calcStat(Stats.SHIELD_RATE, target, null) : 0.;

        final double xpRate = player.getRateExp();
        final double spRate = player.getRateSp();
        final double dropRate = player.getRateItems();
        final double adenaRate = player.getRateAdena();
        final double spoilRate = player.getRateSpoil();

        final double fireResist = player.calcStat(Element.FIRE.getDefence(), 0., target, null);
        final double windResist = player.calcStat(Element.WIND.getDefence(), 0., target, null);
        final double waterResist = player.calcStat(Element.WATER.getDefence(), 0., target, null);
        final double earthResist = player.calcStat(Element.EARTH.getDefence(), 0., target, null);
        final double holyResist = player.calcStat(Element.HOLY.getDefence(), 0., target, null);
        final double unholyResist = player.calcStat(Element.UNHOLY.getDefence(), 0., target, null);

        final double bleedPower = player.calcStat(Stats.BLEED_POWER, target, null);
        final double bleedResist = player.calcStat(Stats.BLEED_RESIST, target, null);
        final double poisonPower = player.calcStat(Stats.POISON_POWER, target, null);
        final double poisonResist = player.calcStat(Stats.POISON_RESIST, target, null);
        final double stunPower = player.calcStat(Stats.STUN_POWER, target, null);
        final double stunResist = player.calcStat(Stats.STUN_RESIST, target, null);
        final double rootPower = player.calcStat(Stats.ROOT_POWER, target, null);
        final double rootResist = player.calcStat(Stats.ROOT_RESIST, target, null);
        final double sleepPower = player.calcStat(Stats.SLEEP_POWER, target, null);
        final double sleepResist = player.calcStat(Stats.SLEEP_RESIST, target, null);
        final double paralyzePower = player.calcStat(Stats.PARALYZE_POWER, target, null);
        final double paralyzeResist = player.calcStat(Stats.PARALYZE_RESIST, target, null);
        final double mentalPower = player.calcStat(Stats.MENTAL_POWER, target, null);
        final double mentalResist = player.calcStat(Stats.MENTAL_RESIST, target, null);
        final double debuffPower = player.calcStat(Stats.DEBUFF_POWER, target, null);
        final double debuffResist = player.calcStat(Stats.DEBUFF_RESIST, target, null);
        final double cancelPower = player.calcStat(Stats.CANCEL_POWER, target, null);
        final double cancelResist = player.calcStat(Stats.CANCEL_RESIST, target, null);

        final double swordResist = 100. - player.calcStat(Stats.SWORD_WPN_VULNERABILITY, target, null);
        final double dualResist = 100. - player.calcStat(Stats.DUAL_WPN_VULNERABILITY, target, null);
        final double bluntResist = 100. - player.calcStat(Stats.BLUNT_WPN_VULNERABILITY, target, null);
        final double daggerResist = 100. - player.calcStat(Stats.DAGGER_WPN_VULNERABILITY, target, null);
        final double bowResist = 100. - player.calcStat(Stats.BOW_WPN_VULNERABILITY, target, null);
        final double crossbowResist = 100. - player.calcStat(Stats.CROSSBOW_WPN_VULNERABILITY, target, null);
        final double poleResist = 100. - player.calcStat(Stats.POLE_WPN_VULNERABILITY, target, null);
        final double fistResist = 100. - player.calcStat(Stats.FIST_WPN_VULNERABILITY, target, null);

        final double critChanceResist = 100. - player.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, target, null);
        final double critDamResistStatic = player.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, target, null);
        final double critDamResist = 100. - 100 * (player.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, 1., target, null) - critDamResistStatic);

        final NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(1);

        final HtmlMessage msg = new HtmlMessage(5);
        msg.setHtml(HtmCache.getInstance().getHtml("command/whoami.htm", player));
        msg.replace("%hpRegen%", df.format(hpRegen));
        msg.replace("%cpRegen%", df.format(cpRegen));
        msg.replace("%mpRegen%", df.format(mpRegen));
        msg.replace("%hpDrain%", df.format(hpDrain));
        msg.replace("%mpDrain%", df.format(mpDrain));
        msg.replace("%hpGain%", df.format(hpGain));
        msg.replace("%mpGain%", df.format(mpGain));
        msg.replace("%critPerc%", df.format(critPerc));
        msg.replace("%critStatic%", df.format(critStatic));
        msg.replace("%mCritRate%", df.format(mCritRate));
        msg.replace("%blowRate%", df.format(blowRate));
        msg.replace("%shieldDef%", df.format(shieldDef));
        msg.replace("%shieldRate%", df.format(shieldRate));
        msg.replace("%xpRate%", df.format(xpRate));
        msg.replace("%spRate%", df.format(spRate));
        msg.replace("%dropRate%", df.format(dropRate));
        msg.replace("%adenaRate%", df.format(adenaRate));
        msg.replace("%spoilRate%", df.format(spoilRate));
        msg.replace("%fireResist%", df.format(fireResist));
        msg.replace("%windResist%", df.format(windResist));
        msg.replace("%waterResist%", df.format(waterResist));
        msg.replace("%earthResist%", df.format(earthResist));
        msg.replace("%holyResist%", df.format(holyResist));
        msg.replace("%darkResist%", df.format(unholyResist));
        msg.replace("%bleedPower%", df.format(bleedPower));
        msg.replace("%bleedResist%", df.format(bleedResist));
        msg.replace("%poisonPower%", df.format(poisonPower));
        msg.replace("%poisonResist%", df.format(poisonResist));
        msg.replace("%stunPower%", df.format(stunPower));
        msg.replace("%stunResist%", df.format(stunResist));
        msg.replace("%rootPower%", df.format(rootPower));
        msg.replace("%rootResist%", df.format(rootResist));
        msg.replace("%sleepPower%", df.format(sleepPower));
        msg.replace("%sleepResist%", df.format(sleepResist));
        msg.replace("%paralyzePower%", df.format(paralyzePower));
        msg.replace("%paralyzeResist%", df.format(paralyzeResist));
        msg.replace("%mentalPower%", df.format(mentalPower));
        msg.replace("%mentalResist%", df.format(mentalResist));
        msg.replace("%debuffPower%", df.format(debuffPower));
        msg.replace("%debuffResist%", df.format(debuffResist));
        msg.replace("%cancelPower%", df.format(cancelPower));
        msg.replace("%cancelResist%", df.format(cancelResist));
        msg.replace("%swordResist%", df.format(swordResist));
        msg.replace("%dualResist%", df.format(dualResist));
        msg.replace("%bluntResist%", df.format(bluntResist));
        msg.replace("%daggerResist%", df.format(daggerResist));
        msg.replace("%bowResist%", df.format(bowResist));
        msg.replace("%crossbowResist%", df.format(crossbowResist));
        msg.replace("%fistResist%", df.format(fistResist));
        msg.replace("%poleResist%", df.format(poleResist));
        msg.replace("%critChanceResist%", df.format(critChanceResist));
        msg.replace("%critDamResist%", df.format(critDamResist));
        player.sendPacket(msg);

        return true;
    }
}
