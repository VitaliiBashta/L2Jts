package handler.bbs.custom.privateOffice.engine.interfaces;
/**
 * @author Mangol
 * @since 18.02.2016
 */
public interface IEnumComponent
{
	default boolean isIncluded()
	{
		return false;
	}

	default boolean isAnswer()
	{
		return false;
	}

	default int getItemId()
	{
		return 0;
	}

	default long getItemCount()
	{
		return 0;
	}

	default int getWearItemId()
	{
		return 0;
	}

	default long getWearItemCount()
	{
		return 0;
	}

	default long getMaxCount()
	{
		return 0;
	}

	default int[] getLevels()
	{
		throw new NullPointerException();
	}

	default int[] getCounts()
	{
		throw new NullPointerException();
	}

	default long[] getCountsL()
	{
		throw new NullPointerException();
	}

	default int[] getDays()
	{
		throw new NullPointerException();
	}

	default int[] getItemIds()
	{
		throw new NullPointerException();
	}

	default long[] getItemCounts()
	{
		throw new NullPointerException();
	}
}
