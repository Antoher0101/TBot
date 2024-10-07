<#setting datetime_format="HH:mm, dd MMM yyyy">
<#setting locale="ru_RU">
<#assign startTripNumber = (page - 1) * pageSize + 1 />

Список доступных рейсов (стр. ${page} из ${totalPages}):

<#list trips as trip>
${startTripNumber + trip_index}. Рейс №${trip.tripNumber} (${trip.transport.transportType.icon}) — ${trip.transport.title}
Отправление: ${trip.cityFrom.title} — ${trip.departureTime?datetime("yyyy-MM-dd'T'HH:mm")}
Прибытие: ${trip.cityTo.title} — ${trip.arrivalTime?datetime("yyyy-MM-dd'T'HH:mm")}

</#list>

Выберите рейс для продолжения.