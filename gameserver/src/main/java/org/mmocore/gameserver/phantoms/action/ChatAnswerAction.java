package org.mmocore.gameserver.phantoms.action;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.clientCustoms.PhantomConfig;
import org.mmocore.gameserver.data.xml.holder.PhantomPhraseHolder;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.serverpackets.Say2;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Language;

/**
 * Created by Hack
 * Date: 23.08.2016 7:50
 */
public class ChatAnswerAction extends AbstractPhantomAction {
    private final Player sender;

    public ChatAnswerAction(Player sender) {
        this.sender = sender;
    }

    @Override
    public long getDelay() {
        return PhantomConfig.chatAnswerDelay;
    }

    @Override
    public void run() {
        if (Rnd.chance(PhantomConfig.chatAnswerChance) && sender != null && !actor.getMemory().isIgnoredChatNick(sender.getName())) {
            String phrase = PhantomPhraseHolder.getInstance().getRandomPhrase(ChatType.TELL);
            if (phrase != null) {
                sender.sendPacket(new Say2(actor.getObjectId(), ChatType.TELL, actor.getName(), phrase, Language.RUSSIAN));
                actor.getMemory().addIgnoredChatNick(sender.getName());
            }
        }
    }
}
