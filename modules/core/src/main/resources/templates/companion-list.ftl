<#if trips?has_content>
🎉 Найдены попутчики!
<#list trips as trip>
👤 ${trip.client.name} (@${trip.client.user.username})
        ${trip.transport.transportType.icon}Рейс: ${trip.tripNumber}
        📍Отправление: ${trip.stationFrom.city.title}
        🏁Назначение: ${trip.stationTo.city.title}
</#list>
<#else>
🙁 Попутчики не найдены
Не волнуйтесь! Вы все равно можете насладиться своим путешествием.
</#if>