package org.mmocore.gameserver.model.mail;

import org.mmocore.commons.database.dao.JdbcEntity;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.database.dao.impl.MailDAO;
import org.mmocore.gameserver.object.components.items.ItemInstance;

import java.util.HashSet;
import java.util.Set;

public class Mail implements JdbcEntity, Comparable<Mail> {
    public static final int DELETED = 0;
    public static final int READED = 1;
    public static final int REJECTED = 2;
    private static final long serialVersionUID = -8704970972611917153L;
    private final Set<ItemInstance> attachments = new HashSet<>();
    private long createdTime;
    private int messageId;
    private int senderId;
    private String senderName;
    private int receiverId;
    private String receiverName;
    private int expireTime;
    private String topic;
    private String body;
    private long price;
    private SenderType _type = SenderType.NORMAL;
    private boolean isUnread;
    private JdbcEntityState _state = JdbcEntityState.CREATED;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(final int messageId) {
        this.messageId = messageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(final int senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(final String senderName) {
        this.senderName = senderName;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(final int receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(final String receiverName) {
        this.receiverName = receiverName;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(final int expireTime) {
        this.expireTime = expireTime;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(final String topic) {
        this.topic = topic;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public boolean isPayOnDelivery() {
        return price > 0L;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(final long price) {
        this.price = price;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(final boolean isUnread) {
        this.isUnread = isUnread;
    }

    public Set<ItemInstance> getAttachments() {
        return attachments;
    }

    public void addAttachment(final ItemInstance item) {
        attachments.add(item);
    }

    public void setCreatedTime(long t) {
        createdTime = t;
    }

    public boolean isNewMail() {
        return (System.currentTimeMillis() - createdTime) < ServerConfig.receiverDelayMail;
    }

    @Override
    public JdbcEntityState getJdbcState() {
        return _state;
    }

    @Override
    public void setJdbcState(final JdbcEntityState state) {
        _state = state;
    }

    public void save() {
        MailDAO.getInstance().save(this);
    }

    public void update() {
        MailDAO.getInstance().update(this);
    }

    public void delete() {
        MailDAO.getInstance().delete(this);
    }

    public Mail reject() {
        final Mail mail = new Mail();
        mail.setSenderId(getReceiverId());
        mail.setSenderName(getReceiverName());
        mail.setReceiverId(getSenderId());
        mail.setReceiverName(getSenderName());
        mail.setTopic(getTopic());
        mail.setBody(getBody());
        synchronized (getAttachments()) {
            getAttachments().forEach(mail::addAttachment);
            getAttachments().clear();
        }
        mail.setType(SenderType.NEWS_INFORMER);
        mail.setUnread(true);
        return mail;
    }

    public Mail reply() {
        final Mail mail = new Mail();
        mail.setSenderId(getReceiverId());
        mail.setSenderName(getReceiverName());
        mail.setReceiverId(getSenderId());
        mail.setReceiverName(getSenderName());
        mail.setTopic("[Re]" + getTopic());
        mail.setBody(getBody());
        mail.setType(SenderType.NEWS_INFORMER);
        mail.setUnread(true);
        return mail;
    }

    public SenderType getType() {
        return _type;
    }

    public void setType(final SenderType type) {
        _type = type;
    }

    @Override
    public int compareTo(final Mail o) {
        return o.getMessageId() - this.getMessageId();
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
        return ((Mail) o).getMessageId() == getMessageId();
    }

    @Override
    public int hashCode() {
        return messageId;
    }

    public enum SenderType {
        NORMAL,
        NEWS_INFORMER,
        NONE,
        BIRTHDAY;

        public static final SenderType[] VALUES = values();
    }
}
