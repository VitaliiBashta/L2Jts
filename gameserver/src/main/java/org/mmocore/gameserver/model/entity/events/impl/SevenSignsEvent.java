package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;

import java.time.Instant;

public class SevenSignsEvent extends Event {
    private final String BLACKSMITH_MAMMON = "blacksmithMammon";
    private final String MERCHAT_MAMMON = "merchantMammon";
    private final String ORATOR_AND_PREACHER = "oratorAndPreacher";
    private final String LILITH = "lilith";
    private final String ANAKIM = "anakim";
    private final String CREST_DAWN = "crestOfDawn";
    private final String CREST_DUSK = "crestOfDusk";
    private final String SACRIFICE_COMPETITION = "necropolisOfSacrificePeriodCompetition";
    private final String SACRIFICE_RESULT_CABAL_NULL = "necropolisOfSacrificePeriodResultNull";
    private final String SACRIFICE_RESULT_CABAL_DAWN = "necropolisOfSacrificePeriodResultDawn";
    private final String SACRIFICE_RESULT_CABAL_DUSC = "necropolisOfSacrificePeriodResultDusc";
    private final String WORSHIP_COMPETITION = "necropolisOfWorshipPeriodCompetition";
    private final String WORSHIP_RESULT_CABAL_NULL = "necropolisOfWorshipPeriodResultNull";
    private final String WORSHIP_RESULT_CABAL_DAWN = "necropolisOfWorshipPeriodResultDawn";
    private final String WORSHIP_RESULT_CABAL_DUSC = "necropolisOfWorshipPeriodResultDusc";
    private final String SAINT_COMPETITION = "necropolisOfSaintPeriodCompetition";
    private final String SAINT_RESULT_CABAL_NULL = "necropolisOfSaintPeriodResultNull";
    private final String SAINT_RESULT_CABAL_DAWN = "necropolisOfSaintPeriodResultDawn";
    private final String SAINT_RESULT_CABAL_DUSC = "necropolisOfSaintPeriodResultDusc";
    private final String PILGRIM_COMPETITION = "necropolisOfPilgrimPeriodCompetition";
    private final String PILGRIM_RESULT_CABAL_NULL = "necropolisOfPilgrimPeriodResultNull";
    private final String PILGRIM_RESULT_CABAL_DAWN = "necropolisOfPilgrimPeriodResultDawn";
    private final String PILGRIM_RESULT_CABAL_DUSC = "necropolisOfPilgrimPeriodResultDusc";
    private final String PATRIOT_COMPETITION = "necropolisOfPatriotPeriodCompetition";
    private final String PATRIOT_RESULT_CABAL_NULL = "necropolisOfPatriotPeriodResultNull";
    private final String PATRIOT_RESULT_CABAL_DAWN = "necropolisOfPatriotPeriodResultDawn";
    private final String PATRIOT_RESULT_CABAL_DUSC = "necropolisOfPatriotPeriodResultDusc";
    private final String MARTYRDOM_COMPETITION = "necropolisOfMartyrdomPeriodCompetition";
    private final String MARTYRDOM_RESULT_CABAL_NULL = "necropolisOfMartyrdomPeriodResultNull";
    private final String MARTYRDOM_RESULT_CABAL_DAWN = "necropolisOfMartyrdomPeriodResultDawn";
    private final String MARTYRDOM_RESULT_CABAL_DUSC = "necropolisOfMartyrdomPeriodResultDusc";
    private final String DISCIPLE_COMPETITION = "necropolisOfDisciplePeriodCompetition";
    private final String DISCIPLE_RESULT_CABAL_NULL = "necropolisOfDisciplePeriodResultNull";
    private final String DISCIPLE_RESULT_CABAL_DAWN = "necropolisOfDisciplePeriodResultDawn";
    private final String DISCIPLE_RESULT_CABAL_DUSC = "necropolisOfDisciplePeriodResultDusc";
    private final String DEVOTION_COMPETITION = "necropolisOfDevotionPeriodCompetition";
    private final String DEVOTION_RESULT_CABAL_NULL = "necropolisOfDevotionPeriodResultNull";
    private final String DEVOTION_RESULT_CABAL_DAWN = "necropolisOfDevotionPeriodResultDawn";
    private final String DEVOTION_RESULT_CABAL_DUSC = "necropolisOfDevotionPeriodResultDusc";
    private final String HERETIC_COMPETITION = "catacombHereticPeriodCompetition";
    private final String HERETIC_RESULT_CABAL_NULL = "catacombHereticPeriodResultNull";
    private final String HERETIC_RESULT_CABAL_DAWN = "catacombHereticPeriodResultDawn";
    private final String HERETIC_RESULT_CABAL_DUSC = "catacombHereticPeriodResultDusc";
    private final String FORBIDDENPATH_COMPETITION = "catacombForbiddenPathPeriodCompetition";
    private final String FORBIDDENPATH_RESULT_CABAL_NULL = "catacombForbiddenPathPeriodResultNull";
    private final String FORBIDDENPATH_RESULT_CABAL_DAWN = "catacombForbiddenPathPeriodResultDawn";
    private final String FORBIDDENPATH_RESULT_CABAL_DUSC = "catacombForbiddenPathPeriodResultDusc";
    private final String DARKOMENS_COMPETITION = "catacombDarkOmensPeriodCompetition";
    private final String DARKOMENS_RESULT_CABAL_NULL = "catacombDarkOmensPeriodResultNull";
    private final String DARKOMENS_RESULT_CABAL_DAWN = "catacombDarkOmensPeriodResultDawn";
    private final String DARKOMENS_RESULT_CABAL_DUSC = "catacombDarkOmensPeriodResultDusc";
    private final String BRANDED_COMPETITION = "catacombBrandedPeriodCompetition";
    private final String BRANDED_RESULT_CABAL_NULL = "catacombBrandedPeriodResultNull";
    private final String BRANDED_RESULT_CABAL_DAWN = "catacombBrandedPeriodResultDawn";
    private final String BRANDED_RESULT_CABAL_DUSC = "catacombBrandedPeriodResultDusc";
    private final String APOSTATE_COMPETITION = "catacombApostatePeriodCompetition";
    private final String APOSTATE_RESULT_CABAL_NULL = "catacombApostatePeriodResultNull";
    private final String APOSTATE_RESULT_CABAL_DAWN = "catacombApostatePeriodResultDawn";
    private final String APOSTATE_RESULT_CABAL_DUSC = "catacombApostatePeriodResultDusc";
    private final String WITCH_COMPETITION = "catacombWitchPeriodCompetition";
    private final String WITCH_RESULT_CABAL_NULL = "catacombWitchPeriodResultNull";
    private final String WITCH_RESULT_CABAL_DAWN = "catacombWitchPeriodResultDawn";
    private final String WITCH_RESULT_CABAL_DUSC = "catacombWitchPeriodResultDusc";
    private final String SPIRIT_IN = "spiritIn";
    private final String SPIRIT_OUT = "spiritOut";

