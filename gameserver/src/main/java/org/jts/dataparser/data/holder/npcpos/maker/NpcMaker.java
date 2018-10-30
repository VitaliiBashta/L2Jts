package org.jts.dataparser.data.holder.npcpos.maker;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.EnumValue;
import org.jts.dataparser.data.holder.npcpos.DefaultMaker;
import org.jts.dataparser.data.holder.npcpos.maker.spawn_time.DefaultSpawnTime;

import java.util.List;

/**
 * @author : Camelion
 * @date : 30.08.12  20:20
 */
@ParseSuper
public class NpcMaker extends DefaultMaker {
    @EnumValue
    public InitialSpawn initial_spawn; // Присутствует везде

    public DefaultSpawnTime spawn_time; // Может быть null. Задается через NpcMakerObjectFactory

    @Element(start = "npc_begin", end = "npc_end")
    public List<Npc> npcs;

    public static enum InitialSpawn {
        all, // Остальные значения не известны
    }
}