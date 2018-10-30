package org.mmocore.gameserver.phantoms;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.clientCustoms.PhantomConfig;
import org.mmocore.gameserver.data.xml.holder.PhantomHolder;
import org.mmocore.gameserver.data.xml.holder.PhantomOnlineHolder;
import org.mmocore.gameserver.data.xml.holder.PhantomSpawnHolder;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.phantoms.model.Phantom;
import org.mmocore.gameserver.phantoms.template.PhantomSpawnTemplate;
import org.mmocore.gameserver.phantoms.template.PhantomTemplate;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * Created by Hack
 * Date: 21.08.2016 3:31
 * <p>
 * TODO list:
 * - запретить брать ники, которые заняты ботами
 */
public class PhantomLoader {
    private static final Logger log = LoggerFactory.getLogger(PhantomLoader.class);
    private static final PhantomLoader instance = new PhantomLoader();
    private Future<?> spawnWaveTask;

    public static PhantomLoader getInstance() {
        return instance;
    }

    private void spawnPhantom(Location location, ItemTemplate.Grade minGrade, ItemTemplate.Grade maxGrade) {
        PhantomTemplate template = getTemplateForSpawn(minGrade, maxGrade);
        if (template == null)
            return;
        Phantom phantom = PhantomFactory.getInstance().create(template);
        PhantomOnlineHolder.getInstance().addPhantom(phantom);
        phantom.setLoc(location);
        phantom.setHeading(Rnd.get(65535));
        phantom.schedulePhantomSpawn();
    }

    public PhantomTemplate getTemplateForSpawn(ItemTemplate.Grade minGrade, ItemTemplate.Grade maxGrade) {
        for (int i = 0; i < 20; i++) {
            PhantomTemplate template = PhantomHolder.getInstance().getRandomPhantomTemplate(minGrade, maxGrade);
            if (template != null && !PhantomOnlineHolder.getInstance().contains(template.getName()))
                return template;
        }
        log.warn("Can't find free PhantomTemplate! Please add more phantom templates in xml storage." +
                " [min grade: " + minGrade + "] [max grade: " + maxGrade + "].");
        return null;
    }

    private void spawnWave() {
        for (PhantomSpawnTemplate template : PhantomSpawnHolder.getInstance().getSpawns()) {
            for (int i = 0; i < template.getCount(); i++)
                spawnPhantom(template.getTerritory().getRandomLoc(ReflectionManager.DEFAULT.getGeoIndex()),
                        template.getGradeMin(), template.getGradeMax());
        }
    }

    public Future<?> getSpawnWaveTask() {
        return spawnWaveTask;
    }

    public void load() {
        spawnWaveTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SpawnWaveTask(),
                PhantomConfig.firstWaveDelay * 60000, PhantomConfig.waveRespawn * 60000);
        log.info("Phantom System: completely loaded.");
    }

    private class SpawnWaveTask implements Runnable {
        @Override
        public void run() {
            try {
                spawnWave();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
