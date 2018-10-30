package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.entity.olympiad.OlympiadDatabase;
import org.mmocore.gameserver.model.entity.olympiad.OlympiadManager;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.List;


public class AdminOlympiad implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        switch (command) {
            case admin_oly_save: {
                if (!OlympiadConfig.ENABLE_OLYMPIAD) {
                    return false;
                }

                try {
                    OlympiadDatabase.save();
                } catch (Exception e) {

                }
                activeChar.sendAdminMessage("olympaid data saved.");
                break;
            }
            case admin_add_oly_points: {
                if (wordList.length < 3) {
                    activeChar.sendAdminMessage("Command syntax: //add_oly_points <char_name> <point_to_add>");
                    activeChar.sendAdminMessage("This command can be applied only for online players.");
                    return false;
                }

                final Player player = World.getPlayer(wordList[1]);
                if (player == null) {
                    activeChar.sendAdminMessage("Character " + wordList[1] + " not found in game.");
                    return false;
                }

                final int pointToAdd;

                try {
                    pointToAdd = Integer.parseInt(wordList[2]);
                } catch (NumberFormatException e) {
                    activeChar.sendAdminMessage("Please specify integer value for olympiad points.");
                    return false;
                }

                final int curPoints = Olympiad.getNoblePoints(player.getObjectId());
                Olympiad.manualSetNoblePoints(player.getObjectId(), curPoints + pointToAdd);
                final int newPoints = Olympiad.getNoblePoints(player.getObjectId());

                activeChar.sendAdminMessage("Added " + pointToAdd + " points to character " + player.getName());
                activeChar.sendAdminMessage("Old points: " + curPoints + ", new points: " + newPoints);
                break;
            }
            case admin_oly_start: {
                Olympiad._manager = new OlympiadManager();
                Olympiad._inCompPeriod = true;

                new Thread(Olympiad._manager).start();

                AnnouncementUtils.announceToAll(SystemMsg.SHARPEN_YOUR_SWORDS_TIGHTEN_THE_STITCHING_IN_YOUR_ARMOR_AND_MAKE_HASTE_TO_A_GRAND_OLYMPIAD_MANAGER__BATTLES_IN_THE_GRAND_OLYMPIAD_GAMES_ARE_NOW_TAKING_PLACE);
                break;
            }
            case admin_oly_stop: {
                Olympiad._inCompPeriod = false;
                AnnouncementUtils.announceToAll(SystemMsg.MUCH_CARNAGE_HAS_BEEN_LEFT_FOR_THE_CLEANUP_CREW_OF_THE_OLYMPIAD_STADIUM);
                try {
                    OlympiadDatabase.save();
                } catch (Exception e) {

                }

                break;
            }
            case admin_add_hero: {
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("Command syntax: //add_hero <char_name>");
                    activeChar.sendAdminMessage("This command can be applied only for online players.");
                    return false;
                }

                final Player player = World.getPlayer(wordList[1]);
                if (player == null) {
                    activeChar.sendAdminMessage("Character " + wordList[1] + " not found in game.");
                    return false;
                }

                final StatsSet hero = new StatsSet();
                hero.set(Olympiad.CLASS_ID, player.getPlayerClassComponent().getBaseClassId());
                hero.set(Olympiad.CHAR_ID, player.getObjectId());
                hero.set(Olympiad.CHAR_NAME, player.getName());

                final List<StatsSet> heroesToBe = new ArrayList<>();
                heroesToBe.add(hero);

                Hero.getInstance().computeNewHeroes(heroesToBe);

                activeChar.sendAdminMessage("Hero status added to player " + player.getName());
                break;
            }
        }

        return true;
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
        admin_oly_save,
        admin_add_oly_points,
        admin_oly_start,
        admin_add_hero,
        admin_oly_stop
    }
}