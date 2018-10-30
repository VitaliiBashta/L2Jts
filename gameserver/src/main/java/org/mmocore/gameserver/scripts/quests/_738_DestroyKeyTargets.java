package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.network.lineage.components.NpcString;

/**
 * @author VISTALL
 * @date 16:23/12.04.2011
 */
public class _738_DestroyKeyTargets extends Dominion_KillSpecialUnitQuest {
    public _738_DestroyKeyTargets() {
        super();
        addLevelCheck(40);
    }

    @Override
    protected NpcString startNpcString() {
        return NpcString.DEFEAT_S1_WARSMITHS_AND_OVERLORDS;
    }

    @Override
    protected NpcString progressNpcString() {
        return NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_WARSMITHS_AND_OVERLORDS;
    }

    @Override
    protected NpcString doneNpcString() {
        return NpcString.YOU_DESTROYED_THE_ENEMYS_PROFESSIONALS;
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
                ClassId.necromancer,
                ClassId.sword_singer,
                ClassId.bladedancer,
                ClassId.overlord,
                ClassId.warsmith,
                ClassId.soultaker,
                ClassId.sword_muse,
                ClassId.spectral_dancer,
                ClassId.dominator,
                ClassId.maestro,
                ClassId.inspector,
                ClassId.judicator
        };
    }
}
