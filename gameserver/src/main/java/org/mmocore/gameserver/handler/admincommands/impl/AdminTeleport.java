package org.mmocore.gameserver.handler.admincommands.impl;


import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Util;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;


public class AdminTeleport implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanTeleport) {
            return false;
        }

        switch (command) {
            case admin_show_moves:
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/teleports.htm"));
                break;
            case admin_show_moves_other:
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/tele/other.htm"));
                break;
            case admin_show_teleport:
                showTeleportCharWindow(activeChar);
                break;
            case admin_teleport_to_character:
                teleportToCharacter(activeChar, activeChar.getTarget());
                break;
            case admin_teleport_to:
            case admin_teleportto:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //teleportto charName");
                    return false;
                }
                final String chaName = Util.joinStrings(" ", wordList, 1);
                final Player cha = GameObjectsStorage.getPlayer(chaName);
                if (cha == null) {
                    activeChar.sendAdminMessage("Player '" + chaName + "' not found in world");
                    return false;
                }
                teleportToCharacter(activeChar, cha);
                break;
            case admin_move_to:
            case admin_moveto:
            case admin_teleport:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //teleport x y z [ref]");
                    return false;
                }

                String coords = String.join(" ", (CharSequence[]) Arrays.copyOfRange(wordList, 1, 4));
                int ref = ArrayUtils.valid(wordList, 4) != null && !ArrayUtils.valid(wordList, 4).isEmpty()
                        ? Integer.parseInt(wordList[4]) : 0;
                teleportTo(activeChar, activeChar, coords, ref);
                break;
            case admin_walk:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //walk x y z");
                    return false;
                }
                try {
                    activeChar.moveToLocation(Location.parseLoc(Util.joinStrings(" ", wordList, 1)), 0, true);
                } catch (IllegalArgumentException e) {
                    activeChar.sendAdminMessage("USAGE: //walk x y z");
                    return false;
                }
                break;
            case admin_observe:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //observe x y z");
                    return false;
                }
                activeChar.enterObserverMode(Location.parseLoc(Util.joinStrings(" ", wordList, 1, 3)));
                break;
            case admin_gonorth:
            case admin_gosouth:
            case admin_goeast:
            case admin_gowest:
            case admin_goup:
            case admin_godown:
                final int val = wordList.length < 2 ? 150 : Integer.parseInt(wordList[1]);
                int x = activeChar.getX();
                int y = activeChar.getY();
                int z = activeChar.getZ();
                if (command == Commands.admin_goup) {
                    z += val;
                } else if (command == Commands.admin_godown) {
                    z -= val;
                } else if (command == Commands.admin_goeast) {
                    x += val;
                } else if (command == Commands.admin_gowest) {
                    x -= val;
                } else if (command == Commands.admin_gosouth) {
                    y += val;
                } else if (command == Commands.admin_gonorth) {
                    y -= val;
                }

                activeChar.teleToLocation(x, y, z);
                showTeleportWindow(activeChar);
                break;
            case admin_tele:
                showTeleportWindow(activeChar);
                break;
            case admin_teleto:
            case admin_tele_to:
            case admin_instant_move:
                if (wordList.length > 1 && "r".equalsIgnoreCase(wordList[1])) {
                    activeChar.setTeleMode(2);
                } else if (wordList.length > 1 && "end".equalsIgnoreCase(wordList[1])) {
                    activeChar.setTeleMode(0);
                } else {
                    activeChar.setTeleMode(1);
                }
                break;
            case admin_tonpc:
            case admin_to_npc:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //tonpc npcId");
                    return false;
                }
                final String npcId = Util.joinStrings(" ", wordList, 1);
                final NpcInstance npc;
                try {
                    if ((npc = GameObjectsStorage.getByNpcId(Integer.parseInt(npcId))) != null) {
                        teleportToCharacter(activeChar, npc);
                        return true;
                    }
                } catch (Exception e) {
                }

                activeChar.sendAdminMessage("Npc " + npcId + " not found");
                break;
            case admin_toobject:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //toobject objectId");
                    return false;
                }
                final Integer target = Integer.parseInt(wordList[1]);
                final GameObject obj;
                if ((obj = GameObjectsStorage.findObject(target)) != null) {
                    teleportToCharacter(activeChar, obj);
                    return true;
                }
                activeChar.sendAdminMessage("Object " + target + " not found");
                break;
        }

        if (!activeChar.getPlayerAccess().CanEditChar) {
            return false;
        }

        switch (command) {
            case admin_teleport_character:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //teleport_character x y z");
                    return false;
                }
                teleportCharacter(activeChar, Util.joinStrings(" ", wordList, 1));
                showTeleportCharWindow(activeChar);
                break;
            case admin_recall:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //recall charName");
                    return false;
                }
                String targetName = Util.joinStrings(" ", wordList, 1);
                Player recall_player = GameObjectsStorage.getPlayer(targetName);
                if (recall_player != null) {
                    teleportTo(activeChar, recall_player, activeChar.getLoc(), activeChar.getReflectionId());
                    return true;
                }
                final int obj_id = CharacterDAO.getInstance().getObjectIdByName(targetName);
                if (obj_id > 0) {
                    teleportCharacter_offline(obj_id, activeChar.getLoc());
                    activeChar.sendAdminMessage(targetName + " is offline. Offline teleport used...");
                } else {
                    activeChar.sendAdminMessage("->" + targetName + "<- is incorrect.");
                }
                break;
            case admin_pc:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //pc charName");
                    return false;
                }
                targetName = Util.joinStrings(" ", wordList, 1);
                recall_player = GameObjectsStorage.getPlayer(targetName);
                if (recall_player != null && recall_player.isInParty()) {
                    for (final Player pl : recall_player.getParty().getPartyMembers()) {
                        teleportTo(activeChar, pl, activeChar.getLoc(), activeChar.getReflectionId());
                    }
                    return true;
                }
                break;
            case admin_setref: {
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("Usage: //setref <reflection>");
                    return false;
                }

                final int ref_id = Integer.parseInt(wordList[1]);
                if (ref_id != 0 && ReflectionManager.getInstance().get(ref_id) == null) {
                    activeChar.sendAdminMessage("Reflection <" + ref_id + "> not found.");
                    return false;
                }

                GameObject target = activeChar;
                final GameObject obj = activeChar.getTarget();
                if (obj != null) {
                    target = obj;
                }

                target.setReflection(ref_id);
                target.decayMe();
                target.spawnMe();
                break;
            }
            case admin_getref:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("Usage: //getref <char_name>");
                    return false;
                }
                final Player cha = GameObjectsStorage.getPlayer(wordList[1]);
                if (cha == null) {
                    activeChar.sendAdminMessage("Player '" + wordList[1] + "' not found in world");
                    return false;
                }
                activeChar.sendAdminMessage("Player '" + wordList[1] + "' in reflection: " + activeChar.getReflectionId() + ", name: " + activeChar.getReflection().getName());
                break;
        }

        if (!activeChar.getPlayerAccess().CanEditNPC) {
            return false;
        }

        switch (command) {
            case admin_recall_npc:
                recallNPC(activeChar);
                break;
        }

        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void showTeleportWindow(final Player activeChar) {
        final HtmlMessage adminReply = new HtmlMessage(5);

        final StringBuilder replyMSG = new StringBuilder("<html><title>Teleport Menu</title>");
        replyMSG.append("<body>");

        replyMSG.append("<br>");
        replyMSG.append("<center><table>");

        replyMSG.append("<tr><td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"North\" action=\"bypass -h admin_gonorth\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Up\" action=\"bypass -h admin_goup\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("<tr><td><button value=\"West\" action=\"bypass -h admin_gowest\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"East\" action=\"bypass -h admin_goeast\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
        replyMSG.append("<tr><td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"South\" action=\"bypass -h admin_gosouth\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Down\" action=\"bypass -h admin_godown\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        replyMSG.append("</table></center>");
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    private void showTeleportCharWindow(final Player activeChar) {
        final GameObject target = activeChar.getTarget();
        if (!target.isPlayer()) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }
        final Player player = target.getPlayer();
        final HtmlMessage adminReply = new HtmlMessage(5);

        final StringBuilder replyMSG = new StringBuilder("<html><title>Teleport Character</title>");
        replyMSG.append("<body>");
        replyMSG.append("The character you will teleport is ").append(player.getName()).append('.');
        replyMSG.append("<br>");

        replyMSG.append("Co-ordinate x");
        replyMSG.append("<edit var=\"char_cord_x\" width=110>");
        replyMSG.append("Co-ordinate y");
        replyMSG.append("<edit var=\"char_cord_y\" width=110>");
        replyMSG.append("Co-ordinate z");
        replyMSG.append("<edit var=\"char_cord_z\" width=110>");
        replyMSG.append("<button value=\"Teleport\" action=\"bypass -h admin_teleport_character $char_cord_x $char_cord_y $char_cord_z\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
        replyMSG.append("<button value=\"Teleport near you\" action=\"bypass -h admin_teleport_character ").append(activeChar.getX()).append(' ').append(activeChar.getY()).append(' ').append(activeChar.getZ()).append("\" width=115 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
        replyMSG.append("<center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>");
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    private void teleportTo(final Player activeChar, final Player target, final String Cords, final int ref) {
        try {
            teleportTo(activeChar, target, Location.parseLoc(Cords), ref);
        } catch (IllegalArgumentException e) {
            activeChar.sendAdminMessage("You must define 3 coordinates required to teleport");
        }
    }

    private void teleportTo(final Player activeChar, final Player target, final Location loc, final int ref) {
        target.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        target.teleToLocation(loc, ref);

        if (target.equals(activeChar)) {
            activeChar.sendAdminMessage("You have been teleported to " + loc + ", reflection id: " + ref);
        }
    }

    private void teleportCharacter(final Player activeChar, final String Cords) {
        final GameObject target = activeChar.getTarget();
        if (target == null || !target.isPlayer()) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }
        if (target.getObjectId() == activeChar.getObjectId()) {
            activeChar.sendAdminMessage("You cannot teleport yourself.");
            return;
        }
        teleportTo(activeChar, (Player) target, Cords, activeChar.getReflectionId());
    }

    private void teleportCharacter_offline(final int obj_id, final Location loc) {
        if (obj_id == 0) {
            return;
        }

        Connection con = null;
        PreparedStatement st = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            st = con.prepareStatement("UPDATE characters SET x=?,y=?,z=? WHERE obj_Id=? LIMIT 1");
            st.setInt(1, loc.x);
            st.setInt(2, loc.y);
            st.setInt(3, loc.z);
            st.setInt(4, obj_id);
            st.executeUpdate();
        } catch (Exception e) {

        } finally {
            DbUtils.closeQuietly(con, st);
        }
    }

    private void teleportToCharacter(final Player activeChar, final GameObject target) {
        if (target == null) {
            return;
        }

        activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        activeChar.teleToLocation(target.getLoc(), target.getReflectionId());

        activeChar.sendAdminMessage("You have teleported to " + target);
    }

    private void recallNPC(final Player activeChar) {
        final GameObject obj = activeChar.getTarget();
        if (obj != null && obj.isNpc()) {
            obj.setLoc(activeChar.getLoc());
            ((NpcInstance) obj).broadcastCharInfo();
            activeChar.sendAdminMessage("You teleported npc " + obj.getName() + " to " + activeChar.getLoc().toString() + '.');
        } else {
            activeChar.sendAdminMessage("Target is't npc.");
        }
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_show_moves,
        admin_show_moves_other,
        admin_show_teleport,
        admin_teleport_to_character,
        admin_teleportto,
        admin_teleport_to,
        admin_move_to,
        admin_moveto,
        admin_teleport,
        admin_teleport_character,
        admin_observe,
        admin_recall,
        admin_walk,
        admin_recall_npc,
        admin_gonorth,
        admin_gosouth,
        admin_goeast,
        admin_gowest,
        admin_goup,
        admin_godown,
        admin_tele,
        admin_teleto,
        admin_tele_to,
        admin_instant_move,
        admin_tonpc,
        admin_to_npc,
        admin_toobject,
        admin_setref,
        admin_getref,
        admin_pc
    }
}
