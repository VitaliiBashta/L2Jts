package handler.bbs.custom.privateOffice.engine.listener;
import handler.bbs.abstracts.AbstractCommunityBoard;
import handler.bbs.custom.privateOffice.engine.interfaces.IDefault;
import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.object.Player;

/**
 * @author Mangol
 * @since 17.02.2016
 */
public class AnswerServiceListener implements OnAnswerListener
{
	private final IDefault privateOffice;
	private final Player player;
	private final String bypass;
	private final Object[] params;
	private final long expireTime;

	public AnswerServiceListener(final IDefault privateOffice, final Player player, final String bypass, final long expireTime, final Object... params)
	{
		this.privateOffice = privateOffice;
		this.player = player;
		this.bypass = bypass;
		this.params = params;
		this.expireTime = expireTime > 0L ? System.currentTimeMillis() + expireTime : 0L;
	}

	@Override
	public void sayYes()
	{
		if(player == null)
		{
			return;
		}
		if(privateOffice.isIncluded(player))
		{
			privateOffice.request(player, bypass, params);
		}
	}

	@Override
	public void sayNo()
	{
		if(player == null)
		{
			return;
		}
		AbstractCommunityBoard.useSaveCommand(player);
	}

	@Override
	public long expireTime()
	{
		return expireTime;
	}
}
