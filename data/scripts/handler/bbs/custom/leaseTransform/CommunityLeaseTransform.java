package handler.bbs.custom.leaseTransform;
import handler.bbs.ScriptBbsHandler;
import handler.bbs.object.CommunityComponent;
import org.jts.dataparser.data.holder.TransformHolder;
import org.jts.dataparser.data.holder.pcparameter.common.LevelParameter;
import org.jts.dataparser.data.holder.setting.common.PlayerSex;
import org.jts.dataparser.data.holder.transform.TCombat;
import org.jts.dataparser.data.holder.transform.TOptionsSex;
import org.jts.dataparser.data.holder.transform.TransformData;
import org.jts.dataparser.data.holder.transform.type.TransformType;
import org.mmocore.commons.converter.Converter;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.configuration.config.community.CServiceConfig;
import org.mmocore.gameserver.data.xml.holder.custom.community.CLeaseTransformHolder;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.community.enums.DBState;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.custom.community.leaseTransform.BonusTemplate;
import org.mmocore.gameserver.templates.custom.community.leaseTransform.LeaseTransformTemplate;
import org.mmocore.gameserver.templates.custom.community.leaseTransform.TimesTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mangol
 * @since 01.02.2016
 */
public class CommunityLeaseTransform extends ScriptBbsHandler {
	private static final CLeaseTransformHolder holder = CLeaseTransformHolder.getInstance();
	private static final TransformHolder transformHolder = TransformHolder.getInstance();
	private static final int countLeaseStr = 3;

	@Override
	public String[] getBypassCommands() {
		return new String[] { "_bbsLeaseTransform" };
	}

