package handler.onshiftaction;
import handler.onshiftaction.commons.RewardListInfo;
import org.mmocore.commons.pagination.Pagination;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestEventType;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.npc.AggroList;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.PositionUtils;
import org.mmocore.gameserver.utils.Util;

import java.util.*;

/**
 * @author VISTALL
 * @date 2:43/19.08.2011
 */
public class OnShiftAction_NpcInstance extends ScriptOnShiftActionHandler<NpcInstance> {
	@Override
	public Class<NpcInstance> getClazz() {
		return NpcInstance.class;
	}

	@Override
	public boolean call(NpcInstance npc, Player player) {
		if(!AllSettingsConfig.ALLOW_NPC_SHIFTCLICK && !player.isGM()) {
			if(AllSettingsConfig.ALT_GAME_SHOW_DROPLIST) {
				if(npc.isDead()) {
					return false;
				}
				droplist(player, npc, null);
			}
			return false;
		}

		// Для мертвых мобов не показываем табличку, иначе спойлеры плачут
		if(npc.isDead()) {
			return false;
		}

		String dialog;

		if(AllSettingsConfig.ALT_FULL_NPC_STATS_PAGE) {
			dialog = HtmCache.getInstance().getHtml("scripts/actions/player.L2NpcInstance.onActionShift.full.htm", player);
			dialog = dialog.replaceFirst("%class%", String.valueOf(npc.getClass().getSimpleName().replaceFirst("L2", "").replaceFirst("Instance", "")));
			dialog = dialog.replaceFirst("%id%", String.valueOf(npc.getNpcId()));
			dialog = dialog.replaceFirst("%respawn%", String.valueOf(npc.getSpawn() != null ? Util.formatTime(npc.getSpawn().getRespawnDelay()) : "0"));
			dialog = dialog.replaceFirst("%walkSpeed%", String.valueOf(npc.getWalkSpeed()));
			dialog = dialog.replaceFirst("%evs%", String.valueOf(npc.getEvasionRate(null)));
			dialog = dialog.replaceFirst("%acc%", String.valueOf(npc.getAccuracy()));
			dialog = dialog.replaceFirst("%crt%", String.valueOf(npc.getCriticalHit(null, null)));
			dialog = dialog.replaceFirst("%aspd%", String.valueOf(npc.getPAtkSpd()));
			dialog = dialog.replaceFirst("%cspd%", String.valueOf(npc.getMAtkSpd()));
			dialog = dialog.replaceFirst("%currentMP%", String.valueOf(npc.getCurrentMp()));
			dialog = dialog.replaceFirst("%currentHP%", String.valueOf(npc.getCurrentHp()));
			dialog = dialog.replaceFirst("%loc%", "");
			dialog = dialog.replaceFirst("%dist%", String.valueOf((int) npc.getDistance3D(player)));
			dialog = dialog.replaceFirst("%spReward%", String.valueOf(npc.getSpReward()));
			dialog = dialog.replaceFirst("%xyz%", npc.getLoc().x + " " + npc.getLoc().y + ' ' + npc.getLoc().z);
			dialog = dialog.replaceFirst("%ai_type%", npc.getAI().getClass().getSimpleName());
			dialog = dialog.replaceFirst("%direction%", PositionUtils.getDirectionTo(npc, player).toString().toLowerCase());

			StringBuilder b = new StringBuilder("");
			for(Event e : npc.getEvents()) {
				b.append(e.toString()).append(';');
			}
			dialog = dialog.replaceFirst("%event%", b.toString());
		}
		else {
			dialog = HtmCache.getInstance().getHtml("scripts/actions/player.L2NpcInstance.onActionShift.htm", player);
		}

		dialog = dialog.replaceFirst("%name%", nameNpc(npc));
		dialog = dialog.replaceFirst("%id%", String.valueOf(npc.getNpcId()));
		dialog = dialog.replaceFirst("%level%", String.valueOf(npc.getLevel()));
		dialog = dialog.replaceFirst("%respawn%", String.valueOf(npc.getSpawn() != null ? Util.formatTime(npc.getSpawn().getRespawnDelay()) : "0"));
		dialog = dialog.replaceFirst("%factionId%", String.valueOf(npc.getFaction()));
		dialog = dialog.replaceFirst("%aggro%", String.valueOf(npc.getAggroRange()));
		dialog = dialog.replaceFirst("%maxHp%", String.valueOf(npc.getMaxHp()));
		dialog = dialog.replaceFirst("%maxMp%", String.valueOf(npc.getMaxMp()));
		dialog = dialog.replaceFirst("%pDef%", String.valueOf(npc.getPDef(null)));
		dialog = dialog.replaceFirst("%mDef%", String.valueOf(npc.getMDef(null, null)));
		dialog = dialog.replaceFirst("%pAtk%", String.valueOf(npc.getPAtk(null)));
		dialog = dialog.replaceFirst("%mAtk%", String.valueOf(npc.getMAtk(null, null)));
		dialog = dialog.replaceFirst("%expReward%", String.valueOf(npc.getExpReward()));
		dialog = dialog.replaceFirst("%spReward%", String.valueOf(npc.getSpReward()));
		dialog = dialog.replaceFirst("%runSpeed%", String.valueOf(npc.getRunSpeed()));

		// Дополнительная инфа для ГМов
		if(player.isGM()) {
			dialog = dialog.replaceFirst("%AI%", String.valueOf(npc.getAI()) + ",<br1>active: " + npc.getAI().isActive() + ",<br1>intention: " + npc.getAI().getIntention());
		}
		else {
			dialog = dialog.replaceFirst("%AI%", "");
		}

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog);
		player.sendPacket(msg);

