package services;

import org.mmocore.gameserver.configuration.config.custom.AcpConfig;
import org.mmocore.gameserver.data.client.holder.ItemNameLineHolder;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.custom.acp.action.enums.Acp;
import org.mmocore.gameserver.templates.client.ItemNameLine;
import org.mmocore.gameserver.utils.Util;

import java.util.List;
import java.util.Optional;

/**
 * Create by Mangol on 30.12.2015.
 */
public class AutoAcpService
{
	private static final ItemNameLineHolder itemHolder = ItemNameLineHolder.getInstance();

	@Bypass("services.AutoACPService:setAuto")
	public void setAuto(final Player player, final NpcInstance npc, final String[] arg)
	{
		if(player == null)
		{
			return;
		}
		if(arg == null || arg.length < 2)
		{
			player.sendMessage(new CustomMessage("component.acp.incorrectly").toString(player));
			generateHtml(player);
			return;
		}
		final Acp acp = Acp.valueOf(arg[0]);
		final boolean value = Boolean.parseBoolean(arg[1]);
		acp.setAuto(player, value);
		acp.start(player, value);
		generateHtml(player);
	}

	@Bypass("services.AutoACPService:setPercent")
	public void setPercent(final Player player, final NpcInstance npc, final String[] arg)
	{
		if(player == null)
		{
			return;
		}
		if(arg == null || arg.length < 2)
		{
			player.sendMessage(new CustomMessage("component.acp.incorrectly").toString(player));
			generateHtml(player);
			return;
		}
		try
		{
			final Acp acp = Acp.valueOf(arg[0]);
			final boolean autoUse = acp.isAuto(player);
			acp.setPercent(player, (double) Integer.parseInt(arg[1]));
			acp.start(player, autoUse);
			generateHtml(player);
		}
		catch(final NumberFormatException e)
		{
			player.sendMessage(new CustomMessage("component.acp.incorrectly").toString(player));
			generateHtml(player);
		}
	}

	@Bypass("services.AutoACPService:setReuse")
	public void setReuse(final Player player, final NpcInstance npc, final String[] arg)
	{
		if(player == null)
		{
			return;
		}
		if(arg == null || arg.length < 2)
		{
			player.sendMessage(new CustomMessage("component.acp.incorrectly").toString(player));
			generateHtml(player);
			return;
		}
		try
		{
			final Acp acp = Acp.valueOf(arg[0]);
			final boolean autoUse = acp.isAuto(player);
			acp.setReuse(player, Integer.parseInt(arg[1]));
			acp.start(player, autoUse);
			generateReuse(player);
		}
		catch(final NumberFormatException e)
		{
			player.sendMessage(new CustomMessage("component.acp.incorrectly").toString(player));
			generateReuse(player);
		}
	}

	@Bypass("services.AutoACPService:setItem")
	public void setItem(final Player player, final NpcInstance npc, final String[] arg)
	{
		if(player == null)
		{
			return;
		}
		if(arg == null || arg.length < 2)
		{
			player.sendMessage(new CustomMessage("component.acp.incorrectly").toString(player));
			generateHtml(player);
			return;
		}
		try
		{
			final Acp acp = Acp.valueOf(arg[0]);
			final boolean autoUse = acp.isAuto(player);
			acp.setItem(player, Integer.parseInt(arg[1]));
			acp.start(player, autoUse);
			generateShowItem(player, acp);
		}
		catch(final NumberFormatException e)
		{
			player.sendMessage(new CustomMessage("component.acp.incorrectly").toString(player));
			generateItem(player);
		}
	}

	@Bypass("services.AutoACPService:showBasic")
	public void showBasic(final Player player, final NpcInstance npc, final String[] arg)
	{
		if(player == null)
		{
			return;
		}
		generateHtml(player);
	}

	@Bypass("services.AutoACPService:showReuse")
	public void showReuse(final Player player, final NpcInstance npc, final String[] arg)
	{
		if(player == null)
		{
			return;
		}
		generateReuse(player);
	}

	@Bypass("services.AutoACPService:showItem")
	public void showItem(final Player player, final NpcInstance npc, final String[] arg)
	{
		if(player == null)
		{
			return;
		}
		generateItem(player);
	}

