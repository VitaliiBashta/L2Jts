package org.mmocore.gameserver.model.base;

public enum SkillMastery {
    NONE,
    ZERO_REUSE {
        @Override
        public boolean hasZeroReuse() {
            return true;
        }
    },
    DOUBLE_TIME {
        @Override
        public boolean hasDoubleTime() {
            return true;
        }
    },
    INC_POWER {
        @Override
        public boolean hasZeroReuse() {
            return true;
        }

        @Override
        public boolean hasIncreasedPower() {
            return true;
        }
    };

    public boolean hasZeroReuse() {
        return false;
    }

    public boolean hasDoubleTime() {
        return false;
    }

    public boolean hasIncreasedPower() {
        return false;
    }
}