		return true;
	}

	@Bypass("actions.OnActionShift:droplist")
	public void droplist(Player player, NpcInstance npc, String[] par) {
		if(player == null || npc == null) {
			return;
		}

		if(AllSettingsConfig.ALT_GAME_SHOW_DROPLIST) {
			RewardListInfo.showInfo(player, npc);
		}
	}

	@Bypass("actions.OnActionShift:stats")
	public void stats(Player player, NpcInstance npc, String[] par) {
		if(player == null || npc == null) {
			return;
		}

		String dialog = HtmCache.getInstance().getHtml("scripts/actions/player.L2NpcInstance.stats.htm", player);
		dialog = dialog.replaceFirst("%name%", nameNpc(npc));
		dialog = dialog.replaceFirst("%level%", String.valueOf(npc.getLevel()));
		dialog = dialog.replaceFirst("%factionId%", String.valueOf(npc.getFaction()));
		dialog = dialog.replaceFirst("%aggro%", String.valueOf(npc.getAggroRange()));
		dialog = dialog.replaceFirst("%race%", getNpcRaceById(npc.getTemplate().getRace().ordinal()));
		dialog = dialog.replaceFirst("%maxHp%", String.valueOf(npc.getMaxHp()));
		dialog = dialog.replaceFirst("%maxMp%", String.valueOf(npc.getMaxMp()));
		dialog = dialog.replaceFirst("%pDef%", String.valueOf(npc.getPDef(null)));
		dialog = dialog.replaceFirst("%mDef%", String.valueOf(npc.getMDef(null, null)));
		dialog = dialog.replaceFirst("%pAtk%", String.valueOf(npc.getPAtk(null)));
		dialog = dialog.replaceFirst("%mAtk%", String.valueOf(npc.getMAtk(null, null)));
		dialog = dialog.replaceFirst("%accuracy%", String.valueOf(npc.getAccuracy()));
		dialog = dialog.replaceFirst("%evasionRate%", String.valueOf(npc.getEvasionRate(null)));
		dialog = dialog.replaceFirst("%criticalHit%", String.valueOf(npc.getCriticalHit(null, null)));
		dialog = dialog.replaceFirst("%runSpeed%", String.valueOf(npc.getRunSpeed()));
		dialog = dialog.replaceFirst("%walkSpeed%", String.valueOf(npc.getWalkSpeed()));
		dialog = dialog.replaceFirst("%pAtkSpd%", String.valueOf(npc.getPAtkSpd()));
		dialog = dialog.replaceFirst("%mAtkSpd%", String.valueOf(npc.getMAtkSpd()));

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog);
		player.sendPacket(msg);
	}

	@Bypass("actions.OnActionShift:quests")
	public void quests(Player player, NpcInstance npc, String[] par) {
		if(player == null || npc == null) {
			return;
		}

		StringBuilder dialog = new StringBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(nameNpc(npc)).append("<br></font></center><br>");

		Map<QuestEventType, Quest[]> list = npc.getTemplate().getQuestEvents();
		for(Map.Entry<QuestEventType, Quest[]> entry : list.entrySet()) {
			for(Quest q : entry.getValue()) {
				dialog.append(entry.getKey()).append(' ').append(q.getClass().getSimpleName()).append("<br1>");
			}
		}

		dialog.append("</body></html>");

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog.toString());
		player.sendPacket(msg);
	}

	@Bypass("actions.OnActionShift:skills")
	public void skills(Player player, NpcInstance npc, String[] par) {
		if(player == null || npc == null) {
			return;
		}

		StringBuilder dialog = new StringBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(nameNpc(npc)).append("<br></font></center>");

		Collection<SkillEntry> list = npc.getAllSkills();
		if(list != null && !list.isEmpty()) {
			dialog.append("<br>Active:<br>");
			for(SkillEntry s : list) {
				if(s.getTemplate().isActive()) {
					dialog.append(s.getName()).append("<br1>");
				}
			}
			dialog.append("<br>Passive:<br>");
			for(SkillEntry s : list) {
				if(!s.getTemplate().isActive()) {
					dialog.append(s.getName()).append("<br1>");
				}
			}
		}

		dialog.append("</body></html>");

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog.toString());
		player.sendPacket(msg);
	}

	@Bypass("actions.OnActionShift:effects")
	public void effects(Player player, NpcInstance npc, String[] par) {
		if(player == null || npc == null) {
			return;
		}

		StringBuilder dialog = new StringBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(nameNpc(npc)).append("<br></font></center><br>");

		List<Effect> list = npc.getEffectList().getAllEffects();
		if(!list.isEmpty()) {
			for(Effect e : list) {
				dialog.append(e.getSkill().getName()).append("<br1>");
			}
		}

		dialog.append("<br><center><button value=\"");
		dialog.append(player.isLangRus() ? "Обновить" : "Refresh");
		dialog.append("\" action=\"bypass -h htmbypass_actions.OnActionShift:effects\" width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" /></center></body></html>");

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog.toString());
		player.sendPacket(msg);
	}

	@Bypass("actions.OnActionShift:resists")
	public void resists(Player player, NpcInstance npc, String[] par) {
		if(player == null || npc == null) {
			return;
		}

		StringBuilder dialog = new StringBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(nameNpc(npc)).append("<br></font></center><table width=\"80%\">");

		boolean hasResist;

		hasResist = addResist(dialog, "Fire", npc.calcStat(Stats.DEFENCE_FIRE, 0, null, null));
		hasResist |= addResist(dialog, "Wind", npc.calcStat(Stats.DEFENCE_WIND, 0, null, null));
		hasResist |= addResist(dialog, "Water", npc.calcStat(Stats.DEFENCE_WATER, 0, null, null));
		hasResist |= addResist(dialog, "Earth", npc.calcStat(Stats.DEFENCE_EARTH, 0, null, null));
		hasResist |= addResist(dialog, "Light", npc.calcStat(Stats.DEFENCE_HOLY, 0, null, null));
		hasResist |= addResist(dialog, "Darkness", npc.calcStat(Stats.DEFENCE_UNHOLY, 0, null, null));
		hasResist |= addResist(dialog, "Bleed", npc.calcStat(Stats.BLEED_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Poison", npc.calcStat(Stats.POISON_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Stun", npc.calcStat(Stats.STUN_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Root", npc.calcStat(Stats.ROOT_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Sleep", npc.calcStat(Stats.SLEEP_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Paralyze", npc.calcStat(Stats.PARALYZE_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Mental", npc.calcStat(Stats.MENTAL_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Debuff", npc.calcStat(Stats.DEBUFF_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Cancel", npc.calcStat(Stats.CANCEL_RESIST, 0, null, null));
		hasResist |= addResist(dialog, "Sword", 100 - npc.calcStat(Stats.SWORD_WPN_VULNERABILITY, null, null));
		hasResist |= addResist(dialog, "Dual Sword", 100 - npc.calcStat(Stats.DUAL_WPN_VULNERABILITY, null, null));
		hasResist |= addResist(dialog, "Blunt", 100 - npc.calcStat(Stats.BLUNT_WPN_VULNERABILITY, null, null));
		hasResist |= addResist(dialog, "Dagger", 100 - npc.calcStat(Stats.DAGGER_WPN_VULNERABILITY, null, null));
		hasResist |= addResist(dialog, "Bow", 100 - npc.calcStat(Stats.BOW_WPN_VULNERABILITY, null, null));
		hasResist |= addResist(dialog, "Crossbow", 100 - npc.calcStat(Stats.CROSSBOW_WPN_VULNERABILITY, null, null));
		hasResist |= addResist(dialog, "Polearm", 100 - npc.calcStat(Stats.POLE_WPN_VULNERABILITY, null, null));
		hasResist |= addResist(dialog, "Fist", 100 - npc.calcStat(Stats.FIST_WPN_VULNERABILITY, null, null));

		if(!hasResist) {
			dialog.append("</table>No resists</body></html>");
		}
		else {
			dialog.append("</table></body></html>");
		}

		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog.toString());
		player.sendPacket(msg);
	}

	@Bypass("actions.OnActionShift:aggro")
	public void aggro(Player player, NpcInstance npc, String[] par) {
		if(player == null || npc == null)
			return;
		String dialog = HtmCache.getInstance().getHtml("scripts/OnActionShift/aggro.htm", player);

		StringBuilder playersBuilder = new StringBuilder();

		Set<AggroList.HateInfo> set = new TreeSet<>(AggroList.DamageComparator.getInstance().reversed());
		set.addAll(npc.getAggroList().getCharMap().values());
		int page = (par.length >= 1) && (par[0].matches("\\d+")) ? Integer.parseInt(par[0]) : 0;
		Pagination<AggroList.HateInfo> pagination = new Pagination<>(new ArrayList<>(set), 10).setPage(page);
		page = pagination.getPage();
		int prevPage = !pagination.isFirst() ? pagination.getPreviousPage() : pagination.getPage();
		int nextPage = !pagination.isLast() ? pagination.getNextPage() : pagination.getLastPage();
		for(AggroList.HateInfo aggroInfo : pagination.getItems()) {
			playersBuilder.append("<tr><td>").append(aggroInfo.attacker.getName()).append("</td><td>").append(aggroInfo.damage).append("</td><td>").append(aggroInfo.hate).append("</td></tr>");
		}
		dialog = dialog.replace("<?players?>", playersBuilder.toString());
		dialog = dialog.replace("<?page?>", String.valueOf(page));
		dialog = dialog.replace("<?nextPage?>", String.valueOf(nextPage));
		dialog = dialog.replace("<?prevPage?>", String.valueOf(prevPage));
		dialog = dialog.replace("<?curPage?>", String.valueOf(pagination.getPageNumber()));
		dialog = dialog.replace("<?lastPage?>", String.valueOf(pagination.isNecessary() ? pagination.getPageCount() : 1));
		HtmlMessage msg = new HtmlMessage(npc);
		msg.setHtml(dialog);
		player.sendPacket(msg);
	}

	private boolean addResist(StringBuilder dialog, String name, double val) {
		if(val == 0) {
			return false;
		}

		dialog.append("<tr><td>").append(name).append("</td><td>");
		if(val == Double.POSITIVE_INFINITY) {
			dialog.append("MAX");
		}
		else if(val == Double.NEGATIVE_INFINITY) {
			dialog.append("MIN");
		}
		else {
			dialog.append(String.valueOf((int) val));
			dialog.append("</td></tr>");
			return true;
		}

		dialog.append("</td></tr>");
		return true;
	}

	public String getNpcRaceById(int raceId) {
		switch(raceId) {
			case 1:
				return "Undead";
			case 2:
				return "Magic Creatures";
			case 3:
				return "Beasts";
			case 4:
				return "Animals";
			case 5:
				return "Plants";
			case 6:
				return "Humanoids";
			case 7:
				return "Spirits";
			case 8:
				return "Angels";
			case 9:
				return "Demons";
			case 10:
				return "Dragons";
			case 11:
				return "Giants";
			case 12:
				return "Bugs";
			case 13:
				return "Fairies";
			case 14:
				return "Humans";
			case 15:
				return "Elves";
			case 16:
				return "Dark Elves";
			case 17:
				return "Orcs";
			case 18:
				return "Dwarves";
			case 19:
				return "Others";
			case 20:
				return "Non-living Beings";
			case 21:
				return "Siege Weapons";
			case 22:
				return "Defending Army";
			case 23:
				return "Mercenaries";
			case 24:
				return "Unknown Creature";
			case 25:
				return "Kamael";
			default:
				return "Not defined";
		}
	}

	private String nameNpc(NpcInstance npc) {
		if(npc.getNameNpcString() == NpcString.NONE) {
			return HtmlUtils.htmlNpcName(npc.getNpcId());
		}
		else {
			return HtmlUtils.htmlNpcString(npc.getNameNpcString().getId(), npc.getName());
		}
	}
}
