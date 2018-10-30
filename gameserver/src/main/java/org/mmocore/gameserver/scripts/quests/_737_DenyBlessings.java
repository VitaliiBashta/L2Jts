package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.network.lineage.components.NpcString;

/**
 * @author VISTALL
 * @date 16:19/12.04.2011
 */
public class _737_DenyBlessings extends Dominion_KillSpecialUnitQuest {
    public _737_DenyBlessings() {
        super();
        addLevelCheck(40);
    }

    @Override
    protected NpcString startNpcString() {
        return NpcString.DEFEAT_S1_HEALERS_AND_BUFFERS;
    }

    @Override
    protected NpcString progressNpcString() {
        return NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_HEALERS_AND_BUFFERS;
    }

    @Override
    protected NpcString doneNpcString() {
        return NpcString.YOU_HAVE_WEAKENED_THE_ENEMYS_SUPPORT;
    }

    @Override
    protected int getRandomMin() {
        return 3;
    }

    @Override
    protected int getRandomMax() {
        return 8;
    }

    @Override
    protected ClassId[] getTargetClassIds() {
        return new ClassId[]{
                ClassId.bishop,
                ClassId.prophet,
                ClassId.elder,
                ClassId.elder,
                ClassId.shillien_elder,
                ClassId.cardinal,
                ClassId.hierophant,
                ClassId.eva_saint,
                ClassId.shillien_saint,
                ClassId.doomcryer
        };
    }
}
