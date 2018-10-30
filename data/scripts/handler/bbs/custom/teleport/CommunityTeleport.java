package handler.bbs.custom.teleport;
import handler.bbs.ScriptBbsHandler;
import handler.bbs.dao.teleport.CommunityTeleportDAO;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.configuration.config.community.CTeleportConfig;
import org.mmocore.gameserver.data.xml.holder.custom.community.CTeleportHolder;
import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ConfirmDlg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.community.TeleportPoint;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.Language;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Util;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author L2CCCP
 * @author Mangol
 */
public class CommunityTeleport extends ScriptBbsHandler
{
	private static final CommunityTeleportDAO dao = CommunityTeleportDAO.getInstance();
	private static final CTeleportHolder teleportHolder = CTeleportHolder.getInstance();

	@Override
	public String[] getBypassCommands()
	{
		return new String[] { "_bbsteleport" };
	}

	@Override
	public void onBypassCommand(final Player player, final String bypass)
	{
		if(!CTeleportConfig.allowTeleport)
		{
			player.sendMessage(new CustomMessage("scripts.services.off"));
			useCommand(player, "_bbshome");
			return;
		}
		String html = "";
		if(bypass.equals("_bbsteleport"))
		{
			html = getCache().getHtml(CBasicConfig.BBS_PATH + "/teleport/index.htm", player);
		}
		else if(bypass.startsWith("_bbsteleport:page"))
		{
			final String[] path = bypass.split(":");
			if(path.length > 3)
			{
				html = getCache().getHtml(CBasicConfig.BBS_PATH + "/teleport/" + path[2] + "/" + path[3] + ".htm", player);
			}
			else
			{
				html = getCache().getHtml(CBasicConfig.BBS_PATH + "/teleport/" + path[2] + ".htm", player);
			}
		}
		else
		{
			if(bypass.equals("_bbsteleport:save_page"))
			{
				showTeleportPoint(player);
				return;
			}
			if(bypass.startsWith("_bbsteleport:delete"))
			{
				dao.deleteTeleportPoint(player, Integer.parseInt(bypass.split(":")[2]));
				showTeleportPoint(player);
				return;
			}
			if(bypass.startsWith("_bbsteleport:save"))
			{
				String point = "";
				final String[] next = bypass.split(" ");
				if(next.length > 1)
				{
					for(int i = 1; i < next.length; i++)
					{
						point += " " + next[i];
					}
				}
				if(point.length() > 0)
				{
					addTeleportPoint(player, point);
				}
				showTeleportPoint(player);
				return;
			}
			if(bypass.startsWith("_bbsteleport:go"))
			{
				html = getCache().getHtml(CBasicConfig.BBS_PATH + "/teleport/index.htm", player);
				final String[] cord = bypass.split(":");
				final int x = Integer.parseInt(cord[2]);
				final int y = Integer.parseInt(cord[3]);
				final int z = Integer.parseInt(cord[4]);
				goTeleportPoint(player, new Location(x, y, z));
			}
			else if(bypass.startsWith("_bbsteleport:id"))
			{
				final int id = Integer.parseInt(bypass.split(":")[2]);
				final Optional<TeleportPoint> point = teleportHolder.getTeleportId(id);
				if(point.isPresent())
				{
					goTeleportId(player, point.get());
				}
				html = getCache().getHtml(CBasicConfig.BBS_PATH + "/teleport/index.htm", player);
			}
		}
		separateAndSend(html, player);
	}

