package org.mmocore.gameserver.configuration.config.chatFilter;

import java.util.regex.Pattern;

/**
 * Create by Mangol on 08.12.2015.
 */
@Deprecated
public class ChatFilterConfig {
    public static Pattern[] ABUSEWORD_LIST = {};

    public static boolean containsAbuseWord(final String s) {
        for (final Pattern pattern : ABUSEWORD_LIST) {
            if (pattern.matcher(s).matches()) {
                return true;
            }
        }
        return false;
    }
}
