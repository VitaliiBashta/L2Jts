<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE list SYSTEM "events.dtd">
<list>
    <event id="999" name="BattleForTreasure" impl="Reflection">
        <parameter name="pattern" value="0 */30 * * * ?"/>	<!-- Таймер запуска -->
		<parameter name="min_level" value="1"/>
		<parameter name="max_level" value="85"/>
		<!-- Время на евент в минутах-->
		<parameter name="end_time" value="45"/>
        <parameter name="boss_location" value="147320;142824;-15864"/>
        <!-- Кто набил 1000 очков, тот выйграл-->
        <parameter name="maxPoint" value="1000"/>
        <!-- Донес флаг до своей базы получил 200 очков-->
        <parameter name="stealFlagPoint" value="200"/>
        <!-- Убил игрока другого цвета получил 3 очка -->
        <parameter name="killPoint" value="3"/>
        <on_time>
            <!--за 10 минут открываем регу-->
            <on time="-600">
                <announce val="1"/>
                <start name="registration" />
            </on>
            <!-- Через 8 минут закрываем регу, отправляет играков в инстанс-->
            <on time="-120">
                <announce val="0"/>
                <teleport_players id="players" />
            </on>
            <on time="0">
                <start name="event" />
            </on>
        </on_time>
		<!-- Будем обсчитывать на всех игроков 1 раз (ПСЭ, весь дроп упадет на землю)-->
		<objects name="rewardBoss">
			<rewardlist>
				<reward item_id="57" min="1000000" max="10000000" chance="90.0000"/>
				<reward item_id="1" min="1" max="1" chance="10.0000"/>
			</rewardlist>
		</objects>
		<!-- Будем обсчитывать каждому игроку (Сколько игроков, столько раз и посчитает, и возможно будет кажому разная награда)-->
		<objects name="rewardWin">
			<rewardlist>
				<reward item_id="65" min="1" max="10" chance="90.0000"/>
			</rewardlist>
			<rewardlist>
				<reward item_id="10000" min="2" max="20" chance="100.0000"/>
			</rewardlist>
		</objects>
		<!-- Будем обсчитывать каждому игроку (Сколько игроков, столько раз и посчитает, и возможно будет кажому разная награда)-->
		<objects name="rewardLose">
			<rewardlist>
				<reward item_id="10152" min="1" max="6" chance="90.0000"/>
			</rewardlist>
			<rewardlist>
				<reward item_id="10153" min="1" max="3" chance="100.0000"/>
			</rewardlist>
		</objects>
	</event>
</list>