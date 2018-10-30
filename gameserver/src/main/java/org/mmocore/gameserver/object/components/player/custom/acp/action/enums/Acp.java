package org.mmocore.gameserver.object.components.player.custom.acp.action.enums;

import org.mmocore.gameserver.object.Player;

/**
 * Create by Mangol on 01.01.2016.
 */
public enum Acp {
    cp {
        @Override
        public boolean isAuto(final Player player) {
            return player.getAcpComponent().getTemplate().isAutoCp();
        }

        @Override
        public int getItemId(final Player player) {
            return player.getAcpComponent().getTemplate().getCpItemId();
        }

        @Override
        public int getReuse(final Player player) {
            return player.getAcpComponent().getTemplate().getReuseCp();
        }

        @Override
        public double getPercent(final Player player) {
            return player.getAcpComponent().getTemplate().getCpPercent();
        }

        @Override
        public void setReuse(final Player player, final int value) {
            player.getAcpComponent().getTemplate().setReuseCp(value);
        }

        @Override
        public void setItem(final Player player, final int value) {
            player.getAcpComponent().getTemplate().setCpItemId(value);
        }

        @Override
        public void setAuto(final Player player, final boolean value) {
            player.getAcpComponent().getTemplate().setAutoCp(value);
        }

        @Override
        public void setPercent(final Player player, final double value) {
            player.getAcpComponent().getTemplate().setCpPercent(value);
        }

        @Override
        public void start(final Player player, final boolean value) {
            player.getAcpComponent().startAndStopAutoCp(value);
        }
    },
    small_cp {
        @Override
        public boolean isAuto(final Player player) {
            return player.getAcpComponent().getTemplate().isAutoSmallCp();
        }

        @Override
        public int getItemId(final Player player) {
            return player.getAcpComponent().getTemplate().getSmallCpItemId();
        }

        @Override
        public int getReuse(final Player player) {
            return player.getAcpComponent().getTemplate().getReuseSmallCp();
        }

        @Override
        public double getPercent(final Player player) {
            return player.getAcpComponent().getTemplate().getSmallCpPercent();
        }

        @Override
        public void setReuse(final Player player, final int value) {
            player.getAcpComponent().getTemplate().setReuseSmallCp(value);
        }

        @Override
        public void setItem(final Player player, final int value) {
            player.getAcpComponent().getTemplate().setSmallCpItemId(value);
        }

        @Override
        public void setAuto(final Player player, final boolean value) {
            player.getAcpComponent().getTemplate().setAutoSmallCp(value);
        }

        @Override
        public void setPercent(final Player player, final double value) {
            player.getAcpComponent().getTemplate().setSmallCpPercent(value);
        }

        @Override
        public void start(final Player player, final boolean value) {
            player.getAcpComponent().startAndStopAutoSmallCp(value);
        }
    },
    hp {
        @Override
        public boolean isAuto(final Player player) {
            return player.getAcpComponent().getTemplate().isAutoHp();
        }

        @Override
        public int getItemId(final Player player) {
            return player.getAcpComponent().getTemplate().getHpItemId();
        }

        @Override
        public int getReuse(final Player player) {
            return player.getAcpComponent().getTemplate().getReuseHp();
        }

        @Override
        public double getPercent(final Player player) {
            return player.getAcpComponent().getTemplate().getHpPercent();
        }

        @Override
        public void setReuse(final Player player, final int value) {
            player.getAcpComponent().getTemplate().setReuseHp(value);
        }

        @Override
        public void setItem(final Player player, final int value) {
            player.getAcpComponent().getTemplate().setHpItemId(value);
        }

        @Override
        public void setAuto(final Player player, final boolean value) {
            player.getAcpComponent().getTemplate().setAutoHp(value);
        }

        @Override
        public void setPercent(final Player player, final double value) {
            player.getAcpComponent().getTemplate().setHpPercent(value);
        }

        @Override
        public void start(final Player player, final boolean value) {
            player.getAcpComponent().startAndStopAutoHp(value);
        }
    },
    mp {
        @Override
        public boolean isAuto(final Player player) {
            return player.getAcpComponent().getTemplate().isAutoMp();
        }

        @Override
        public int getItemId(final Player player) {
            return player.getAcpComponent().getTemplate().getMpItemId();
        }

        @Override
        public int getReuse(final Player player) {
            return player.getAcpComponent().getTemplate().getReuseMp();
        }

        @Override
        public double getPercent(final Player player) {
            return player.getAcpComponent().getTemplate().getMpPercent();
        }

        @Override
        public void setReuse(final Player player, final int value) {
            player.getAcpComponent().getTemplate().setReuseMp(value);
        }

        @Override
        public void setItem(final Player player, final int value) {
            player.getAcpComponent().getTemplate().setMpItemId(value);
        }

        @Override
        public void setAuto(final Player player, final boolean value) {
            player.getAcpComponent().getTemplate().setAutoMp(value);
        }

        @Override
        public void setPercent(final Player player, final double value) {
            player.getAcpComponent().getTemplate().setMpPercent(value);
        }

        @Override
        public void start(final Player player, final boolean value) {
            player.getAcpComponent().startAndStopAutoMp(value);
        }
    };

    public abstract boolean isAuto(final Player player);

    public abstract int getItemId(final Player player);

    public abstract int getReuse(final Player player);

    public abstract double getPercent(final Player player);

    public abstract void setReuse(final Player player, final int value);

    public abstract void setItem(final Player player, final int value);

    public abstract void setAuto(final Player player, final boolean value);

    public abstract void setPercent(final Player player, final double value);

    public abstract void start(final Player player, final boolean value);
}
