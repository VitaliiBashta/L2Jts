package org.mmocore.gameserver.configuration.parser.chatFilter;

import org.mmocore.gameserver.configuration.config.chatFilter.ChatFilterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Create by Mangol on 08.12.2015.
 * TODO: Сделать нормальный чат фильтр.
 */
@Deprecated
public class AbuseWorldsConfigParser {
    private static final Logger _log = LoggerFactory.getLogger(AbuseWorldsConfigParser.class);
    private static final AbuseWorldsConfigParser instance = new AbuseWorldsConfigParser();

    public static AbuseWorldsConfigParser getInstance() {
        return instance;
    }

    public void load() {
        final List<Pattern> tmp = new ArrayList<>();
        LineNumberReader lnr = null;
        try {
            String line;
            String configFile = "configuration/chatFilter/abusewords.txt";
            final File file = new File(configFile);
            lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            while ((line = lnr.readLine()) != null) {
                final StringTokenizer st = new StringTokenizer(line, "\n\r");
                if (st.hasMoreTokens()) {
                    tmp.add(Pattern.compile(".*" + st.nextToken() + ".*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
                }
            }
            ChatFilterConfig.ABUSEWORD_LIST = tmp.toArray(new Pattern[tmp.size()]);
            tmp.clear();
            _log.info(file.getName() + " loaded " + ChatFilterConfig.ABUSEWORD_LIST.length + " abuse words.");
        } catch (IOException e1) {
            _log.warn("Error reading abuse: " + e1);
        } finally {
            try {
                if (lnr != null) {
                    lnr.close();
                }
            } catch (Exception e2) {
                // nothing
            }
        }
    }

    public void reload() {
        load();
    }
}
