package org.mmocore.gameserver.handler.admincommands.impl;


import org.mmocore.gameserver.data.xml.holder.SkillAcquireHolder;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.SkillLearn;
import org.mmocore.gameserver.model.base.AcquireType;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillCoolTime;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillList;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Calculator;
import org.mmocore.gameserver.stats.funcs.Func;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.world.World;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


public class AdminSkill implements IAdminCommandHandler {
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        final StringTokenizer st = new StringTokenizer(fullString);

        if (!activeChar.getPlayerAccess().CanEditChar) {
            return false;
        }

        switch (command) {
            case admin_show_skills:
                showSkillsPage(activeChar);
                break;
            case admin_show_effects:
                showEffects(activeChar);
                break;
            case admin_remove_skills:
                removeSkillsPage(activeChar);
                break;
            case admin_skill_list:
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/skills.htm"));
                break;
            case admin_skill_index:
                if (wordList.length > 1) {
                    activeChar.sendPacket(new HtmlMessage(5).setFile("admin/skills/" + wordList[1] + ".htm"));
                }
                break;
            case admin_add_skill:
                adminAddSkill(activeChar, wordList);
                break;
            case admin_remove_skill:
                adminRemoveSkill(activeChar, wordList);
                break;
            case admin_get_skills:
                adminGetSkills(activeChar);
                break;
            case admin_reset_skills:
                adminResetSkills(activeChar);
                break;
            case admin_give_all_skills:
                adminGiveAllSkills(activeChar);
                break;
            case admin_debug_stats:
                debug_stats(activeChar);
                break;
            case admin_remove_cooldown:
                int radius;
                try {
                    st.nextToken(); //cmd
                    radius = Integer.parseInt(st.nextToken());
                } catch (NumberFormatException e) {
                    radius = 0;
                } catch (NoSuchElementException e) {
                    radius = 0;
                }
                if (radius > 0) {
                    for (final Player player : World.getAroundPlayers(activeChar, radius, 600)) {
                        player.resetReuse();
                        player.sendPacket(new SkillCoolTime(player));
                        player.sendAdminMessage("Откат всех скилов обнулен.");
                    }
                } else {
                    final Player target = activeChar.getTarget().isPlayer() ? activeChar.getTarget().getPlayer() : activeChar;
                    target.resetReuse();
                    target.sendPacket(new SkillCoolTime(target));
                    target.sendAdminMessage("Откат всех скилов обнулен.");
                }
                break;
            case admin_buff:
                for (int i = 7041; i <= 7064; i++) {
                    activeChar.addSkill(SkillTable.getInstance().getSkillEntry(i, 1));
                }
                activeChar.sendPacket(new SkillList(activeChar));
                break;
            case admin_cancel_effect:
                if (activeChar.getTarget() == null || !activeChar.isPlayer()) {
                    activeChar.sendMessage("Incorrect target");
                    return false;
                }
                if (wordList.length < 1) {
                    activeChar.sendMessage("Internal error");
                    return false;
                }
                int id;
                try {
                    id = Integer.parseInt(wordList[1]);
                } catch (Exception e) {
                    activeChar.sendMessage("Internal error.");
                    return false;
                }
                activeChar.getTarget().getPlayer().getEffectList().getAllEffects()
                        .stream().filter(e -> e.getSkill().getId() == id).forEach(Effect::exit);
                showEffects(activeChar);
        }

