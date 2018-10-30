package handler.bbs;

import handler.bbs.abstracts.AbstractCommunityBoard;
import handler.bbs.object.CommunityComponent;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.handler.bbs.BbsHandlerHolder;
import org.mmocore.gameserver.handler.bbs.IBbsHandler;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.object.components.player.PlayerListenerList;
import org.mmocore.gameserver.object.components.player.community.ICommunityComponent;

/**
 * @author VISTALL
 * @date 2:17/19.08.2011
 */
public abstract class ScriptBbsHandler extends AbstractCommunityBoard implements OnInitScriptListener, IBbsHandler
{
	@Override
	public void onInit()
	{
		if(CBasicConfig.COMMUNITYBOARD_ENABLED)
		{
			BbsHandlerHolder.getInstance().registerHandler(this);
			PlayerListenerList.addGlobal((OnPlayerEnterListener) player -> {
				ICommunityComponent component = new CommunityComponent(player);
				player.setCommunityComponent(component);
				component.restore();
			});
		}
	}
}
