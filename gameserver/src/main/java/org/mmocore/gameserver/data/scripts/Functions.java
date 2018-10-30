package org.mmocore.gameserver.data.scripts;

import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TIntObjectMap;
import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.lang.reference.HardReferences;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.StringHolder;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExNoticePostArrived;
import org.mmocore.gameserver.network.lineage.serverpackets.NpcSay;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.ItemFunctions;

// TODO: убрать эту хрень
public class Functions {
    public final HardReference<NpcInstance> npc = HardReferences.emptyRef();

    public static void show(final String text, final Player self, final NpcInstance npc, final Object... arg) {
        if (text == null || self == null) {
            return;
        }

        final HtmlMessage msg = npc == null ? new HtmlMessage(5) : new HtmlMessage(npc);

        // приводим нашу html-ку в нужный вид
        if (text.endsWith(".htm")) {
            msg.setFile(text);
            msg.replace("%player_level%", String.valueOf(self.getLevel()));
        } else {
            msg.setHtml(HtmlUtils.bbParse(text));
        }

        if (arg != null && arg.length % 2 == 0) {
            for (int i = 0; i < arg.length; i = +2) {
                msg.replace(String.valueOf(arg[i]), String.valueOf(arg[i + 1]));
            }
        }

        self.sendPacket(msg);
    }

    public static void showFromStringHolder(final String message, final Player self) {
        show(StringHolder.getInstance().getString(self, message), self, null);
    }

    // Белый чат
    @Deprecated
    public static void npcSay(final NpcInstance npc, final String text) {
        ChatUtils.say(npc, ServerConfig.CHAT_RANGE, NpcString.NONE, text);
    }

    // Белый чат
    public static void npcSay(final NpcInstance npc, final NpcString npcString, final String... params) {
        ChatUtils.say(npc, ServerConfig.CHAT_RANGE, npcString, params);
    }

    // private message
    @Deprecated
    public static void npcSayToPlayer(final NpcInstance npc, final Player player, final String text) {
        player.sendPacket(new NpcSay(npc, ChatType.NPC_ALL, NpcString.NONE, text));
    }

    // Shout (желтый) чат
    @Deprecated
    public static void npcShout(final NpcInstance npc, final String text) {
        ChatUtils.shout(npc, NpcString.NONE, text);
    }

    public static void sendSystemMail(final Player receiver, final String title, final String body, final TIntLongMap items) {
        if (receiver == null || !receiver.isOnline()) {
            return;
        }
        if (title == null) {
            return;
        }
        if (items.size() > 8) {
            return;
        }

        final Mail mail = new Mail();
        mail.setSenderId(1);
        mail.setSenderName("Admin");
        mail.setReceiverId(receiver.getObjectId());
        mail.setReceiverName(receiver.getName());
        mail.setTopic(title);
        mail.setBody(body);

        final TIntLongIterator iterator = items.iterator();
        for (int i = items.size(); i > 0; i--) {
            iterator.advance();
            final ItemInstance item = ItemFunctions.createItem(iterator.key());
            item.setLocation(ItemInstance.ItemLocation.MAIL);
            item.setCount(iterator.value());
            item.save();
            mail.addAttachment(item);
        }
        mail.setType(Mail.SenderType.NEWS_INFORMER);
        mail.setUnread(true);
        mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
        mail.save();

        receiver.sendPacket(ExNoticePostArrived.STATIC_TRUE);
        receiver.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
    }

    public static boolean isPvPEventStarted() {
        final TIntObjectMap<Event> events = EventHolder.getInstance().getEvents();
        for (final Event event : events.valueCollection()) {
            if (event.getType() == EventType.PVP_EVENT && event.isInProgress())
                return true;
        }

        return false;
    }

    public void show(final String text, final Player self) {
        show(text, self, getNpc());
    }

    public NpcInstance getNpc() {
        return npc.get();
    }
}
