package handler.bbs.custom.privateOffice.engine.interfaces;
import org.mmocore.gameserver.object.Player;

/**
 * @author Mangol
 * @since 24.02.2016
 */
public interface IDefault
{
	/**
	 * Используется для показа основного окна после того как прошли все проверки на валидация бипасса, и включен ли сервис вообще.
	 *
	 * @param player - игрок
	 * @param bypass - бипасс который будем разбирать, возможно страницу ?
	 * @param params - какие-то параметры для большего функцонала
	 */
	void content(final Player player, final String bypass, final Object... params);

	/**
	 * Используется после того как прошли проверки на валидация бипасса, и включен ли вообще блок.
	 *
	 * @param player - игрок
	 * @param bypass - бипасс который будем разбирать, и вытягивать нужные нам значения, для проверки и тд.
	 * @param params - какие-то параметры для большего функцонала
	 */
	void request(final Player player, final String bypass, final Object... params);

	/**
	 * Использовать только после того как прошли все проверки в @request.
	 *
	 * @param player - игрок
	 * @param params - какие-то параметры для большего функцонала
	 */
	void reply(final Player player, final Object... params);

	/**
	 * Проверка на основе которого покажет окно того, или иного блока @content , для выбора действия.
	 *
	 * @param player - игрок
	 * @param bypass - бипасс который будем проверять валиден ли он, если нет false.
	 * @return -
	 */
	default boolean validContentBypass(final Player player, final String bypass)
	{
		return true;
	}

	/**
	 * Проверка на основе которого выполниться метод @request.
	 *
	 * @param player - игрок
	 * @param bypass - бипасс который будем проверять валиден ли он, если нет false.
	 * @return -
	 */
	default boolean validRequestBypass(final Player player, final String bypass)
	{
		return true;
	}

	/**
	 * Проверяет на наличие запроса подтверждения использования того, или иного типа у игрока
	 *
	 * @param player - игрок
	 * @return -
	 */
	default boolean validAnswer(final Player player)
	{
		return true;
	}

	/**
	 * Включен ли сервис, если нет то отправляем месагу, и выкидывает на предыдущую страницу.
	 * @param player - игрок.
	 * @return
	 */
	boolean isIncluded(final Player player);
}