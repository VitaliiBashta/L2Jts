package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.data.xml.holder.PetitionGroupHolder;
import org.mmocore.gameserver.model.petition.PetitionMainGroup;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Language;

import java.util.Collection;

/**
 * @author VISTALL
 */
public class ExResponseShowStepOne extends GameServerPacket {
    private final Language language;

    public ExResponseShowStepOne(final Player player) {
        language = player.getLanguage();
    }

    @Override
    protected void writeData() {
        final Collection<PetitionMainGroup> petitionGroups = PetitionGroupHolder.getInstance().getPetitionGroups();
        writeD(petitionGroups.size());
        for (final PetitionMainGroup group : petitionGroups) {
            writeC(group.getId());
            writeS(group.getName(language));
        }
    }
}