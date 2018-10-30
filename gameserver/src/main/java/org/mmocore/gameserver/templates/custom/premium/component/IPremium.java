package org.mmocore.gameserver.templates.custom.premium.component;

import java.util.Map;
import java.util.Optional;

/**
 * @author Mangol
 * @since 26.03.2016
 */
public interface IPremium {
    void addTime(final PremiumTime time);

    Optional<PremiumTime> getTime(final int id);

    Map<Integer, PremiumTime> getTimes();
}
