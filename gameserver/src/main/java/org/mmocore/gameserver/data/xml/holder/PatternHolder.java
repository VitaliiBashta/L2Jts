package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hack
 * Date: 07.09.2016 2:39
 * <p>
 * TODO: перенести все паттерны сюда
 */
public class PatternHolder extends AbstractHolder {
    private static final PatternHolder instance = new PatternHolder();
    private final Map<PatternType, String> holder = new HashMap<>();

    public PatternHolder() {
        File patternFile = new File("configuration/customPatterns.txt");
        if (patternFile.exists())
            try {
                Files.lines(patternFile.toPath()).forEach(this::splitPattern);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static PatternHolder getInstance() {
        return instance;
    }

    private void splitPattern(String str) {
        String[] strs = str.split("::");
        if (strs.length != 2)
            return;
        holder.put(PatternType.valueOf(strs[0]), strs[1]);
    }

    public void addPattern(PatternType type, String p) {
        holder.put(type, p);
    }

    public String getPattern(PatternType type) {
        return holder.get(type);
    }

    @Override
    public void clear() {
        holder.clear();
    }

    @Override
    public int size() {
        return holder.size();
    }

    public enum PatternType {
        ServiceRename
    }
}