        return true;
    }

    private void debug_stats(final Player activeChar) {
        final GameObject target_obj = activeChar.getTarget();
        if (target_obj == null || !target_obj.isCreature()) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        final Creature target = (Creature) target_obj;

        final Calculator[] calculators = target.getCalculators();

        String log_str = "--- Debug for " + target.getName() + " ---\r\n";

        for (final Calculator calculator : calculators) {
            if (calculator == null) {
                continue;
            }
            double initialValue = calculator.getBase();
            log_str += "Stat: " + calculator._stat.getValue() + ", prevValue: " + calculator.getLast() + "\r\n";
            final Func[] funcs = calculator.getFunctions();
            for (int i = 0; i < funcs.length; i++) {
                String order = Integer.toHexString(funcs[i].order).toUpperCase();
                if (order.length() == 1) {
                    order = '0' + order;
                }
                log_str += "\tFunc #" + i + "@ [0x" + order + ']' + funcs[i].getClass().getSimpleName() + '\t' + initialValue;
                if (funcs[i].getCondition() == null || funcs[i].getCondition().test(activeChar, target, null, initialValue)) {
                    initialValue = funcs[i].calc(activeChar, target, null, initialValue);
                }
                log_str += " -> " + initialValue + (funcs[i].owner != null ? "; owner: " + funcs[i].owner.toString() : "; no owner") + "\r\n";
            }
        }

        Log.add(log_str, "debug_stats");
    }

    /**
     * This function will give all the skills that the gm target can have at its
     * level to the traget
     *
     * @param activeChar: the gm char
     */
    private void adminGiveAllSkills(final Player activeChar) {
        final GameObject target = activeChar.getTarget();
        if (target == null || !target.isPlayer() || !activeChar.equals(target) && !activeChar.getPlayerAccess().CanEditCharAll) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }
        final Player player = target.getPlayer();
        int unLearnable = 0;
        int skillCounter = 0;
        Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.NORMAL);
        while (skills.size() > unLearnable) {
            unLearnable = 0;
            for (final SkillLearn s : skills) {
                final SkillEntry sk = SkillTable.getInstance().getSkillEntry(s.getId(), s.getLevel());
                if (sk == null || !sk.getTemplate().getCanLearn(player.getPlayerClassComponent().getClassId())) {
                    unLearnable++;
                    continue;
                }
                if (player.getSkillLevel(sk.getId()) == -1) {
                    skillCounter++;
                }
                player.addSkill(sk, true);
            }
            skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.NORMAL);
        }

        player.sendMessage("Admin gave you " + skillCounter + " skills.");
        player.sendPacket(new SkillList(player));
        activeChar.sendAdminMessage("You gave " + skillCounter + " skills to " + player.getName());
    }

    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void removeSkillsPage(final Player activeChar) {
        final GameObject target = activeChar.getTarget();
        final Player player;
        if (target.isPlayer() && (activeChar.equals(target) || activeChar.getPlayerAccess().CanEditCharAll)) {
            player = (Player) target;
        } else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        final Collection<SkillEntry> skills = player.getAllSkills();

        final HtmlMessage adminReply = new HtmlMessage(5);
        final StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=260><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_skills\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table>");
        replyMSG.append("<br><br>");
        replyMSG.append("<center>Editing character: ").append(player.getName()).append("</center>");
        replyMSG.append("<br><table width=270><tr><td>Lv: ").append(player.getLevel()).append(' ').append(player.getPlayerClassComponent().getClassId().name()).append("</td></tr></table>");
        replyMSG.append("<br><center>Click on the skill you wish to remove:</center>");
        replyMSG.append("<br><table width=270>");
        replyMSG.append("<tr><td width=80>Name:</td><td width=60>Level:</td><td width=40>Id:</td></tr>");
        for (final SkillEntry element : skills) {
            replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_remove_skill ").append(element.getId()).append("\">").append(element.getName()).append("</a></td><td width=60>").append(element.getLevel()).append("</td><td width=40>").append(element.getId()).append("</td></tr>");
        }
        replyMSG.append("</table>");
        replyMSG.append("<br><center><table>");
        replyMSG.append("Remove custom skill:");
        replyMSG.append("<tr><td>Id: </td>");
        replyMSG.append("<td><edit var=\"id_to_remove\" width=110></td></tr>");
        replyMSG.append("</table></center>");
        replyMSG.append("<center><button value=\"Remove skill\" action=\"bypass -h admin_remove_skill $id_to_remove\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>");
        replyMSG.append("<br><center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15></center>");
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    private void showSkillsPage(final Player activeChar) {
        final GameObject target = activeChar.getTarget();
        final Player player;
        if (target != null && target.isPlayer() && (activeChar.equals(target) || activeChar.getPlayerAccess().CanEditCharAll)) {
            player = (Player) target;
        } else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        final HtmlMessage adminReply = new HtmlMessage(5);

        final StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=260><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table>");
        replyMSG.append("<br><br>");
        replyMSG.append("<center>Editing character: ").append(player.getName()).append("</center>");
        replyMSG.append("<br><table width=270><tr><td>Lv: ").append(player.getLevel()).append(' ').append(player.getPlayerClassComponent().getClassId().name()).append("</td></tr></table>");
        replyMSG.append("<br><center><table>");
        replyMSG.append("<tr><td><button value=\"Add skills\" action=\"bypass -h admin_skill_list\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Get skills\" action=\"bypass -h admin_get_skills\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("<tr><td><button value=\"Delete skills\" action=\"bypass -h admin_remove_skills\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Reset skills\" action=\"bypass -h admin_reset_skills\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("<tr><td><button value=\"Give All Skills\" action=\"bypass -h admin_give_all_skills\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Refresh\" action=\"bypass -h admin_remove_cooldown\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("</table></center>");
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    private void showEffects(final Player activeChar) {
        final GameObject target = activeChar.getTarget();
        final Player player;
        if (target != null && target.isPlayer() && (activeChar.equals(target) || activeChar.getPlayerAccess().CanEditCharAll)) {
            player = (Player) target;
        } else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        final HtmlMessage adminReply = new HtmlMessage(5);

        final StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=260><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table>");
        replyMSG.append("<br><br>");
        replyMSG.append("<center>Editing character: ").append(player.getName()).append("</center>");

        replyMSG.append("<br><center><button value=\"");
        replyMSG.append(player.isLangRus() ? "Обновить" : "Refresh");
        replyMSG.append("\" action=\"bypass -h admin_show_effects\" width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" /></center>");
        replyMSG.append("<br>");

        final List<Effect> list = player.getEffectList().getAllEffects();
        if (list != null && !list.isEmpty()) {
            int i = 0;
            for (final Effect e : list) {
                SkillEntry sk = e.getSkill();
                replyMSG.append("<table><tr>")
                        .append("<td width=32>").append(setIcon(sk.getTemplate().getIcon())).append("</td>")
                        .append("<td>")
                        .append("<table width=216>")
                        .append("<tr><td><font color=\"LEVEL\">").append(sk.getName()).append("</font> - ").append(e.getTimeLeft()).append(" sec").append("</td></tr>")
                        .append("<tr><td>").append("Lvl: ").append(sk.getLevel()).append(" id: ").append(sk.getId()).append("</td></tr>")
                        .append("</table>")
                        .append("</td>")
                        .append("<td>").append(setButton("admin_cancel_effect " + sk.getId())).append("</td>")
                        .append("</tr></table>");
						/*
						.append(sk.getName()).append(' ').append(e.getSkill().getLevel()).append(" - ")
						.append(sk.getTemplate().isToggle() ? "Infinity" : (e.getTimeLeft() + " seconds")).append("<br1>");
						*/
                if (++i > 24)
                    break;
            }
        }
        replyMSG.append("<br></body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    private String setButton(String bypass) {
        return "<button width=" + 22 + " height=" + 20 + " back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h " + bypass + "\" value=\"" + "X" + "\">";
    }

    private String setIcon(String src) {
        return setIcon(src);
    }

    private String setIcon(String src) {
        return "<img src=\"" + src + "\" width=" + 32 + " height=" + 32 + ">";
    }


    private void adminGetSkills(final Player activeChar) {
        final GameObject target = activeChar.getTarget();
        final Player player;
        if (target.isPlayer() && (activeChar.equals(target) || activeChar.getPlayerAccess().CanEditCharAll)) {
            player = (Player) target;
        } else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        if (player.getName().equals(activeChar.getName())) {
            player.sendMessage("There is no point in doing it on your character.");
        } else {
            final Collection<SkillEntry> skills = player.getAllSkills();
            final SkillEntry[] adminSkills = activeChar.getAllSkillsArray();
            for (final SkillEntry element : adminSkills) {
                activeChar.removeSkill(element, true);
            }
            for (final SkillEntry element : skills) {
                activeChar.addSkill(element, true);
            }
            activeChar.sendAdminMessage("You now have all the skills of  " + player.getName() + '.');
        }

        showSkillsPage(activeChar);
    }

    private void adminResetSkills(final Player activeChar) {
        final GameObject target = activeChar.getTarget();
        if (!target.isPlayer() || !activeChar.equals(target) && !activeChar.getPlayerAccess().CanEditCharAll) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }
        final Player player = target.getPlayer();
        player.getSkills().stream()
                .filter(skillEntry -> !SkillAcquireHolder.getInstance().isSkillPossible(player, skillEntry)
                        && !skillEntry.getTemplate().isCommon())
                .forEach(skillEntry -> player.removeSkill(skillEntry, true));
        player.checkSkills();
        player.sendPacket(new SkillList(player));
        player.sendMessage("[GM]" + activeChar.getName() + " has updated your skills.");

        showSkillsPage(activeChar);
    }

    private void adminAddSkill(final Player activeChar, final String[] wordList) {
        final GameObject target = activeChar.getTarget();
        if (target == null || !target.isPlayer() || !activeChar.equals(target) && !activeChar.getPlayerAccess().CanEditCharAll) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }
        final Player player = target.getPlayer();
        if (wordList.length == 3) {
            final int id = Integer.parseInt(wordList[1]);
            final int level = Integer.parseInt(wordList[2]);
            final SkillEntry skill = SkillTable.getInstance().getSkillEntry(id, level);
            if (skill != null) {
                player.sendMessage("Admin gave you the skill " + skill.getName() + '.');
                player.addSkill(skill, true);
                player.sendPacket(new SkillList(player));
                activeChar.sendAdminMessage("You gave the skill " + skill.getName() + " to " + player.getName() + '.');
            } else {
                activeChar.sendAdminMessage("Error: there is no such skill.");
            }
        }

        showSkillsPage(activeChar);
    }

    private void adminRemoveSkill(final Player activeChar, final String[] wordList) {
        final GameObject target = activeChar.getTarget();
        if (!target.isPlayer() || !activeChar.equals(target) && !activeChar.getPlayerAccess().CanEditCharAll) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }
        final Player player = target.getPlayer();
        if (wordList.length == 2) {
            final int id = Integer.parseInt(wordList[1]);
            final int level = player.getSkillLevel(id);
            final SkillEntry skill = SkillTable.getInstance().getSkillEntry(id, level);
            if (skill != null) {
                player.sendMessage("Admin removed the skill " + skill.getName() + '.');
                player.removeSkill(skill, true);
                player.sendPacket(new SkillList(player));
                activeChar.sendAdminMessage("You removed the skill " + skill.getName() + " from " + player.getName() + '.');
            } else {
                activeChar.sendAdminMessage("Error: there is no such skill.");
            }
        }

        removeSkillsPage(activeChar);
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_show_skills,
        admin_remove_skills,
        admin_skill_list,
        admin_skill_index,
        admin_add_skill,
        admin_remove_skill,
        admin_get_skills,
        admin_reset_skills,
        admin_give_all_skills,
        admin_show_effects,
        admin_debug_stats,
        admin_remove_cooldown,
        admin_buff,
        admin_cancel_effect
    }
}
