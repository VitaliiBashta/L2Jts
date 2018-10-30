package org.jts.dataparser.data.holder.instantzonedata.entrance_cond;

import org.jts.dataparser.data.annotations.value.EnumValue;
import org.jts.dataparser.data.annotations.value.IntValue;

/**
 * @author : Camelion
 * @date : 27.08.12  15:18
 */
public class DefaultEntranceCond {
    @EnumValue(withoutName = true)
    public EntranceCond cond; // Проверять уровень, или проверять квест

    @IntValue(withoutName = true)
    public int cond_param; // Неизвестно (принимает значения 0 или 1)

    public static enum EntranceCond {
        check_level, check_quest
    }
}
