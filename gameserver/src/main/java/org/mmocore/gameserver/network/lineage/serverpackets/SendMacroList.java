package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.player.interfaces.Macro;

/**
 * packet type id 0xe7
 * <p/>
 * sample
 * <p/>
 * e7
 * d // unknown change of Macro edit,add,delete
 * c // unknown
 * c //count of Macros
 * c // unknown
 * <p/>
 * d // id
 * S // macro name
 * S // desc
 * S // acronym
 * c // icon
 * c // count
 * <p/>
 * c // entry
 * c // type
 * d // skill id
 * c // shortcut id
 * S // command name
 * <p/>
 * format:		cdccdSSScc (ccdcS)
 */
public class SendMacroList extends GameServerPacket {
    private final int rev;
    private final int count;
    private final Macro macro;

    public SendMacroList(final int rev, final int count, final Macro macro) {
        this.rev = rev;
        this.count = count;
        this.macro = macro;
    }

    @Override
    protected final void writeData() {
        writeD(rev); // macro change revision (changes after each macro edition)
        writeC(0); //unknown
        writeC(count); //count of Macros
        writeC(macro != null ? 1 : 0); //unknown

        if (macro != null) {
            writeD(macro.id); //Macro ID
            writeS(macro.name); //Macro Name
            writeS(macro.descr); //Desc
            writeS(macro.acronym); //acronym
            writeC(macro.icon); //icon

            writeC(macro.commands.length); //count

            for (int i = 0; i < macro.commands.length; i++) {
                final Macro.L2MacroCmd cmd = macro.commands[i];
                writeC(i + 1); //i of count
                writeC(cmd.type); //type  1 = skill, 3 = action, 4 = shortcut
                writeD(cmd.d1); // skill id
                writeC(cmd.d2); // shortcut id
                writeS(cmd.cmd); // command name
            }
        }
    }
}