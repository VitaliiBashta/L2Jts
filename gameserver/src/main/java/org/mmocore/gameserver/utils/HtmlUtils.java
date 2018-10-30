package org.mmocore.gameserver.utils;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SysString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * @author VISTALL
 * @author Java-man
 */
public class HtmlUtils {
    public static final String PREV_BUTTON = "<button value=\"&$1037;\" action=\"bypass %prev_bypass%\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
    public static final String NEXT_BUTTON = "<button value=\"&$1038;\" action=\"bypass %next_bypass%\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlUtils.class);

    private static final Pattern PATTERN_1 = Pattern.compile("(\\s|\"|\'|\\(|^|\n)\\*(.*?)\\*(\\s|\"|\'|\\)|\\?|\\.|!|:|;|,|$|\n)");
    private static final Pattern PATTERN_2 = Pattern.compile("(\\s|\"|\'|\\(|^|\n)\\$(.*?)\\$(\\s|\"|\'|\\)|\\?|\\.|!|:|;|,|$|\n)");
    private static final Pattern PATTERN_3 = Pattern.compile("%%\\s*\n");
    private static final Pattern PATTERN_4 = Pattern.compile("\n\n+");
    private static final Pattern PATTERN_5 = Pattern.compile(" @");

    public static String htmlResidenceName(final int id) {
        return "&%" + id + ';';
    }

    public static String htmlNpcName(final int npcId) {
        return "&@" + npcId + ';';
    }

    public static String htmlSysString(final SysString sysString) {
        return htmlSysString(sysString.getId());
    }

    public static String htmlSysString(final int id) {
        return "&$" + id + ';';
    }

    public static String htmlItemName(final int itemId) {
        return "&#" + itemId + ';';
    }

    public static String htmlClassName(final int classId) {
        return "<ClassId>" + classId + "</ClassId>";
    }

    public static String htmlNpcString(final NpcString id, final Object... params) {
        return htmlNpcString(id.getId(), params);
    }

    public static String htmlNpcString(final int id, final Object... params) {
        final StringBuilder replace = new StringBuilder("<fstring");
        if (params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                replace.append(" p").append(i + 1).append("=\"").append(params[i]).append('"');
            }
        }
        replace.append('>').append(id).append("</fstring>");
        return replace.toString();
    }

    public static String htmlButton(final String value, final String action, final int width) {
        return htmlButton(value, action, width, 22);
    }

    public static String htmlButton(final String value, final String action, final int width, final int height) {
        return String
                .format("<button value=\"%s\" action=\"%s\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=%d height=%d fore=\"L2UI_CT1.Button_DF_Small\">", value, action, width, height);
    }

    public static String bbParse(String s) {
        if (s == null) {
            return null;
        }

        s = s.replace("\r", "");
        s = PATTERN_1.matcher(s).replaceAll("$1<font color=\"LEVEL\">$2</font>$3"); // *S1*
        s = PATTERN_2.matcher(s).replaceAll("$1<font color=\"00FFFF\">$2</font>$3");// $S1$
        s = Strings.replace(s, "^!(.*?)$", Pattern.MULTILINE, "<font color=\"FFFFFF\">$1</font>\n\n");
        s = PATTERN_3.matcher(s).replaceAll("<br1>");
        s = PATTERN_4.matcher(s).replaceAll("<br>");
        s = Strings.replace(s, "\\[([^\\]\\|]*?)\\|([^\\]]*?)\\]", Pattern.DOTALL, "<a action=\"bypass -h $1\">$2</a>");
        s = PATTERN_5.matcher(s).replaceAll("\" msg=\"");

        return s;
    }

    public static String compress(final HtmlCompressor compressor, final String content) {
        final String result = compressor.compress(content);

        LOGGER.info(String.format(
                "Compression time: %,d ms, Original size: %,d bytes, Compressed size: %,d bytes",
                compressor.getStatistics().getTime(),
                compressor.getStatistics().getOriginalMetrics().getFilesize(),
                compressor.getStatistics().getCompressedMetrics().getFilesize()
        ));

        return result;
    }
}