    public SevenSignsEvent(final MultiValueSet<String> set) {
        super(set);
    }

    @Override
    public void reCalcNextTime(boolean onStart) {
    }

    public void spawnNecropolisSacrifice() {
        spawnAction(SACRIFICE_RESULT_CABAL_NULL, false);
        spawnAction(SACRIFICE_RESULT_CABAL_DAWN, false);
        spawnAction(SACRIFICE_RESULT_CABAL_DUSC, false);
        spawnAction(SACRIFICE_COMPETITION, false);
        if (SevenSigns.getInstance().isRectuiting() || SevenSigns.getInstance().isCompetition()) {
            spawnAction(SACRIFICE_COMPETITION, true);
        } else if (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) {
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE) == SevenSigns.getInstance().getCabalHighestScore()) {
                switch (SevenSigns.getInstance().getCabalHighestScore()) {
                    case SevenSigns.CABAL_NULL: {
                        spawnAction(SACRIFICE_RESULT_CABAL_NULL, true);
                        break;
                    }
                    case SevenSigns.CABAL_DAWN: {
                        spawnAction(SACRIFICE_RESULT_CABAL_DAWN, true);
                        break;
                    }
                    case SevenSigns.CABAL_DUSK: {
                        spawnAction(SACRIFICE_RESULT_CABAL_DUSC, true);
                        break;
                    }
                }
            }
        }
    }

    public void spawnNecropolisDevotion() {
        spawnAction(DEVOTION_RESULT_CABAL_NULL, false);
        spawnAction(DEVOTION_RESULT_CABAL_DAWN, false);
        spawnAction(DEVOTION_RESULT_CABAL_DUSC, false);
        spawnAction(DEVOTION_COMPETITION, false);
        if (SevenSigns.getInstance().isRectuiting() || SevenSigns.getInstance().isCompetition()) {
            spawnAction(DEVOTION_COMPETITION, true);
        } else if (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) {
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE) == SevenSigns.getInstance().getCabalHighestScore()) {
                switch (SevenSigns.getInstance().getCabalHighestScore()) {
                    case SevenSigns.CABAL_NULL: {
                        spawnAction(DEVOTION_RESULT_CABAL_NULL, true);
                        break;
                    }
                    case SevenSigns.CABAL_DAWN: {
                        spawnAction(DEVOTION_RESULT_CABAL_DAWN, true);
                        break;
                    }
                    case SevenSigns.CABAL_DUSK: {
                        spawnAction(DEVOTION_RESULT_CABAL_DUSC, true);
                        break;
                    }
                }
            }
        }
    }

    public void spawnNecropolisPatriot() {
        spawnAction(PATRIOT_RESULT_CABAL_NULL, false);
        spawnAction(PATRIOT_RESULT_CABAL_DAWN, false);
        spawnAction(PATRIOT_RESULT_CABAL_DUSC, false);
        spawnAction(PATRIOT_COMPETITION, false);
        if (SevenSigns.getInstance().isRectuiting() || SevenSigns.getInstance().isCompetition()) {
            spawnAction(PATRIOT_COMPETITION, true);
        } else if (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) {
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE) == SevenSigns.getInstance().getCabalHighestScore()) {
                switch (SevenSigns.getInstance().getCabalHighestScore()) {
                    case SevenSigns.CABAL_NULL: {
                        spawnAction(PATRIOT_RESULT_CABAL_NULL, true);
                        break;
                    }
                    case SevenSigns.CABAL_DAWN: {
                        spawnAction(PATRIOT_RESULT_CABAL_DAWN, true);
                        break;
                    }
                    case SevenSigns.CABAL_DUSK: {
                        spawnAction(PATRIOT_RESULT_CABAL_DUSC, true);
                        break;
                    }
                }
            }
        }
    }

    public void spawnNecropolisPilgrim() {
        spawnAction(PILGRIM_RESULT_CABAL_NULL, false);
        spawnAction(PILGRIM_RESULT_CABAL_DAWN, false);
        spawnAction(PILGRIM_RESULT_CABAL_DUSC, false);
        spawnAction(PILGRIM_COMPETITION, false);
        if (SevenSigns.getInstance().isRectuiting() || SevenSigns.getInstance().isCompetition()) {
            spawnAction(PILGRIM_COMPETITION, true);
        } else if (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) {
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE) == SevenSigns.getInstance().getCabalHighestScore()) {
                switch (SevenSigns.getInstance().getCabalHighestScore()) {
                    case SevenSigns.CABAL_NULL: {
                        spawnAction(PILGRIM_RESULT_CABAL_NULL, true);
                        break;
                    }
                    case SevenSigns.CABAL_DAWN: {
                        spawnAction(PILGRIM_RESULT_CABAL_DAWN, true);
                        break;
                    }
                    case SevenSigns.CABAL_DUSK: {
                        spawnAction(PILGRIM_RESULT_CABAL_DUSC, true);
                        break;
                    }
                }
            }
        }
    }

    public void spawnNecropolisSaint() {
        spawnAction(SAINT_RESULT_CABAL_NULL, false);
        spawnAction(SAINT_RESULT_CABAL_DAWN, false);
        spawnAction(SAINT_RESULT_CABAL_DUSC, false);
        spawnAction(SAINT_COMPETITION, false);
        if (SevenSigns.getInstance().isRectuiting() || SevenSigns.getInstance().isCompetition()) {
            spawnAction(SAINT_COMPETITION, true);
        } else if (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) {
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE) == SevenSigns.getInstance().getCabalHighestScore()) {
                switch (SevenSigns.getInstance().getCabalHighestScore()) {
                    case SevenSigns.CABAL_NULL: {
                        spawnAction(SAINT_RESULT_CABAL_NULL, true);
                        break;
                    }
                    case SevenSigns.CABAL_DAWN: {
                        spawnAction(SAINT_RESULT_CABAL_DAWN, true);
                        break;
                    }
                    case SevenSigns.CABAL_DUSK: {
                        spawnAction(SAINT_RESULT_CABAL_DUSC, true);
                        break;
                    }
                }
            }
        }
    }

    public void spawnNecropolisWorship() {
        spawnAction(WORSHIP_RESULT_CABAL_NULL, false);
        spawnAction(WORSHIP_RESULT_CABAL_DAWN, false);
        spawnAction(WORSHIP_RESULT_CABAL_DUSC, false);
        spawnAction(WORSHIP_COMPETITION, false);
        if (SevenSigns.getInstance().isRectuiting() || SevenSigns.getInstance().isCompetition()) {
            spawnAction(WORSHIP_COMPETITION, true);
        } else if (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) {
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE) == SevenSigns.getInstance().getCabalHighestScore()) {
                switch (SevenSigns.getInstance().getCabalHighestScore()) {
                    case SevenSigns.CABAL_NULL: {
                        spawnAction(WORSHIP_RESULT_CABAL_NULL, true);
                        break;
                    }
                    case SevenSigns.CABAL_DAWN: {
                        spawnAction(WORSHIP_RESULT_CABAL_DAWN, true);
                        break;
                    }
                    case SevenSigns.CABAL_DUSK: {
                        spawnAction(WORSHIP_RESULT_CABAL_DUSC, true);
                        break;
                    }
                }
            }
        }
    }

    public void spawnNecropolisMartydom() {
        spawnAction(MARTYRDOM_RESULT_CABAL_NULL, false);
        spawnAction(MARTYRDOM_RESULT_CABAL_DAWN, false);
        spawnAction(MARTYRDOM_RESULT_CABAL_DUSC, false);
        spawnAction(MARTYRDOM_COMPETITION, false);
        if (SevenSigns.getInstance().isRectuiting() || SevenSigns.getInstance().isCompetition()) {
            spawnAction(MARTYRDOM_COMPETITION, true);
        } else if (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) {
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE) == SevenSigns.getInstance().getCabalHighestScore()) {
                switch (SevenSigns.getInstance().getCabalHighestScore()) {
                    case SevenSigns.CABAL_NULL: {
                        spawnAction(MARTYRDOM_RESULT_CABAL_NULL, true);
                        break;
                    }
                    case SevenSigns.CABAL_DAWN: {
                        spawnAction(MARTYRDOM_RESULT_CABAL_DAWN, true);
                        break;
                    }
                    case SevenSigns.CABAL_DUSK: {
                        spawnAction(MARTYRDOM_RESULT_CABAL_DUSC, true);
                        break;
                    }
                }
            }
        }
    }

    public void spawnNecropolisDisciple() {
        spawnAction(DISCIPLE_RESULT_CABAL_NULL, false);
        spawnAction(DISCIPLE_RESULT_CABAL_DAWN, false);
        spawnAction(DISCIPLE_RESULT_CABAL_DUSC, false);
        spawnAction(DISCIPLE_COMPETITION, false);
        if (SevenSigns.getInstance().isRectuiting() || SevenSigns.getInstance().isCompetition()) {
            spawnAction(DISCIPLE_COMPETITION, true);
        } else if (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) {
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE) == SevenSigns.getInstance().getCabalHighestScore()) {
                switch (SevenSigns.getInstance().getCabalHighestScore()) {
                    case SevenSigns.CABAL_NULL: {
                        spawnAction(DISCIPLE_RESULT_CABAL_NULL, true);
                        break;
                    }
                    case SevenSigns.CABAL_DAWN: {
                        spawnAction(DISCIPLE_RESULT_CABAL_DAWN, true);
                        break;
                    }
                    case SevenSigns.CABAL_DUSK: {
                        spawnAction(DISCIPLE_RESULT_CABAL_DUSC, true);
                        break;
                    }
                }
            }
        }
    }

    public void spawnCatacombDarkOmen() {
        spawnAction(DARKOMENS_RESULT_CABAL_NULL, false);
        spawnAction(DARKOMENS_RESULT_CABAL_DAWN, false);
        spawnAction(DARKOMENS_RESULT_CABAL_DUSC, false);
        spawnAction(DARKOMENS_COMPETITION, false);
        if (SevenSigns.getInstance().isRectuiting() || SevenSigns.getInstance().isCompetition()) {
            spawnAction(DARKOMENS_COMPETITION, true);
        } else if (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) {
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS) == SevenSigns.getInstance().getCabalHighestScore()) {
                switch (SevenSigns.getInstance().getCabalHighestScore()) {
                    case SevenSigns.CABAL_NULL: {
                        spawnAction(DARKOMENS_RESULT_CABAL_NULL, true);
                        break;
                    }
                    case SevenSigns.CABAL_DAWN: {
                        spawnAction(DARKOMENS_RESULT_CABAL_DAWN, true);
                        break;
                    }
                    case SevenSigns.CABAL_DUSK: {
                        spawnAction(DARKOMENS_RESULT_CABAL_DUSC, true);
                        break;
                    }
                }
            }
        }
    }

    public void spawnCatacombBranded() {
        spawnAction(BRANDED_RESULT_CABAL_NULL, false);
        spawnAction(BRANDED_RESULT_CABAL_DAWN, false);
        spawnAction(BRANDED_RESULT_CABAL_DUSC, false);
        spawnAction(BRANDED_COMPETITION, false);
        if (SevenSigns.getInstance().isRectuiting() || SevenSigns.getInstance().isCompetition()) {
            spawnAction(BRANDED_COMPETITION, true);
        } else if (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) {
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS) == SevenSigns.getInstance().getCabalHighestScore()) {
                switch (SevenSigns.getInstance().getCabalHighestScore()) {
                    case SevenSigns.CABAL_NULL: {
                        spawnAction(BRANDED_RESULT_CABAL_NULL, true);
                        break;
                    }
                    case SevenSigns.CABAL_DAWN: {
                        spawnAction(BRANDED_RESULT_CABAL_DAWN, true);
                        break;
                    }
                    case SevenSigns.CABAL_DUSK: {
                        spawnAction(BRANDED_RESULT_CABAL_DUSC, true);
                        break;
                    }
                }
            }
        }
    }

    public void spawnCatacombHeretic() {
        spawnAction(HERETIC_RESULT_CABAL_NULL, false);
        spawnAction(HERETIC_RESULT_CABAL_DAWN, false);
        spawnAction(HERETIC_RESULT_CABAL_DUSC, false);
        spawnAction(HERETIC_COMPETITION, false);
        if (SevenSigns.getInstance().isRectuiting() || SevenSigns.getInstance().isCompetition()) {
            spawnAction(HERETIC_COMPETITION, true);
        } else if (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) {
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS) == SevenSigns.getInstance().getCabalHighestScore()) {
                switch (SevenSigns.getInstance().getCabalHighestScore()) {
                    case SevenSigns.CABAL_NULL: {
                        spawnAction(HERETIC_RESULT_CABAL_NULL, true);
                        break;
                    }
                    case SevenSigns.CABAL_DAWN: {
                        spawnAction(HERETIC_RESULT_CABAL_DAWN, true);
                        break;
                    }
                    case SevenSigns.CABAL_DUSK: {
                        spawnAction(HERETIC_RESULT_CABAL_DUSC, true);
                        break;
                    }
                }
            }
        }
    }

    public void spawnCatacombApostate() {
        spawnAction(APOSTATE_RESULT_CABAL_NULL, false);
        spawnAction(APOSTATE_RESULT_CABAL_DAWN, false);
        spawnAction(APOSTATE_RESULT_CABAL_DUSC, false);
        spawnAction(APOSTATE_COMPETITION, false);
        if (SevenSigns.getInstance().isRectuiting() || SevenSigns.getInstance().isCompetition()) {
            spawnAction(APOSTATE_COMPETITION, true);
        } else if (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) {
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS) == SevenSigns.getInstance().getCabalHighestScore()) {
                switch (SevenSigns.getInstance().getCabalHighestScore()) {
                    case SevenSigns.CABAL_NULL: {
                        spawnAction(APOSTATE_RESULT_CABAL_NULL, true);
                        break;
                    }
                    case SevenSigns.CABAL_DAWN: {
                        spawnAction(APOSTATE_RESULT_CABAL_DAWN, true);
                        break;
                    }
                    case SevenSigns.CABAL_DUSK: {
                        spawnAction(APOSTATE_RESULT_CABAL_DUSC, true);
                        break;
                    }
                }
            }
        }
    }

    public void spawnCatacombForbidenPath() {
        spawnAction(FORBIDDENPATH_RESULT_CABAL_NULL, false);
        spawnAction(FORBIDDENPATH_RESULT_CABAL_DAWN, false);
        spawnAction(FORBIDDENPATH_RESULT_CABAL_DUSC, false);
        spawnAction(FORBIDDENPATH_COMPETITION, false);
        if (SevenSigns.getInstance().isRectuiting() || SevenSigns.getInstance().isCompetition()) {
            spawnAction(FORBIDDENPATH_COMPETITION, true);
        } else if (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) {
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS) == SevenSigns.getInstance().getCabalHighestScore()) {
                switch (SevenSigns.getInstance().getCabalHighestScore()) {
                    case SevenSigns.CABAL_NULL: {
                        spawnAction(FORBIDDENPATH_RESULT_CABAL_NULL, true);
                        break;
                    }
                    case SevenSigns.CABAL_DAWN: {
                        spawnAction(FORBIDDENPATH_RESULT_CABAL_DAWN, true);
                        break;
                    }
                    case SevenSigns.CABAL_DUSK: {
                        spawnAction(FORBIDDENPATH_RESULT_CABAL_DUSC, true);
                        break;
                    }
                }
            }
        }
    }

    public void spawnCatacombWitch() {
        spawnAction(WITCH_RESULT_CABAL_NULL, false);
        spawnAction(WITCH_RESULT_CABAL_DAWN, false);
        spawnAction(WITCH_RESULT_CABAL_DUSC, false);
        spawnAction(WITCH_COMPETITION, false);
        if (SevenSigns.getInstance().isRectuiting() || SevenSigns.getInstance().isCompetition()) {
            spawnAction(WITCH_COMPETITION, true);
        } else if (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) {
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS) == SevenSigns.getInstance().getCabalHighestScore()) {
                switch (SevenSigns.getInstance().getCabalHighestScore()) {
                    case SevenSigns.CABAL_NULL: {
                        spawnAction(WITCH_RESULT_CABAL_NULL, true);
                        break;
                    }
                    case SevenSigns.CABAL_DAWN: {
                        spawnAction(WITCH_RESULT_CABAL_DAWN, true);
                        break;
                    }
                    case SevenSigns.CABAL_DUSK: {
                        spawnAction(WITCH_RESULT_CABAL_DUSC, true);
                        break;
                    }
                }
            }
        }
    }

    public void spawnSevenSignsNPC() {
        spawnAction(BLACKSMITH_MAMMON, false);
        spawnAction(MERCHAT_MAMMON, false);
        spawnAction(LILITH, false);
        spawnAction(ANAKIM, false);
        spawnAction(CREST_DAWN, false);
        spawnAction(CREST_DUSK, false);
        spawnAction(ORATOR_AND_PREACHER, false);
        spawnAction(SPIRIT_IN, false);
        spawnAction(SPIRIT_OUT, false);
        if (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) {
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS) == SevenSigns.getInstance().getCabalHighestScore() && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS) != SevenSigns.CABAL_NULL) {
                spawnAction(BLACKSMITH_MAMMON, true);
                spawnAction(ORATOR_AND_PREACHER, true);
            }
            if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE) == SevenSigns.getInstance().getCabalHighestScore() && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE) != SevenSigns.CABAL_NULL) {
                spawnAction(MERCHAT_MAMMON, true);
                spawnAction(SPIRIT_IN, true);
                spawnAction(SPIRIT_OUT, true);
                switch (SevenSigns.getInstance().getCabalHighestScore()) {
                    case SevenSigns.CABAL_DAWN: {
                        spawnAction(LILITH, true);
                        spawnAction(CREST_DAWN, true);
                        break;
                    }
                    case SevenSigns.CABAL_DUSK: {
                        spawnAction(ANAKIM, true);
                        spawnAction(CREST_DUSK, true);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public EventType getType() {
        return EventType.MAIN_EVENT;
    }

    @Override
    protected Instant startTime() {
        return null;
    }
}
