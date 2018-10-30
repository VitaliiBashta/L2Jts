package org.jts.dataparser.data.holder.npcpos.maker_ex;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.ObjectValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.holder.NpcPosHolder;
import org.jts.dataparser.data.holder.npcpos.DefaultMaker;
import org.jts.dataparser.data.holder.npcpos.common.AIParameters;

import java.util.List;

/**
 * @author : Camelion
 * @date : 30.08.12  21:15
 */
@ParseSuper
public class NpcMakerEx extends DefaultMaker {
    @StringValue
    private String ai;
    @ObjectValue(dotAll = false, objectFactory = NpcPosHolder.AiParamsObjectFactory.class)
    private final AIParameters ai_parameters = new AIParameters(); // Присутствует всегда, params может быть пустым
    @Element(start = "npc_ex_begin", end = "npc_ex_end")
    private List<NpcEx> npcs;

    public String getAI() {
        return ai;
    }

    public AIParameters getAIParameters() {
        return ai_parameters;
    }

    public List<NpcEx> getNpcs() {
        return npcs;
    }
}