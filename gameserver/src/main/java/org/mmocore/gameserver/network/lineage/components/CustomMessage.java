package org.mmocore.gameserver.network.lineage.components;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.mmocore.gameserver.data.StringHolder;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Даный класс является обработчиком серверных интернациональных сообщений.
 */
public class CustomMessage implements IBroadcastPacket {
    private static final Logger _log = LoggerFactory.getLogger(CustomMessage.class);

    private final String _address;
    private List<String> _args;

    public CustomMessage(final String address) {
        _address = address;
    }

    public CustomMessage addString(final String arg) {
        if (_args == null) {
            _args = new ArrayList<>();
        }
        _args.add(arg);
        return this;
    }

    public CustomMessage addNumber(final int i) {
        return addString(String.valueOf(i));
    }

    public CustomMessage addItemName(int itemId) {
        ItemTemplate template = ItemTemplateHolder.getInstance().getTemplate(itemId);
        return template == null ? addString("Unknown Item") : addString(template.getName());
    }

    public CustomMessage addNumber(final long l) {
        return addString(String.valueOf(l));
    }

    public String toString(final Player player) {
        return toString(player.getLanguage());
    }

    public String toString(final Language lang) {
        StrBuilder msg = null;

        final String text = StringHolder.getInstance().getString(_address, lang);
        if (text != null) {
            msg = new StrBuilder(text);

            if (_args != null) {
                for (int i = 0; i < _args.size(); i++) {
                    msg.replaceFirst("{" + i + '}', _args.get(i));
                }
            }
        }

        if (StringUtils.isEmpty(msg)) {
            _log.warn("CustomMessage: string: " + _address + " not found for lang: " + lang + '!');
            return StringUtils.EMPTY;
        }

        return msg.toString();
    }

    @Override
    public GameServerPacket packet(final Player player) {
        return new SystemMessage(SystemMsg.S1).addString(toString(player));
    }
}