package org.mmocore.gameserver.handler.voicecommands.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.handler.voicecommands.VoicedCommandHandler;
import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.manager.CoupleManager;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.entity.Couple;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ConfirmDlg;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.network.lineage.serverpackets.SetupGauge;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.AbnormalEffect;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.GameObjectsStorage;

public class Wedding implements IVoicedCommandHandler {
    private static final String[] _voicedCommands = {"divorce", "engage", "gotolove"};

    public static boolean checkGotoLove(final Player activeChar, final Player partner) {
        if (!activeChar.isMaried()) {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.YoureNotMarried"));
            return false;
        }

        if (activeChar.getPartnerId() == 0) {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PartnerNotInDB"));
            return false;
        }

        if (partner == null) {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PartnerOffline"));
            return false;
        }

        if (partner.isInOlympiadMode() || partner.isFestivalParticipant() || activeChar.isMovementDisabled() || activeChar.isMuted(null) || activeChar
                .isInOlympiadMode() || activeChar.isInDuel() || activeChar.isFestivalParticipant() || activeChar.getPlayer().isTerritoryFlagEquipped() || activeChar.getPlayer()
                .getActiveWeaponFlagAttachment() != null || partner
                .isInZone(ZoneType.no_summon)) {
            activeChar.sendMessage(new CustomMessage("common.TryLater"));
            return false;
        }

        if (!partner.getEvents().isEmpty() || !activeChar.getEvents().isEmpty()) {
            activeChar.sendMessage(new CustomMessage("common.TryLater"));
            return false;
        }

        if (activeChar.isInParty() && activeChar.getParty().isInDimensionalRift() || partner.isInParty() && partner.getParty().isInDimensionalRift()) {
            activeChar.sendMessage(new CustomMessage("common.TryLater"));
            return false;
        }

        if (activeChar.getTeleMode() != 0 || activeChar.getReflection() != ReflectionManager.DEFAULT) {
            activeChar.sendMessage(new CustomMessage("common.TryLater"));
            return false;
        }

        // "Нельзя вызывать персонажей в/из зоны свободного PvP"
        // "в зоны осад"
        // "на Олимпийский стадион"
        // "в зоны определенных рейд-боссов и эпик-боссов"
        // в режиме обсервера или к обсерверу и т.п.
        if (partner.isInZoneBattle() || partner.isInZone(ZoneType.SIEGE) || partner.isInZone(ZoneType.no_restart) || partner.isInOlympiadMode() || activeChar
                .isInZoneBattle() || activeChar.isInZone(ZoneType.SIEGE) || activeChar.isInZone(ZoneType.no_restart) || activeChar
                .isInOlympiadMode() || partner.getReflection() != ReflectionManager.DEFAULT || partner.isInZone(ZoneType.no_summon) || activeChar.isOutOfControl() || partner
                .isOutOfControl()) {
            activeChar.sendPacket(SystemMsg.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING);
            return false;
        }

        return true;
    }

    @Override
    public boolean useVoicedCommand(final String command, final Player activeChar, final String target) {
        if (!ServerConfig.ALLOW_WEDDING) {
            return false;
        }

        if (command.startsWith("engage")) {
            return engage(activeChar);
        } else if (command.startsWith("divorce")) {
            return divorce(activeChar);
        } else if (command.startsWith("gotolove")) {
            return goToLove(activeChar);
        }
        return false;
    }

    public boolean divorce(final Player activeChar) {
        if (activeChar.getPartnerId() == 0) {
            return false;
        }

        final int _partnerId = activeChar.getPartnerId();
        long AdenaAmount = 0;

        if (activeChar.isMaried()) {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.Divorced"));
            AdenaAmount = Math.abs(activeChar.getAdena() / 100 * ServerConfig.WEDDING_DIVORCE_COSTS - 10);
            activeChar.reduceAdena(AdenaAmount, true);
        } else {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.Disengaged"));
        }

        activeChar.setMaried(false);
        activeChar.setPartnerId(0);
        Couple couple = CoupleManager.getInstance().getCouple(activeChar.getCoupleId());
        if (couple != null)
            couple.divorce();

        final Player partner = GameObjectsStorage.getPlayer(_partnerId);

        if (partner != null) {
            partner.setPartnerId(0);
            if (partner.isMaried()) {
                partner.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PartnerDivorce"));
            } else {
                partner.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PartnerDisengage"));
            }
            partner.setMaried(false);

            // give adena
            if (AdenaAmount > 0) {
                partner.addAdena(AdenaAmount);
            }
        }
        return true;
    }