	@Override
	public void onBypassCommand(Player player, String bypass) {
		if(!CServiceConfig.allowLeaseTransform) {
			player.sendMessage(new CustomMessage("scripts.services.off"));
			useSaveCommand(player);
			return;
		}
		if(bypass.startsWith("_bbsLeaseTransform:list")) {
			final String[] val = bypass.split(":");
			if(val.length < 2) {
				player.sendMessage(new CustomMessage("scripts.services.off"));
				useSaveCommand(player);
				return;
			}
			if(val.length == 2) {
				final String html = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/lease/transform/index.htm", player);
				separateAndSend(html, player);
				return;
			}
			else if(val.length < 4) {
				player.sendMessage(new CustomMessage("scripts.services.off"));
				useCommand(player, "_bbsLeaseTransform:list");
				return;
			}
			String html = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/lease/transform/category.htm", player);
			final TransformType type = TransformType.valueOf(val[2].toUpperCase());
			final int pageNumber = Converter.convert(Integer.class, val[3]);
			html = getWritePage(html, pageNumber, player, type);
			separateAndSend(html, player);
		}
		else if(bypass.startsWith("_bbsLeaseTransform:leaseBuy")) {
			final String[] info = bypass.split(":");
			if(info.length < 4) {
				player.sendMessage(new CustomMessage("scripts.services.off"));
				useCommand(player, "_bbsLeaseTransform:list");
				return;
			}
			if(!isCond(player)) {
				useCommand(player, "_bbsLeaseTransform:list");
				return;
			}
			final int idTransform = Converter.convert(Integer.class, info[2]);
			final int keyTime = Converter.convert(Integer.class, info[3]);
			if(!holder.getLeaseTransforms().containsKey(idTransform)) {
				player.sendMessage(new CustomMessage("scripts.services.off"));
				useCommand(player, "_bbsLeaseTransform:list");
				return;
			}
			final LeaseTransformTemplate leaseTransformTemplate = holder.getLeaseTransform(idTransform).get();
			if(!leaseTransformTemplate.getTimes().containsKey(keyTime)) {
				player.sendMessage(new CustomMessage("scripts.services.off"));
				useCommand(player, "_bbsLeaseTransform:list");
				return;
			}

			final TimesTemplate timesTemplate = leaseTransformTemplate.getTimes(keyTime).get();
			if(player.getInventory().getCountOf(timesTemplate.getItemId()) < timesTemplate.getItemCount()) {
				enoughtItem(player, timesTemplate.getItemId(), timesTemplate.getItemCount());
				useCommand(player, "_bbsLeaseTransform:list");
				return;
			}
			if(player.isTransformed()) {
				player.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return;
			}
			if(GeoEngine.getNSWE(player.getX(), player.getY(), player.getZ(), player.getGeoIndex()) != GeoEngine.NSWE_ALL) {
				player.sendPacket(SystemMsg.THE_NEARBY_AREA_IS_TOO_NARROW_FOR_YOU_TO_POLYMORPH);
				return;
			}
			if(player.isBlockTransform()) {
				player.sendPacket(SystemMsg.YOU_ARE_STILL_UNDER_TRANSFORM_PENALTY_AND_CANNOT_BE_POLYMORPHED);
				return;
			}
			if(player.isMounted() || player.isRiding() || player.getMountType() == 2) {
				player.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_PET);
				return;
			}
			if(player.isBuffImmune()) {
				player.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_UNDER_THE_EFFECT_OF_A_SPECIAL_SKILL);
				return;
			}
			if(player.isInBoat()) {
				player.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT);
				return;
			}
			if(player.isSitting()) {
				player.sendPacket(SystemMsg.YOU_CANNOT_TRANSFORM_WHILE_SITTING);
				return;
			}
			// Для трансформации у игрока не должно быть активировано умение Mystic Immunity.
			if(player.getEffectList().getEffectsBySkillId(Skill.SKILL_MYSTIC_IMMUNITY) != null) {
				player.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_UNDER_THE_EFFECT_OF_A_SPECIAL_SKILL);
				return;
			}
			ItemFunctions.removeItem(player, timesTemplate.getItemId(), timesTemplate.getItemCount());
			final CommunityComponent communityComponent = (CommunityComponent) player.getCommunityComponent();
			if(leaseTransformTemplate.getBonus().isPresent()) {
				communityComponent.setLeaseTransformId(leaseTransformTemplate.getId(), DBState.insert);
			}
			final SkillEntry skill = SkillTable.getInstance().getSkillEntry(leaseTransformTemplate.getSkillId(), leaseTransformTemplate.getSkillLevel());
			skill.getEffects(player, player, false, false, timesTemplate.getMinute() * 60000, 0, 5, false);
			useSaveCommand(player);
		}
		else if(bypass.startsWith("_bbsLeaseTransform:lease")) {
			final String[] info = bypass.split(":");
			if(info.length < 3) {
				player.sendMessage(new CustomMessage("scripts.services.off"));
				useCommand(player, "_bbsLeaseTransform:list");
				return;
			}
			final int idTransform = Converter.convert(Integer.class, info[2]);
			final Optional<TransformData> transformData = TransformHolder.getInstance().getTransformId(idTransform);
			if(!holder.getLeaseTransforms().containsKey(idTransform) && !transformData.isPresent()) {
				player.sendMessage(new CustomMessage("scripts.services.off"));
				useCommand(player, "_bbsLeaseTransform:list");
				return;
			}
			final TransformData transform = transformData.get();
			String html;
			if(transform.type == TransformType.COMBAT) {
				html = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/lease/transform/category/combat.htm", player);
				final List<TOptionsSex> begin = getSex(player, transform).get();
				final LeaseTransformTemplate template = holder.getLeaseTransform(idTransform).get();
				final TCombat combat = begin.stream().findFirst().get().combat_begin.stream().findFirst().get();
				int bonusStr = 0;
				int bonusCon = 0;
				int bonusDex = 0;
				int bonusInt = 0;
				int bonusMen = 0;
				int bonusWit = 0;
				if(template.getBonus().isPresent()) {
					final BonusTemplate bonusTemplate = template.getBonus().get();
					bonusStr = bonusTemplate.getStr();
					bonusCon = bonusTemplate.getCon();
					bonusDex = bonusTemplate.getDex();
					bonusInt = bonusTemplate.getInt();
					bonusMen = bonusTemplate.getMen();
					bonusWit = bonusTemplate.getWit();
				}
				final int[] baseStat = combat.basic_stat;
				final double cpRegen = getRegen(player, combat, LevelParameter.cp);
				final double hpRegen = getRegen(player, combat, LevelParameter.hp);
				final double mpRegen = getRegen(player, combat, LevelParameter.mp);
				final int cp = (int) getTable(player, combat, LevelParameter.cp);
				final int hp = (int) getTable(player, combat, LevelParameter.hp);
				final int mp = (int) getTable(player, combat, LevelParameter.mp);
				html = html.replace("<?str?>", getStats(baseStat[0], bonusStr));
				html = html.replace("<?_int?>", getStats(baseStat[1], bonusInt));
				html = html.replace("<?con?>", getStats(baseStat[2], bonusCon));
				html = html.replace("<?dex?>", getStats(baseStat[3], bonusDex));
				html = html.replace("<?wit?>", getStats(baseStat[4], bonusWit));
				html = html.replace("<?men?>", getStats(baseStat[5], bonusMen));
				html = html.replace("<?cpregen?>", String.valueOf(cpRegen));
				html = html.replace("<?hpregen?>", String.valueOf(hpRegen));
				html = html.replace("<?mpregen?>", String.valueOf(mpRegen));
				html = html.replace("<?cp?>", String.valueOf(cp));
				html = html.replace("<?hp?>", String.valueOf(hp));
				html = html.replace("<?mp?>", String.valueOf(mp));
				html = html.replace("<?nameTransform?>", getLeaseName(player, template));
				final String priceStr = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/lease/transform/category/priceMinute.htm", player);
				final StringBuilder builder = new StringBuilder();
				for(final TimesTemplate time : template.getTimesValue()) {
					builder.append(priceStr.replace("<?price_count?>", String.valueOf(time.getItemCount())).replace("<?price_name?>", String.valueOf(getItemName(player.getLanguage(), time.getItemId()))).replace("<?bypass?>", "bypass _bbsLeaseTransform:leaseBuy:" + template.getId() + ":" + time.getKey()).replace("<?minute?>", String.valueOf(time.getMinute())));
				}
				html = html.replace("<?listBuy?>", builder.toString());
				html = html.replace("<?back_bypass?>", "bypass _bbsLeaseTransform:list:combat:1");
			}
			else if(transform.type == TransformType.RIDING_MODE) {
				html = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/lease/transform/category/riding.htm", player);
				final LeaseTransformTemplate template = holder.getLeaseTransform(idTransform).get();
				html = html.replace("<?nameTransform?>", getLeaseName(player, template));
				final String priceStr = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/lease/transform/category/priceMinute.htm", player);
				final StringBuilder builder = new StringBuilder();
				for(final TimesTemplate time : template.getTimesValue()) {
					builder.append(priceStr.replace("<?price_count?>", String.valueOf(time.getItemCount())).replace("<?price_name?>", String.valueOf(getItemName(player.getLanguage(), time.getItemId()))).replace("<?bypass?>", "bypass _bbsLeaseTransform:leaseBuy:" + template.getId() + ":" + time.getKey()).replace("<?minute?>", String.valueOf(time.getMinute())));
				}
				html = html.replace("<?listBuy?>", builder.toString());
				html = html.replace("<?back_bypass?>", "bypass _bbsLeaseTransform:list:riding_mode:1");
			}
			else {
				player.sendMessage(new CustomMessage("scripts.services.off"));
				useCommand(player, "_bbsLeaseTransform:list");
				return;
			}
			separateAndSend(html, player);
		}
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {

	}

	private static boolean isCond(final Player player) {
		if(player == null) {
			return false;
		}
		if(!CServiceConfig.leaseTransformUseEvent && player.getTeam() != TeamType.NONE) {
			player.sendPacket(new CustomMessage("leaseTransform.noEvent"));
			return false;
		}
		if(!CServiceConfig.leaseTransformUseFlag && player.getActiveWeaponFlagAttachment() != null) {
			player.sendPacket(new CustomMessage("leaseTransform.noFlag"));
			return false;
		}
		if(!CServiceConfig.leaseTransformUseOlympiad && player.isInOlympiadMode()) {
			player.sendPacket(new CustomMessage("leaseTransform.noOlympiad"));
			return false;
		}
		if(!CServiceConfig.leaseTransformUseInstance && player.getReflection() != ReflectionManager.DEFAULT) {
			player.sendPacket(new CustomMessage("leaseTransform.noInstance"));
			return false;
		}
		if(!CServiceConfig.leaseTransformUseDuel && player.isInDuel()) {
			player.sendPacket(new CustomMessage("leaseTransform.noDuel"));
			return false;
		}
		if(!CServiceConfig.leaseTransformUseCombat && (player.isInCombat() || player.getPvpFlag() != 0)) {
			player.sendPacket(new CustomMessage("leaseTransform.noCombat"));
			return false;
		}
		if(!CServiceConfig.leaseTransformUseSiege && (player.isOnSiegeField() || player.isInZoneBattle())) {
			player.sendPacket(new CustomMessage("leaseTransform.noSiege"));
			return false;
		}
		if(player.isTransformed()) {
			player.sendPacket(new CustomMessage("leaseTransform.noTransformation"));
			return false;
		}
		if(player.isInWater()) {
			player.sendPacket(new CustomMessage("leaseTransform.noWater"));
			return false;
		}
		return true;
	}

	private static String getStats(final int baseStat, final int bonusStat) {
		final StringBuilder builder = new StringBuilder();
		builder.append(String.valueOf(baseStat));
		if(bonusStat > 0) {
			builder.append("+").append(String.valueOf(bonusStat));
		}
		return builder.toString();
	}

	public static String getWritePage(final String html, final int pageNumber, final Player player, final TransformType type) {
		String htm = html;
		htm = htm.replace("<?numberList?>", String.valueOf(pageNumber));
		switch(type) {
			case COMBAT:
				htm = htm.replace("<?types?>", new CustomMessage("leaseTransform.type.Combats").toString(player));
				break;
			case RIDING_MODE:
				htm = htm.replace("<?types?>", new CustomMessage("leaseTransform.type.Ridings").toString(player));
				break;
		}
		final Map<Integer, LeaseTransformTemplate> leaseTransforms = holder.getLeaseTransforms();
		final int endIndex = (pageNumber * countLeaseStr) - 1;
		final int startIndex = endIndex - (countLeaseStr - 1);
		final List<LeaseTransformTemplate> transformTemplate = new ArrayList<>();
		leaseTransforms.values().stream().filter(t -> transformHolder.getTransformId(t.getId()).get().type == type && (player.getLevel() >= t.getMinLevel() && player.getLevel() <= t.getMaxLevel())).forEach(transformTemplate::add);
		final String lease = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/lease/transform/lease.htm", player);
		if(transformTemplate.size() == 0) {
			htm = htm.replace("<?leases?>", new CustomMessage("leaseTransform.leaseList").toString(player));
		}
		else {
			final StringBuilder leaseBuilder = new StringBuilder();
			for(int i = startIndex; i <= endIndex; i++) {
				if(i > transformTemplate.size() - 1) {
					break;
				}
				final LeaseTransformTemplate transformTemplates = transformTemplate.get(i);
				final String nameLease = getLeaseName(player, transformTemplates);
				final String description = getDescription(player, transformTemplates);
				leaseBuilder.append(lease.replace("<?desc?>", description).replace("<?nameLease?>", nameLease).replace("<?lease_bypass?>", "bypass _bbsLeaseTransform:lease:" + transformTemplates.getId()));
			}
			htm = htm.replace("<?leases?>", leaseBuilder.toString());
		}
		final String bnButton = "bypass _bbsLeaseTransform:list:<?type?>:<?page?>";
		final String finalNextButton = bnButton.replace("<?type?>", type.name().toLowerCase()).replace("<?page?>", String.valueOf(transformTemplate.size() > endIndex ? pageNumber + 1 : pageNumber));
		htm = htm.replace("<?nextPage?>", finalNextButton);
		final String finalBackButton = bnButton.replace("<?type?>", type.name().toLowerCase()).replace("<?page?>", String.valueOf(pageNumber > 1 ? pageNumber - 1 : pageNumber));
		htm = htm.replace("<?backPage?>", finalBackButton);
		return htm;
	}

	private static String getDescription(final Player player, final LeaseTransformTemplate template) {
		if(player.getLanguage() == Language.ENGLISH) {
			return template.getDescriptionEn();
		}
		else {
			return template.getDescriptionRu();
		}
	}

	private static String getLeaseName(final Player player, final LeaseTransformTemplate template) {
		if(player.getLanguage() == Language.ENGLISH) {
			return template.getNameEn();
		}
		else {
			return template.getNameRu();
		}
	}

	private static Optional<List<TOptionsSex>> getSex(final Player player, final TransformData transformData) {
		if(player.getPlayerTemplateComponent().getPlayerSex() == PlayerSex.FEMALE) {
			return Optional.of(transformData.female_begin);
		}
		else if(player.getPlayerTemplateComponent().getPlayerSex() == PlayerSex.MALE) {
			return Optional.of(transformData.male_begin);
		}
		return Optional.empty();
	}

	private static double getRegen(final Player player, final TCombat combat, final LevelParameter type) {
		if(type == LevelParameter.cp) {
			return combat.org_cp_regen[player.getLevel() - 1];
		}
		else if(type == LevelParameter.hp) {
			return combat.org_hp_regen[player.getLevel() - 1];
		}
		else if(type == LevelParameter.mp) {
			return combat.org_mp_regen[player.getLevel() - 1];
		}
		return 0;
	}

	private static double getTable(final Player player, final TCombat combat, final LevelParameter type) {
		if(type == LevelParameter.cp) {
			return combat.cp_table[player.getLevel() - 1];
		}
		else if(type == LevelParameter.hp) {
			return combat.hp_table[player.getLevel() - 1];
		}
		else if(type == LevelParameter.mp) {
			return combat.mp_table[player.getLevel() - 1];
		}
		return 0;
	}
}
