package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.petition.PetitionMainGroup;
import org.mmocore.gameserver.model.petition.PetitionSubGroup;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Language;

import java.util.Collection;

/**
 * @author VISTALL
 */
public class ExResponseShowStepTwo extends GameServerPacket {
    private final Language language;
    private final PetitionMainGroup petitionMainGroup;

    public ExResponseShowStepTwo(final Player player, final PetitionMainGroup gr) {
        language = player.getLanguage();
        petitionMainGroup = gr;
    }

    @Override
    protected void writeData() {
        final Collection<PetitionSubGroup> subGroups = petitionMainGroup.getSubGroups();
        writeD(subGroups.size());
        writeS(petitionMainGroup.getDescription(language));
        for (final PetitionSubGroup g : subGroups) {
            writeC(g.getId());
            writeS(g.getName(language));
        }
    }
}