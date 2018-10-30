package handler.voicecommands;

import org.mmocore.gameserver.configuration.config.custom.AcpConfig;
import org.mmocore.gameserver.object.Player;
import services.AutoAcpService;

/**
 * Create by Mangol on 30.12.2015.
 */
public class AutoAcp extends ScriptIVoiceHandler
{
	@Override
	public boolean useVoicedCommand(final String command, final Player activeChar, final String target)
	{
		if(!AcpConfig.allowAcp)
		{
			activeChar.sendActionFailed();
			return false;
		}
		if(command.equalsIgnoreCase("acp"))
		{
			AutoAcpService.generateHtml(activeChar);
		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return new String[] { "acp" };
	}
}
