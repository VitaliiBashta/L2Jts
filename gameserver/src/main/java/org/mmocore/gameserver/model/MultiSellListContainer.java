package org.mmocore.gameserver.model;


import org.mmocore.gameserver.model.base.MultiSellEntry;

import java.util.ArrayList;
import java.util.List;

public class MultiSellListContainer {
    private final List<MultiSellEntry> _entries = new ArrayList<>();
    private int _listId;
    private boolean _showall = true;
    private boolean _keep_enchanted = false;
    private boolean _is_dutyfree = false;
    private boolean _nokey = false;
    private boolean _allowBBS = false;
    private int _npcObjectId = -1;

    public int getListId() {
        return _listId;
    }

    public void setListId(final int listId) {
        _listId = listId;
    }

    public boolean isShowAll() {
        return _showall;
    }

    public void setShowAll(final boolean bool) {
        _showall = bool;
    }

    public boolean isNoTax() {
        return _is_dutyfree;
    }

    public void setNoTax(final boolean bool) {
        _is_dutyfree = bool;
    }

    public boolean isNoKey() {
        return _nokey;
    }

    public void setNoKey(final boolean bool) {
        _nokey = bool;
    }

    public boolean isKeepEnchant() {
        return _keep_enchanted;
    }

    public void setKeepEnchant(final boolean bool) {
        _keep_enchanted = bool;
    }

    public boolean isBBSAllowed() {
        return _allowBBS;
    }

    public void setBBSAllowed(final boolean bool) {
        _allowBBS = bool;
    }

    public void addEntry(final MultiSellEntry e) {
        _entries.add(e);
    }

    public List<MultiSellEntry> getEntries() {
        return _entries;
    }

    public boolean isEmpty() {
        return _entries.isEmpty();
    }

    public int getNpcObjectId() {
        return _npcObjectId;
    }

    public void setNpcObjectId(final int id) {
        _npcObjectId = id;
    }
}
