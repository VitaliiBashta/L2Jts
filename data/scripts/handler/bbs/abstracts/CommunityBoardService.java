package handler.bbs.abstracts;

import handler.bbs.custom.privateOffice.CommunityPrivateOffice;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import handler.bbs.custom.privateOffice.engine.interfaces.IService;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;

/**
 * @author Java-man
 */
public abstract class CommunityBoardService extends AbstractCommunityBoard implements OnInitScriptListener, IService
{
	@Override
	public void onInit()
	{
		final Services serviceEnum = getService();

		if(serviceEnum != Services.none)
		{
			CommunityPrivateOffice.services.put(serviceEnum, this);
		}
	}
}
