package org.mmocore.gameserver.network.lineage.serverpackets.PremiumShop;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExBR_BuyProduct extends GameServerPacket {
    public static final int RESULT_OK = 1; // ok
    public static final int RESULT_NOT_ENOUGH_POINTS = -1;
    public static final int RESULT_WRONG_PRODUCT = -2; // also -5
    public static final int RESULT_INVENTORY_FULL = -4;
    public static final int RESULT_SALE_PERIOD_ENDED = -7; // also -8
    public static final int RESULT_WRONG_USER_STATE = -9; // also -11
    public static final int RESULT_WRONG_PRODUCT_ITEM = -10;
	
	/*
	 * 	public static final int BR_BUY_SUCCESS = 1; // ok
	public static final int BR_BUY_LACK_OF_POINT = -1;
	public static final int BR_BUY_INVALID_PRODUCT = -2; // also -5
	public static final int BR_BUY_USER_CANCEL = -3;
	public static final int BR_BUY_INVENTROY_OVERFLOW = -4;
	public static final int BR_BUY_CLOSED_PRODUCT = -5;
	public static final int BR_BUY_SERVER_ERROR = -6;
	public static final int BR_BUY_BEFORE_SALE_DATE = -7; // also -8
	public static final int BR_BUY_AFTER_SALE_DATE = -8;
	public static final int BR_BUY_INVALID_USER = -9; // also -11
	public static final int BR_BUY_INVALID_ITEM = -10;
	public static final int BR_BUY_INVALID_USER_STATE = -11;
	public static final int BR_BUY_NOT_DAY_OF_WEEK = -12;
	public static final int BR_BUY_NOT_TIME_OF_DAY = -13;
	public static final int BR_BUY_SOLD_OUT = -14;

	 */

    private final int result;

    public ExBR_BuyProduct(final int result) {
        this.result = result;
    }

    @Override
    protected void writeData() {
        writeD(result);
    }
}