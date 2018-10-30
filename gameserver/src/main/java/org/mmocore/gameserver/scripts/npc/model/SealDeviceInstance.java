package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.ExStartScenePlayer;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;

/**
 * @author pchayka
 */
public final class SealDeviceInstance extends MonsterInstance {
    private boolean _gaveItem = false;

    public SealDeviceInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void reduceCurrentHp(double i, Creature attacker, SkillEntry skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect,
                                boolean transferDamage, boolean isDot, boolean sendMessage, boolean lethal) {
        if (getCurrentHp() < i) {
            if (!_gaveItem && ItemFunctions.getItemCount(attacker.getPlayer(), 13846) < 4) {
                this.setRHandId(15281);
                this.broadcastCharInfo();
                ItemFunctions.addItem(attacker.getPlayer(), 13846, 1, true);
                _gaveItem = true;

                if (ItemFunctions.getItemCount(attacker.getPlayer(), 13846) >= 4) {
                    attacker.getPlayer().showQuestMovie(ExStartScenePlayer.SCENE_SSQ_SEALING_EMPEROR_2ND);
                    ThreadPoolManager.getInstance().schedule(new TeleportPlayer(attacker.getPlayer()), 26500L);
                }
            }
            i = getCurrentHp() - 1;
        }
        attacker.reduceCurrentHp(450, this, null, true, false, true, false, false, false, true);
        super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage, lethal);
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public boolean isLethalImmune() {
        return true;
    }

    @Override
    public boolean isMovementDisabled() {
        return true;
    }

    private class TeleportPlayer extends RunnableImpl {
        final Player _p;

        public TeleportPlayer(Player p) {
            _p = p;
        }

        @Override
        public void runImpl() {
            for (NpcInstance n : _p.getReflection().getNpcs()) {
                if (n.getNpcId() != 32586 && n.getNpcId() != 32587) {
                    n.deleteMe();
                }
            }
            _p.getPlayer().teleToLocation(new Location(-89560, 215784, -7488));
        }
    }
}