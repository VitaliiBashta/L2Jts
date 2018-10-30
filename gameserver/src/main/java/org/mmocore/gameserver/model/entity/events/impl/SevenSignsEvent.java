package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;

import java.time.Instant;

public class SevenSignsEvent extends Event {

    public SevenSignsEvent(final MultiValueSet<String> set) {
        super(set);
    }

    @Override
    public void reCalcNextTime(boolean onStart) {
    }

    public void spawnNecropolisSacrifice() {
        String SACRIFICE_RESULT_CABAL_NULL = "necropolisOfSacrificePeriodResultNull";
        spawnAction(SACRIFICE_RESULT_CABAL_NULL, false);
        String SACRIFICE_RESULT_CABAL_DAWN = "necropolisOfSacrificePeriodResultDawn";
        spawnAction(SACRIFICE_RESULT_CABAL_DAWN, false);
        String SACRIFICE_RESULT_CABAL_DUSC = "necropolisOfSacrificePeriodResultDusc";
        spawnAction(SACRIFICE_RESULT_CABAL_DUSC, false);
        String SACRIFICE_COMPETITION = "necropolisOfSacrificePeriodCompetition";
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
        String DEVOTION_RESULT_CABAL_NULL = "necropolisOfDevotionPeriodResultNull";
        spawnAction(DEVOTION_RESULT_CABAL_NULL, false);
        String DEVOTION_RESULT_CABAL_DAWN = "necropolisOfDevotionPeriodResultDawn";
        spawnAction(DEVOTION_RESULT_CABAL_DAWN, false);
        String DEVOTION_RESULT_CABAL_DUSC = "necropolisOfDevotionPeriodResultDusc";
        spawnAction(DEVOTION_RESULT_CABAL_DUSC, false);
        String DEVOTION_COMPETITION = "necropolisOfDevotionPeriodCompetition";
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
        String PATRIOT_RESULT_CABAL_NULL = "necropolisOfPatriotPeriodResultNull";
        spawnAction(PATRIOT_RESULT_CABAL_NULL, false);
        String PATRIOT_RESULT_CABAL_DAWN = "necropolisOfPatriotPeriodResultDawn";
        spawnAction(PATRIOT_RESULT_CABAL_DAWN, false);
        String PATRIOT_RESULT_CABAL_DUSC = "necropolisOfPatriotPeriodResultDusc";
        spawnAction(PATRIOT_RESULT_CABAL_DUSC, false);
        String PATRIOT_COMPETITION = "necropolisOfPatriotPeriodCompetition";
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
        String PILGRIM_RESULT_CABAL_NULL = "necropolisOfPilgrimPeriodResultNull";
        spawnAction(PILGRIM_RESULT_CABAL_NULL, false);
        String PILGRIM_RESULT_CABAL_DAWN = "necropolisOfPilgrimPeriodResultDawn";
        spawnAction(PILGRIM_RESULT_CABAL_DAWN, false);
        String PILGRIM_RESULT_CABAL_DUSC = "necropolisOfPilgrimPeriodResultDusc";
        spawnAction(PILGRIM_RESULT_CABAL_DUSC, false);
        String PILGRIM_COMPETITION = "necropolisOfPilgrimPeriodCompetition";
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
        String SAINT_RESULT_CABAL_NULL = "necropolisOfSaintPeriodResultNull";
        spawnAction(SAINT_RESULT_CABAL_NULL, false);
        String SAINT_RESULT_CABAL_DAWN = "necropolisOfSaintPeriodResultDawn";
        spawnAction(SAINT_RESULT_CABAL_DAWN, false);
        String SAINT_RESULT_CABAL_DUSC = "necropolisOfSaintPeriodResultDusc";
        spawnAction(SAINT_RESULT_CABAL_DUSC, false);
        String SAINT_COMPETITION = "necropolisOfSaintPeriodCompetition";
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
        String WORSHIP_RESULT_CABAL_NULL = "necropolisOfWorshipPeriodResultNull";
        spawnAction(WORSHIP_RESULT_CABAL_NULL, false);
        String WORSHIP_RESULT_CABAL_DAWN = "necropolisOfWorshipPeriodResultDawn";
        spawnAction(WORSHIP_RESULT_CABAL_DAWN, false);
        String WORSHIP_RESULT_CABAL_DUSC = "necropolisOfWorshipPeriodResultDusc";
        spawnAction(WORSHIP_RESULT_CABAL_DUSC, false);
        String WORSHIP_COMPETITION = "necropolisOfWorshipPeriodCompetition";
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
        String MARTYRDOM_RESULT_CABAL_NULL = "necropolisOfMartyrdomPeriodResultNull";
        spawnAction(MARTYRDOM_RESULT_CABAL_NULL, false);
        String MARTYRDOM_RESULT_CABAL_DAWN = "necropolisOfMartyrdomPeriodResultDawn";
        spawnAction(MARTYRDOM_RESULT_CABAL_DAWN, false);
        String MARTYRDOM_RESULT_CABAL_DUSC = "necropolisOfMartyrdomPeriodResultDusc";
        spawnAction(MARTYRDOM_RESULT_CABAL_DUSC, false);
        String MARTYRDOM_COMPETITION = "necropolisOfMartyrdomPeriodCompetition";
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
        String DISCIPLE_RESULT_CABAL_NULL = "necropolisOfDisciplePeriodResultNull";
        spawnAction(DISCIPLE_RESULT_CABAL_NULL, false);
        String DISCIPLE_RESULT_CABAL_DAWN = "necropolisOfDisciplePeriodResultDawn";
        spawnAction(DISCIPLE_RESULT_CABAL_DAWN, false);
        String DISCIPLE_RESULT_CABAL_DUSC = "necropolisOfDisciplePeriodResultDusc";
        spawnAction(DISCIPLE_RESULT_CABAL_DUSC, false);
        String DISCIPLE_COMPETITION = "necropolisOfDisciplePeriodCompetition";
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
        String DARKOMENS_RESULT_CABAL_NULL = "catacombDarkOmensPeriodResultNull";
        spawnAction(DARKOMENS_RESULT_CABAL_NULL, false);
        String DARKOMENS_RESULT_CABAL_DAWN = "catacombDarkOmensPeriodResultDawn";
        spawnAction(DARKOMENS_RESULT_CABAL_DAWN, false);
        String DARKOMENS_RESULT_CABAL_DUSC = "catacombDarkOmensPeriodResultDusc";
        spawnAction(DARKOMENS_RESULT_CABAL_DUSC, false);
        String DARKOMENS_COMPETITION = "catacombDarkOmensPeriodCompetition";
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
        String BRANDED_RESULT_CABAL_NULL = "catacombBrandedPeriodResultNull";
        spawnAction(BRANDED_RESULT_CABAL_NULL, false);
        String BRANDED_RESULT_CABAL_DAWN = "catacombBrandedPeriodResultDawn";
        spawnAction(BRANDED_RESULT_CABAL_DAWN, false);
        String BRANDED_RESULT_CABAL_DUSC = "catacombBrandedPeriodResultDusc";
        spawnAction(BRANDED_RESULT_CABAL_DUSC, false);
        String BRANDED_COMPETITION = "catacombBrandedPeriodCompetition";
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
        String HERETIC_RESULT_CABAL_NULL = "catacombHereticPeriodResultNull";
        spawnAction(HERETIC_RESULT_CABAL_NULL, false);
        String HERETIC_RESULT_CABAL_DAWN = "catacombHereticPeriodResultDawn";
        spawnAction(HERETIC_RESULT_CABAL_DAWN, false);
        String HERETIC_RESULT_CABAL_DUSC = "catacombHereticPeriodResultDusc";
        spawnAction(HERETIC_RESULT_CABAL_DUSC, false);
        String HERETIC_COMPETITION = "catacombHereticPeriodCompetition";
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
        String APOSTATE_RESULT_CABAL_NULL = "catacombApostatePeriodResultNull";
        spawnAction(APOSTATE_RESULT_CABAL_NULL, false);
        String APOSTATE_RESULT_CABAL_DAWN = "catacombApostatePeriodResultDawn";
        spawnAction(APOSTATE_RESULT_CABAL_DAWN, false);
        String APOSTATE_RESULT_CABAL_DUSC = "catacombApostatePeriodResultDusc";
        spawnAction(APOSTATE_RESULT_CABAL_DUSC, false);
        String APOSTATE_COMPETITION = "catacombApostatePeriodCompetition";
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
        String FORBIDDENPATH_RESULT_CABAL_NULL = "catacombForbiddenPathPeriodResultNull";
        spawnAction(FORBIDDENPATH_RESULT_CABAL_NULL, false);
        String FORBIDDENPATH_RESULT_CABAL_DAWN = "catacombForbiddenPathPeriodResultDawn";
        spawnAction(FORBIDDENPATH_RESULT_CABAL_DAWN, false);
        String FORBIDDENPATH_RESULT_CABAL_DUSC = "catacombForbiddenPathPeriodResultDusc";
        spawnAction(FORBIDDENPATH_RESULT_CABAL_DUSC, false);
        String FORBIDDENPATH_COMPETITION = "catacombForbiddenPathPeriodCompetition";
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
        String WITCH_RESULT_CABAL_NULL = "catacombWitchPeriodResultNull";
        spawnAction(WITCH_RESULT_CABAL_NULL, false);
        String WITCH_RESULT_CABAL_DAWN = "catacombWitchPeriodResultDawn";
        spawnAction(WITCH_RESULT_CABAL_DAWN, false);
        String WITCH_RESULT_CABAL_DUSC = "catacombWitchPeriodResultDusc";
        spawnAction(WITCH_RESULT_CABAL_DUSC, false);
        String WITCH_COMPETITION = "catacombWitchPeriodCompetition";
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
        String BLACKSMITH_MAMMON = "blacksmithMammon";
        spawnAction(BLACKSMITH_MAMMON, false);
        String MERCHAT_MAMMON = "merchantMammon";
        spawnAction(MERCHAT_MAMMON, false);
        String LILITH = "lilith";
        spawnAction(LILITH, false);
        String ANAKIM = "anakim";
        spawnAction(ANAKIM, false);
        String CREST_DAWN = "crestOfDawn";
        spawnAction(CREST_DAWN, false);
        String CREST_DUSK = "crestOfDusk";
        spawnAction(CREST_DUSK, false);
        String ORATOR_AND_PREACHER = "oratorAndPreacher";
        spawnAction(ORATOR_AND_PREACHER, false);
        String SPIRIT_IN = "spiritIn";
        spawnAction(SPIRIT_IN, false);
        String SPIRIT_OUT = "spiritOut";
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
