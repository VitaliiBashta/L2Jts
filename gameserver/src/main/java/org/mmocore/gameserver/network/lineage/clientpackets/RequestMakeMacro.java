package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.interfaces.Macro;
import org.mmocore.gameserver.object.components.player.interfaces.Macro.L2MacroCmd;

/**
 * packet type id 0xcd
 * <p/>
 * sample
 * <p/>
 * cd
 * d // id
 * S // macro name
 * S // unknown  desc
 * S // unknown  acronym
 * c // icon
 * c // count
 * <p/>
 * c // entry
 * c // type
 * d // skill id
 * c // shortcut id
 * S // command name
 * <p/>
 * format:		cdSSScc (ccdcS)
 */
public class RequestMakeMacro extends L2GameClientPacket {
    private Macro _macro;

    @Override
    protected void readImpl() {
        final int _id = readD();
        final String _name = readS(32);
        final String _desc = readS(64);
        final String _acronym = readS(4);
        final int _icon = readC();
        int _count = readC();
        if (_count > 12) {
            _count = 12;
        }
        final L2MacroCmd[] commands = new L2MacroCmd[_count];
        for (int i = 0; i < _count; i++) {
            final int entry = readC();
            final int type = readC(); // 1 = skill, 3 = action, 4 = shortcut
            final int d1 = readD(); // skill or page number for shortcuts
            final int d2 = readC();
            final String command = readS().replace(";", "").replace(",", "");
            commands[i] = new L2MacroCmd(entry, type, d1, d2, command);
        }
        _macro = new Macro(_id, _icon, _name, _desc, _acronym, commands);
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.getMacroComponent().getAllMacroses().length > 48) {
            activeChar.sendPacket(SystemMsg.YOU_MAY_CREATE_UP_TO_48_MACROS);
            return;
        }

        if (_macro.name.isEmpty()) {
            activeChar.sendPacket(SystemMsg.ENTER_THE_NAME_OF_THE_MACRO);
            return;
        }

        if (_macro.descr.length() > 32) {
            activeChar.sendPacket(SystemMsg.MACRO_DESCRIPTIONS_MAY_CONTAIN_UP_TO_32_CHARACTERS);
            return;
        }

        activeChar.getMacroComponent().registerMacro(_macro);
    }
}