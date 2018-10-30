<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE list SYSTEM "events.dtd">
<list>
    <event id="0" name="Capture Zone" impl="CaptureZone">
        <parameter name="pattern" value="0 */30 * * * ?"/>
        <parameter name="rewardItemId" value="57"/>
        <parameter name="rewardItemCount" value="100000"/>
        <!--Время тика в секундах (раз в n секунд выдает награду команде, удерживающей зону) -->
        <parameter name="tickTime" value="60"/>
        <parameter name="event_zone" value="[zone]"/>
        <parameter name="npc_id" value="36667"/>
        <parameter name="location" value="123432 115224 -3664" />
        <on_time>
            <on time="-120">
                <announceFromHolder val="events.CaptureZone.StartEvent" />
            </on>
            <on time="-118">
                <announceFromHolder val="events.CaptureZone.StartEvent" />
            </on>
            <on time="-116">
                <announceFromHolder val="events.CaptureZone.StartEvent1" />
            </on>
            <on time="-114">
                <announceFromHolder val="events.CaptureZone.StartEvent2" />
            </on>
            <on time="-112">
                <announceFromHolder val="events.CaptureZone.StartEvent3" />
            </on>
            <on time="-110">
                <announceFromHolder val="events.CaptureZone.StartEvent4" />
            </on>
            <on time="-108">
                <announceFromHolder val="events.CaptureZone.StartEvent5" />
            </on>
            <on time="0">
                <announceFromHolder val="events.CaptureZone.Spawned" />
                <start name="event" />
            </on>
            <on time="3600">
                <announceFromHolder val="events.CaptureZone.StopEvent" />
                <stop name="event" />
            </on>
        </on_time>
    </event>
</list>