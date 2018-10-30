package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class ExtractStone extends Skill {
    private static final int ExtractScrollSkill = 2630;
    private static final int ExtractedCoarseRedStarStone = 13858;
    private static final int ExtractedCoarseBlueStarStone = 13859;
    private static final int ExtractedCoarseGreenStarStone = 13860;

    private static final int ExtractedRedStarStone = 14009;
    private static final int ExtractedBlueStarStone = 14010;
    private static final int ExtractedGreenStarStone = 14011;

    private static final int RedStarStone1 = 18684;
    private static final int RedStarStone2 = 18685;
    private static final int RedStarStone3 = 18686;

    private static final int BlueStarStone1 = 18687;
    private static final int BlueStarStone2 = 18688;
    private static final int BlueStarStone3 = 18689;

    private static final int GreenStarStone1 = 18690;
    private static final int GreenStarStone2 = 18691;
    private static final int GreenStarStone3 = 18692;

    private static final int FireEnergyCompressionStone = 14015;
    private static final int WaterEnergyCompressionStone = 14016;
    private static final int WindEnergyCompressionStone = 14017;
    private static final int EarthEnergyCompressionStone = 14018;
    private static final int DarknessEnergyCompressionStone = 14019;
    private static final int SacredEnergyCompressionStone = 14020;

    private static final int SeedFire = 18679;
    private static final int SeedWater = 18678;
    private static final int SeedWind = 18680;
    private static final int SeedEarth = 18681;
    private static final int SeedDarkness = 18683;
    private static final int SeedDivinity = 18682;

    private final List<Integer> _npcIds = new ArrayList<>();

    public ExtractStone(final StatsSet set) {
        super(set);
        final StringTokenizer st = new StringTokenizer(set.getString("npcIds", ""), ";");
        while (st.hasMoreTokens()) {
            _npcIds.add(Integer.valueOf(st.nextToken()));
        }
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (target == null || !target.isNpc() || getItemId(target.getNpcId()) == 0) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return false;
        }

        if (!_npcIds.isEmpty() && !_npcIds.contains(Integer.valueOf(target.getNpcId()))) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return false;
        }

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    /**
     * Возвращает ID предмета получаемого из npcId.
     *
     * @return
     */
    private int getItemId(final int npcId) {
        switch (npcId) {
            case RedStarStone1:
            case RedStarStone2:
            case RedStarStone3:
                if (_id == ExtractScrollSkill) {
                    return ExtractedCoarseRedStarStone;
                }
                return ExtractedRedStarStone;
            case BlueStarStone1:
            case BlueStarStone2:
            case BlueStarStone3:
                if (_id == ExtractScrollSkill) {
                    return ExtractedCoarseBlueStarStone;
                }
                return ExtractedBlueStarStone;
            case GreenStarStone1:
            case GreenStarStone2:
            case GreenStarStone3:
                if (_id == ExtractScrollSkill) {
                    return ExtractedCoarseGreenStarStone;
                }
                return ExtractedGreenStarStone;
            case SeedFire:
                return FireEnergyCompressionStone;
            case SeedWater:
                return WaterEnergyCompressionStone;
            case SeedWind:
                return WindEnergyCompressionStone;
            case SeedEarth:
                return EarthEnergyCompressionStone;
            case SeedDarkness:
                return DarknessEnergyCompressionStone;
            case SeedDivinity:
                return SacredEnergyCompressionStone;
            default:
                return 0;
        }
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        final Player player = activeChar.getPlayer();
        if (player == null) {
            return;
        }

        for (final Creature target : targets) {
            if (target != null && getItemId(target.getNpcId()) != 0) {
                final double rate = ServerConfig.RATE_QUESTS_DROP * player.getPremiumAccountComponent().getPremiumBonus().getQuestDropRate();
                final long count = _id == ExtractScrollSkill ? 1 : Math.min(10, Rnd.get((int) (getLevel() * rate + 1)));
                final int itemId = getItemId(target.getNpcId());

                if (count > 0) {
                    player.getInventory().addItem(itemId, count);
                    player.sendPacket(new PlaySound(Quest.SOUND_ITEMGET));
                    player.sendPacket(SystemMessage.obtainItems(itemId, count, 0));
                    player.sendChanges();
                } else {
                    player.sendPacket(SystemMsg.THE_COLLECTION_HAS_FAILED);
                }

                target.doDie(player);
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}
