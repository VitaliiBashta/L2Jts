package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.configuration.config.EventsConfig;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.attachments.FlagItemAttachment;
import org.mmocore.gameserver.skills.SkillEntry;

public class RequestMagicSkillUse extends L2GameClientPacket {
    private Integer _magicId;
    private boolean _ctrlPressed;
    private boolean _shiftPressed;

    /**
     * packet type id 0x39
     * format:		cddc
     */
    @Override
    protected void readImpl() {
        _magicId = readD();
        _ctrlPressed = readD() != 0;
        _shiftPressed = readC() != 0;
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();

        if (activeChar == null) {
            return;
        }

        activeChar.setActive();

        if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        }

        final SkillEntry skill = activeChar.getKnownSkill(_magicId);
        if (skill != null) {
            if (skill.isDisabled()) {
                return;
            }

            if (!(skill.getTemplate().isActive() || skill.getTemplate().isToggle())) {
                return;
            }

            final FlagItemAttachment attachment = activeChar.getActiveWeaponFlagAttachment();
            if (attachment != null && !attachment.canCast(activeChar, skill)) {
                activeChar.sendActionFailed();
                return;
            }

            // В режиме трансформации доступны только скилы трансформы
            if (activeChar.isTransformed() && !activeChar.getAllSkills().contains(skill)) {
                return;
            }

            if (skill.getTemplate().isToggle())
            // TODO: DS: что это здесь вообще делает ?
            {
                if (!OtherConfig.CUSTOM_FAKE_DEATH && activeChar.isFakeDeath()) {
                    activeChar.sendActionFailed();
                    return;
                }
                if (activeChar.isSitting()) {
                    activeChar.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
                    activeChar.sendActionFailed();
                    return;
                } else if (activeChar.getEffectList().getEffectsBySkill(skill) != null) {
                    activeChar.getEffectList().stopEffect(skill.getId());
                    activeChar.sendActionFailed();
                    return;
                }
            }

            //FIXME: кастыль
            if (activeChar.isInActivePvpEvent() && ArrayUtils.contains(EventsConfig.disallowedSkills, skill.getId())) {
                activeChar.sendMessage("Can not be cast on PvP event!");
                activeChar.sendActionFailed();
                return;
            }


            final Creature target = skill.getTemplate().getAimingTarget(activeChar, activeChar.getTarget());
            if (target != null && (target.isPlayable()) && activeChar != target) {
                if (activeChar.atMutualWarWith(target.getPlayer()) && skill.getTemplate().isOffensive()) {
                    _ctrlPressed = true;
                }
                if (EventsConfig.TVTCantAttackOurTeam || EventsConfig.CFTCantAttackOurTeam) {
                    if (activeChar.isInActivePvpEvent() && (target.getPlayer().getTeam() == activeChar.getTeam() && skill.getTemplate().isOffensive())) {
                        activeChar.sendMessage("Can not be Attack our team!");
                        activeChar.sendActionFailed();
                        return;
                    }
                }
            }

            activeChar.setGroundSkillLoc(null);
            activeChar.getAI().Cast(skill, target, _ctrlPressed, _shiftPressed);
        } else {
            activeChar.sendActionFailed();
        }
    }
}