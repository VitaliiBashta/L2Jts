package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.model.reward.RewardGroup;
import org.mmocore.gameserver.model.reward.RewardList;
import org.mmocore.gameserver.model.reward.RewardType;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VISTALL
 * @date 16:15/14.12.2010
 */
public final class NpcHolder extends AbstractHolder {
    private static final NpcHolder INSTANCE = new NpcHolder();

    private final TIntObjectHashMap<NpcTemplate> npcs = new TIntObjectHashMap<>(20000);
    private TIntObjectHashMap<List<NpcTemplate>> npcsByLevel;
    private NpcTemplate[] allTemplates;
    private Map<String, NpcTemplate> npcsNames;

    private NpcHolder() {
    }

    public static NpcHolder getInstance() {
        return INSTANCE;
    }

    public void addTemplate(final NpcTemplate template) {
        if (npcs.contains(template.npcId)) {
            _log.warn("NPC redefined: " + template.npcId);
        }
/*		double lvlMod = PCParameterHolder.getInstance().getLevelBonus().returnValue(template.level);
		double conMod = PCParameterHolder.getInstance().getConBonus().returnValue(template.getBaseAttr().getCON());
		double menMod = PCParameterHolder.getInstance().getMenBonus().returnValue(template.getBaseAttr().getMEN());
		double strMod = PCParameterHolder.getInstance().getStrBonus().returnValue(template.getBaseAttr().getSTR());
		double intMod = PCParameterHolder.getInstance().getIntBonus().returnValue(template.getBaseAttr().getINT());
		template.setBaseHpMax(template.getBaseHpMax() * conMod);
		template.setBaseMpMax(template.getBaseMpMax() * menMod);
		template.setBasePAtk(template.getBasePAtk() * strMod * lvlMod);
		template.setBaseMAtk(template.getBaseMAtk() * intMod * intMod * lvlMod * lvlMod);
		template.setBasePDef(template.getBasePDef() * lvlMod);
		template.setBaseMDef(template.getBaseMDef() * menMod * lvlMod);
		if(template.npcId == 25697)
		{
			System.out.println(template.getBasePAtk());
		}*/
        npcs.put(template.npcId, template);
    }

    public NpcTemplate getTemplate(final int id) {
        final NpcTemplate npc = ArrayUtils.valid(allTemplates, id);
        if (npc == null) {
            warn("Not defined npc id : " + id + ", or out of range!", null);// new Exception());
        }

        return npc;
    }

    public NpcTemplate getTemplateByName(final String name) {
        return npcsNames.get(name.toLowerCase());
    }

    public List<NpcTemplate> getAllOfLevel(final int lvl) {
        return npcsByLevel.get(lvl);
    }

    public NpcTemplate[] getAll() {
        return npcs.values(new NpcTemplate[npcs.size()]);
    }

    private void buildFastLookupTable() {
        npcsByLevel = new TIntObjectHashMap<>();
        npcsNames = new HashMap<>();

        int highestId = 0;
        for (final int id : npcs.keys()) {
            if (id > highestId) {
                highestId = id;
            }
        }

        allTemplates = new NpcTemplate[highestId + 1];
        for (TIntObjectIterator<NpcTemplate> iterator = npcs.iterator(); iterator.hasNext(); ) {
            iterator.advance();
            final int npcId = iterator.key();
            final NpcTemplate npc = iterator.value();

            allTemplates[npcId] = npc;

            List<NpcTemplate> byLevel;
            if ((byLevel = npcsByLevel.get(npc.level)) == null) {
                npcsByLevel.put(npcId, byLevel = new ArrayList<>());
            }
            byLevel.add(npc);

            npcsNames.put(npc.name.toLowerCase(), npc);
        }
    }

    @Override
    protected void process() {
        buildFastLookupTable();
    }

    @Override
    public int size() {
        return npcs.size();
    }

    @Override
    public void clear() {
        npcsNames.clear();
        npcs.clear();
    }

    public void addEventDrop(final RewardList eventDrop) {
        for (final NpcTemplate npc : allTemplates) {
            if (npc != null && !npc.getRewards().isEmpty()) {
                loop:
                for (final RewardList rl : npc.getRewards().values()) {
                    for (final RewardGroup rg : rl) {
                        if (!rg.isAdena()) {
                            npc.getRewards().put(RewardType.EVENT, eventDrop);
                            break loop;
                        }
                    }
                }
            }
        }
    }

    public void removeEventDrop() {
        for (final NpcTemplate npc : allTemplates) {
            if (npc != null && !npc.getRewards().isEmpty()) {
                npc.getRewards().remove(RewardType.EVENT);
            }
        }
    }
}
