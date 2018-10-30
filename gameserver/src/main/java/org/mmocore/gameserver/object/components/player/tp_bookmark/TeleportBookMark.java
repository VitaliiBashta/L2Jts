package org.mmocore.gameserver.object.components.player.tp_bookmark;

import org.mmocore.gameserver.utils.Location;

public class TeleportBookMark extends Location {
    private int _icon;
    private String _name, _acronym;

    public TeleportBookMark(int x, int y, int z, int aicon, String aname, String aacronym) {
        super(x, y, z);
        setIcon(aicon);
        setName(aname);
        setAcronym(aacronym);
    }

    public int getIcon() {
        return _icon;
    }

    public void setIcon(int val) {
        _icon = val;
    }

    public String getName() {
        return _name;
    }

    public void setName(String val) {
        _name = val;
    }

    public String getAcronym() {
        return _acronym;
    }

    public void setAcronym(String val) {
        _acronym = val;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) {
            return false;
        }

        TeleportBookMark tp = (TeleportBookMark) o;

        return tp.getName().equals(getName()) && tp.getX() == getX() && tp.getY() == getY() && tp.getZ() == getZ();
    }
}