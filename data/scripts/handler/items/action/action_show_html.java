package handler.items.action;

import gnu.trove.set.hash.TIntHashSet;
import handler.items.ScriptItemHandler;

import org.jts.dataparser.data.holder.itemdata.ItemData.ItemAction;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.RadarControl;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KilRoy
 * TODO, FIXME
 * 20885	html=[br_lana_chcard000.htm] - нет русского
 * 20886	html=[br_gnosian_chcard000.htm] - нет русского
 * 20889	html=[br_mortia_chcard000.htm] - нет русского
 * 20894	html=[br_haertbane_chcard000.htm] - нет русского
 * 
 * High Five section - нет ни на РПГ-Клабе, ни в паке с ГлориДейза
 * 17214	html=[ssq2_essey_q10294_01.htm]
 * 17215	html=[ssq2_essey_p1_q10294_01.htm]
 * 17216	html=[ssq2_essey_p2_q10294_01.htm]
 * 17217	html=[ssq2_essey_p3_q10294_01.htm]
 * 17218	html=[ssq2_essey_p4_q10294_01.htm]
 * 17219	html=[ssq2_eris_memory_q10295_01.htm]
 * 17220	html=[ssq2_memo_library_q10293_01.htm]
 */
public class action_show_html extends ScriptItemHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(action_show_html.class);
	private static final RadarControl RADAR_PACKET = new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.FLAG_ON_MAP, new Location(51995, -51265, -3104));
	private final int[] itemIds;

	public action_show_html()
	{
		final TIntHashSet set = new TIntHashSet();
		for(final ItemTemplate template : ItemTemplateHolder.getInstance().getAllTemplates())
		{
			if(template == null)
			{
				continue;
			}
			if(template.getActionType() == ItemAction.action_show_html)
			{
				set.add(template.getItemId());
			}
		}
		itemIds = set.toArray();
	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(!playable.isPlayer())
		{
			return false;
		}

		final Player player = playable.getPlayer();

		if(player.isActionsDisabled())
		{
			player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		final String html_link = item.getTemplate().getDefaultHtml();
		if(html_link != null && !html_link.isEmpty())
		{
			final HtmlMessage packet = new HtmlMessage(5).setFile(html_link);
			packet.replace("%player_level%", String.valueOf(playable.getPlayer().getLevel()));
			player.sendPacket(packet);
			if(item.getItemId() == 7063) // TODO[K] - разрбраться в чем фишка установки радара на офе
			{
				player.sendPacket(RADAR_PACKET);
			}
		}
		else
		{
			LOGGER.warn("ItemID: " + item.getItemId() + " not allowed html!");
		}
		player.sendActionFailed();
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return itemIds;
	}
}