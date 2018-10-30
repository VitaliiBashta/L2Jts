package org.mmocore.gameserver.scripts.npc.model.residences.fortress.siege;

import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.model.entity.events.impl.FortressSiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.DoorObject;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.ai.residences.fortress.siege.MercenaryCaption;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;

import java.util.List;

/**
 * @author VISTALL
 * @date 8:41/19.04.2011
 */
public class MercenaryCaptionInstance extends MonsterInstance {
    private DoorDeathListener _doorDeathListener = new DoorDeathListener();

    public MercenaryCaptionInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setHasChatWindow(false);
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        Fortress f = getFortress();
        FortressSiegeEvent event = f.getSiegeEvent();
        List<DoorObject> objects = event.getObjects(FortressSiegeEvent.ENTER_DOORS);
        for (DoorObject d : objects) {
            d.getDoor().addListener(_doorDeathListener);
        }
    }

    @Override
    public boolean isAttackable(Creature attacker) {
        return isAutoAttackable(attacker);
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        FortressSiegeEvent event = getEvent(FortressSiegeEvent.class);
        if (event == null) {
            return false;
        }
        Player player = attacker.getPlayer();
        if (player == null) {
            return false;
        }
        SiegeClanObject object = event.getSiegeClan(FortressSiegeEvent.DEFENDERS, player.getClan());
        if (object == null) {
            return false;
        }
        return true;
    }

    @Override
    public void onDeath(Creature killer) {
        super.onDeath(killer);

        ChatUtils.shout(this, NpcString.THE_GODS_HAVE_FORSAKEN_US__RETREAT);
    }

    @Override
    public void onDecay() {
        super.onDecay();

        Fortress f = getFortress();
        FortressSiegeEvent event = f.getSiegeEvent();
        List<DoorObject> objects = event.getObjects(FortressSiegeEvent.ENTER_DOORS);
        for (DoorObject d : objects) {
            d.getDoor().removeListener(_doorDeathListener);
        }
    }

    private class DoorDeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature door, Creature killer) {
            if (isDead()) {
                return;
            }

            FortressSiegeEvent event = door.getEvent(FortressSiegeEvent.class);
            if (event == null) {
                return;
            }

            ChatUtils.shout(MercenaryCaptionInstance.this, NpcString.WE_HAVE_BROKEN_THROUGH_THE_GATE_DESTROY_THE_ENCAMPMENT_AND_MOVE_TO_THE_COMMAND_POST);

            List<DoorObject> objects = event.getObjects(FortressSiegeEvent.ENTER_DOORS);
            for (DoorObject d : objects) {
                d.open(event);
            }

            ((MercenaryCaption) getAI()).startMove(true);
        }
    }
}
