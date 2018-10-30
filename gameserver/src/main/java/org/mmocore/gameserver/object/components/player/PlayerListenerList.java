package org.mmocore.gameserver.object.components.player;

import org.mmocore.gameserver.listener.actor.player.*;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.creatures.listeners.CharListenerList;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.transformdata.TransformComponent;

/**
 * @author G1ta0
 */
public class PlayerListenerList extends CharListenerList {
    public PlayerListenerList(final Player actor) {
        super(actor);
    }

    @Override
    public Player getActor() {
        return (Player) actor;
    }

    public void onEnter() {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnPlayerEnterListener.class::isInstance).forEach(listener -> {
                ((OnPlayerEnterListener) listener).onPlayerEnter(getActor());
            });
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnPlayerEnterListener.class::isInstance).forEach(listener -> {
                ((OnPlayerEnterListener) listener).onPlayerEnter(getActor());
            });
        }
    }

    public void onExit() {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnPlayerExitListener.class::isInstance).forEach(listener -> {
                ((OnPlayerExitListener) listener).onPlayerExit(getActor());
            });
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnPlayerExitListener.class::isInstance).forEach(listener -> {
                ((OnPlayerExitListener) listener).onPlayerExit(getActor());
            });
        }
    }

    public void onForcedDisconnect() {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnForcedDisconnectListener.class::isInstance).forEach(listener -> {
                ((OnForcedDisconnectListener) listener).onForcedDisconnect(getActor());
            });
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnForcedDisconnectListener.class::isInstance).forEach(listener -> {
                ((OnForcedDisconnectListener) listener).onForcedDisconnect(getActor());
            });
        }
    }

    public void onTeleport(final int x, final int y, final int z, final Reflection reflection) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnTeleportListener.class::isInstance).forEach(listener -> {
                ((OnTeleportListener) listener).onTeleport(getActor(), x, y, z, reflection);
            });
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnTeleportListener.class::isInstance).forEach(listener -> {
                ((OnTeleportListener) listener).onTeleport(getActor(), x, y, z, reflection);
            });
        }
    }

    public void onPartyInvite() {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnPlayerPartyInviteListener.class::isInstance).forEach(listener -> {
                ((OnPlayerPartyInviteListener) listener).onPartyInvite(getActor());
            });
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnPlayerPartyInviteListener.class::isInstance).forEach(listener -> {
                ((OnPlayerPartyInviteListener) listener).onPartyInvite(getActor());
            });
        }
    }

    public void onPartyLeave() {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnPlayerPartyLeaveListener.class::isInstance).forEach(listener -> {
                ((OnPlayerPartyLeaveListener) listener).onPartyLeave(getActor());
            });
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnPlayerPartyLeaveListener.class::isInstance).forEach(listener -> {
                ((OnPlayerPartyLeaveListener) listener).onPartyLeave(getActor());
            });
        }
    }

    public void onSummonServitor(final Servitor summon) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnPlayerSummonServitorListener.class::isInstance).forEach(listener -> {
                ((OnPlayerSummonServitorListener) listener).onSummonServitor(getActor(), summon);
            });
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnPlayerSummonServitorListener.class::isInstance).forEach(listener -> {
                ((OnPlayerSummonServitorListener) listener).onSummonServitor(getActor(), summon);
            });
        }
    }

    public void onSay(final ChatType type, final String target, final String text) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnPlayerSayListener.class::isInstance).forEach(listener -> {
                ((OnPlayerSayListener) listener).onSay(getActor(), type, target, text);
            });
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnPlayerSayListener.class::isInstance).forEach(listener -> {
                ((OnPlayerSayListener) listener).onSay(getActor(), type, target, text);
            });
        }
    }

    public void onMailActivation() {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnMailActivationListener.class::isInstance).forEach(listener -> {
                ((OnMailActivationListener) listener).onMailActivation(getActor());
            });
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnMailActivationListener.class::isInstance).forEach(listener -> {
                ((OnMailActivationListener) listener).onMailActivation(getActor());
            });
        }
    }

    public void onItemPickup(final ItemInstance item) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnItemPickupListener.class::isInstance).forEach(listener -> {
                ((OnItemPickupListener) listener).onItemPickup(getActor(), item);
            });
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnItemPickupListener.class::isInstance).forEach(listener -> {
                ((OnItemPickupListener) listener).onItemPickup(getActor(), item);
            });
        }
    }

    public void onLevelUp(final int level) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnLevelUpListener.class::isInstance).forEach(listener -> {
                ((OnLevelUpListener) listener).onLevelUp(getActor(), level);
            });
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnLevelUpListener.class::isInstance).forEach(listener -> {
                ((OnLevelUpListener) listener).onLevelUp(getActor(), level);
            });
        }
    }

    public void onPlayerChat(final String text, final ChatType chatType, final String target) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnPlayerChatListener.class::isInstance).forEach(listener -> {
                ((OnPlayerChatListener) listener).onPlayerChat(getActor(), text, chatType, target);
            });
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnPlayerChatListener.class::isInstance).forEach(listener -> {
                ((OnPlayerChatListener) listener).onPlayerChat(getActor(), text, chatType, target);
            });
        }
    }

    public void onReceivedPacket(final Object... param) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnPlayerReceivedPacketListener.class::isInstance).forEach(listener -> {
                ((OnPlayerReceivedPacketListener) listener).onReceivedPacket(getActor(), param);
            });
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnPlayerReceivedPacketListener.class::isInstance).forEach(listener -> {
                ((OnPlayerReceivedPacketListener) listener).onReceivedPacket(getActor(), param);
            });
        }
    }

    public void onCurrentCp(final double currentCp, final double currentCpPercent) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnPlayerCurrentCpListener.class::isInstance).forEach(listener -> ((OnPlayerCurrentCpListener) listener).onPlayerCurrentCp(getActor(), currentCp, currentCpPercent));
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnPlayerCurrentCpListener.class::isInstance).forEach(listener -> ((OnPlayerCurrentCpListener) listener).onPlayerCurrentCp(getActor(), currentCp, currentCpPercent));
        }
    }

    public void onCurrentHp(final double currentHp, final double currentHpPercent) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnPlayerCurrentHpListener.class::isInstance).forEach(listener -> ((OnPlayerCurrentHpListener) listener).onPlayerCurrentHp(getActor(), currentHp, currentHpPercent));
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnPlayerCurrentHpListener.class::isInstance).forEach(listener -> ((OnPlayerCurrentHpListener) listener).onPlayerCurrentHp(getActor(), currentHp, currentHpPercent));
        }
    }

    public void onCurrentMp(final double currentMp, final double currentMpPercent) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnPlayerCurrentMpListener.class::isInstance).forEach(listener -> ((OnPlayerCurrentMpListener) listener).onPlayerCurrentMp(getActor(), currentMp, currentMpPercent));
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnPlayerCurrentMpListener.class::isInstance).forEach(listener -> ((OnPlayerCurrentMpListener) listener).onPlayerCurrentMp(getActor(), currentMp, currentMpPercent));
        }
    }

    public void onAddItem(final ItemInstance itemInstance) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnPlayerAddItemListener.class::isInstance).forEach(listener -> ((OnPlayerAddItemListener) listener).onPlayerAddItem(getActor(), itemInstance));
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnPlayerAddItemListener.class::isInstance).forEach(listener -> ((OnPlayerAddItemListener) listener).onPlayerAddItem(getActor(), itemInstance));
        }
    }

    public void onEnterTranform(final TransformComponent component) {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnPlayerEnterLeaveTransformListener.class::isInstance).forEach(listener -> ((OnPlayerEnterLeaveTransformListener) listener).onPlayerTransfromEnter(getActor(), component));
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnPlayerEnterLeaveTransformListener.class::isInstance).forEach(listener -> ((OnPlayerEnterLeaveTransformListener) listener).onPlayerTransfromEnter(getActor(), component));
        }
    }

    public void onLeaveTranform() {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnPlayerEnterLeaveTransformListener.class::isInstance).forEach(listener -> ((OnPlayerEnterLeaveTransformListener) listener).onPlayerTransfromLeave(getActor()));
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnPlayerEnterLeaveTransformListener.class::isInstance).forEach(listener -> ((OnPlayerEnterLeaveTransformListener) listener).onPlayerTransfromLeave(getActor()));
        }
    }
}