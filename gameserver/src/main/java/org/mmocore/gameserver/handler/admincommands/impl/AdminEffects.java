package org.mmocore.gameserver.handler.admincommands.impl;

import org.jts.dataparser.data.holder.TransformHolder;
import org.jts.dataparser.data.holder.transform.TransformData;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.base.InvisibleType;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.Earthquake;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.AbnormalEffect;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Util;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;

import java.util.List;
import java.util.Optional;

public class AdminEffects implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanUseEffect) {
            return false;
        }

        int val;
        AbnormalEffect ae = AbnormalEffect.NULL;
        GameObject target = activeChar.getTarget();

        switch (command) {
            case admin_invis:
            case admin_vis:
                if (activeChar.isInvisible()) {
                    activeChar.setInvisibleType(InvisibleType.NONE);
//					activeChar.setMessageRefusal(false);
//					activeChar.setBlockAll(false);
                    activeChar.sendAdminMessage("Now, you can be seen.");
                    activeChar.broadcastCharInfo();
                    activeChar.sendEtcStatusUpdate();
                    if (activeChar.getServitor() != null) {
                        activeChar.getServitor().broadcastCharInfo();
                    }
                } else {
                    activeChar.setInvisibleType(InvisibleType.NORMAL);
//					activeChar.setMessageRefusal(true);
//					activeChar.setBlockAll(true);
                    activeChar.sendAdminMessage("Now, you cannot be seen.");
                    activeChar.sendUserInfo(true);
                    activeChar.sendEtcStatusUpdate();
                    World.removeObjectFromPlayers(activeChar);
                }
                break;
            case admin_gmspeed:
                if (wordList.length < 2) {
                    val = 0;
                } else {
                    try {
                        val = Integer.parseInt(wordList[1]);
                    } catch (Exception e) {
                        activeChar.sendAdminMessage("//gmspeed [0..4]");
                        return false;
                    }
                }
                final List<Effect> superhaste = activeChar.getEffectList().getEffectsBySkillId(7029);
                final int sh_level = superhaste == null ? 0 : superhaste.isEmpty() ? 0 : superhaste.get(0).getSkill().getLevel();

                if (val == 0) {
                    if (sh_level != 0) {
                        activeChar.doCast(SkillTable.getInstance().getSkillEntry(7029, sh_level), activeChar, true); //снимаем еффект
                    }
                } else if (val >= 1 && val <= 4) {
                    if (val != sh_level) {
                        if (sh_level != 0) {
                            activeChar.doCast(SkillTable.getInstance().getSkillEntry(7029, sh_level), activeChar, true); //снимаем еффект
                        }
                        activeChar.doCast(SkillTable.getInstance().getSkillEntry(7029, val), activeChar, true);
                    }
                    activeChar.sendAdminMessage(((val * 0.5) * 100) + "% fast");
                } else {
                    activeChar.sendAdminMessage("//gmspeed [0..4]");
                }
                break;
            case admin_invul:
                handleInvul(activeChar, activeChar);
                break;
        }

        if (!activeChar.isGM()) {
            return false;
        }

        switch (command) {
            case admin_offline_vis:
                for (final Player player : GameObjectsStorage.getPlayers()) {
                    if (player != null && player.isInOfflineMode()) {
                        player.setInvisibleType(InvisibleType.NONE);
                        player.decayMe();
                        player.spawnMe();
                    }
                }
                break;
            case admin_offline_invis:
                for (final Player player : GameObjectsStorage.getPlayers()) {
                    if (player != null && player.isInOfflineMode()) {
                        player.setInvisibleType(InvisibleType.NORMAL);
                        player.decayMe();
                    }
                }
                break;
            case admin_earthquake:
                try {
                    final int intensity = Integer.parseInt(wordList[1]);
                    final int duration = Integer.parseInt(wordList[2]);
                    activeChar.broadcastPacket(new Earthquake(activeChar.getLoc(), intensity, duration));
                } catch (Exception e) {
                    activeChar.sendAdminMessage("USAGE: //earthquake intensity duration");
                    return false;
                }
                break;
            case admin_block:
                if (target == null || !target.isCreature()) {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
                if (((Creature) target).isBlocked()) {
                    return false;
                }
                ((Creature) target).abortAttack(true, false);
                ((Creature) target).abortCast(true, false);
                ((Creature) target).block();
                activeChar.sendAdminMessage("Target blocked.");
                break;
            case admin_unblock:
                if (target == null || !target.isCreature()) {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
                if (!((Creature) target).isBlocked()) {
                    return false;
                }
                ((Creature) target).unblock();
                activeChar.sendAdminMessage("Target unblocked.");
                break;
            case admin_changename:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //changename newName");
                    return false;
                }
                if (target == null) {
                    target = activeChar;
                }
                if (!target.isCreature()) {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
                final String oldName = target.getName();
                final String newName = Util.joinStrings(" ", wordList, 1);

                ((Creature) target).setName(newName);
                ((Creature) target).broadcastCharInfo();

                activeChar.sendAdminMessage("Changed name from " + oldName + " to " + newName + '.');
                break;
            case admin_setinvul:
                if (target == null || !target.isPlayer()) {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
                handleInvul(activeChar, (Player) target);
                break;
            case admin_getinvul:
                if (target != null && target.isCreature()) {
                    activeChar.sendAdminMessage("Target " + target.getName() + "(object ID: " + target.getObjectId() + ") is " + (!((Creature) target).isInvul() ? "NOT " : "") + "invul");
                }
                break;
            case admin_social:
                if (wordList.length < 2) {
                    val = Rnd.get(1, 7);
                } else {
                    try {
                        val = Integer.parseInt(wordList[1]);
                    } catch (NumberFormatException nfe) {
                        activeChar.sendAdminMessage("USAGE: //social value");
                        return false;
                    }
                }
                if (target == null || target.equals(activeChar)) {
                    activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), val));
                } else if (target.isCreature()) {
                    ((Creature) target).broadcastPacket(new SocialAction(target.getObjectId(), val));
                }
                break;
            case admin_abnormal:
                try {
                    if (wordList.length > 1) {
                        ae = AbnormalEffect.getByName(wordList[1]);
                    }
                } catch (Exception e) {
                    activeChar.sendAdminMessage("USAGE: //abnormal name");
                    activeChar.sendAdminMessage("//abnormal - Clears all abnormal effects");
                    return false;
                }

                final Creature effectTarget = target == null ? activeChar : (Creature) target;

                if (ae == AbnormalEffect.NULL) {
                    effectTarget.startAbnormalEffect(AbnormalEffect.NULL);
                    effectTarget.sendMessage("Abnormal effects clearned by admin.");
                    if (!effectTarget.equals(activeChar)) {
                        effectTarget.sendMessage("Abnormal effects clearned.");
                    }
                } else {
                    effectTarget.startAbnormalEffect(ae);
                    effectTarget.sendMessage("Admin added abnormal effect: " + ae.getName());
                    if (!effectTarget.equals(activeChar)) {
                        effectTarget.sendMessage("Added abnormal effect: " + ae.getName());
                    }
                }
                break;
            case admin_transform:
                try {
                    val = Integer.parseInt(wordList[1]);
                } catch (Exception e) {
                    activeChar.sendAdminMessage("USAGE: //transform transform_id");
                    return false;
                }
                final Optional<TransformData> data = TransformHolder.getInstance().getTransformId(val);
                if (data != null) {
                    activeChar.stopTransformation();
                    activeChar.setTransformation(data.get());
                }
                break;
            case admin_showmovie:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //showmovie id");
                    return false;
                }
                final int id;
                try {
                    id = Integer.parseInt(wordList[1]);
                } catch (NumberFormatException e) {
                    activeChar.sendAdminMessage("You must specify id");
                    return false;
                }
                activeChar.showQuestMovie(id);
                break;
        }

        return true;
    }

    private void handleInvul(final Player activeChar, final Player target) {
        if (target.isInvul()) {
            target.setIsInvul(false);
            target.stopAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
            if (target.getServitor() != null) {
                target.getServitor().setIsInvul(false);
                target.getServitor().stopAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
            }
            target.sendAdminMessage("Now, you can die.");
        } else {
            target.setIsInvul(true);
            target.startAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
            if (target.getServitor() != null) {
                target.getServitor().setIsInvul(true);
                target.getServitor().startAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
            }
            target.sendAdminMessage("Now, you cannot die.");
        }
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_invis,
        admin_vis,
        admin_offline_vis,
        admin_offline_invis,
        admin_earthquake,
        admin_block,
        admin_unblock,
        admin_changename,
        admin_gmspeed,
        admin_invul,
        admin_setinvul,
        admin_getinvul,
        admin_social,
        admin_abnormal,
        admin_transform,
        admin_showmovie
    }
}