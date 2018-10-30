package org.mmocore.gameserver.model.mail;

import org.mmocore.gameserver.object.components.items.ItemInstance;

public class Attachment {
    private int messageId;

    private ItemInstance item;
    private Mail mail;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(final int messageId) {
        this.messageId = messageId;
    }

    public ItemInstance getItem() {
        return item;
    }

    public void setItem(final ItemInstance item) {
        this.item = item;
    }

    public Mail getMail() {
        return mail;
    }

    public void setMail(final Mail mail) {
        this.mail = mail;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        return ((Attachment) o).getItem() == getItem();
    }
}
