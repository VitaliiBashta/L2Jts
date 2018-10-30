package org.jts.dataparser.data.pch;

import org.jts.dataparser.data.pch.linker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created with IntelliJ IDEA. User: camelion Date: 1/13/13 Time: 1:47 PM To
 * change this template use File | Settings | File Templates.
 */
public class LinkerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkerFactory.class);
    private static Map<String, String> links = new HashMap<>();
    private static Map<String, int[]> skill_pch = new HashMap<>();
    private static Map<String, String> option_pch_links = new HashMap<>();

    private static LinkerFactory ourInstance = new LinkerFactory();
    private boolean isLoaded;

    private LinkerFactory() {
        load();
    }

    public static LinkerFactory getInstance() {
        return ourInstance;
    }

    public static void addLink(String link, String value) {
        links.put(link, value);
    }

    public static void addOptionPchLink(String link, String value) {
        option_pch_links.put(link, value);
    }

    public static void addLinkSkillPch(String link, int[] objects) {
        skill_pch.put(link, objects);
    }

    private void load() {
        if (!isLoaded) {
            CastledataPchLinker.getInstance().load();
            CategoryPchLinker.getInstance().load();
            ManualPchLinker.getInstance().load();
            InstantzoneDataPchLinker.getInstance().load();
            ItemPchLinker.getInstance().load();
            SkillPchLinker.getInstance().load();
            MultisellPchLinker.getInstance().load();
            NpcPchLinker.getInstance().load();
            OptionPchLinker.getInstance().load();
            QuestPchLinker.getInstance().load();
            isLoaded = true;
        }
    }

    public void unload() {
        if (isLoaded) {
            links.clear();
            isLoaded = false;
        }
    }

    public String findValueFor(String link) {
        return links.get(link);
    }

    public int findClearValue(String link) {
        int value = 0;
        try {
            value = Integer.parseInt(links.get("@" + link));
        } catch (final Exception e) {
            LOGGER.warn("findClearValue returned exception for |{}| link", link);
        }
        return value;
    }

    public String findLinkFromValue(int linkId) {
        final String key = "";
        for (Entry<String, String> entry : links.entrySet())
            if (entry.getValue().equals(String.valueOf(linkId)))
                return entry.getKey().replace("@", "");
        LOGGER.warn("method findLinkFromValue returned null from ID: " + linkId);
        return key;
    }

    public String optionPchfindValueFor(String link) {
        return option_pch_links.get(link);
    }

    public int optionPchfindClearValue(String link) {
        int value = Integer.parseInt(option_pch_links.get("@" + link));
        return value;
    }

    public String optionPchfindLinkFromValue(int linkId) {
        final String key = "";
        for (Entry<String, String> entry : option_pch_links.entrySet())
            if (entry.getValue().equals(String.valueOf(linkId)))
                return entry.getKey().replace("@", "");
        LOGGER.warn("method optionPchfindLinkFromValue returned null from ID: " + linkId);
        return key;
    }

    public int[] skillPchIdfindClearValue(String link) {
        int[] value = skill_pch.get("@" + link).clone();
        return value;
    }
}