	@Bypass("services.AutoACPService:showItemList")
	public void showItemList(final Player player, final NpcInstance npc, final String[] arg)
	{
		if(player == null)
		{
			return;
		}
		if(arg == null || arg.length < 1)
		{
			player.sendMessage(new CustomMessage("component.acp.incorrectly").toString(player));
			generateItem(player);
			return;
		}
		final Acp acp = Acp.valueOf(arg[0]);
		generateShowItem(player, acp);
	}

	public static void generateHtml(final Player activeChar)
	{
		final String ON = "<font color=\"669900\">On</font>";
		final String OFF = "<font color=\"FF3333\">Off</font>";
		final String auto_cp = !activeChar.getAcpComponent().getTemplate().isAutoCp() ? OFF : ON;
		final String auto_small_cp = !activeChar.getAcpComponent().getTemplate().isAutoSmallCp() ? OFF : ON;
		final String auto_hp = !activeChar.getAcpComponent().getTemplate().isAutoHp() ? OFF : ON;
		final String auto_mp = !activeChar.getAcpComponent().getTemplate().isAutoMp() ? OFF : ON;
		final int auto_cp_percent = (int) activeChar.getAcpComponent().getTemplate().getCpPercent();
		final int auto_small_cp_percent = (int) activeChar.getAcpComponent().getTemplate().getSmallCpPercent();
		final int auto_hp_percent = (int) activeChar.getAcpComponent().getTemplate().getHpPercent();
		final int auto_mp_percent = (int) activeChar.getAcpComponent().getTemplate().getMpPercent();
		final String auto_small_cp_str_boolean = !activeChar.getAcpComponent().getTemplate().isAutoSmallCp() ? "<button action=\"" + "bypass -h htmbypass_services.AutoACPService:setAuto small_cp true" + "\" value=\"off\" fore=\"L2UI_CT1.ListCTRL_DF_Title\" back=\"l2ui_ct1.ListCTRL_DF_Title_Down\" width=\"32\" height=\"16\">" : "<button action=\"" + "bypass -h htmbypass_services.AutoACPService:setAuto small_cp false" + "\" value=\"on\" fore=\"L2UI_CT1.ListCTRL_DF_Title\" back=\"l2ui_ct1.ListCTRL_DF_Title_Down\" width=\"32\" height=\"16\">";
		final String auto_cp_str_boolean = !activeChar.getAcpComponent().getTemplate().isAutoCp() ? "<button action=\"" + "bypass -h htmbypass_services.AutoACPService:setAuto cp true" + "\" value=\"off\" fore=\"L2UI_CT1.ListCTRL_DF_Title\" back=\"l2ui_ct1.ListCTRL_DF_Title_Down\" width=\"32\" height=\"16\">" : "<button action=\"" + "bypass -h htmbypass_services.AutoACPService:setAuto cp false" + "\" value=\"on\" fore=\"L2UI_CT1.ListCTRL_DF_Title\" back=\"l2ui_ct1.ListCTRL_DF_Title_Down\" width=\"32\" height=\"16\">";
		final String auto_hp_str_boolean = !activeChar.getAcpComponent().getTemplate().isAutoHp() ? "<button action=\"" + "bypass -h htmbypass_services.AutoACPService:setAuto hp true" + "\" value=\"off\" fore=\"L2UI_CT1.ListCTRL_DF_Title\" back=\"l2ui_ct1.ListCTRL_DF_Title_Down\" width=\"32\" height=\"16\">" : "<button action=\"" + "bypass -h htmbypass_services.AutoACPService:setAuto hp false" + "\" value=\"on\" fore=\"L2UI_CT1.ListCTRL_DF_Title\" back=\"l2ui_ct1.ListCTRL_DF_Title_Down\" width=\"32\" height=\"16\">";
		final String auto_mp_str_boolean = !activeChar.getAcpComponent().getTemplate().isAutoMp() ? "<button action=\"" + "bypass -h htmbypass_services.AutoACPService:setAuto mp true" + "\" value=\"off\" fore=\"L2UI_CT1.ListCTRL_DF_Title\" back=\"l2ui_ct1.ListCTRL_DF_Title_Down\" width=\"32\" height=\"16\">" : "<button action=\"" + "bypass -h htmbypass_services.AutoACPService:setAuto mp false" + "\" value=\"on\" fore=\"L2UI_CT1.ListCTRL_DF_Title\" back=\"l2ui_ct1.ListCTRL_DF_Title_Down\" width=\"32\" height=\"16\">";
		final HtmlMessage dialog = new HtmlMessage(5);
		dialog.setFile("custom/acp/auto_acp.htm");
		dialog.replace("{auto_cp}", auto_cp);
		dialog.replace("{auto_small_cp}", auto_small_cp);
		dialog.replace("{auto_hp}", auto_hp);
		dialog.replace("{auto_mp}", auto_mp);
		dialog.replace("{percent_cp}", String.valueOf(auto_cp_percent + "%"));
		dialog.replace("{percent_small_cp}", String.valueOf(auto_small_cp_percent + "%"));
		dialog.replace("{percent_hp}", String.valueOf(auto_hp_percent + "%"));
		dialog.replace("{percent_mp}", String.valueOf(auto_mp_percent + "%"));
		dialog.replace("{auto_cp_boolean}", auto_cp_str_boolean);
		dialog.replace("{auto_small_cp_boolean}", auto_small_cp_str_boolean);
		dialog.replace("{auto_hp_boolean}", auto_hp_str_boolean);
		dialog.replace("{auto_mp_boolean}", auto_mp_str_boolean);
		activeChar.sendPacket(dialog);
	}

