package org.mmocore.gameserver.manager;

import org.jts.dataparser.data.holder.NpcPosHolder;
import org.jts.dataparser.data.holder.npcpos.maker.Npc;
import org.jts.dataparser.data.holder.npcpos.maker.NpcMaker;
import org.jts.dataparser.data.holder.npcpos.maker_ex.NpcEx;
import org.jts.dataparser.data.holder.npcpos.maker_ex.NpcMakerEx;
import org.jts.dataparser.data.pch.LinkerFactory;
import org.mmocore.gameserver.ai.maker.default_maker;
import org.mmocore.gameserver.ai.maker.spawn_define;
import org.mmocore.gameserver.configuration.config.PtsDataConfig;
import org.mmocore.gameserver.data.scripts.Scripts;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author KilRoy
 */
public class SpawnMakerManager {
    public static final boolean DEBUG = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(SpawnMakerManager.class);
    private final Map<String, default_maker> specialMakers = new ConcurrentHashMap<>();
    private final Map<String, default_maker> makers = new ConcurrentHashMap<>();

    private SpawnMakerManager() {
    }

    public static SpawnMakerManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void makerInit() {
        if (PtsDataConfig.PTS_DATA_ENABLED_MAKER_SPAWN) {
            loadNpcPosMaker();
            LOGGER.info("Loaded {} default(siege, special, etc...) maker.", specialMakers.size());
            LOGGER.info("Loaded {} npc(spawn, event, etc...) makerEx.", makers.size());
            onStartServer();
        } else {
            LOGGER.info("Maker loader is disabled. Continue...");
        }
    }

    @SuppressWarnings("unchecked")
    private void loadNpcPosMaker() {
        for (final NpcMaker maker : NpcPosHolder.getInstance().getNpcMakers()) {
            final default_maker defaultMaker = new default_maker(maker.getName(), maker.getMaximumNpc());

            for (final Npc npcs : maker.npcs) {
                final int npcId = LinkerFactory.getInstance().findClearValue(npcs.getNpcName()) - 1000000;
                if (npcId > 0 && npcId < 1000000) {
                    final NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
                    if (template == null) {
                        LOGGER.error("Template for NpcID {} not find. Maker not load.", npcId);
                        continue;
                    }

                    final spawn_define def = new spawn_define(template, npcs);
                    def.addTerritories(maker.getTerritoryNames(), maker.getBannedTerritory());
                    defaultMaker.addSpawnDefine(def);
                    specialMakers.put((maker.getName() == null || maker.getName().isEmpty()) ? maker.getTerritoryNames()[0] : maker.getName(), defaultMaker);
                } else {
                    LOGGER.error("Not find npcId={}.", npcId);
                }
            }
        }
        for (final NpcMakerEx makerEx : NpcPosHolder.getInstance().getNpcMakersEx()) {
            Class<default_maker> makerClass;
            try {
                makerClass = (Class<default_maker>) Class.forName("org.mmocore.gameserver.ai.maker." + makerEx.getAI());
            } catch (ClassNotFoundException e) {
                makerClass = (Class<default_maker>) Scripts.getInstance().getClasses().get("ai.maker." + makerEx.getAI());
            }

            default_maker defaultMaker = null;
            if (makerClass == null) {
                if (DEBUG) {
                    LOGGER.error("Not found maker class={} for AI={}", makerEx.getName(), makerEx.getAI());
                }
                try {
                    makerClass = (Class<default_maker>) Class.forName("org.mmocore.gameserver.ai.maker.default_maker");
                    defaultMaker = ((Constructor<default_maker>) makerClass.getConstructors()[0]).newInstance(makerEx.getName(), makerEx.getMaximumNpc());
                } catch (final Exception e) {
                    LOGGER.error("Loader return error from loading DefaultMaker class. Continue...");
                    continue;
                }
            } else {
                if (makerClass.isAnnotationPresent(Deprecated.class)) {
                    LOGGER.error("Maker class={}, is deprecated for maker={}", makerEx.getAI(), makerEx.getName());
                }

                try {
                    defaultMaker = ((Constructor<default_maker>) makerClass.getConstructors()[0]).newInstance(makerEx.getName(), makerEx.getMaximumNpc());
                } catch (final Exception e) {
                    LOGGER.error("Unable to create maker={} for AI={}", makerEx.getName(), makerEx.getAI(), e);
                    continue;
                }
            }
            if (defaultMaker == null) {
                LOGGER.error("Fatal error from loading makerEx");
                continue;
            } else if (makerEx.getAIParameters() != null && makerEx.getAIParameters().getParams().size() > 0) {
                final Class<?> dmClass = defaultMaker.getClass();
                Field field;
                for (final String param : makerEx.getAIParameters().getParams().keySet()) {
                    if (!param.isEmpty()) {
                        try {
                            field = dmClass.getField(param);
                        } catch (final Exception e) {
                            continue;
                        }

                        final Object val = makerEx.getAIParameters().getParams().get(param);
                        try {
                            if (field.getType().getSimpleName().equalsIgnoreCase("boolean")) {
                                field.setBoolean(defaultMaker, Boolean.parseBoolean(val.toString()));
                            } else if (field.getType().getSimpleName().equalsIgnoreCase("int")) {
                                field.setInt(defaultMaker, Integer.valueOf(val.toString()));
                            } else if (field.getType().getSimpleName().equalsIgnoreCase("long")) {
                                field.setLong(defaultMaker, Long.valueOf(val.toString()));
                            } else if (field.getType().getSimpleName().equalsIgnoreCase("double")) {
                                field.setDouble(defaultMaker, Double.valueOf(val.toString()));
                            } else if (field.getType().getSimpleName().equalsIgnoreCase("string")) {
                                field.set(defaultMaker, val);
                            } else {
                                LOGGER.warn("Unsuported field type on maker={} for param={}", makerEx.getName(), param);
                            }
                        } catch (final Exception e) {
                            LOGGER.warn("Make field not success on maker={} for param={}", makerEx.getName(), param);
                        }
                    }
                }
            }
            for (final NpcEx npcs : makerEx.getNpcs()) {
                if (npcs.getNpcName().contains("_fort_") || makerEx.getAI().contains("immo_boss_maker")) // Затычки для спавна
                {
                    continue;
                }
                final int npcId = LinkerFactory.getInstance().findClearValue(npcs.getNpcName()) - 1000000;
                if (npcId > 0 && npcId < 1000000) {
                    final NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
                    if (template == null) {
                        LOGGER.error("Template for NpcID {} not find. Maker not load.", npcId);
                        continue;
                    }

                    final spawn_define def = new spawn_define(template, npcs);
                    def.setDefaultMaker(defaultMaker);
                    def.addTerritories(makerEx.getTerritoryNames(), makerEx.getBannedTerritory());
                    defaultMaker.addSpawnDefine(def);
                    makers.put(makerEx.getName(), defaultMaker);
                } else {
                    LOGGER.error("Not find npcId={}.", npcId);
                }
            }
        }
    }

    private void onStartServer() {
        for (final default_maker maker : makers.values()) {
            if (maker != null) {
                maker.onStart();
            }
        }
        for (final default_maker specialMaker : specialMakers.values()) {
            if (specialMaker != null) {
                specialMaker.onStart();
            }
        }
    }

    private static class LazyHolder {
        private static final SpawnMakerManager INSTANCE = new SpawnMakerManager();
    }
}