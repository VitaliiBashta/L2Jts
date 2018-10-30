package handler.bbs.custom.privateOffice.services.player.appearance.enums;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.jts.dataparser.data.holder.setting.common.PlayerSex;

/**
 * @author Mangol
 * @since 01.03.2016
 */
public enum EFace
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

	private static final ETypes[] humanMale = { ETypes.a, ETypes.b, ETypes.c };
	private static final ETypes[] humanFemale = { ETypes.a, ETypes.b, ETypes.c };
	private static final ETypes[] elfMale = { ETypes.a, ETypes.b, ETypes.c};
	private static final ETypes[] elfFemale = { ETypes.a, ETypes.b, ETypes.c};
	private static final ETypes[] darkelfMale = { ETypes.a, ETypes.b, ETypes.c };
	private static final ETypes[] darkelfFemale = { ETypes.a, ETypes.b, ETypes.c};
	private static final ETypes[] orkMale = { ETypes.a, ETypes.b, ETypes.c };
	private static final ETypes[] orkFemale = { ETypes.a, ETypes.b, ETypes.c };
	private static final ETypes[] dwarfMale = { ETypes.a, ETypes.b, ETypes.c };
	private static final ETypes[] dwarfFemale = { ETypes.a, ETypes.b, ETypes.c};
	private static final ETypes[] kamaelMale = { ETypes.a, ETypes.b, ETypes.c };
	private static final ETypes[] kamaelFemale = { ETypes.a, ETypes.b, ETypes.c };

	public static ETypes[] getFace(final PlayerRace playerRace, final PlayerSex playerSex)
	{
		final EFace face;
		switch(playerRace)
		{
			case human:
				face = EFace.human;
				break;
			case elf:
				face = EFace.elf;
				break;
			case darkelf:
				face = EFace.darkelf;
				break;
			case orc:
				face = EFace.orc;
				break;
			case dwarf:
				face = EFace.dwarf;
				break;
			case kamael:
				face = EFace.kamael;
				break;
			default:
				throw new NullPointerException("not player Race " + playerRace.name());
		}
		switch(playerSex)
		{
			case MALE:
				return face.getMale();
			case FEMALE:
				return face.getFemale();
		}
		return new ETypes[0];
	}
}
