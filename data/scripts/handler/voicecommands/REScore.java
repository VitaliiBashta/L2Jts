package handler.voicecommands;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.services.event.REvent;

/**
 * @author Mangol
 * @since 08.10.2016
 */
public class REScore extends ScriptIVoiceHandler {
	private static final String[] COMMAND = new String[] { "res" };

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target) {
		if(command.equals("res")) {
			REvent.stats(activeChar);
		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList() {
		return COMMAND;
	}
}
