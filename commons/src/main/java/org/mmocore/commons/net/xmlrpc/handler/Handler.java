package org.mmocore.commons.net.xmlrpc.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KilRoy
 */
public abstract class Handler {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Handler.class);
    protected static final String OK = ReturnMessage.OK.name();
    protected static final String NOT_FIND = ReturnMessage.NOT_FIND.name();
    protected static final String NOT_VALID = ReturnMessage.NOT_VALID.name();
    protected static final String ERROR = ReturnMessage.ERROR.name();
    protected static final String ACC_BANNED = ReturnMessage.ACC_BANNED.name();
    protected static final String EMAIL_NOT_CHECKED = ReturnMessage.EMAIL_NOT_CHECKED.name();
    protected static final String IP_LOCK_NOT_VALID = ReturnMessage.IP_LOCK_NOT_VALID.name();
    protected static final String FIND_RESULT = ReturnMessage.FIND_RESULT.name();
}