package handler.items.action;

import gnu.trove.set.hash.TIntHashSet;
import handler.items.ScriptItemHandler;

import org.jts.dataparser.data.holder.ItemDataHolder;
import org.jts.dataparser.data.holder.itemdata.ItemData;
import org.jts.dataparser.data.holder.itemdata.ItemData.ItemAction;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KilRoy
 */
public class action_start_quest extends ScriptItemHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(action_start_quest.class);
	private static final HtmlMessage ONETIME_QUEST = new HtmlMessage(5).setFile("pts/finishedquest.htm");
	private static final HtmlMessage DAILY_QUEST = new HtmlMessage(5).setFile("pts/finisheddailyquest.htm");
	private static final HtmlMessage FULL_QUEST = new HtmlMessage(5).setFile("pts/fullquest.htm");
	private static int[] itemIds;

	public action_start_quest()
	{
		final TIntHashSet set = new TIntHashSet();
		for(final ItemTemplate template : ItemTemplateHolder.getInstance().getAllTemplates())
		{
			if(template == null)
			{
				continue;
			}
			if(template.getActionType() == ItemAction.action_start_quest)
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
		QuestState questState = player.getQuestState(item.getTemplate().getQuestId());
		final Quest quest = QuestManager.getQuest(item.getTemplate().getQuestId());
		if(player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - (player.getInventoryLimit() * 0.20))
		{
			player.sendPacket(SystemMsg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
			return false;
		}
		else if(questState != null)
		{
			if(questState.isStarted())
			{
				player.sendPacket(SystemMsg.THIS_ITEM_CANNOT_BE_USED_BECAUSE_YOU_ARE_ALREADY_PARTICIPATING_IN_THE_QUEST_THAT_CAN_BE_STARTED_WITH_THIS_ITEM);
				return false;
			}
			else if(questState.isCompleted())
			{
				player.sendPacket(ONETIME_QUEST);
				return false;
			}
			else if(!questState.isNowAvailable())
			{
				player.sendPacket(DAILY_QUEST);
				return false;
			}
			else if(!validateQuestCount(player))
			{
				player.sendPacket(FULL_QUEST);
				return false;
			}
			else if(questState.isCreated())
			{
				useQuestItem(player, item);
			}
		}
		else if(questState == null)
		{
			player.createNewQuestState(quest.getId());
			questState = player.getQuestState(quest.getId());
			if(questState != null)
			{
				if(!questState.isNowAvailable())
				{
					player.sendPacket(DAILY_QUEST);
					return false;
				}
				else if(!validateQuestCount(player))
				{
					player.sendPacket(FULL_QUEST);
					return false;
				}
				useQuestItem(player, item);
			}
		}
		return true;
	}

	private boolean useQuestItem(final Player player, final ItemInstance item)
	{
		final int itemId = item.getItemId();
		try
		{
			String html_link = null;
			for(final ItemData itemData : ItemDataHolder.getInstance().getItemDatas())
			{
				if(itemData.itemId == itemId)
				{
					if(!itemData.html.contains("item_default.htm"))
					{
						html_link = itemData.html;
					}
				}
			}
			if(html_link != null && !html_link.isEmpty())
			{
				final HtmlMessage packet = new HtmlMessage(5).setFile("pts/quest_item/" + html_link);
				player.sendPacket(packet);
			}
			else
			{
				player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
				LOGGER.warn("ItemID: " + itemId + " not allowed html!");
				return false;
			}
		}
		catch(final Exception e)
		{
			LOGGER.warn("QuestItemID: " + itemId + " not corrected work, or dataparser returned null!", e);
			return false;
		}
		return true;
	}

	private boolean validateQuestCount(final Player player)
	{
		int count = 0;
		for(final QuestState quest : player.getAllQuestsStates())
		{
			if(quest != null && quest.getQuest().isVisible() && quest.isStarted() && quest.getCond() > 0 && !quest.getQuest().isUnderLimit())
			{
				count++;
			}
		}
		if(count >= 40)
		{
			return false;
		}
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return itemIds;
	}
}