// ----------------------------------------------------------------------------
// This file is not part of GNU Crypto
//
// Tiger160 has been derived from the Tiger class in GNU Crypto,
// and has been developed by jonelo.
// ----------------------------------------------------------------------------

package org.mmocore.commons.crypt.adapt.gnu.crypto.hash;

import org.mmocore.commons.crypt.adapt.gnu.crypto.Registry;

public class Tiger160 extends Tiger {
    /**
     * Trivial 0-arguments constructor.
     */
    public Tiger160() {
        super();
        name = Registry.TIGER160_HASH;
    }

    /**
     * Private copying constructor for cloning.
     *
     * @param that The instance being cloned.
     */
    private Tiger160(final Tiger160 that) {
        this();
        this.a = that.a;
        this.b = that.b;
        this.c = that.c;
        this.count = that.count;
        this.buffer = (that.buffer != null) ? that.buffer.clone() : null;
    }

    @Override
    public Object clone() {
        return new Tiger160(this);
    }


    @Override
    protected byte[] getResult() {
        return new byte[]{
                (byte) a, (byte) (a >>> 8), (byte) (a >>> 16), (byte) (a >>> 24), (byte) (a >>> 32), (byte) (a >>> 40), (byte) (a >>> 48),
                (byte) (a >>> 56), (byte) b, (byte) (b >>> 8), (byte) (b >>> 16), (byte) (b >>> 24), (byte) (b >>> 32), (byte) (b >>> 40),
                (byte) (b >>> 48), (byte) (b >>> 56), (byte) c, (byte) (c >>> 8), (byte) (c >>> 16), (byte) (c >>> 24)
        };
    }

}