	public static void generateReuse(final Player activeChar)
	{
		final int auto_small_cp_reuse = activeChar.getAcpComponent().getTemplate().getReuseSmallCp() / 1000;
		final int auto_cp_reuse = activeChar.getAcpComponent().getTemplate().getReuseCp() / 1000;
		final int auto_hp_reuse = activeChar.getAcpComponent().getTemplate().getReuseHp() / 1000;
		final int auto_mp_reuse = activeChar.getAcpComponent().getTemplate().getReuseMp() / 1000;
		final HtmlMessage dialog = new HtmlMessage(5);
		dialog.setFile("custom/acp/auto_acp_reuse.htm");
		dialog.replace("{auto_cp_reuse}", String.valueOf(auto_cp_reuse + " sec"));
		dialog.replace("{auto_small_cp_reuse}", String.valueOf(auto_small_cp_reuse + " sec"));
		dialog.replace("{auto_hp_reuse}", String.valueOf(auto_hp_reuse + " sec"));
		dialog.replace("{auto_mp_reuse}", String.valueOf(auto_mp_reuse + " sec"));
		dialog.replace("{max_reuse}", String.valueOf(AcpConfig.MAX_REUSE / 1000));
		dialog.replace("{min_reuse}", String.valueOf(AcpConfig.MIN_REUSE / 1000));
		activeChar.sendPacket(dialog);
	}

