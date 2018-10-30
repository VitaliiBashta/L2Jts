package org.mmocore.gameserver.network.lineage.clientpackets;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.model.CharSelectInfoPackage;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.serverpackets.Ex2ndPasswordVerify;
import org.mmocore.gameserver.skills.TimeStamp;

/**
 * @author VISTALL
 */
public class RequestEx2ndPasswordVerify extends L2GameClientPacket {
    protected static final TIntObjectMap<TimeStamp> BAN_INFO = new TIntObjectHashMap<>();

    private String password;

    @Override
    protected void readImpl() {
        password = readS();
    }

    @Override
    protected void runImpl() {
        if (!ExtConfig.EX_2ND_PASSWORD_CHECK) {
            return;
        }

        final GameClient client = getClient();

        final CharSelectInfoPackage p = ArrayUtils.valid(client.getPlayers(), client.getSelectedIndex());
        if (p == null) {
            return;
        }

        TimeStamp info = BAN_INFO.get(p.getObjectId());
        if (info != null && info.getEndTime() > 0 && info.hasNotPassed()) {
            client.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_BAN, info.getLevel()));
            return;
        }

        if (p.getPassword().equals(password)) {
            BAN_INFO.remove(p.getObjectId());

            p.setPasswordChecked(true);
            client.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_OK, 0));
            client.playerSelected(client.getSelectedIndex());
        } else {
            info = info == null ? new TimeStamp(p.getObjectId(), 0, 0) : info;
            if (info.getLevel() == 0) {
                BAN_INFO.put(p.getObjectId(), info);
            }

            info.setLevel(info.getLevel() + 1);

            if (info.getLevel() >= 5) {
                info.setEndTime(System.currentTimeMillis() + 691200000L);
                client.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_BAN, info.getLevel()));
            } else {
                client.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_WRONG, info.getLevel()));
            }
        }
    }
}
