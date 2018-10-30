package services;
import handler.bbs.custom.classMaster.CommunityClassMaster;
import org.apache.commons.lang3.math.NumberUtils;
import org.mmocore.gameserver.configuration.config.community.CClassMasterConfig;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Util;

public class ClassMaster
{
	@Bypass("services.Class:choice")
	public void choice(final Player player, final NpcInstance npc, final String[] arg)
	{
		if(!NumberUtils.isNumber(arg[0]) || !NumberUtils.isNumber(arg[1]))
		{
			return;
		}

		final int class_id = Integer.parseInt(arg[0]);
		final int id = Integer.parseInt(arg[1]);
		final HtmlMessage html = new HtmlMessage(5).setFile("scripts/services/classMaster/index.htm");
		String content = "";
		final String template = HtmCache.getInstance().getHtml("scripts/services/classMaster/template.htm", player);
		String block;

		final int first = CClassMasterConfig.itemId[id];
		final int second = CClassMasterConfig.secondItem[id];
		final long first_count = CClassMasterConfig.priceCount[id];
		final long second_count = CClassMasterConfig.secondPrice[id];

		block = template;
		block = block.replace("{icon}", Util.getItemIcon(first));
		block = block.replace("{name}", Util.getItemName(player.getLanguage(), first));
		block = block.replace("{link}", "bypass -h htmbypass_services.Class:take " + class_id + " " + id + " " + 0);
		block = block.replace("{price}", Util.formatPay(player, first_count, first));
		content += block;

		block = template;
		block = block.replace("{icon}", Util.getItemIcon(second));
		block = block.replace("{name}", Util.getItemName(player.getLanguage(), second));
		block = block.replace("{link}", "bypass -h htmbypass_services.Class:take " + class_id + " " + id + " " + 1);
		block = block.replace("{price}", Util.formatPay(player, second_count, second));
		content += block;

		html.replace("%content%", content);
		player.sendPacket(html);
	}

	@Bypass("services.Class:take")
	public void take(final Player player, final NpcInstance npc, final String[] arg)
	{
		if(!NumberUtils.isNumber(arg[0]) || !NumberUtils.isNumber(arg[1]) || !NumberUtils.isNumber(arg[2]))
		{
			return;
		}

		final int class_id = Integer.parseInt(arg[0]);
		final int id = Integer.parseInt(arg[1]);
		final int pay = Integer.parseInt(arg[2]);
		CommunityClassMaster.getInstance().changeClass(player, class_id, id, pay);
	}
}