	private void goTeleportId(final Player player, final TeleportPoint teleportPoint)
	{
		final int level = player.getLevel();
		final String name = teleportPoint.getName();
		final int priceId = teleportPoint.getPriceId();
		final int count = teleportPoint.getPriceCount();
		final int minLevel = teleportPoint.getMinLevel();
		final int maxLevel = teleportPoint.getMaxLevel();
		final boolean pk = teleportPoint.isPk();
		final boolean premium = teleportPoint.isPremium();
		final int premiumPriceId = teleportPoint.getPremiumPriceId();
		final int premiumCount = teleportPoint.getPremiumPriceCount();
		final Location location = teleportPoint.getLocation();
		final boolean isConfirm = teleportPoint.isConfirm();
		if(level < minLevel || level > maxLevel)
		{
			player.sendMessage(new CustomMessage("teleport.point.level.min.max").addNumber(minLevel).addNumber(maxLevel));
			return;
		}
		if(pk && player.getKarma() > 0)
		{
			player.sendMessage(new CustomMessage("teleport.point.pk.denied"));
			return;
		}
		if(premium && !player.getPremiumAccountComponent().hasBonus())
		{
			player.sendMessage(new CustomMessage("teleport.point.only.premium"));
			return;
		}
		if(!checkFirstConditions(player))
		{
			return;
		}
		final int item = player.getPremiumAccountComponent().hasBonus() ? premiumPriceId : priceId;
		final int price = player.getPremiumAccountComponent().hasBonus() ? premiumCount : count;
		final boolean freeLevel = player.getLevel() <= CTeleportConfig.freeLevelTeleport;
		if (isConfirm) {
			teleportByAsk(player, teleportPoint, item, price);
		} else if(getPay(player, item, freeLevel ? 0 : price, true)) {
			player.teleToLocation(location);
			player.sendMessage(new CustomMessage("teleport.point.success.location").addString(name));
		}
	}

	private void teleportByAsk(Player player, TeleportPoint tp, int priceId, int priceCount) {
		ConfirmDlg ask = new ConfirmDlg(SystemMsg.S1, 30000);
		String itemName = Util.getItemName(priceId);
		ask.addString(player.isLangRus() ? "Желаете ли вы телепортироваться за " + priceCount + " " + itemName + "?"
				: "Do you want to teleport for " + priceCount + " " + itemName + "?");
		player.ask(ask, new OnAnswerListener() {
			@Override
			public void sayYes() {
				if(getPay(player, priceId, priceCount, true))
				{
					player.teleToLocation(tp.getLocation());
					player.sendMessage(new CustomMessage("teleport.point.success.location").addString(tp.getName()));
				}
			}
		});
	}

	private void goTeleportPoint(final Player player, final Location location)
	{
		if(!checkFirstConditions(player))
		{
			return;
		}
		player.teleToLocation(location);
	}

	private void showTeleportPoint(final Player player)
	{
		if(CTeleportConfig.pointForPremium && !player.getPremiumAccountComponent().hasBonus())
		{
			player.sendMessage(new CustomMessage("teleport.personal.point.only.premium"));
			useCommand(player, "_bbsteleport");
			return;
		}
		final String template = getCache().getHtml(CBasicConfig.BBS_PATH + "/teleport/template.htm", player);
		String block;
		String points = "";
		final Collection<TeleportPoint> locationsList = player.getCommunityComponent().getTeleportPoints().values();
		int counter = 0;
		for(final TeleportPoint loc : locationsList)
		{
			if(counter % 2 == 0)
			{
				counter = 0;
				points += "</td></tr><tr><td>";
			}
			else
			{
				points += "</td><td>";
			}
			block = template;
			block = block.replace("{name}", loc.getName());
			block = block.replace("{id}", String.valueOf(loc.getId()));
			block = block.replace("{x}", String.valueOf(loc.getLocation().getX()));
			block = block.replace("{y}", String.valueOf(loc.getLocation().getY()));
			block = block.replace("{z}", String.valueOf(loc.getLocation().getZ()));
			points += block;
			counter++;
		}
		String content = getCache().getHtml(CBasicConfig.BBS_PATH + "/teleport/save.htm", player);
		final String emptyEng = "<center><font color=\"FF0000\">You don't have points!</font></center>";
		final String emptyRu = "<center><font color=\"FF0000\">У вас нет сохраненных точек!</font></center>";
		content = content.replace("{points}", points.equals("") ? (player.getLanguage() == Language.ENGLISH ? emptyEng : emptyRu) : points);
		content = content.replace("{all_price}", Util.formatAdena(CTeleportConfig.savePrice) + " " + getItemName(player.getLanguage(), CTeleportConfig.saveItemId));
		content = content.replace("{premium_price}", Util.formatAdena(CTeleportConfig.premiumSavePrice) + " " + getItemName(player.getLanguage(), CTeleportConfig.premiumSaveItemId));
		content = content.replace("{point_count}", String.valueOf(CTeleportConfig.maxPointsCount));
		separateAndSend(content, player);
	}

