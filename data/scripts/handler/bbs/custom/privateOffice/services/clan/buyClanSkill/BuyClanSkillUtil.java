package handler.bbs.custom.privateOffice.services.clan.buyClanSkill;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.custom.community.BuyClanSkillTemplate;

import java.util.*;

/**
 * @author Mangol
 * @since 05.03.2016
 */
public class BuyClanSkillUtil
{
	protected static List<BuyClanSkillTemplate> getAvaliableList(final Collection<BuyClanSkillTemplate> skillLearns, final Collection<SkillEntry> skills, final int level)
	{
		final Map<Integer, BuyClanSkillTemplate> skillMap = new TreeMap<>();
		final List<BuyClanSkillTemplate> list = new ArrayList<>();
		skillLearns.stream().filter(l -> l.getMinLevel() <= level).forEach(l -> {
			final Optional<SkillEntry> skillEntryId = skills.stream().filter(s -> s.getId() == l.getId()).findFirst();
			if(skillEntryId.isPresent())
			{
				final Optional<SkillEntry> skillEntryLevel = skillEntryId.filter(s -> s.getLevel() == l.getLevel() - 1);
				if(skillEntryLevel.isPresent())
				{
					skillMap.put(l.getId(), l);
				}
			}
			else
			{
				if(l.getLevel() == 1)
				{
					skillMap.put(l.getId(), l);
				}
			}
		});
		list.addAll(skillMap.values());
		return list;
	}
}
