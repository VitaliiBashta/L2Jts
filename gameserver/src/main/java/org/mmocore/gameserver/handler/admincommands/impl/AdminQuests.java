package org.mmocore.gameserver.handler.admincommands.impl;


import org.mmocore.commons.text.PrintfFormat;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;

import java.util.Map;


public class AdminQuests implements IAdminCommandHandler {
    private static final PrintfFormat fmtHEAD = new PrintfFormat("<center><font color=\"LEVEL\">%s [id=%d]</font><br><edit var=\"new_val\" width=100 height=12></center><br>");
    private static final PrintfFormat fmtRow = new PrintfFormat("<tr><td>%s</td><td>%s</td><td width=30>%s</td></tr>");
    private static final PrintfFormat fmtSetButton = new PrintfFormat("<button value=\"Set\" action=\"bypass -h admin_quest %d %s %s %s %s\" width=30 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\">");
    private static final PrintfFormat fmtFOOT = new PrintfFormat("<br><br><br><center><button value=\"Clear Quest\" action=\"bypass -h admin_quest %d CLEAR %s\" width=100 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"> <button value=\"Quests List\" action=\"bypass -h admin_quests %s\" width=100 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
    private static final PrintfFormat fmtListRow = new PrintfFormat("<tr><td><a action=\"bypass -h admin_quest %d %s\">%s</a></td><td>%s</td></tr>");
    private static final PrintfFormat fmtListNew = new PrintfFormat("<tr><td><edit var=\"new_quest\" width=100 height=12></td><td><button value=\"Add\" action=\"bypass -h admin_quest $new_quest STATE 2 %s\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td></tr>");

    private static boolean ShowQuestState(final QuestState qs, final Player activeChar) {
        final Map<String, String> vars = qs.getVars();
        final int id = qs.getQuest().getId();
        final String char_name = qs.getPlayer().getName();

        final HtmlMessage adminReply = new HtmlMessage(5);
        final StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append(fmtHEAD.sprintf(new Object[]{qs.getQuest().getClass().getSimpleName(), id}));
        replyMSG.append("<table width=260>");
        replyMSG.append(fmtRow.sprintf(new Object[]{"PLAYER: ", char_name, ""}));
        replyMSG.append(fmtRow.sprintf(new Object[]{
                "STATE: ",
                qs.getStateName(),
                fmtSetButton.sprintf(id, "STATE", "$new_val", char_name, "")
        }));
        for (final Map.Entry<String, String> entry : vars.entrySet()) {
            if (!"<state>".equalsIgnoreCase(entry.getKey())) {
                replyMSG.append(fmtRow.sprintf(new Object[]{
                        entry.getKey() + ": ",
                        entry.getValue(),
                        fmtSetButton.sprintf(id, "VAR", entry.getKey(), "$new_val", char_name)
                }));
            }
        }
        replyMSG.append(fmtRow.sprintf(new Object[]{
                "<edit var=\"new_name\" width=50 height=12>",
                "~new var~",
                fmtSetButton.sprintf(id, "VAR", "$new_name", "$new_val", char_name)
        }));
        replyMSG.append("</table>");
        replyMSG.append(fmtFOOT.sprintf(new Object[]{id, char_name, char_name}));
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
        //vars.clear();
        return true;
    }

    private static boolean ShowQuestList(final Player targetChar, final Player activeChar) {
        final HtmlMessage adminReply = new HtmlMessage(5);
        final StringBuilder replyMSG = new StringBuilder("<html><body><table width=260>");
        for (final QuestState qs : targetChar.getAllQuestsStates()) {
            if (qs != null && qs.getQuest().getId() != 255) {
                replyMSG.append(fmtListRow.sprintf(new Object[]{
                        qs.getQuest().getId(),
                        targetChar.getName(),
                        qs.getQuest().getName(),
                        qs.getStateName()
                }));
            }
        }
        replyMSG.append(fmtListNew.sprintf(new Object[]{targetChar.getName()}));
        replyMSG.append("</table></body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);

        return true;
    }

    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanEditCharAll) {
            return false;
        }

        switch (command) {
            case admin_quests:


                return ShowQuestList(getTargetChar(wordList, 1, activeChar), activeChar);

            case admin_quest:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //quest id|name [SHOW|STATE|VAR|CLEAR] ...");
                    return true;
                }
                final Quest _quest = QuestManager.getQuest2(wordList[1]);
                if (_quest == null) {
                    activeChar.sendAdminMessage("Quest " + wordList[1] + " undefined");
                    return true;
                }
                if (wordList.length < 3 || "SHOW".equalsIgnoreCase(wordList[2])) {
                    return cmd_Show(_quest, wordList, activeChar);
                }
                if ("STATE".equalsIgnoreCase(wordList[2])) {
                    return cmd_State(_quest, wordList, activeChar);
                }
                if ("VAR".equalsIgnoreCase(wordList[2])) {
                    return cmd_Var(_quest, wordList, activeChar);
                }
                if ("CLEAR".equalsIgnoreCase(wordList[2])) {
                    return cmd_Clear(_quest, wordList, activeChar);
                }
                return cmd_Show(_quest, wordList, activeChar);
        }
        return true;
    }

    private boolean cmd_Clear(final Quest _quest, final String[] wordList, final Player activeChar) {
        // quest id|name CLEAR [target]
        final Player targetChar = getTargetChar(wordList, 3, activeChar);
        final QuestState qs = targetChar.getQuestState(_quest);
        if (qs == null) {
            activeChar.sendAdminMessage("Player " + targetChar.getName() + " havn't Quest [" + _quest.getName() + ']');
            return false;
        }
        qs.exitQuest(true);
        return ShowQuestList(targetChar, activeChar);
    }

    private boolean cmd_Show(final Quest _quest, final String[] wordList, final Player activeChar) {
        // quest id|name SHOW [target]
        final Player targetChar = getTargetChar(wordList, 3, activeChar);
        final QuestState qs = targetChar.getQuestState(_quest);
        if (qs == null) {
            activeChar.sendAdminMessage("Player " + targetChar.getName() + " havn't Quest [" + _quest.getName() + ']');
            return false;
        }
        return ShowQuestState(qs, activeChar);
    }

    private boolean cmd_Var(final Quest _quest, final String[] wordList, final Player activeChar) {
        if (wordList.length < 5) {
            activeChar.sendAdminMessage("USAGE: //quest id|name VAR varname newvalue [target]");
            return false;
        }

        final Player targetChar = getTargetChar(wordList, 5, activeChar);
        final QuestState qs = targetChar.getQuestState(_quest);
        if (qs == null) {
            activeChar.sendAdminMessage("Player " + targetChar.getName() + " havn't Quest [" + _quest.getName() + "], init quest by command:");
            activeChar.sendAdminMessage("//quest id|name STATE 1|2|3 [target]");
            return false;
        }
        if ("~".equalsIgnoreCase(wordList[4]) || "#".equalsIgnoreCase(wordList[4])) {
            qs.removeMemo(wordList[3]);
        } else {
            qs.setMemoState(wordList[3], wordList[4]);
        }
        return ShowQuestState(qs, activeChar);
    }

    private boolean cmd_State(final Quest _quest, final String[] wordList, final Player activeChar) {
        if (wordList.length < 4) {
            activeChar.sendAdminMessage("USAGE: //quest id|name STATE 1|2|3 [target]");
            return false;
        }

        int state = 0;
        try {
            state = Integer.parseInt(wordList[3]);
        } catch (Exception e) {
            activeChar.sendAdminMessage("Wrong State ID: " + wordList[3]);
            return false;
        }

        final Player targetChar = getTargetChar(wordList, 4, activeChar);
        QuestState qs = targetChar.getQuestState(_quest);
        if (qs == null) {
            activeChar.sendAdminMessage("Init Quest [" + _quest.getName() + "] for " + targetChar.getName());
            qs = _quest.newQuestState(targetChar, state);
            qs.setMemoState("cond", "1");
        } else {
            qs.setState(state);
        }

        return ShowQuestState(qs, activeChar);
    }

    private Player getTargetChar(final String[] wordList, final int wordListIndex, final Player activeChar) {
        // цель задана аргументом
        if (wordListIndex >= 0 && wordList.length > wordListIndex) {
            final Player player = World.getPlayer(wordList[wordListIndex]);
            if (player == null) {
                activeChar.sendAdminMessage("Can't find player: " + wordList[wordListIndex]);
            }
            return player;
        }
        // цель задана текущим таргетом
        final GameObject my_target = activeChar.getTarget();
        if (my_target != null && my_target.isPlayer()) {
            return (Player) my_target;
        }
        // в качестве цели сам админ
        return activeChar;
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
        admin_quests,
        admin_quest
    }
}