package org.jts.dataparser.data.holder.transform;

import org.jts.dataparser.data.annotations.array.DoubleArray;
import org.jts.dataparser.data.annotations.array.IntArray;

/**
 * Create by Mangol on 02.10.2015.
 */
public class TCombat {
    @IntArray
    public int[] basic_stat; // str;int;con;dex;wit;men
    @IntArray
    public int[] base_defend; // Chest,Legs,Pitch,Boots,Glooves,Underwear,Cloak
    @IntArray
    public int[] base_magic_defend;
    @DoubleArray
    public double[] org_hp_regen;
    @DoubleArray
    public double[] org_mp_regen;
    @DoubleArray
    public double[] org_cp_regen;
    @DoubleArray
    public double[] level_bonus;
    @IntArray
    public int[] str_bonus;
    @IntArray
    public int[] int_bonus;
    @IntArray
    public int[] con_bonus;
    @IntArray
    public int[] dex_bonus;
    @IntArray
    public int[] wit_bonus;
    @IntArray
    public int[] men_bonus;
    @DoubleArray
    public double[] hp_table;
    @DoubleArray
    public double[] mp_table;
    @DoubleArray
    public double[] cp_table;

    public void setStr(final int val) {
        basic_stat[0] += val;
    }

    public void setInt(final int val) {
        basic_stat[1] += val;
    }

    public void setCon(final int val) {
        basic_stat[2] += val;
    }

    public void setDex(final int val) {
        basic_stat[3] += val;
    }

    public void setWit(final int val) {
        basic_stat[4] += val;
    }

    public void setMen(final int val) {
        basic_stat[5] += val;
    }

    @Override
    public TCombat clone() {
        final TCombat combat = new TCombat();
        combat.basic_stat = basic_stat.clone();
        combat.base_defend = base_defend.clone();
        combat.base_magic_defend = base_magic_defend.clone();
        combat.org_hp_regen = org_hp_regen.clone();
        combat.org_mp_regen = org_mp_regen.clone();
        combat.org_cp_regen = org_cp_regen.clone();
        combat.level_bonus = level_bonus.clone();
        combat.str_bonus = str_bonus.clone();
        combat.int_bonus = int_bonus.clone();
        combat.con_bonus = con_bonus.clone();
        combat.dex_bonus = dex_bonus.clone();
        combat.wit_bonus = wit_bonus.clone();
        combat.men_bonus = men_bonus.clone();
        combat.hp_table = hp_table.clone();
        combat.mp_table = mp_table.clone();
        combat.cp_table = cp_table.clone();
        return combat;
    }
}
