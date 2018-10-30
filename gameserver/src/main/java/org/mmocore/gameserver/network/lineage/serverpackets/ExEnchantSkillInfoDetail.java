package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.tables.SkillTreeTable;

/**
 * Дамп с оффа, 828 протокол:
 * 0000: fe 5e 00 01 00 00 00 46 00 00 00 65 00 00 00 7e
 * 0010: cc 12 00 fe fc bb 00 00 00 00 00 61 00 00 00 01
 * 0020: 00 00 00 05 00 00 00 9b 25 00 00 00 00 00 00
 * <p/>
 * Скилл: Drain Health (id: 70)
 * Левел скила: 53
 * Нужен предмет: Giant's Secret Codex of Mastery - 1 штука
 * Точим на +1 Power (lvl: 101)
 * Требуется SP: 1231998
 * Требуется exp: 12319998
 * Шанс успеха: 97%
 * <p/>
 * Еще дампы для примера:
 * 0000: fe 5e 00 01 00 00 00 7a 00 00 00 6b 00 00 00 60
 * 0010: 81 1e 00 c0 0d 31 01 00 00 00 00 50 00 00 00 01
 * 0020: 00 00 00 05 00 00 00 9b 25 00 00 00 00 00 00
 * <p/>
 * 0000: fe 5e 00 03 00 00 00 9a 01 00 00 d0 00 00 00 20
 * 0010: a9 03 00 40 9b 24 00 00 00 00 00 64 00 00 00 01
 * 0020: 00 00 00 05 00 00 00 9a 25 00 00 00 00 00 00
 * <p/>
 * 0000: fe 5e 00 00 00 00 00 6f 00 00 00 65 00 00 00 d5
 * 0010: 79 04 00 55 c2 2c 00 00 00 00 00 61 00 00 00 01
 * 0020: 00 00 00 05 00 00 00 de 19 00 00 00 00 00 00
 */
public class ExEnchantSkillInfoDetail extends GameServerPacket {
    private final int unk = 0;
    private final int skillId;
    private final int skillLvl;
    private final int sp;
    private final int chance;
    private final int bookId, adenaCount;

    public ExEnchantSkillInfoDetail(final int skillId, final int skillLvl, final int sp, final int chance, final int bookId, final int adenaCount) {
        this.skillId = skillId;
        this.skillLvl = skillLvl;
        this.sp = sp;
        this.chance = chance;
        this.bookId = bookId;
        this.adenaCount = adenaCount;
    }

    @Override
    protected void writeData() {
        //FIXME GraciaEpilogue ddddd dx[dd]
        writeD(unk); // ?
        writeD(skillId);
        writeD(skillLvl);
        writeD(sp);
        writeD(chance);

        writeD(2);
        writeD(57); // adena
        writeD(adenaCount); // adena count ?
        if (bookId > 0) {
            writeD(bookId); // book
            writeD(1); // book count
        } else {
            writeD(SkillTreeTable.NORMAL_ENCHANT_BOOK); // book
            writeD(0); // book count
        }
    }
}