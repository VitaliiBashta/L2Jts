package org.mmocore.gameserver.scripts.ai.monas.FurnaceSpawnRoom;

import org.mmocore.gameserver.ai.CharacterAI;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.data.scripts.Scripts;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.object.Creature;

import java.lang.reflect.Constructor;

/**
 * @author KilRoy
 * Базовый AI для Furnace (18914)
 */
public class FurnaceDefault extends DefaultAI {
    public FurnaceDefault(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();

        final NpcInstance actor = getActor();

        if (actor == null)
            return;

        if (actor.getZone(ZoneType.dummy).getName().startsWith("[balance_"))
            setNewAi("FurnaceBalance");
        else if (actor.getZone(ZoneType.dummy).getName().startsWith("[magic_"))
            setNewAi("FurnaceMagic");
        else if (actor.getZone(ZoneType.dummy).getName().startsWith("[protection_"))
            setNewAi("FurnaceProtection");
        else if (actor.getZone(ZoneType.dummy).getName().startsWith("[will_"))
            setNewAi("FurnaceWill");
    }

    private void setNewAi(final String aiName) {
        Constructor<?> aiConstructor = null;
        aiConstructor = Scripts.getInstance().getClasses().get("ai.monas.FurnaceSpawnRoom." + aiName).getConstructors()[0];
        if (aiConstructor != null) {
            try {
                getActor().setAI((CharacterAI) aiConstructor.newInstance(getActor()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            getActor().getAI().startAITask();
        }
    }

    @Override
    protected boolean checkAggression(final Creature target) {
        return false;
    }

    @Override
    protected void onEvtAggression(final Creature attacker, final int aggro) {
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }
}