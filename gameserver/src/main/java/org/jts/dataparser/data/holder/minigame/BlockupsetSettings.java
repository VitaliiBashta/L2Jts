package org.jts.dataparser.data.holder.minigame;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.array.StringArray;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.ObjectValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.common.ItemName_Count;
import org.jts.dataparser.data.common.Point3;
import org.jts.dataparser.data.common.Point4;

import java.util.List;

/**
 * @author : Camelion
 * @date : 30.08.12 13:34
 */
public class BlockupsetSettings {
    @IntValue
    public int game_period;
    @StringArray
    public String[] blue_enter_skill;
    @StringArray
    public String[] red_enter_skill;
    @StringArray
    public String[] waiting_skill;
    @ObjectValue
    public ItemName_Count default_reward;
    @ObjectValue
    public WinnerReward winner_reward;
    @StringArray
    public String[] delete_items_after_match;
    @Element(start = "blockupset_stage_begin", end = "blockupset_stage_end")
    public List<BlockupsetStage> stages;

    public static class BlockupsetStage {
        @IntValue
        public int stage;
        @ObjectArray
        public Point4[] blockupset_zone_territory;
        @Element(start = "red_start_point_begin", end = "red_start_point_end")
        public PointList red_start_point;
        @Element(start = "blue_start_point_begin", end = "blue_start_point_end")
        public PointList blue_start_point;
        @Element(start = "red_banish_point_begin", end = "red_banish_point_end")
        public PointList red_banish_point;
        @Element(start = "blue_banish_point_begin", end = "blue_banish_point_end")
        public PointList blue_banish_point;
    }

    public static class WinnerReward {
        @StringValue(withoutName = true)
        public String item_name;
        @IntValue(withoutName = true)
        public int count;
        @IntValue(withoutName = true)
        public int unknown;
    }

    public static class PointList {
        @ObjectValue(name = "point")
        public List<Point3> points;
    }
}
