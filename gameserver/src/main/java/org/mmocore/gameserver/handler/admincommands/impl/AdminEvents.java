package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;

public class AdminEvents implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().IsEventGm) {
            return false;
        }

        switch (command) {
            case admin_events: {
                if (wordList.length == 1) {
                    activeChar.sendPacket(new HtmlMessage(5).setFile("admin/events/events.htm"));
                } else {
                    activeChar.sendPacket(new HtmlMessage(5).setFile("admin/events/" + wordList[1].trim()));
                }
                break;
            }
            case admin_start_event: {
                if (wordList.length == 2) {
                    final int eventId = Integer.parseInt(wordList[1]);
                    if (EventHolder.getInstance().getEvent(EventType.PVP_EVENT, eventId) != null) {
                        final Event event = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, eventId);
                        event.registerActions();
                        event.startEvent();
                        activeChar.sendAdminMessage("You start " + event.getName() + " event!");
                    }
                } else {
                    activeChar.sendAdminMessage("You must call //start_event ID");
                }
                break;
            }
            case admin_fast_start_event: {
                if (wordList.length == 2) {
                    final int eventId = Integer.parseInt(wordList[1]);
                    if (EventHolder.getInstance().getEvent(EventType.PVP_EVENT, eventId) != null) {
                        final Event event = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, eventId);
                        event.registerActions(true);
                        activeChar.sendAdminMessage("You start " + event.getName() + " event!");
                    }
                } else {
                    activeChar.sendAdminMessage("You must call //start_event ID");
                }
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
        admin_events,
        admin_start_event,
        admin_fast_start_event
    }
}