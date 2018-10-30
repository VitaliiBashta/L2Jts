package org.mmocore.gameserver.data;

import com.google.common.collect.HashBasedTable;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Language;

import java.io.*;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * @author VISTALL
 * @author Java-man
 * @date 19:27/29.12.2010
 */
public final class StringHolder extends AbstractHolder {
    private static final StringHolder INSTANCE = new StringHolder();
    private static final Pattern EQUAL_PATTERN = Pattern.compile("=");
    private HashBasedTable<Language, String, String> strings;

    private StringHolder() {
    }

    public static StringHolder getInstance() {
        return INSTANCE;
    }

    public String getString(final String name, final Player player) {
        final Language lang = player == null ? Language.ENGLISH : player.getLanguage();
        return getString(name, lang);
    }

    public String getString(final Player player, final String name) {
        final Language lang = player == null ? Language.ENGLISH : player.getLanguage();
        return getString(name, lang);
    }

    public String getString(final String address, final Language lang) {
        return strings.get(lang, address);
    }

    public void load() {
        strings = HashBasedTable.create();

        final StringBuilder value = new StringBuilder();

        for (final Language lang : Language.VALUES) {
            final File f = new File(ServerConfig.DATAPACK_ROOT, "data/string/strings_" + lang.getShortName() + ".properties");
            if (!f.exists()) {
                warn("Not find file: " + f.getAbsolutePath());
                continue;
            }
            try {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
                final LineNumberReader lineNumberReader = new LineNumberReader(bufferedReader);
                String line;
                while ((line = lineNumberReader.readLine()) != null) {
                    if (line.startsWith("#")) {
                        continue;
                    }
                    final String[] lineParts = EQUAL_PATTERN.split(line);
                    if (lineParts.length < 2) {
                        error("Error on line: " + line + "; file: " + f.getName());
                        continue;
                    }
                    final String name = lineParts[0];
                    value.append(lineParts[1]);
                    if (lineParts.length > 2) {
                        IntStream.rangeClosed(2, lineParts.length).forEach(m -> value.append('=').append(lineParts[m - 1]));
                    }
                    strings.put(lang, name, value.toString());
                    value.setLength(0);
                }
            } catch (Exception e) {
                error("Exception: " + e, e);
            }
        }

        log();
    }

    public void reload() {
        clear();
        load();
    }

    @Override
    public void log() {
        for (final Map.Entry<Language, Map<String, String>> entry : strings.rowMap().entrySet())
            info("load strings: " + entry.getValue().size() + " for lang: " + entry.getKey());
    }

    @Override
    public int size() {
        return strings.size();
    }

    @Override
    public void clear() {
        strings.clear();
    }
}
