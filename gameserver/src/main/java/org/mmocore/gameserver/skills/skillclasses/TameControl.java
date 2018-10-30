package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class TameControl extends Skill {
    private final int _type;

    public TameControl(final StatsSet set) {
        super(set);
        _type = set.getInteger("type", 0);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }

        if (!activeChar.isPlayer()) {
            return;
        }

        final Player player = activeChar.getPlayer();
        switch (_type) {
            case 0:
                for (final Creature target : targets) {
                    if (target instanceof TameControlTarget) {
                        ((TameControlTarget) target).free();
                    }
                }
                break;
            case 1:
                for (final NpcInstance npc : player.getTamedBeasts()) {
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player, AllSettingsConfig.FOLLOW_RANGE);
                }
                break;
            case 3:
                for (final NpcInstance npc : player.getTamedBeasts()) {
                    ((TameControlTarget) npc).doCastBuffs();
                }
                break;
            case 4:
                for (final NpcInstance npc : player.getTamedBeasts()) {
                    ((TameControlTarget) npc).free();
                }
                break;
        }
    }

    public interface TameControlTarget {
        void doCastBuffs();

        void free();
    }
}