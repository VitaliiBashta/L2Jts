package handler.bbs.custom.privateOffice.engine.interfaces;
import handler.bbs.abstracts.AbstractCommunityBoard;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import handler.bbs.custom.privateOffice.engine.listener.AnswerServiceListener;
import org.apache.commons.lang3.tuple.Pair;
import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;

/**
 * @author Mangol
 * @since 10.02.2016
 */
public interface IService extends IDefault
{
	/**
	 * Проверка включен ли сервис.
	 * @param player - игрок
	 */
	@Override
	default boolean isIncluded(final Player player)
	{
		if(!getService().isIncluded())
		{
			player.sendPacket(new CustomMessage("bbs.service.notIncluded"));
			AbstractCommunityBoard.useSaveCommand(player);
			return false;
		}
		return true;
	}

	/**
	 * Проверяет на наличие запроса подтверждения использования того, или иного типа у игрока
	 * @param player - игрок
	 * @return -
	 */
	@Override
	default boolean validAnswer(final Player player)
	{
		final Pair<Integer, OnAnswerListener> listener = player.getAskListener(false);
		if(listener != null && listener.getValue() instanceof AnswerServiceListener)
		{
			player.sendPacket(new CustomMessage("bbs.service.answer"));
			AbstractCommunityBoard.useSaveCommand(player);
			return true;
		}
		return false;
	}

	default void wear(final Player player, final Object... params){}

	Services getService();
}