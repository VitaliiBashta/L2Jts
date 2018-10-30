package org.mmocore.gameserver.skills.effects;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ItemFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Mangol
 * @author : Java-man
 * @author : Rognarok
 * @date : 23.04.14  12:21
 */
public class Effect_i_restoration_random extends Effect {
    private static final Pattern effectPattern = Pattern.compile("\\{(\\S+)\\}");
    private static final Pattern groupPattern = Pattern.compile("\\{\\[([\\d:;]+?)\\]([\\d.e-]+)\\}");
    private final List<List<Item>> items;
    private final String[] capsule;
    private final double[] chances;
    private int capsule_itemId;
    private long capsule_count;

    public Effect_i_restoration_random(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        final String cp = template.getParam().getString("action_capsule", null);
        capsule = cp != null ? cp.split(";") : null;
        if (capsule != null) {
            capsule_itemId = Integer.parseInt(capsule[0]);
            capsule_count = Long.parseLong(capsule[1]);
        }
        final String[] groups = getTemplate().getParam().getString("extract").split(";");
        items = new ArrayList<List<Item>>(groups.length);
        chances = new double[groups.length];
        double prevChance = 0;
        final Matcher e = effectPattern.matcher(getTemplate().getParam().getString("extract"));
        int i = 0;
        if (e.find()) {
            final String groupsE = e.group(1);
            final Matcher groupM = groupPattern.matcher(groupsE);
            while (groupM.find()) {
                final String its = groupM.group(1);
                final List<Item> list = new ArrayList<>(its.split(";").length);
                for (final String item : its.split(";")) {
                    final String id = item.split(":")[0];
                    final String count = item.split(":")[1];
                    final Item it = new Item();
                    it.itemId = Integer.parseInt(id);
                    it.count = Long.parseLong(count);
                    list.add(it);
                }
                final double chance = Double.parseDouble(groupM.group(2));
                items.add(i, list);
                chances[i] = prevChance + chance;
                prevChance = chances[i];
                i++;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        final Playable playable = (Playable) getEffector();
        double chance = (double) Rnd.get(0, 1000000) / 10000;
        double prevChance = 0.0D;
        int i;
        for (i = 0; i < chances.length; i++) {
            if (chance > prevChance && chance < chances[i]) {
                break;
            }
        }
        if (playable != null && capsule != null) {
            ItemFunctions.removeItem(playable, capsule_itemId, capsule_count, true);
        }
        if (i < chances.length) {
            final List<Item> itemList = items.get(i);
            for (final Item item : itemList) {
                ItemFunctions.addItem(playable, item.itemId, item.count, true);
            }
        } else {
            getEffected().sendPacket(SystemMsg.THERE_WAS_NOTHING_FOUND_INSIDE);
        }
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }

    private final class Item {
        public int itemId;
        public long count;
    }
}
