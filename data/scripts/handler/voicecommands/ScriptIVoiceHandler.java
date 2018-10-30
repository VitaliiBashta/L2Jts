package handler.voicecommands;

import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.handler.voicecommands.VoicedCommandHandler;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;

/**
 * @author KilRoy
 */
public abstract class ScriptIVoiceHandler implements IVoicedCommandHandler, OnInitScriptListener
{
	@Override
	public void onInit()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}
}