package handler.bbs.custom.privateOffice.services.player.appearance.enums;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.jts.dataparser.data.holder.setting.common.PlayerSex;

/**
 * @author Mangol
 * @since 29.02.2016
 */
public enum EHairStyle
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

	private static final ETypes[] humanMale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d, ETypes.e };
	private static final ETypes[] humanFemale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d, ETypes.e, ETypes.f, ETypes.g };
	private static final ETypes[] elfMale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d, ETypes.e };
	private static final ETypes[] elfFemale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d, ETypes.e, ETypes.f, ETypes.g };
	private static final ETypes[] darkelfMale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d, ETypes.e };
	private static final ETypes[] darkelfFemale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d, ETypes.e, ETypes.f, ETypes.g };
	private static final ETypes[] orkMale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d, ETypes.e };
	private static final ETypes[] orkFemale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d, ETypes.e, ETypes.f, ETypes.g };
	private static final ETypes[] dwarfMale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d, ETypes.e };
	private static final ETypes[] dwarfFemale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d, ETypes.e, ETypes.f, ETypes.g };
	private static final ETypes[] kamaelMale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d, ETypes.e };
	private static final ETypes[] kamaelFemale = { ETypes.a, ETypes.b, ETypes.c, ETypes.d, ETypes.e, ETypes.f, ETypes.g };

	public static ETypes[] getHairStyle(final PlayerRace playerRace, final PlayerSex playerSex)
	{
		final EHairStyle hairStyle;
		switch(playerRace)
		{
			case human:
				hairStyle = EHairStyle.human;
				break;
			case elf:
				hairStyle = EHairStyle.elf;
				break;
			case darkelf:
				hairStyle = EHairStyle.darkelf;
				break;
			case orc:
				hairStyle = EHairStyle.orc;
				break;
			case dwarf:
				hairStyle = EHairStyle.dwarf;
				break;
			case kamael:
				hairStyle = EHairStyle.kamael;
				break;
			default:
				throw new NullPointerException("not player Race " + playerRace.name());
		}
		switch(playerSex)
		{
			case MALE:
				return hairStyle.getMale();
			case FEMALE:
				return hairStyle.getFemale();
		}
		return new ETypes[0];
	}
}
