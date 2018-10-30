package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.network.lineage.components.NpcString;

/**
 * @author VISTALL
 * @date 16:14/12.04.2011
 */
public class _735_MakeSpearsDull extends Dominion_KillSpecialUnitQuest {
    public _735_MakeSpearsDull() {
        super();
        addLevelCheck(40);
    }

    @Override
    protected NpcString startNpcString() {
        return NpcString.DEFEAT_S1_WARRIORS_AND_ROGUES;
    }

    @Override
    protected NpcString progressNpcString() {
        return NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_WARRIORS_AND_ROGUES;
    }

    @Override
    protected NpcString doneNpcString() {
        return NpcString.YOU_WEAKENED_THE_ENEMYS_ATTACK;
    }

    @Override
    protected int getRandomMin() {
        return 15;
    }

    @Override
    protected int getRandomMax() {
        return 20;
    }

    @Override
    protected ClassId[] getTargetClassIds() {
        return new ClassId[]{
                ClassId.gladiator,
                ClassId.warlord,
                ClassId.treasure_hunter,
                ClassId.hawkeye,
                ClassId.plains_walker,
                ClassId.silver_ranger,
                ClassId.abyss_walker,
                ClassId.phantom_ranger,
                ClassId.destroyer,
                ClassId.tyrant,
                ClassId.bounty_hunter,
                ClassId.duelist,
                ClassId.dreadnought,
                ClassId.sagittarius,
                ClassId.adventurer,
                ClassId.wind_rider,
                ClassId.moonlight_sentinel,
                ClassId.ghost_hunter,
                ClassId.ghost_sentinel,
                ClassId.titan,
                ClassId.grand_khauatari,
                ClassId.fortune_seeker,
                ClassId.berserker,
                ClassId.m_soul_breaker,
                ClassId.f_soul_breaker,
                ClassId.arbalester,
                ClassId.doombringer,
                ClassId.m_soul_hound,
                ClassId.f_soul_hound,
                ClassId.trickster
        };
    }
}