	public static void generateItem(final Player activeChar)
	{
		final int auto_small_cp_item = activeChar.getAcpComponent().getTemplate().getSmallCpItemId();
		final int auto_cp_item = activeChar.getAcpComponent().getTemplate().getCpItemId();
		final int auto_hp_item = activeChar.getAcpComponent().getTemplate().getHpItemId();
		final int auto_mp_item = activeChar.getAcpComponent().getTemplate().getMpItemId();
		final int countCpItem = (int) activeChar.getInventory().getCountOf(auto_cp_item);
		final int countHpItem = (int) activeChar.getInventory().getCountOf(auto_hp_item);
		final int countMpItem = (int) activeChar.getInventory().getCountOf(auto_mp_item);
		final int countSmallCpItem = (int) activeChar.getInventory().getCountOf(auto_small_cp_item);
		final HtmlMessage dialog = new HtmlMessage(5);
		dialog.setFile("custom/acp/auto_acp_item.htm");
		final ItemNameLine itemNameLineSmallCp = itemHolder.get(activeChar.getLanguage(), auto_small_cp_item);
		final ItemNameLine itemNameLineCp = itemHolder.get(activeChar.getLanguage(), auto_cp_item);
		final ItemNameLine itemNameLineHp = itemHolder.get(activeChar.getLanguage(), auto_hp_item);
		final ItemNameLine itemNameLineMp = itemHolder.get(activeChar.getLanguage(), auto_mp_item);
		if(itemNameLineCp.getName().length() > 17)
		{
			final Optional<ItemNameLine> customFunction = itemNameLineCp.getFunction("acp");
			dialog.replace("{auto_cp_item_name}", String.valueOf(customFunction.get().getName()));
		}
		else
		{
			dialog.replace("{auto_cp_item_name}", String.valueOf(itemNameLineCp.getName()));
		}
		if(itemNameLineHp.getName().length() > 17)
		{
			final Optional<ItemNameLine> customFunction = itemNameLineHp.getFunction("acp");
			dialog.replace("{auto_hp_item_name}", String.valueOf(customFunction.get().getName()));
		}
		else
		{
			dialog.replace("{auto_hp_item_name}", String.valueOf(itemNameLineHp.getName()));
		}
		if(itemNameLineMp.getName().length() > 17)
		{
			final Optional<ItemNameLine> customFunction = itemNameLineMp.getFunction("acp");
			dialog.replace("{auto_mp_item_name}", String.valueOf(customFunction.get().getName()));
		}
		else
		{
			dialog.replace("{auto_mp_item_name}", String.valueOf(itemNameLineMp.getName()));
		}
		if(itemNameLineSmallCp.getName().length() > 17)
		{
			final Optional<ItemNameLine> customFunction = itemNameLineSmallCp.getFunction("acp");
			dialog.replace("{auto_small_cp_item_name}", String.valueOf(customFunction.get().getName()));
		}
		else
		{
			dialog.replace("{auto_small_cp_item_name}", String.valueOf(itemNameLineSmallCp.getName()));
		}
		dialog.replace("{auto_small_cp_item_count}", String.valueOf(countSmallCpItem));
		dialog.replace("{auto_cp_item_count}", String.valueOf(countCpItem));
		dialog.replace("{auto_hp_item_count}", String.valueOf(countHpItem));
		dialog.replace("{auto_mp_item_count}", String.valueOf(countMpItem));
		activeChar.sendPacket(dialog);
	}

	public static void generateShowItem(final Player activeChar, final Acp acp)
	{
		List<Integer> list = null;
		switch(acp)
		{
			case cp:
				list = AcpConfig.allowCpItemId;
				break;
			case small_cp:
				list = AcpConfig.allowSmallCpItemId;
				break;
			case hp:
				list = AcpConfig.allowHpItemId;
				break;
			case mp:
				list = AcpConfig.allowMpItemId;
				break;
		}


		final String template = HtmCache.getInstance().getHtml("custom/acp/template/auto_acp_show_item_template.htm", activeChar);
		String block;
		String listStr = "";
		int count = 0;
		for(final int item : list)
		{
			if(count == 5)
			{
				break;
			}
			final int countItem = (int) activeChar.getInventory().getCountOf(item);
			block = template;
			block = block.replace("{link}", "bypass -h htmbypass_services.AutoACPService:setItem " + acp.name() + " " + item);
			block = block.replace("{icon}", Util.getItemIcon(item));
			final ItemNameLine itemNameLine = itemHolder.get(activeChar.getLanguage(), item);
			final String countItemStr = countItem > 10000 ? "10000+" : String.valueOf(countItem);
			final String finalCountItemStr = new CustomMessage("component.acp.count").addString(countItemStr).toString(activeChar);
			if(itemNameLine.getDescription().length() > 60)
			{
				final Optional<ItemNameLine> customFunction = itemNameLine.getFunction("acp");
				block = block.replace("{inform}", customFunction.isPresent() ? customFunction.get().getDescription() + " " + finalCountItemStr : "" + "-" + finalCountItemStr);
			}
			else
			{
				block = block.replace("{inform}", itemNameLine.getDescription() + " " + finalCountItemStr);
			}
			listStr += block;
			count++;
		}
		final HtmlMessage html = new HtmlMessage(5).setFile("custom/acp/list/auto_acp_item_show_list.htm");
		html.replace("{list}", listStr);
		activeChar.sendPacket(html);
	}
}
