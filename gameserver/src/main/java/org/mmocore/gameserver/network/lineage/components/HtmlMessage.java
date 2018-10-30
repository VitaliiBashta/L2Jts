package org.mmocore.gameserver.network.lineage.components;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.handler.npcdialog.INpcDialogAppender;
import org.mmocore.gameserver.handler.npcdialog.NpcDialogAppenderHolder;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.ExNpcQuestHtmlMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.NpcHtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.Language;
import org.mmocore.gameserver.utils.ThymeleafJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Класс обработки HTML диалогов перед отправкой клиенту.
 *
 * @author G1ta0
 */
public class HtmlMessage implements IBroadcastPacket {
    private static final Logger _log = LoggerFactory.getLogger(HtmlMessage.class);
    private final int _npcObjId;
    private String _filename;
    private String _html;
    private List<String> _replaces;
    private NpcInstance _npc;
    private int _npcId;
    private int _val;
    private int _questId;
    private Optional<Context> context = Optional.empty();

    public HtmlMessage(final NpcInstance npc, final String filename, final int val) {
        _npc = npc;
        _npcId = npc.getNpcId();
        _npcObjId = npc.getObjectId();
        _filename = filename;
        _val = val;
    }

    public HtmlMessage(final NpcInstance npc) {
        this(npc, null, -1);
    }

    public HtmlMessage(final int npcObjId) {
        _npcObjId = npcObjId;
    }

    public HtmlMessage setHtml(final String text) {
        _html = text;
        return this;
    }

    public HtmlMessage setFile(Player player, String file) {
        Language lang = player.getLanguage() == null ? Language.ENGLISH : player.getLanguage();
        return setFile("data/html-" + lang.getShortName() + "/" + file);
    }

    public final HtmlMessage setFile(final String file) {
        _filename = file;
        return this;
    }

    public final HtmlMessage setQuestId(final int questId) {
        _questId = questId;
        return this;
    }

    public HtmlMessage replace(final String pattern, final String value) {
        if (pattern == null || value == null) {
            return this;
        }
        if (_replaces == null) {
            _replaces = new ArrayList<>();
        }
        _replaces.add(pattern);
        _replaces.add(value);

        return this;
    }

    public HtmlMessage replace(final String pattern, final NpcString npcString, final Object... arg) {
        if (pattern == null) {
            return this;
        }
        if (npcString.getSize() != arg.length) {
            throw new IllegalArgumentException("Not valid size of parameters: " + npcString);
        }
        return replace(pattern, HtmlUtils.htmlNpcString(npcString, arg));
    }

    public void addVariable(final String name, final Object value) {
        if (!context.isPresent()) {
            context = Optional.of(new Context());
        }
        context.get().setVariable(name, value);
    }

    public void addVariables(final Map<String, Object> variables) {
        context.get().setVariables(variables);
    }

    @Override
    public GameServerPacket packet(final Player player) {
        StrBuilder html = null;
        String content = null;

        if (_filename != null) {
            if (_filename.startsWith("data/html/")) {
                _log.warn("HtmlMessage: incorrect root : " + _filename, new Exception());
                _filename = _filename.replace("data/html/", "");
            }

            if (player.isGM()) {
                final String str = _filename.lastIndexOf("/") > 0 ? _filename.substring(_filename.lastIndexOf("/")).replace("/", "") : _filename;
                if (!StringUtils.EMPTY.equals(str))
                    player.sendHTMLMessage(str);
            }

            content = HtmCache.getInstance().getHtml(_filename, player);
        } else {
            content = _html;
        }

        if (content != null) {
            html = new StrBuilder(content.length() + 16);
            content = ThymeleafJob.getInstance().process(content, context);
            if (!content.startsWith("<html>")) {
                html.append("<html><body>");
                html.append(content);
                html.append("</body></html>");
            } else {
                html.append(content);
            }

            final List<INpcDialogAppender> appends = NpcDialogAppenderHolder.getInstance().getAppenders(_npcId);
            if (appends != null) {
                for (final INpcDialogAppender append : appends) {
                    final String returnVal = append.getAppend(player, _npc, _val);
                    if (returnVal != null) {
                        html.replaceFirst("</body>", "<br>" + HtmlUtils.bbParse(returnVal) + "</body>");
                    }
                }
            }

            if (_replaces != null) {
                for (int i = 0; i < _replaces.size(); i += 2) {
                    html.replaceAll(_replaces.get(i), _replaces.get(i + 1));
                }
            }

            html.replaceAll("%npcId%", String.valueOf(_npcId));
            html.replaceAll("%npcname%", HtmlUtils.htmlNpcName(_npcId));
            html.replaceAll("%objectId%", String.valueOf(_npcObjId));
            html.replaceAll("%playername%", player.getName());

            player.setLastNpc(_npc);
            player.getBypassStorage().parseHtml(html, false);
        }

        if (StringUtils.isEmpty(html)) {
            if (_filename != null) {
                _log.warn("HtmlMessage: file " + _filename + " not found, lang: " + player.getLanguage() + '!');
            } else {
                _log.warn("HtmlMessage: Html message text is null or empty!", new Exception());
            }
        }

        if (_questId == 0) {
            return new NpcHtmlMessage(_npcObjId, html);
        } else {
            return new ExNpcQuestHtmlMessage(_npcObjId, html, _questId);
        }
    }
}
