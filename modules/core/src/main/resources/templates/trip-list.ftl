<#setting datetime_format="HH:mm, dd MMM yyyy">
<#setting locale="ru_RU">
<#assign startTripNumber = (page - 1) * pageSize + 1 />

<#if trips?has_content>
    <#list trips as trip>
${startTripNumber + trip_index}. Рейс №${trip.tripNumber} (${trip.transport.transportType.icon}) — ${trip.transport.title}
Отправление: ${trip.stationFrom.city.title} — ${trip.departureTime?datetime("yyyy-MM-dd'T'HH:mm")}
Прибытие: ${trip.stationTo.city.title} — ${trip.arrivalTime?datetime("yyyy-MM-dd'T'HH:mm")}

    </#list>
<#else>
    У вас нет рейсов.
</#if>