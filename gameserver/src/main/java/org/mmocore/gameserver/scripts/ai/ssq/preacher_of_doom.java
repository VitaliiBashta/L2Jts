package org.mmocore.gameserver.scripts.ai.ssq;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.world.World;

import java.util.List;

public class preacher_of_doom extends DefaultAI {
    private static final long castDelay = 60 * 1000L;
    private static final long buffDelay = 1000L;
    /**
     * Messages of NPCs *
     */
    private static final NpcString[] preacherText = {
            NpcString.THIS_WORLD_WILL_SOON_BE_ANNIHILATED,
            NpcString.ALL_IS_LOST__PREPARE_TO_MEET_THE_GODDESS_OF_DEATH,
            NpcString.ALL_IS_LOST__THE_PROPHECY_OF_DESTRUCTION_HAS_BEEN_FULFILLED,
            NpcString.THE_END_OF_TIME_HAS_COME__THE_PROPHECY_OF_DESTRUCTION_HAS_BEEN_FULFILLED
    };
    private long _castVar = 0;
    private long _buffVar = 0;

    public preacher_of_doom(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead()) {
            return true;
        }

        int winningCabal = SevenSigns.getInstance().getCabalHighestScore();

        if (winningCabal == SevenSigns.CABAL_NULL) {
            return true;
        }

        int losingCabal = SevenSigns.CABAL_NULL;

        if (winningCabal == SevenSigns.CABAL_DAWN) {
            losingCabal = SevenSigns.CABAL_DUSK;
        } else if (winningCabal == SevenSigns.CABAL_DUSK) {
            losingCabal = SevenSigns.CABAL_DAWN;
        }

        if (_castVar + castDelay < System.currentTimeMillis()) {
            _castVar = System.currentTimeMillis();
            Functions.npcSay(actor, preacherText[Rnd.get(preacherText.length)]);
        }
        /**
         * For each known player in range, cast either the positive or negative buff. <BR>
         * The stats affected depend on the player type, either a fighter or a mystic. <BR>
         * Curse of Destruction (Loser) - Fighters: -25% Accuracy, -25% Effect Resistance - Mystics: -25% Casting Speed, -25% Effect Resistance
         *
         * Blessing of Prophecy (Winner) - Fighters: +25% Max Load, +25% Effect Resistance - Mystics: +25% Magic Cancel Resist, +25% Effect Resistance
         */
        if (_buffVar + buffDelay < System.currentTimeMillis()) {
            _buffVar = System.currentTimeMillis();
            for (Player player : World.getAroundPlayers(actor, 300, 200)) {
                if (player == null) {
                    continue;
                }
                int playerCabal = SevenSigns.getInstance().getPlayerCabal(player);
                int i0 = Rnd.get(100);
                int i1 = Rnd.get(10000);
                if (playerCabal == losingCabal) {
                    if (player.isMageClass()) {
                        int PREACHER_MAGE_SKILL_ID = 4362;
                        List<Effect> effects = player.getEffectList().getEffectsBySkillId(PREACHER_MAGE_SKILL_ID);
                        if (effects == null || effects.size() <= 0) {
                            if (i1 < 1) {
                                Functions.npcSay(actor, NpcString.YOU_DONT_HAVE_ANY_HOPE__YOUR_END_HAS_COME);
                            }

                            SkillEntry skill = SkillTable.getInstance().getSkillEntry(PREACHER_MAGE_SKILL_ID, 1);
                            if (skill != null) {
                                actor.altUseSkill(skill, player);
                            }
                        } else if (i0 < 5) {
                            if (i1 < 500) {
                                Functions.npcSay(actor, NpcString.A_CURSE_UPON_YOU);
                            }

                            SkillEntry skill = SkillTable.getInstance().getSkillEntry(PREACHER_MAGE_SKILL_ID, 2);
                            if (skill != null) {
                                actor.altUseSkill(skill, player);
                            }
                        }
                    } else {
                        int PREACHER_FIGHTER_SKILL_ID = 4361;
                        List<Effect> effects = player.getEffectList().getEffectsBySkillId(PREACHER_FIGHTER_SKILL_ID);
                        if (effects == null || effects.size() <= 0) {
                            if (i1 < 1) {
                                Functions.npcSay(actor, NpcString.S1__YOU_BRING_AN_ILL_WIND, player.getName());
                            }

                            SkillEntry skill = SkillTable.getInstance().getSkillEntry(PREACHER_FIGHTER_SKILL_ID, 1);
                            if (skill != null) {
                                actor.altUseSkill(skill, player);
                            }
                        } else if (i0 < 5) {
                            if (i1 < 500) {
                                Functions.npcSay(actor, NpcString.S1__YOU_MIGHT_AS_WELL_GIVE_UP, player.getName());
                            }

                            SkillEntry skill = SkillTable.getInstance().getSkillEntry(PREACHER_FIGHTER_SKILL_ID, 2);
                            if (skill != null) {
                                actor.altUseSkill(skill, player);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}