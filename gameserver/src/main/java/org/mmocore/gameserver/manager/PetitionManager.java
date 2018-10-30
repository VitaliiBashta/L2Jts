package org.mmocore.gameserver.manager;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.handler.petition.IPetitionHandler;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.Say2;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.TimeUtils;
import org.mmocore.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Petition Manager
 *
 * @author n0nam3
 * @date 21/08/2010 0:11
 */
public final class PetitionManager implements IPetitionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetitionManager.class.getName());
    private final AtomicInteger _nextId = new AtomicInteger();
    private final Map<Integer, Petition> _pendingPetitions = new ConcurrentHashMap<>();
    private final Map<Integer, Petition> _completedPetitions = new ConcurrentHashMap<>();

    private PetitionManager() {
        LOGGER.info("Initializing PetitionManager");
    }

    public static PetitionManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public int getNextId() {
        return _nextId.incrementAndGet();
    }

    public void clearCompletedPetitions() {
        final int numPetitions = getPendingPetitionCount();

        getCompletedPetitions().clear();
        LOGGER.info("PetitionManager: Completed petition data cleared. " + numPetitions + " petition(s) removed.");
    }

    public void clearPendingPetitions() {
        final int numPetitions = getPendingPetitionCount();

        getPendingPetitions().clear();
        LOGGER.info("PetitionManager: Pending petition queue cleared. " + numPetitions + " petition(s) removed.");
    }

    public boolean acceptPetition(final Player respondingAdmin, final int petitionId) {
        if (!isValidPetition(petitionId)) {
            return false;
        }

        final Petition currPetition = getPendingPetitions().get(petitionId);

        if (currPetition.getResponder() != null) {
            return false;
        }

        currPetition.setResponder(respondingAdmin);
        currPetition.setState(PetitionState.In_Process);

        // Petition application accepted. (Send to Petitioner)
        currPetition.sendPetitionerPacket(new SystemMessage(SystemMsg.PETITION_APPLICATION_ACCEPTED));

        // Petition application accepted. Reciept No. is <ID>
        currPetition.sendResponderPacket(new SystemMessage(SystemMsg.PETITION_APPLICATION_ACCEPTED_RECEIPT_NO_IS_S1).addNumber(currPetition.getId()));

        // Petition consultation with <Player> underway.
        currPetition.sendResponderPacket(new SystemMessage(SystemMsg.PETITION_CONSULTATION_WITH_S1_UNDER_WAY).addString(currPetition.getPetitioner().getName()));
        return true;
    }

    public boolean cancelActivePetition(final Player player) {
        for (final Petition currPetition : getPendingPetitions().values()) {
            if (currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId()) {
                return currPetition.endPetitionConsultation(PetitionState.Petitioner_Cancel);
            }

            if (currPetition.getResponder() != null && currPetition.getResponder().getObjectId() == player.getObjectId()) {
                return currPetition.endPetitionConsultation(PetitionState.Responder_Cancel);
            }
        }

        return false;
    }

    public void checkPetitionMessages(final Player petitioner) {
        if (petitioner != null) {
            for (final Petition currPetition : getPendingPetitions().values()) {
                if (currPetition == null) {
                    continue;
                }

                if (currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == petitioner.getObjectId()) {
                    for (final Say2 logMessage : currPetition.getLogMessages()) {
                        petitioner.sendPacket(logMessage);
                    }

                    return;
                }
            }
        }
    }

    public boolean endActivePetition(final Player player) {
        if (!player.isGM()) {
            return false;
        }

        for (final Petition currPetition : getPendingPetitions().values()) {
            if (currPetition == null) {
                continue;
            }

            if (currPetition.getResponder() != null && currPetition.getResponder().getObjectId() == player.getObjectId()) {
                return currPetition.endPetitionConsultation(PetitionState.Completed);
            }
        }

        return false;
    }

    protected Map<Integer, Petition> getCompletedPetitions() {
        return _completedPetitions;
    }

    protected Map<Integer, Petition> getPendingPetitions() {
        return _pendingPetitions;
    }

    public int getPendingPetitionCount() {
        return getPendingPetitions().size();
    }

    public int getPlayerTotalPetitionCount(final Player player) {
        if (player == null) {
            return 0;
        }

        int petitionCount = 0;

        for (final Petition currPetition : getPendingPetitions().values()) {
            if (currPetition == null) {
                continue;
            }

            if (currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId()) {
                petitionCount++;
            }
        }

        for (final Petition currPetition : getCompletedPetitions().values()) {
            if (currPetition == null) {
                continue;
            }

            if (currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId()) {
                petitionCount++;
            }
        }

        return petitionCount;
    }

    public boolean isPetitionInProcess() {
        for (final Petition currPetition : getPendingPetitions().values()) {
            if (currPetition == null) {
                continue;
            }

            if (currPetition.getState() == PetitionState.In_Process) {
                return true;
            }
        }

        return false;
    }

    public boolean isPetitionInProcess(final int petitionId) {
        if (!isValidPetition(petitionId)) {
            return false;
        }

        final Petition currPetition = getPendingPetitions().get(petitionId);
        return currPetition.getState() == PetitionState.In_Process;
    }

    public boolean isPlayerInConsultation(final Player player) {
        if (player != null) {
            for (final Petition currPetition : getPendingPetitions().values()) {
                if (currPetition == null) {
                    continue;
                }

                if (currPetition.getState() != PetitionState.In_Process) {
                    continue;
                }

                if (currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId() || currPetition.getResponder() != null && currPetition.getResponder().getObjectId() == player.getObjectId()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isPetitioningAllowed() {
        return AllSettingsConfig.PETITIONING_ALLOWED;
    }

    public boolean isPlayerPetitionPending(final Player petitioner) {
        if (petitioner != null) {
            for (final Petition currPetition : getPendingPetitions().values()) {
                if (currPetition == null) {
                    continue;
                }

                if (currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == petitioner.getObjectId()) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isValidPetition(final int petitionId) {
        return getPendingPetitions().containsKey(petitionId);
    }

    public boolean rejectPetition(final Player respondingAdmin, final int petitionId) {
        if (!isValidPetition(petitionId)) {
            return false;
        }

        final Petition currPetition = getPendingPetitions().get(petitionId);

        if (currPetition.getResponder() != null) {
            return false;
        }

        currPetition.setResponder(respondingAdmin);
        return currPetition.endPetitionConsultation(PetitionState.Responder_Reject);
    }

    public boolean sendActivePetitionMessage(final Player player, final String messageText) {
        //if (!isPlayerInConsultation(player))
        //return false;

        final Say2 cs;

        for (final Petition currPetition : getPendingPetitions().values()) {
            if (currPetition == null) {
                continue;
            }

            if (currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId()) {
                cs = new Say2(player.getObjectId(), ChatType.PETITION_PLAYER, player.getName(), messageText, null);
                currPetition.addLogMessage(cs);

                currPetition.sendResponderPacket(cs);
                currPetition.sendPetitionerPacket(cs);
                return true;
            }

            if (currPetition.getResponder() != null && currPetition.getResponder().getObjectId() == player.getObjectId()) {
                cs = new Say2(player.getObjectId(), ChatType.PETITION_GM, player.getName(), messageText, null);
                currPetition.addLogMessage(cs);

                currPetition.sendResponderPacket(cs);
                currPetition.sendPetitionerPacket(cs);
                return true;
            }
        }

        return false;
    }

    public void sendPendingPetitionList(final Player activeChar) {
        final StringBuilder htmlContent = new StringBuilder(600 + getPendingPetitionCount() * 300);
        htmlContent.append("<html><body><center><table width=270><tr>" + "<td width=45><button value=\"Main\" action=\"bypass -h admin_admin\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" + "<td width=180><center>Petition Menu</center></td>" + "<td width=45><button value=\"Back\" action=\"bypass -h admin_admin\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table><br>" + "<table width=\"270\">" + "<tr><td><table width=\"270\"><tr><td><button value=\"Reset\" action=\"bypass -h admin_reset_petitions\" width=\"80\" height=\"21\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" + "<td align=right><button value=\"Refresh\" action=\"bypass -h admin_view_petitions\" width=\"80\" height=\"21\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table><br></td></tr>");

        if (getPendingPetitionCount() == 0) {
            htmlContent.append("<tr><td>There are no currently pending petitions.</td></tr>");
        } else {
            htmlContent.append("<tr><td><font color=\"LEVEL\">Current Petitions:</font><br></td></tr>");
        }

        boolean color = true;
        int petcount = 0;
        for (final Petition currPetition : getPendingPetitions().values()) {
            if (currPetition == null) {
                continue;
            }

            final Instant instant = Instant.ofEpochMilli(currPetition.getSubmitTime());

            htmlContent.append("<tr><td width=\"270\"><table width=\"270\" cellpadding=\"2\" bgcolor=").append(color ? "131210" : "444444")
                    .append("><tr><td width=\"130\">").append(TimeUtils.dateTimeFormat(instant));
            htmlContent.append("</td><td width=\"140\" align=right><font color=\"").append(currPetition.getPetitioner().isOnline() ? "00FF00" : "999999")
                    .append("\">").append(currPetition.getPetitioner().getName()).append("</font></td></tr>");
            htmlContent.append("<tr><td width=\"130\">");
            if (currPetition.getState() != PetitionState.In_Process) {
                htmlContent.append("<table width=\"130\" cellpadding=\"2\"><tr><td><button value=\"View\" action=\"bypass -h admin_view_petition ")
                        .append(currPetition.getId()).append("\" width=\"50\" height=\"21\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><button value=\"Reject\" action=\"bypass -h admin_reject_petition ").append(currPetition.getId()).append("\" width=\"50\" height=\"21\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table>");
            } else {
                htmlContent.append("<font color=\"").append(currPetition.getResponder().isOnline() ? "00FF00" : "999999").append("\">")
                        .append(currPetition.getResponder().getName()).append("</font>");
            }
            htmlContent.append("</td>").append(currPetition.getTypeAsString()).append("<td width=\"140\" align=right>")
                    .append(currPetition.getTypeAsString()).append("</td></tr></table></td></tr>");
            color = !color;
            petcount++;
            if (petcount > 10) {
                htmlContent.append("<tr><td><font color=\"LEVEL\">There is more pending petition...</font><br></td></tr>");
                break;
            }
        }

        htmlContent.append("</table></center></body></html>");

        final HtmlMessage htmlMsg = new HtmlMessage(0);
        htmlMsg.setHtml(htmlContent.toString());
        activeChar.sendPacket(htmlMsg);
    }

    public int submitPetition(final Player petitioner, final String petitionText, final int petitionType) {
        // Create a new petition instance and add it to the list of pending petitions.
        final Petition newPetition = new Petition(petitioner, petitionText, petitionType);
        final int newPetitionId = newPetition.getId();
        getPendingPetitions().put(newPetitionId, newPetition);

        // Notify all GMs that a new petition has been submitted.
        final String msgContent = petitioner.getName() + " has submitted a new petition."; //(ID: " + newPetitionId + ").";
        GmManager.broadcastToGMs(new Say2(petitioner.getObjectId(), ChatType.HERO_VOICE, "Petition System", msgContent, null));

        return newPetitionId;
    }

    public void viewPetition(final Player activeChar, final int petitionId) {
        if (!activeChar.isGM()) {
            return;
        }

        if (!isValidPetition(petitionId)) {
            return;
        }

        final Petition currPetition = getPendingPetitions().get(petitionId);

        final HtmlMessage html = new HtmlMessage(0);
        html.setFile("admin/petition.htm");
        html.replace("%petition%", String.valueOf(currPetition.getId()));
        final Instant instant = Instant.ofEpochMilli(currPetition.getSubmitTime());
        html.replace("%time%", TimeUtils.dateTimeFormat(instant));
        html.replace("%type%", currPetition.getTypeAsString());
        html.replace("%petitioner%", currPetition.getPetitioner().getName());
        html.replace("%online%", (currPetition.getPetitioner().isOnline() ? "00FF00" : "999999"));
        html.replace("%text%", currPetition.getContent());

        activeChar.sendPacket(html);
    }

    @Override
    public void handle(final Player player, final int id, final String txt) {
        if (GmManager.getAllVisibleGMs().isEmpty()) {
            player.sendPacket(new SystemMessage(SystemMsg.THERE_ARE_NO_GMS_CURRENTLY_VISIBLE_IN_THE_PUBLIC_LIST_AS_THEY_MAY_BE_PERFORMING_OTHER_FUNCTIONS_AT_THE_MOMENT));
            return;
        }

        if (!isPetitioningAllowed()) {
            player.sendPacket(new SystemMessage(SystemMsg.CANNOT_CONNECT_TO_PETITION_SERVER));
            return;
        }

        if (isPlayerPetitionPending(player)) {
            player.sendPacket(new SystemMessage(SystemMsg.YOU_MAY_ONLY_SUBMIT_ONE_PETITION_ACTIVE_AT_A_TIME));
            return;
        }

        if (getPendingPetitionCount() == AllSettingsConfig.MAX_PETITIONS_PENDING) {
            player.sendPacket(new SystemMessage(SystemMsg.THE_PETITION_SYSTEM_IS_CURRENTLY_UNAVAILABLE_PLEASE_TRY_AGAIN_LATER));
            return;
        }

        final int totalPetitions = getPlayerTotalPetitionCount(player) + 1;

        if (totalPetitions > AllSettingsConfig.MAX_PETITIONS_PER_PLAYER) {
            player.sendPacket(new SystemMessage(SystemMsg.WE_HAVE_RECEIVED_S1_PETITIONS_FROM_YOU_TODAY_AND_THAT_IS_THE_MAXIMUM_THAT_YOU_CAN_SUBMIT_IN_ONE_DAY));
            return;
        }

        if (txt.length() > 255) {
            player.sendPacket(new SystemMessage(SystemMsg.PETITIONS_CANNOT_EXCEED_255_CHARACTERS));
            return;
        }

        if (id >= PetitionType.values().length) {
            LOGGER.warn("PetitionManager: Invalid petition type : " + id);
            return;
        }

        final int petitionId = submitPetition(player, txt, id);

        player.sendPacket(new SystemMessage(SystemMsg.PETITION_APPLICATION_ACCEPTED_RECEIPT_NO_IS_S1).addNumber(petitionId));
        player.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUBMITTED_S1_PETITIONS_S2).addNumber(totalPetitions).addNumber(AllSettingsConfig.MAX_PETITIONS_PER_PLAYER - totalPetitions));
        player.sendPacket(new SystemMessage(SystemMsg.THERE_ARE_S1_PETITIONS_PENDING).addNumber(getPendingPetitionCount()));
    }

    public enum PetitionState {
        Pending,
        Responder_Cancel,
        Responder_Missing,
        Responder_Reject,
        Responder_Complete,
        Petitioner_Cancel,
        Petitioner_Missing,
        In_Process,
        Completed
    }

    public enum PetitionType {
        Immobility,
        Recovery_Related,
        Bug_Report,
        Quest_Related,
        Bad_User,
        Suggestions,
        Game_Tip,
        Operation_Related,
        Other
    }

    private static class LazyHolder {
        private static final PetitionManager INSTANCE = new PetitionManager();
    }

    private class Petition {
        private final long _submitTime = System.currentTimeMillis();
        private final int _id;
        private final PetitionType _type;
        private final String _content;
        private final List<Say2> _messageLog = new ArrayList<>();
        private final int _petitioner;
        private long _endTime = -1;
        private PetitionState _state = PetitionState.Pending;
        private int _responder;

        public Petition(final Player petitioner, final String petitionText, final int petitionType) {
            _id = getNextId();
            _type = PetitionType.values()[petitionType - 1];
            _content = petitionText;
            _petitioner = petitioner.getObjectId();
        }

        protected boolean addLogMessage(final Say2 cs) {
            return _messageLog.add(cs);
        }

        protected List<Say2> getLogMessages() {
            return _messageLog;
        }

        public boolean endPetitionConsultation(final PetitionState endState) {
            setState(endState);
            _endTime = System.currentTimeMillis();

            if (getResponder() != null && getResponder().isOnline()) {
                if (endState == PetitionState.Responder_Reject && (getPetitioner() != null && getPetitioner().isOnline())) {
                    getPetitioner().sendMessage("Your petition was rejected. Please try again later.");
                } else {
                    // Ending petition consultation with <Player>.
                    if (getPetitioner() != null && getPetitioner().isOnline()) {
                        getResponder().sendPacket(new SystemMessage(SystemMsg.ENDING_PETITION_CONSULTATION_WITH_S1).addString(getPetitioner().getName()));
                    }
                    if (endState == PetitionState.Petitioner_Cancel)
                    // Receipt No. <ID> petition cancelled.
                    {
                        getResponder().sendPacket(new SystemMessage(SystemMsg.RECEIPT_NO_S1_PETITION_CANCELLED).addNumber(getId()));
                    }
                }
            }

            // End petition consultation and inform them, if they are still online.
            if (getPetitioner() != null && getPetitioner().isOnline()) {
                getPetitioner().sendPacket(new SystemMessage(SystemMsg.ENDING_PETITION_CONSULTATION));
            }

            getCompletedPetitions().put(getId(), this);
            return getPendingPetitions().remove(getId()) != null;
        }

        public String getContent() {
            return _content;
        }

        public int getId() {
            return _id;
        }

        public Player getPetitioner() {
            return World.getPlayer(_petitioner);
        }

        public Player getResponder() {
            return World.getPlayer(_responder);
        }

        public void setResponder(final Player responder) {
            if (getResponder() != null) {
                return;
            }

            _responder = responder.getObjectId();
        }

        @SuppressWarnings("unused")
        public long getEndTime() {
            return _endTime;
        }

        public long getSubmitTime() {
            return _submitTime;
        }

        public PetitionState getState() {
            return _state;
        }

        public void setState(final PetitionState state) {
            _state = state;
        }

        public String getTypeAsString() {
            return _type.toString().replace("_", " ");
        }

        public void sendPetitionerPacket(final L2GameServerPacket responsePacket) {
            if (getPetitioner() == null || !getPetitioner().isOnline())
            //endPetitionConsultation(PetitionState.Petitioner_Missing);
            {
                return;
            }

            getPetitioner().sendPacket(responsePacket);
        }

        public void sendResponderPacket(final L2GameServerPacket responsePacket) {
            if (getResponder() == null || !getResponder().isOnline()) {
                endPetitionConsultation(PetitionState.Responder_Missing);
                return;
            }

            getResponder().sendPacket(responsePacket);
        }
    }
}
