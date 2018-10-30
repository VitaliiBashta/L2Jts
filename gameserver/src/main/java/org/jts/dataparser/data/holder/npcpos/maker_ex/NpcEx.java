package org.jts.dataparser.data.holder.npcpos.maker_ex;

import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.ObjectValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.holder.NpcPosHolder;
import org.jts.dataparser.data.holder.npcpos.common.AIParameters;
import org.jts.dataparser.data.holder.npcpos.common.DefaultMakerNpc;

/**
 * @author : Camelion
 * @date : 30.08.12  22:13
 */
@ParseSuper
public class NpcEx extends DefaultMakerNpc {
    @StringValue
    public String nickname; // Данная строка не задействована нигде кроме этого файла
    @ObjectValue(canBeNull = true, objectFactory = NpcPosHolder.AiParamsObjectFactory.class)
    public AIParameters ai_parameters = new AIParameters(); // Присутствует всегда, params может быть пустым
}
