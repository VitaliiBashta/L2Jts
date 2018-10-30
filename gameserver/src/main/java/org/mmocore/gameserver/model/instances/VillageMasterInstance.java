package org.mmocore.gameserver.model.instances;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.SubClass;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.base.ClassType;
import org.mmocore.gameserver.model.base.PlayerClass;
import org.mmocore.gameserver.model.entity.events.impl.CastleSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class VillageMasterInstance extends NpcInstance {
    public VillageMasterInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(final Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("create_clan")) {
            if (command.length() > 12) {
                final String val = command.substring(12);
                createClan(player, val);
            }
        } else if (command.startsWith("create_academy")) {
            if (command.length() > 15) {
                final String sub = command.substring(15, command.length());
                createSubPledge(player, sub, Clan.SUBUNIT_ACADEMY, 5, "");
            }
        } else if (command.startsWith("create_royal")) {
            if (command.length() > 15) {
                final String[] sub = command.substring(13, command.length()).split(" ", 2);
                if (sub.length == 2) {
                    createSubPledge(player, sub[1], Clan.SUBUNIT_ROYAL1, 6, sub[0]);
                }
            }
        } else if (command.startsWith("create_knight")) {
            if (command.length() > 16) {
                final String[] sub = command.substring(14, command.length()).split(" ", 2);
                if (sub.length == 2) {
                    createSubPledge(player, sub[1], Clan.SUBUNIT_KNIGHT1, 7, sub[0]);
                }
            }
        } else if (command.startsWith("assign_subpl_leader")) {
            if (command.length() > 22) {
                final String[] sub = command.substring(20, command.length()).split(" ", 2);
                if (sub.length == 2) {
                    assignSubPledgeLeader(player, sub[1], sub[0]);
                }
            }
        } else if (command.startsWith("assign_new_clan_leader")) {
            if (command.length() > 23) {
                final String val = command.substring(23);
                setLeader(player, val);
            }
        } else if (command.startsWith("assign_cancel_new_clan_leader")) {
            cancelLeader(player);
        } else if (command.startsWith("create_ally")) {
            if (command.length() > 12) {
                final String val = command.substring(12);
                createAlly(player, val);
            }
        } else if (command.startsWith("dissolve_ally")) {
            dissolveAlly(player);
        } else if (command.startsWith("dissolve_clan")) {
            dissolveClan(player);
        } else if (command.startsWith("increase_clan_level")) {
            levelUpClan(player);
        } else if (command.startsWith("learn_clan_skills")) {
            showClanSkillList(player);
        } else if (command.startsWith("ShowCouponExchange")) {
            if (ItemFunctions.getItemCount(player, 8869) > 0 || ItemFunctions.getItemCount(player, 8870) > 0) {
                command = "Multisell 800";
            } else {
                command = "Link villagemaster/reflect_weapon_master_noticket.htm";
            }
            super.onBypassFeedback(player, command);
        } else if ("CertificationList".equalsIgnoreCase(command)) {
            CertificationFunctions.showCertificationList(this, player);
        } else if ("GetCertification65".equalsIgnoreCase(command)) {
            CertificationFunctions.getCertification65(this, player);
        } else if ("GetCertification70".equalsIgnoreCase(command)) {
            CertificationFunctions.getCertification70(this, player);
        } else if ("GetCertification80".equalsIgnoreCase(command)) {
            CertificationFunctions.getCertification80(this, player);
        } else if ("GetCertification75List".equalsIgnoreCase(command)) {
            CertificationFunctions.getCertification75List(this, player);
        } else if ("GetCertification75C".equalsIgnoreCase(command)) {
            CertificationFunctions.getCertification75(this, player, true);
        } else if ("GetCertification75M".equalsIgnoreCase(command)) {
            CertificationFunctions.getCertification75(this, player, false);
        } else if (command.startsWith("Subclass")) {
            if (player.getServitor() != null && player.getServitor().isSummon()) {
                player.sendPacket(SystemMsg.A_SUBCLASS_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SERVITOR_OR_PET_IS_SUMMONED);
                return;
            }

            // Саб класс нельзя получить или поменять, пока используется скилл или персонаж находится в режиме трансформации
            if (player.isActionsDisabled() || player.isTransformed()) {
                player.sendPacket(SystemMsg.SUBCLASSES_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SKILL_IS_IN_USE);
                return;
            }

            if (player.getWeightPenalty() >= 3) {
                player.sendPacket(SystemMsg.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_WHILE_YOU_ARE_OVER_YOUR_WEIGHT_LIMIT);
                return;
            }

            if (player.getInventoryLimit() * 0.8 < player.getInventory().getSize()) {
                player.sendPacket(SystemMsg.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT);
                return;
            }

            final StringBuilder content = new StringBuilder("<html><body>");
            final HtmlMessage html = new HtmlMessage(this);

            final Map<Integer, SubClass> playerClassList = player.getPlayerClassComponent().getSubClasses();
            final Set<PlayerClass> subsAvailable;

            if (player.getLevel() < 40) {
                content.append("You must be level 40 or more to operate with your sub-classes.");
                content.append("</body></html>");
                html.setHtml(content.toString());
                player.sendPacket(html);
                return;
            }

            int classId = 0;
            int newClassId = 0;
            int intVal = 0;

            try {
                for (final String id : command.substring(9, command.length()).split(" ")) {
                    if (intVal == 0) {
                        intVal = Integer.parseInt(id);
                        continue;
                    }
                    if (classId > 0) {
                        newClassId = Integer.parseInt(id);
                        continue;
                    }
                    classId = Integer.parseInt(id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            switch (intVal) {
                case 1: // Возвращает список сабов, которые можно взять (см case 4)
                    subsAvailable = getAvailableSubClasses(player, true);

                    if (subsAvailable != null && !subsAvailable.isEmpty()) {
                        content.append("Add Subclass:<br>Which subclass do you wish to add?<br>");

                        for (final PlayerClass subClass : subsAvailable) {
                            content.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Subclass 4 ").append(subClass.ordinal()).append("\">").append(HtmlUtils.htmlClassName(subClass.ordinal())).append("</a><br>");
                        }
                    } else {
                        player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime"));
                        return;
                    }
                    break;
                case 2: // Установка уже взятого саба (см case 5)
                    content.append("Change Subclass:<br>");

                    final int baseClassId = player.getPlayerClassComponent().getBaseClassId();

                    if (playerClassList.size() < 2) {
                        content.append("You can't change subclasses when you don't have a subclass to begin with.<br><a action=\"bypass -h npc_").append(getObjectId()).append("_Subclass 1\">Add subclass.</a>");
                    } else {
                        content.append("Which class would you like to switch to?<br>");

                        if (baseClassId == player.getPlayerClassComponent().getActiveClassId()) {
                            content.append(HtmlUtils.htmlClassName(baseClassId)).append(" <font color=\"LEVEL\">(Base Class)</font><br><br>");
                        } else {
                            content.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Subclass 5 ").append(baseClassId).append("\">").append(HtmlUtils.htmlClassName(baseClassId)).append("</a> " + "<font color=\"LEVEL\">(Base Class)</font><br><br>");
                        }

                        for (final SubClass subClass : playerClassList.values()) {
                            if (subClass.isBase()) {
                                continue;
                            }
                            final int subClassId = subClass.getClassId();

                            if (subClassId == player.getPlayerClassComponent().getActiveClassId()) {
                                content.append(HtmlUtils.htmlClassName(subClassId)).append("<br>");
                            } else {
                                content.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Subclass 5 ").append(subClassId).append("\">").append(HtmlUtils.htmlClassName(subClassId)).append("</a><br>");
                            }
                        }
                    }
                    break;
                case 3: // Отмена сабкласса - список имеющихся (см case 6)
                    content.append("Change Subclass:<br>Which of the following sub-classes would you like to change?<br>");

                    for (final SubClass sub : playerClassList.values()) {
                        content.append("<br>");
                        if (!sub.isBase()) {
                            content.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Subclass 6 ").append(sub.getClassId()).append("\">").append(HtmlUtils.htmlClassName(sub.getClassId())).append("</a><br>");
                        }
                    }

                    content.append("<br>If you change a sub-class, you'll start at level 40 after the 2nd class transfer.");
                    break;
                case 4: // Добавление сабкласса - обработка выбора из case 1
                    boolean allowAddition = false;

                    subsAvailable = getAvailableSubClasses(player, true);
                    if (subsAvailable != null && !subsAvailable.isEmpty()) {
                        for (final PlayerClass subClass : subsAvailable) {
                            if (subClass.ordinal() == classId) {
                                allowAddition = true;
                                break;
                            }
                        }
                    }

                    // Проверка хватает ли уровня
                    if (player.getLevel() < AllSettingsConfig.ALT_GAME_LEVEL_TO_GET_SUBCLASS) {
                        player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.NoSubBeforeLevel").addNumber(AllSettingsConfig.ALT_GAME_LEVEL_TO_GET_SUBCLASS));
                        allowAddition = false;
                    }

                    if (!playerClassList.isEmpty()) {
                        for (final SubClass subClass : playerClassList.values()) {
                            if (subClass.getLevel() < AllSettingsConfig.ALT_GAME_LEVEL_TO_GET_SUBCLASS) {
                                player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.NoSubBeforeLevel").addNumber(AllSettingsConfig.ALT_GAME_LEVEL_TO_GET_SUBCLASS));
                                allowAddition = false;
                                break;
                            }
                        }
                    }

                    if (OlympiadConfig.ENABLE_OLYMPIAD && Olympiad.isRegisteredInComp(player)) {
                        player.sendPacket(new SystemMessage(SystemMsg.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD_BECAUSE_YOU_HAVE_CHANGE_TO_YOUR_SUB_CLASS).addName(player));
                        return;
                    }
                    if (player.isInDuel()) {
                        return;
                    }
                    if (!player.isInPeaceZone()) {
                        return;
                    }
                    /*
                     * Если требуется квест - проверка прохождения Mimir's Elixir (Path to Subclass)
                     * Для камаэлей квест 236_SeedsOfChaos
                     * Если саб первый, то проверить начилие предмета, если не первый, то даём сабкласс.
                     * Если сабов нету, то проверяем наличие предмета.
                     */
                    if (!AllSettingsConfig.ALT_GAME_SUBCLASS_WITHOUT_QUESTS && !playerClassList.isEmpty() && playerClassList.size() < 2 + AllSettingsConfig.ALT_GAME_SUB_ADD) {
                        if (player.isQuestCompleted(234)) {
                            if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael) {
                                allowAddition = player.isQuestCompleted(236);
                                if (!allowAddition) {
                                    player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.QuestSeedsOfChaos"));
                                }
                            } else {
                                allowAddition = player.isQuestCompleted(235);
                                if (!allowAddition) {
                                    player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.QuestMimirsElixir"));
                                }
                            }
                        } else {
                            player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.QuestFatesWhisper"));
                            allowAddition = false;
                        }
                    }

                    if (allowAddition) {
                        if (!player.getPlayerClassComponent().addSubClass(classId, true, 0)) {
                            player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded"));
                            return;
                        }

                        content.append("Add Subclass:<br>The subclass of <font color=\"LEVEL\">").append(HtmlUtils.htmlClassName(classId)).append("</font> has been added.");
                        player.sendPacket(SystemMsg.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
                    } else {
                        html.setFile("villagemaster/SubClass_Fail.htm");
                    }
                    break;
                case 5: // Смена саба на другой из уже взятых - обработка выбора из case 2
                    /*
                     * If the character is less than level 75 on any of their
                     * previously chosen classes then disallow them to change to
                     * their most recently added sub-class choice.
                     */
					/*for(L2SubClass<?> sub : playerClassList.values())
						if(sub.isBase() && sub.getLevel() < Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS)
						{
							player.sendMessage("You may not change to your subclass before you are level " + Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS, "Вы не можете добавить еще сабкласс пока у вас уровень " + Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS + " на Вашем предыдущем сабклассе.");
							return;
						}*/

                    if (OlympiadConfig.ENABLE_OLYMPIAD && Olympiad.isRegisteredInComp(player)) {
                        player.sendPacket(new SystemMessage(SystemMsg.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD_BECAUSE_YOU_HAVE_CHANGE_TO_YOUR_SUB_CLASS).addName(player));
                        return;
                    }
                    if (player.isInDuel()) {
                        return;
                    }
                    if (!player.isInPeaceZone()) {
                        return;
                    }

                    player.getPlayerClassComponent().setActiveSubClass(classId, true);

                    content.append("Change Subclass:<br>Your active subclass is now a <font color=\"LEVEL\">").append(HtmlUtils.htmlClassName(player.getPlayerClassComponent().getActiveClassId())).append("</font>.");

                    player.sendPacket(SystemMsg.YOU_HAVE_SUCCESSFULLY_SWITCHED_TO_YOUR_SUBCLASS);
                    // completed.
                    break;
                case 6: // Отмена сабкласса - обработка выбора из case 3
                    content.append("Please choose a subclass to change to. If the one you are looking for is not here, " + //
                            "please seek out the appropriate master for that class.<br>" + //
                            "<font color=\"LEVEL\">Warning!</font> All classes and skills for this class will be removed.<br><br>");

                    subsAvailable = getAvailableSubClasses(player, false);

                    if (!subsAvailable.isEmpty()) {
                        for (final PlayerClass subClass : subsAvailable) {
                            content.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_Subclass 7 ").append(classId).append(' ').append(subClass.ordinal()).append("\">").append(HtmlUtils.htmlClassName(subClass.ordinal())).append("</a><br>");
                        }
                    } else {
                        player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime"));
                        return;
                    }
                    break;
                case 7: // Отмена сабкласса - обработка выбора из case 6
                    // player.sendPacket(Msg.YOUR_PREVIOUS_SUB_CLASS_WILL_BE_DELETED_AND_YOUR_NEW_SUB_CLASS_WILL_START_AT_LEVEL_40__DO_YOU_WISH_TO_PROCEED); // Change confirmation.

                    if (OlympiadConfig.ENABLE_OLYMPIAD && Olympiad.isRegisteredInComp(player)) {
                        player.sendPacket(new SystemMessage(SystemMsg.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD_BECAUSE_YOU_HAVE_CHANGE_TO_YOUR_SUB_CLASS).addName(player));
                        return;
                    }
                    if (player.isInDuel()) {
                        return;
                    }
                    if (!player.isInPeaceZone()) {
                        return;
                    }
                    if (player.getPlayerClassComponent().modifySubClass(classId, newClassId)) {
                        content.append("Change Subclass:<br>Your subclass has been changed to <font color=\"LEVEL\">").append(HtmlUtils.htmlClassName(newClassId)).append("</font>.");
                        player.sendPacket(SystemMsg.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
                    } else {
                        player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded"));
                        return;
                    }
                    break;
            }
            content.append("</body></html>");

            // If the content is greater than for a basic blank page,
            // then assume no external HTML file was assigned.
            if (content.length() > 26) {
                html.setHtml(content.toString());
            }

            player.sendPacket(html);
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public String getHtmlPath(final int npcId, final int val, final Player player) {
        final String pom;
        if (val == 0) {
            pom = String.valueOf(npcId);
        } else {
            pom = npcId + "-" + val;
        }

        return "villagemaster/" + pom + ".htm";
    }

    // Private stuff
    public void createClan(final Player player, final String clanName) {
        if (player.getLevel() < 10) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_MEET_THE_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN);
            return;
        }

        if (player.getClanId() != 0) {
            player.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_CREATE_A_CLAN);
            return;
        }

        if (!player.canCreateClan()) {
            // you can't create a new clan within 10 days
            player.sendPacket(SystemMsg.YOU_MUST_WAIT_10_DAYS_BEFORE_CREATING_A_NEW_CLAN);
            return;
        }
        if (clanName.length() > 16) {
            player.sendPacket(SystemMsg.CLAN_NAMES_LENGTH_IS_INCORRECT);
            return;
        }
        if (!Util.isMatchingRegexp(clanName, ServerConfig.CLAN_NAME_TEMPLATE)) {
            // clan name is not matching template
            player.sendPacket(SystemMsg.CLAN_NAME_IS_INVALID);
            return;
        }

        final Clan clan = ClanTable.getInstance().createClan(player, clanName);
        if (clan == null) {
            // clan name is already taken
            player.sendPacket(SystemMsg.THIS_NAME_ALREADY_EXISTS);
            return;
        }

        // should be update packet only
        player.sendPacket(clan.listAll());
        player.sendPacket(new PledgeShowInfoUpdate(clan), SystemMsg.YOUR_CLAN_HAS_BEEN_CREATED);
        player.updatePledgeClass();
        player.broadcastCharInfo();
    }

    public void setLeader(final Player leader, final String newLeader) {
        if (!leader.isClanLeader()) {
            leader.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return;
        }

        if (leader.getEvent(SiegeEvent.class) != null) {
            leader.sendMessage(new CustomMessage("scripts.services.Rename.SiegeNow"));
            return;
        }

        final Clan clan = leader.getClan();
        final SubUnit mainUnit = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
        final UnitMember member = mainUnit.getUnitMember(newLeader);

        if (mainUnit.getNewLeaderObjectId() != 0) {
            showChatWindow(leader, "villagemaster/clan-in-progress.htm");
            return;
        }

        if (member == null) {
            showChatWindow(leader, "villagemaster/clan-20.htm");
            return;
        }

        if (member.getLeaderOf() != Clan.SUBUNIT_NONE) {
            leader.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.CannotAssignUnitLeader"));
            return;
        }

        showChatWindow(leader, "villagemaster/clan-success.htm");
        mainUnit.setNewLeaderObjectId(member.getObjectId());
        mainUnit.changeLeader();
        clan.broadcastClanStatus(true, true, false);
    }

    public void cancelLeader(final Player leader) {
        if (!leader.isClanLeader()) {
            leader.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return;
        }

        if (leader.getEvent(SiegeEvent.class) != null) {
            leader.sendMessage(new CustomMessage("scripts.services.Rename.SiegeNow"));
            return;
        }

        final Clan clan = leader.getClan();
        final SubUnit mainUnit = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);

        if (mainUnit.getNewLeaderObjectId() == 0) {
            showChatWindow(leader, "villagemaster/clan-cancel-no.htm");
            return;
        }

        final UnitMember member = mainUnit.getUnitMember(leader.getObjectId());
        if (member == null) {
            return;
        }
        showChatWindow(leader, "villagemaster/clan-canceled.htm");
        mainUnit.setNewLeaderObjectId(0);
        mainUnit.setLeader(member, false);
        mainUnit.cancelChangeLeader();
        clan.broadcastClanStatus(true, true, false);
    }

    public void createSubPledge(final Player player, final String clanName, int pledgeType, final int minClanLvl, final String leaderName) {
        UnitMember subLeader = null;

        final Clan clan = player.getClan();

        if (clan == null || !player.isClanLeader()) {
            player.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_CREATE_A_CLAN);
            return;
        }

        if (!Util.isMatchingRegexp(clanName, ServerConfig.CLAN_NAME_TEMPLATE)) {
            player.sendPacket(SystemMsg.CLAN_NAME_IS_INVALID);
            return;
        }

        final Collection<SubUnit> subPledge = clan.getAllSubUnits();
        for (final SubUnit element : subPledge) {
            if (element.getName().equals(clanName)) {
                player.sendPacket(SystemMsg.ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME_PLEASE_ENTER_A_DIFFERENT_NAME);
                return;
            }
        }

        if (ClanTable.getInstance().getClanByName(clanName) != null) {
            player.sendPacket(SystemMsg.ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME_PLEASE_ENTER_A_DIFFERENT_NAME);
            return;
        }

        if (clan.getLevel() < minClanLvl) {
            player.sendPacket(SystemMsg.THE_CONDITIONS_NECESSARY_TO_CREATE_A_MILITARY_UNIT_HAVE_NOT_BEEN_MET);
            return;
        }

        final SubUnit unit = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);

        if (pledgeType != Clan.SUBUNIT_ACADEMY) {
            subLeader = unit.getUnitMember(leaderName);
            if (subLeader == null) {
                player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.PlayerCantBeAssignedAsSubUnitLeader"));
                return;
            } else if (subLeader.getLeaderOf() != Clan.SUBUNIT_NONE) {
                player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.ItCantBeSubUnitLeader"));
                return;
            }
        }

        pledgeType = clan.createSubPledge(player, pledgeType, subLeader, clanName);
        if (pledgeType == Clan.SUBUNIT_NONE) {
            return;
        }

        clan.broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(clan.getSubUnit(pledgeType)));

        if (pledgeType == Clan.SUBUNIT_ACADEMY) {
            player.sendPacket(new SystemMessage(SystemMsg.CONGRATULATIONS_THE_S1S_CLAN_ACADEMY_HAS_BEEN_CREATED).addString(player.getClan().getName()));
        } else if (pledgeType >= Clan.SUBUNIT_KNIGHT1) {
            player.sendPacket(new SystemMessage(SystemMsg.THE_KNIGHTS_OF_S1_HAVE_BEEN_CREATED).addString(player.getClan().getName()));
        } else if (pledgeType >= Clan.SUBUNIT_ROYAL1) {
            player.sendPacket(new SystemMessage(SystemMsg.THE_ROYAL_GUARD_OF_S1_HAVE_BEEN_CREATED).addString(player.getClan().getName()));
        } else {
            player.sendPacket(SystemMsg.YOUR_CLAN_HAS_BEEN_CREATED);
        }

        if (subLeader != null) {
            clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(subLeader));
            if (subLeader.isOnline()) {
                subLeader.getPlayer().updatePledgeClass();
                subLeader.getPlayer().broadcastCharInfo();
            }
        }
    }

    public void assignSubPledgeLeader(final Player player, final String clanName, final String leaderName) {
        final Clan clan = player.getClan();

        if (clan == null) {
            player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.ClanDoesntExist"));
            return;
        }

        if (!player.isClanLeader()) {
            player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return;
        }

        SubUnit targetUnit = null;
        for (final SubUnit unit : clan.getAllSubUnits()) {
            if (unit.getType() == Clan.SUBUNIT_MAIN_CLAN || unit.getType() == Clan.SUBUNIT_ACADEMY) {
                continue;
            }
            if (unit.getName().equalsIgnoreCase(clanName)) {
                targetUnit = unit;
            }

        }
        if (targetUnit == null) {
            player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.SubUnitNotFound"));
            return;
        }
        final SubUnit mainUnit = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
        final UnitMember subLeader = mainUnit.getUnitMember(leaderName);
        if (subLeader == null) {
            player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.PlayerCantBeAssignedAsSubUnitLeader"));
            return;
        }

        if (subLeader.getLeaderOf() != Clan.SUBUNIT_NONE) {
            player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.ItCantBeSubUnitLeader"));
            return;
        }

        targetUnit.setLeader(subLeader, true);
        clan.broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(targetUnit));

        clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(subLeader));
        if (subLeader.isOnline()) {
            subLeader.getPlayer().updatePledgeClass();
            subLeader.getPlayer().broadcastCharInfo();
        }

        player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.NewSubUnitLeaderHasBeenAssigned"));
    }

    private void dissolveClan(final Player player) {
        if (player == null || player.getClan() == null) {
            return;
        }
        final Clan clan = player.getClan();

        if (!player.isClanLeader()) {
            player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return;
        }
        if (clan.getAllyId() != 0) {
            player.sendPacket(SystemMsg.YOU_CANNOT_DISPERSE_THE_CLANS_IN_YOUR_ALLIANCE);
            return;
        }
        if (clan.isAtWar() > 0) {
            player.sendPacket(SystemMsg.YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_ENGAGED_IN_A_WAR);
            return;
        }
        if (clan.getCastle() != 0 || clan.getHasHideout() != 0 || clan.getHasFortress() != 0) {
            player.sendPacket(SystemMsg.UNABLE_TO_DISPERSE_YOUR_CLAN_OWNS_ONE_OR_MORE_CASTLES_OR_HIDEOUTS);
            return;
        }

        for (final Residence r : ResidenceHolder.getInstance().getResidences()) {
            if (r.getSiegeEvent().getSiegeClan(SiegeEvent.ATTACKERS, clan) != null || r.getSiegeEvent().getSiegeClan(SiegeEvent.DEFENDERS, clan) != null || r.getSiegeEvent().getSiegeClan(CastleSiegeEvent.DEFENDERS_WAITING, clan) != null) {
                player.sendPacket(SystemMsg.UNABLE_TO_DISSOLVE_YOUR_CLAN_HAS_REQUESTED_TO_PARTICIPATE_IN_A_CASTLE_SIEGE);
                return;
            }
        }

        ClanTable.getInstance().dissolveClan(player);
    }

    public void levelUpClan(final Player player) {
        final Clan clan = player.getClan();
        if (clan == null) {
            return;
        }
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return;
        }

        boolean increaseClanLevel = false;

        switch (clan.getLevel()) {
            case 0:
                // Upgrade to 1
                if (player.getSp() >= 20000 && player.getAdena() >= 650000) {
                    player.setSp(player.getSp() - 20000);
                    player.reduceAdena(650000, true);
                    increaseClanLevel = true;
                }
                break;
            case 1:
                // Upgrade to 2
                if (player.getSp() >= 100000 && player.getAdena() >= 2500000) {
                    player.setSp(player.getSp() - 100000);
                    player.reduceAdena(2500000, true);
                    increaseClanLevel = true;
                }
                break;
            case 2:
                // Upgrade to 3
                // itemid 1419 == Blood Mark
                if (player.getSp() >= 350000 && player.getInventory().destroyItemByItemId(1419, 1)) {
                    player.setSp(player.getSp() - 350000);
                    increaseClanLevel = true;
                }
                break;
            case 3:
                // Upgrade to 4
                // itemid 3874 == Alliance Manifesto
                if (player.getSp() >= 1000000 && player.getInventory().destroyItemByItemId(3874, 1)) {
                    player.setSp(player.getSp() - 1000000);
                    increaseClanLevel = true;
                }
                break;
            case 4:
                // Upgrade to 5
                // itemid 3870 == Seal of Aspiration
                if (player.getSp() >= 2500000 && player.getInventory().destroyItemByItemId(3870, 1)) {
                    player.setSp(player.getSp() - 2500000);
                    increaseClanLevel = true;
                }
                break;
            case 5:
                // Upgrade to 6
                if (clan.getReputationScore() >= AllSettingsConfig.clanLvlUpRepTo6 && clan.getAllSize() >= AllSettingsConfig.clanLvlUpSizeTo6) {
                    clan.incReputation(-AllSettingsConfig.clanLvlUpRepTo6, false, "LvlUpClan");
                    increaseClanLevel = true;
                }
                break;
            case 6:
                // Upgrade to 7
                if (clan.getReputationScore() >= AllSettingsConfig.clanLvlUpRepTo7 && clan.getAllSize() >= AllSettingsConfig.clanLvlUpSizeTo7) {
                    clan.incReputation(-AllSettingsConfig.clanLvlUpRepTo7, false, "LvlUpClan");
                    increaseClanLevel = true;
                }
                break;
            case 7:
                // Upgrade to 8
                if (clan.getReputationScore() >= AllSettingsConfig.clanLvlUpRepTo8 && clan.getAllSize() >= AllSettingsConfig.clanLvlUpSizeTo8) {
                    clan.incReputation(-AllSettingsConfig.clanLvlUpRepTo8, false, "LvlUpClan");
                    increaseClanLevel = true;
                }
                break;
            case 8:
                // Upgrade to 9
                // itemId 9910 == Blood Oath
                if (clan.getReputationScore() >= AllSettingsConfig.clanLvlUpRepTo9 && clan.getAllSize() >= AllSettingsConfig.clanLvlUpSizeTo9) {
                    if (player.getInventory().destroyItemByItemId(9910, 150L)) {
                        clan.incReputation(-AllSettingsConfig.clanLvlUpRepTo9, false, "LvlUpClan");
                        increaseClanLevel = true;
                    }
                }
                break;
            case 9:
                // Upgrade to 10
                // itemId 9911 == Blood Alliance
                if (clan.getReputationScore() >= AllSettingsConfig.clanLvlUpRepTo10 && clan.getAllSize() >= AllSettingsConfig.clanLvlUpSizeTo10) {
                    if (player.getInventory().destroyItemByItemId(9911, 5)) {
                        clan.incReputation(-AllSettingsConfig.clanLvlUpRepTo10, false, "LvlUpClan");
                        increaseClanLevel = true;
                    }
                }
                break;
            case 10:
                // Upgrade to 11
                if (clan.getReputationScore() >= AllSettingsConfig.clanLvlUpRepTo11 && clan.getAllSize() >= AllSettingsConfig.clanLvlUpSizeTo11 && clan.getCastle() > 0) {
                    final Castle castle = ResidenceHolder.getInstance().getResidence(clan.getCastle());
                    final Dominion dominion = castle.getDominion();
                    if (dominion.getLordObjectId() == player.getObjectId()) {
                        clan.incReputation(-AllSettingsConfig.clanLvlUpRepTo11, false, "LvlUpClan");
                        increaseClanLevel = true;
                    }
                }
                break;
        }

        if (increaseClanLevel) {
            clan.setLevel(clan.getLevel() + 1);
            clan.updateClanInDB();

            player.broadcastCharInfo();

            doCast(SkillTable.getInstance().getSkillEntry(5103, 1), player, true);

            if (clan.getLevel() >= 4) {
                SiegeUtils.addSiegeSkills(player);
            }

            if (clan.getLevel() == 5) {
                player.sendPacket(SystemMsg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);
            }

            // notify all the members about it
            final PledgeShowInfoUpdate pu = new PledgeShowInfoUpdate(clan);
            final PledgeStatusChanged ps = new PledgeStatusChanged(clan);
            for (final UnitMember mbr : clan) {
                if (mbr.isOnline()) {
                    mbr.getPlayer().updatePledgeClass();
                    mbr.getPlayer().sendPacket(SystemMsg.YOUR_CLANS_LEVEL_HAS_INCREASED, pu, ps);
                    mbr.getPlayer().broadcastCharInfo();
                }
            }
        } else {
            player.sendPacket(SystemMsg.CLAN_HAS_FAILED_TO_INCREASE_SKILL_LEVEL);
        }
    }

    public void createAlly(final Player player, final String allyName) {
        // D5 You may not ally with clan you are battle with.
        // D6 Only the clan leader may apply for withdraw from alliance.
        // DD No response. Invitation to join an
        // D7 Alliance leaders cannot withdraw.
        // D9 Different Alliance
        // EB alliance information
        // Ec alliance name $s1
        // ee alliance leader: $s2 of $s1
        // ef affilated clans: total $s1 clan(s)
        // f6 you have already joined an alliance
        // f9 you cannot new alliance 10 days
        // fd cannot accept. clan ally is register as enemy during siege battle.
        // fe you have invited someone to your alliance.
        // 100 do you wish to withdraw from the alliance
        // 102 enter the name of the clan you wish to expel.
        // 202 do you realy wish to dissolve the alliance
        // 502 you have accepted alliance
        // 602 you have failed to invite a clan into the alliance
        // 702 you have withdraw

        if (!player.isClanLeader()) {
            player.sendPacket(SystemMsg.ONLY_CLAN_LEADERS_MAY_CREATE_ALLIANCES);
            return;
        }
        if (player.getClan().getAllyId() != 0) {
            player.sendPacket(SystemMsg.YOU_ALREADY_BELONG_TO_ANOTHER_ALLIANCE);
            return;
        }
        if (allyName.length() > 16) {
            player.sendPacket(SystemMsg.INCORRECT_LENGTH_FOR_AN_ALLIANCE_NAME);
            return;
        }
        if (!Util.isMatchingRegexp(allyName, ServerConfig.ALLY_NAME_TEMPLATE)) {
            player.sendPacket(SystemMsg.INCORRECT_ALLIANCE_NAME);
            return;
        }
        if (player.getClan().getLevel() < 5) {
            player.sendPacket(SystemMsg.TO_CREATE_AN_ALLIANCE_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER);
            return;
        }
        if (ClanTable.getInstance().getAllyByName(allyName) != null) {
            player.sendPacket(SystemMsg.THIS_ALLIANCE_NAME_ALREADY_EXISTS);
            return;
        }
        if (!player.getClan().canCreateAlly()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_CREATE_A_NEW_ALLIANCE_WITHIN_1_DAY_AFTER_DISSOLUTION);
            return;
        }

        final Alliance alliance = ClanTable.getInstance().createAlliance(player, allyName);
        if (alliance == null) {
            return;
        }

        player.broadcastCharInfo();
        player.sendMessage("Alliance " + allyName + " has been created.");
    }

    private void dissolveAlly(final Player player) {
        if (player == null || player.getAlliance() == null) {
            return;
        }

        if (!player.isAllyLeader()) {
            player.sendPacket(SystemMsg.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
            return;
        }

        if (player.getAlliance().getMembersCount() > 1) {
            player.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_DISSOLVE_THE_ALLIANCE);
            return;
        }

        ClanTable.getInstance().dissolveAlly(player);
    }

    private Set<PlayerClass> getAvailableSubClasses(final Player player, final boolean isNew) {
        final int charClassId = player.getPlayerClassComponent().getBaseClassId();
        final PlayerRace npcRace = getVillageMasterRace();
        final ClassType npcTeachType = getVillageMasterTeachType();

        final PlayerClass currClass = PlayerClass.values()[charClassId];// .valueOf(charClassName);

        /**
         * If the race of your main class is Elf or Dark Elf, you may not select
         * each class as a subclass to the other class, and you may not select
         * Overlord and Warsmith class as a subclass.
         *
         * You may not select a similar class as the subclass. The occupations
         * classified as similar classes are as follows:
         *
         * Treasure Hunter, Plainswalker and Abyss Walker Hawkeye, Silver Ranger
         * and Phantom Ranger Paladin, Dark Avenger, Temple Knight and Shillien
         * Knight Warlocks, Elemental Summoner and Phantom Summoner Elder and
         * Shillien Elder Swordsinger and Bladedancer Sorcerer, Spellsinger and
         * Spellhowler
         *
         * Kamael могут брать только сабы Kamael
         * Другие классы не могут брать сабы Kamael
         *
         */
        final Set<PlayerClass> availSubs = currClass.getAvailableSubclasses();
        if (availSubs == null) {
            return Collections.emptySet();
        }

        // Из списка сабов удаляем мейн класс игрока
        availSubs.remove(currClass);

        for (final PlayerClass availSub : availSubs) {
            // Удаляем из списка возможных сабов, уже взятые сабы и их предков
            for (final SubClass subClass : player.getPlayerClassComponent().getSubClasses().values()) {
                if (availSub.ordinal() == subClass.getClassId()) {
                    availSubs.remove(availSub);
                    continue;
                }

                // Удаляем из возможных сабов их родителей, если таковые есть у чара
                final ClassId parent = ClassId.VALUES[availSub.ordinal()].getParent(player.getSex());
                if (parent != null && parent.getId() == subClass.getClassId()) {
                    availSubs.remove(availSub);
                    continue;
                }

                // Удаляем из возможных сабов родителей текущих сабклассов, иначе если взять саб berserker
                // и довести до 3ей профы - doombringer, игроку будет предложен berserker вновь (дежавю)
                final ClassId subParent = ClassId.VALUES[subClass.getClassId()].getParent(player.getSex());
                if (subParent != null && subParent.getId() == availSub.ordinal()) {
                    availSubs.remove(availSub);
                }
            }

            if (!AllSettingsConfig.ALT_GAME_SUB_ALL_MASTERS) {
                if (!availSub.isOfRace(PlayerRace.human) && !availSub.isOfRace(PlayerRace.elf)) {
                    if (!availSub.isOfRace(npcRace)) {
                        availSubs.remove(availSub);
                    }
                } else if (!availSub.isOfType(npcTeachType)) {
                    availSubs.remove(availSub);
                }
            }

            // Особенности саб классов камаэль
            if (availSub.isOfRace(PlayerRace.kamael)) {
                // Для Soulbreaker-а и SoulHound не предлагаем Soulbreaker-а другого пола
                if ((currClass == PlayerClass.MaleSoulHound || currClass == PlayerClass.FemaleSoulHound || currClass == PlayerClass.FemaleSoulbreaker || currClass == PlayerClass.MaleSoulbreaker) && (availSub == PlayerClass.FemaleSoulbreaker || availSub == PlayerClass.MaleSoulbreaker)) {
                    availSubs.remove(availSub);
                }

                // Для Berserker(doombringer) и Arbalester(trickster) предлагаем Soulbreaker-а только своего пола
                if (currClass == PlayerClass.Berserker || currClass == PlayerClass.Doombringer || currClass == PlayerClass.Arbalester || currClass == PlayerClass.Trickster) {
                    if (player.getSex() == 1 && availSub == PlayerClass.MaleSoulbreaker || player.getSex() == 0 && availSub == PlayerClass.FemaleSoulbreaker) {
                        availSubs.remove(availSub);
                    }
                }

                // Inspector доступен, только когда вкачаны 2 возможных первых саба камаэль(+ мейн класс)
                if (availSub == PlayerClass.Inspector && player.getPlayerClassComponent().getSubClasses().size() < (isNew ? 3 : 4)) {
                    availSubs.remove(availSub);
                }
            }
        }
        return availSubs;
    }

    private PlayerRace getVillageMasterRace() {
        switch (getTemplate().getRace().ordinal()) {
            case 14:
                return PlayerRace.human;
            case 15:
                return PlayerRace.elf;
            case 16:
                return PlayerRace.darkelf;
            case 17:
                return PlayerRace.orc;
            case 18:
                return PlayerRace.dwarf;
            case 25:
                return PlayerRace.kamael;
        }
        return null;
    }

    private ClassType getVillageMasterTeachType() {
        switch (getNpcId()) {
            case 30031:
            case 30037:
            case 30070:
            case 30120:
            case 30191:
            case 30289:
            case 30857:
            case 30905:
            case 32095:
            case 30141:
            case 30305:
            case 30358:
            case 30359:
            case 31336:
                return ClassType.Priest;

            case 30115:
            case 30174:
            case 30175:
            case 30176:
            case 30694:
            case 30854:
            case 31331:
            case 31755:
            case 31996:
            case 32098:
            case 32147:
            case 32160:
            case 30154:
            case 31285:
            case 31288:
            case 31326:
            case 31977:
            case 32150:
                return ClassType.Mystic;
            default:
        }

        return ClassType.Fighter;
    }
}
