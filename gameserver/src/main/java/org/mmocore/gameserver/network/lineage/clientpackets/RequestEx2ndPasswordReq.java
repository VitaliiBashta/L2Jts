package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.database.dao.impl.CharacterAccessDAO;
import org.mmocore.gameserver.model.CharSelectInfoPackage;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.serverpackets.Ex2ndPasswordAck;

/**
 * @author VISTALL
 */
public class RequestEx2ndPasswordReq extends L2GameClientPacket {
    private int _type;
    private String _password, _newPassword;

    @Override
    protected void readImpl() {
        _type = readC();
        _password = readS();
        if (_type == 2) {
            _newPassword = readS();
        }
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

        switch (_type) {
            case 0:
                if (p.getPassword() != null) {
                    return;
                }

                if (_password.length() < 6 || _password.length() > 8) {
                    client.sendPacket(new Ex2ndPasswordAck(_type, Ex2ndPasswordAck.WRONG_PATTERN));
                    return;
                }

                p.setPassword(_password);
                client.sendPacket(new Ex2ndPasswordAck(_type, Ex2ndPasswordAck.SUCCESS));

                CharacterAccessDAO.getInstance().update(p.getObjectId(), _password);
                break;
            case 2:
                if (p.getPassword() == null) {
                    return;
                }

                if (!p.getPassword().equals(_password)) {
                    client.sendPacket(new Ex2ndPasswordAck(_type, Ex2ndPasswordAck.WRONG_PATTERN));
                    return;
                }

                if (_newPassword.length() < 6 || _newPassword.length() > 8) {
                    client.sendPacket(new Ex2ndPasswordAck(_type, Ex2ndPasswordAck.WRONG_PATTERN));
                    return;
                }

                p.setPassword(_newPassword);
                client.sendPacket(new Ex2ndPasswordAck(_type, Ex2ndPasswordAck.SUCCESS));

                CharacterAccessDAO.getInstance().update(p.getObjectId(), _newPassword);
                break;
        }
    }
}
