package handler.bbs.custom.privateOffice.services.player.appearance.enums;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.jts.dataparser.data.holder.setting.common.PlayerSex;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Mangol
 * @since 29.02.2016
 */
public enum EHairColor
{
	human
			{
				@Override
				public ETypes[] getMale()
				{
					return humanMale;
				}

				@Override
				public ETypes[] getFemale()
				{
					return humanFemale;
				}
			},
	elf
			{
				@Override
				public ETypes[] getMale()
				{
					return elfMale;
				}

				@Override
				public ETypes[] getFemale()
				{
					return elfFemale;
				}
			},
	darkelf
			{
				@Override
				public ETypes[] getMale()
				{
					return darkelfMale;
				}

				@Override
				public ETypes[] getFemale()
				{
					return darkelfFemale;
				}
			},
	orc
			{
				@Override
				public ETypes[] getMale()
				{
					return orkMale;
				}

				@Override
				public ETypes[] getFemale()
				{
					return orkFemale;
				}
			},
	dwarf
			{
				@Override
				public ETypes[] getMale()
				{
					return dwarfMale;
				}

				@Override
				public ETypes[] getFemale()
				{
					return dwarfFemale;
				}
			},
	kamael
			{
				@Override
				public ETypes[] getMale()
				{
					return kamaelMale;
				}

				@Override
				public ETypes[] getFemale()
				{
					return kamaelFemale;
				}
			};

	protected abstract ETypes[] getMale();

	protected abstract ETypes[] getFemale();

	private static final ETypes[] humanMale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d };
	private static final ETypes[] humanFemale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d };
	private static final ETypes[] elfMale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d };
	private static final ETypes[] elfFemale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d };
	private static final ETypes[] darkelfMale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d };
	private static final ETypes[] darkelfFemale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d };
	private static final ETypes[] orkMale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d };
	private static final ETypes[] orkFemale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d };
	private static final ETypes[] dwarfMale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d };
	private static final ETypes[] dwarfFemale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d };
	private static final ETypes[] kamaelMale = { ETypes.a, ETypes.b, ETypes.c };
	private static final ETypes[] kamaelFemale = { ETypes.a, ETypes.b, ETypes.c };

	public static ETypes[] getHairColor(final PlayerRace playerRace, final PlayerSex playerSex)
	{
		final EHairColor hairColor;
		switch(playerRace)
		{
			case human:
				hairColor = EHairColor.human;
				break;
			case elf:
				hairColor = EHairColor.elf;
				break;
			case darkelf:
				hairColor = EHairColor.darkelf;
				break;
			case orc:
				hairColor = EHairColor.orc;
				break;
			case dwarf:
				hairColor = EHairColor.dwarf;
				break;
			case kamael:
				hairColor = EHairColor.kamael;
				break;
			default:
				throw new NullPointerException("not player Race " + playerRace.name());
		}
		switch(playerSex)
		{
			case MALE:
				return hairColor.getMale();
			case FEMALE:
				return hairColor.getFemale();
		}
		return new ETypes[0];
	}

	public static Optional<ETypes> value(final String name)
	{
		for(final ETypes types : ETypes.values())
		{
			if(Objects.equals(types.name(), name))
			{
				return Optional.of(types);
			}
		}
		return Optional.empty();
	}
}
