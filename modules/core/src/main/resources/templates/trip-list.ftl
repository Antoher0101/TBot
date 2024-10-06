Список рейсов
<#list trips as trip>
    ${trip_index + 1}. Рейс №${trip.tripNumber}
    Время отправления: ${trip.departureTime}
    Место отправления: ${trip.cityFrom.title}
    Место назначения: ${trip.cityTo.title}
</#list>
Страница ${currentPage} из ${totalPages}