	private void addTeleportPoint(final Player player, final String point)
	{
		if(!checkFirstConditions(player))
		{
			return;
		}
		if(CTeleportConfig.pointForPremium && !player.getPremiumAccountComponent().hasBonus())
		{
			player.sendMessage(new CustomMessage("teleport.personal.point.only.premium"));
			return;
		}
		if(player.isMovementDisabled() || player.isOutOfControl())
		{
			player.sendMessage(new CustomMessage("teleport.personal.point.outofcontrol"));
			return;
		}
		if(player.isInZone(ZoneType.battle_zone) || player.isInZone(ZoneType.no_escape)
				|| player.isInZone(ZoneType.epic) || player.isInZone(ZoneType.SIEGE)
				|| player.isInZone(ZoneType.RESIDENCE) || player.getPlayerVariables().getBoolean(PlayerVariables.JAILED))
		{
			player.sendMessage(new CustomMessage("teleport.personal.point.forbidden.zone"));
			return;
		}
		if (CTeleportConfig.disallowSavePointsInAnyZone) {
			final List<Zone> zones = new ArrayList<>();
			World.getZones(zones, player.getLoc(), player.getReflection());
			for (Zone zone : zones)
				if (zone.getType() != ZoneType.FISHING) {
					player.sendMessage(new CustomMessage("teleport.personal.point.forbidden.zone"));
					return;
				}
		}
		final int itemId = player.getPremiumAccountComponent().hasBonus() ? CTeleportConfig.premiumSaveItemId : CTeleportConfig.saveItemId;
		final int price = player.getPremiumAccountComponent().hasBonus() ? CTeleportConfig.premiumSavePrice : CTeleportConfig.savePrice;
		final int teleportCount = dao.selectCountTeleportPoint(player.getObjectId());
		if(teleportCount < CTeleportConfig.maxPointsCount)
		{
			if(price == 0)
			{
				dao.selectCountToInsert(player, point);
			}
			else if(player.getInventory().getCountOf(itemId) >= price)
			{
				player.getInventory().destroyItemByItemId(itemId, price);
				dao.selectCountToInsert(player, point);
			}
			else
			{
				enoughtItem(player, itemId, price);
			}
		}
		else
		{
			player.sendMessage(new CustomMessage("teleport.personal.point.max").addNumber(CTeleportConfig.maxPointsCount));
		}
	}

	private boolean checkFirstConditions(final Player player)
	{
		if(player == null || player.isDead())
		{
			return false;
		}
		if(player.isMovementDisabled() || player.isOutOfControl())
		{
			player.sendMessage(new CustomMessage("teleport.personal.point.outofcontrol"));
			return false;
		}
		if(player.getActiveWeaponFlagAttachment() != null)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
			return false;
		}
		if(player.isInOlympiadMode())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_IN_AN_OLYMPIAD_MATCH);
			return false;
		}
		if(!CTeleportConfig.inInstance && player.getReflection() != ReflectionManager.DEFAULT)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_IN_AN_INSTANT_ZONE);
			return false;
		}
		if(player.isInDuel())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_DUEL);
			return false;
		}
		if(!CTeleportConfig.inPvp && player.getPvpFlag() > 0 || !CTeleportConfig.inCombat && player.isInCombat())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE);
			return false;
		}
		if(!CTeleportConfig.inSiege && (player.isOnSiegeField() || player.isInZoneBattle()))
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_A_LARGESCALE_BATTLE_SUCH_AS_A_CASTLE_SIEGE_FORTRESS_SIEGE_OR_HIDEOUT_SIEGE);
			return false;
		}
		if(player.isFlying())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_FLYING);
			return false;
		}
		if(!CTeleportConfig.inWater && player.isInWater())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER);
			return false;
		}
		if(!CTeleportConfig.inBoat && player.isInBoat())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER);
			return false;
		}
		if(!CTeleportConfig.inSkillOffensive && player.getEffectList().getEffect(e -> e.getSkill().getTemplate().isOffensive()).isPresent()) {
			player.sendMessage(new CustomMessage("teleport.skillOffensive"));
			return false;
		}
		if (player.isInStoreMode() || player.isInTrade()) {
			player.sendMessage("You cannot teleport while trading!");
			return false;
		}
		return true;
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}
}