    public boolean engage(final Player activeChar) {
        // check target
        if (activeChar.getTarget() == null) {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.NoneTargeted"));
            return false;
        }
        // check if target is a L2Player
        if (!activeChar.getTarget().isPlayer()) {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.OnlyAnotherPlayer"));
            return false;
        }
        // check if player is already engaged
        if (activeChar.getPartnerId() != 0) {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.AlreadyEngaged"));
            if (ServerConfig.WEDDING_PUNISH_INFIDELITY) {
                activeChar.startAbnormalEffect(AbnormalEffect.BIG_HEAD);
                // Head
                // lets recycle the sevensigns debuffs
                final int skillId;

                int skillLevel = 1;

                if (activeChar.getLevel() > 40) {
                    skillLevel = 2;
                }

                if (activeChar.isMageClass()) {
                    skillId = 4361;
                } else {
                    skillId = 4362;
                }

                final SkillEntry skill = SkillTable.getInstance().getSkillEntry(skillId, skillLevel);

                if (activeChar.getEffectList().getEffectsBySkill(skill) == null) {
                    skill.getEffects(activeChar, activeChar, false, false);
                    activeChar.sendPacket(new SystemMessage(SystemMsg.S1S_EFFECT_CAN_BE_FELT).addSkillName(skillId, skillLevel));
                }
            }
            return false;
        }

        final Player ptarget = (Player) activeChar.getTarget();

        // check if player target himself
        if (ptarget.getObjectId() == activeChar.getObjectId()) {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.EngagingYourself"));
            return false;
        }

        if (ptarget.isMaried()) {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PlayerAlreadyMarried"));
            return false;
        }

        if (ptarget.getPartnerId() != 0) {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PlayerAlreadyEngaged"));
            return false;
        }

        final Pair<Integer, OnAnswerListener> entry = ptarget.getAskListener(false);
        if (entry != null && entry.getValue() instanceof CoupleAnswerListener) {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PlayerAlreadyAsked"));
            return false;
        }

        if (ptarget.getPartnerId() != 0) {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PlayerAlreadyEngaged"));
            return false;
        }

        if (ptarget.getSex() == activeChar.getSex() && !ServerConfig.WEDDING_SAMESEX) {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.SameSex"));
            return false;
        }

        if (!ptarget.getFriendComponent().getList().containsKey(activeChar.getObjectId())) {
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.NotInFriendlist"));
            return false;
        }

        final ConfirmDlg packet = new ConfirmDlg(SystemMsg.S1, 60000).addString(new CustomMessage("voicedcommandhandlers.Wedding.AskEngage").toString(ptarget));
        ptarget.ask(packet, new CoupleAnswerListener(activeChar, ptarget, 60000));
        return true;
    }

    public boolean goToLove(final Player activeChar) {
        final Player partner = GameObjectsStorage.getPlayer(activeChar.getPartnerId());

        if (!checkGotoLove(activeChar, partner))
            return false;

        if (!activeChar.reduceAdena(ServerConfig.WEDDING_TELEPORT_PRICE, true)) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return false;
        }


        final int teleportTimer = ServerConfig.WEDDING_TELEPORT_INTERVAL;

        activeChar.abortAttack(true, true);
        activeChar.abortCast(true, true);
        activeChar.sendActionFailed();
        activeChar.stopMove();
        activeChar.startParalyzed();

        activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.Teleport").addNumber(Math.max(teleportTimer / 60, 1)));
        activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);

        // SoE Animation section
        activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1050, 1, teleportTimer, 0));
        activeChar.sendPacket(new SetupGauge(activeChar, SetupGauge.BLUE, teleportTimer));
        // End SoE Animation section

        // continue execution later
        ThreadPoolManager.getInstance().schedule(new EscapeFinalizer(activeChar, partner.getLoc()), teleportTimer * 1000L);
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return _voicedCommands;
    }

    public void onLoad() {
        VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
    }

    public void onReload() {
    }

    public void onShutdown() {
    }

    private static class CoupleAnswerListener implements OnAnswerListener {
        private final HardReference<Player> _playerRef1;
        private final HardReference<Player> _playerRef2;
        private final long _expireTime;

        public CoupleAnswerListener(final Player player1, final Player player2, final long expireTime) {
            _playerRef1 = player1.getRef();
            _playerRef2 = player2.getRef();
            _expireTime = expireTime > 0L ? System.currentTimeMillis() + expireTime : 0L;
        }

        @Override
        public void sayYes() {
            final Player player1;
            final Player player2;
            if ((player1 = _playerRef1.get()) == null || (player2 = _playerRef2.get()) == null) {
                return;
            }

            CoupleManager.getInstance().createCouple(player1, player2);
            player1.sendMessage(new CustomMessage("org.mmocore.gameserver.model.L2Player.EngageAnswerYes"));
        }

        @Override
        public void sayNo() {
            if (_playerRef1.get() == null || _playerRef2.get() == null)
                return;

            _playerRef1.get().sendMessage(new CustomMessage("org.mmocore.gameserver.model.L2Player.EngageAnswerNo"));
        }

        @Override
        public long expireTime() {
            return _expireTime;
        }
    }

    static class EscapeFinalizer extends RunnableImpl {
        private final Player _activeChar;
        private final Location _loc;

        EscapeFinalizer(final Player activeChar, final Location loc) {
            _activeChar = activeChar;
            _loc = loc;
        }

        @Override
        public void runImpl() throws Exception {
            _activeChar.stopParalyzed();

            final Player partner = GameObjectsStorage.getPlayer(_activeChar.getPartnerId());

            if (_activeChar.isDead() || !checkGotoLove(_activeChar, partner))
                return;

            _activeChar.teleToLocation(_loc);
        }
    }
}
