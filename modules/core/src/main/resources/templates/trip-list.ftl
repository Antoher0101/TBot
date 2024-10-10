<#setting datetime_format="HH:mm, dd MMM yyyy">
<#setting locale="ru_RU">

<#if trips?has_content>
    <#list trips as trip>
Рейс №${trip.tripNumber} (${trip.transport.transportType.icon}) — ${trip.transport.title}
Отправление: ${trip.cityFrom.title} — ${trip.departureTime?datetime("yyyy-MM-dd'T'HH:mm")}
Прибытие: ${trip.cityTo.title} — ${trip.arrivalTime?datetime("yyyy-MM-dd'T'HH:mm")}

    </#list>
<#else>
    У вас нет рейсов.
</#if>