package handler.bbs.custom.privateOffice.services.player.appearance.enums;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Mangol
 * @since 29.02.2016
 */
public enum ETypes
{
	a(0),
	b(1),
	c(2),
	d(3),
	e(4),
	f(5),
	g(6);
	private final int id;

	ETypes(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public static String getName(final int id)
	{
		for(final ETypes types : ETypes.values())
		{
			if(types.getId() == id)
			{
				return types.name();
			}
		}
		return "NONE";
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
