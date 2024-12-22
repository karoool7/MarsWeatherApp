document.addEventListener("DOMContentLoaded", function () {
    fetch("http://localhost:8080/get20days")
            .then((response) => response.json())
            .then((data) => {
                initializeWeatherDataFor20Days(data);
            })
            .catch((error) => console.error("Błąd pobierania danych:", error));
});

function initializeWeatherDataFor20Days(sol) {
    var firstFloor = document.querySelector('.first-floor');
    var secondFloor = document.querySelector('.second-floor');
    var thirdFloor = document.querySelector('.third-floor');
    var fourthFloor = document.querySelector('.fourth-floor');

    var floors = [firstFloor, secondFloor, thirdFloor, fourthFloor];
    var currentDate = new Date();
    currentDate.setDate(currentDate.getDate() + 1);

    var dayOfWeekShort = ['nd', 'pon', 'wt', 'śr', 'czw', 'pt', 'sob'];

    var totalItems = floors.length * 5;
    var index = sol.length - 1;

    for (var j = 0; j < floors.length; j++) {
        for (var i = 0; i < 5; i++) {
            var floor = document.createElement('div');
            floor.classList.add('floor');

            var dateDiv = document.createElement('div');
            dateDiv.classList.add('date');

            var day = currentDate.getDate();
            var month = currentDate.getMonth() + 1;
            var year = currentDate.getFullYear();

            day = day < 10 ? '0' + day : day;
            month = month < 10 ? '0' + month : month;

            var dayOfWeekIndex = currentDate.getDay();
            var dayOfWeek = dayOfWeekShort[dayOfWeekIndex];

            dateDiv.textContent = `${dayOfWeek}, ${day}.${month}.${year}`;

            currentDate.setDate(currentDate.getDate() + 1);

            var tempDiv = document.createElement('div');
            tempDiv.classList.add('temp');
            tempDiv.textContent = getTempsForDay(sol[index]);
            index--;

            floor.appendChild(dateDiv);
            floor.appendChild(tempDiv);

            floors[j].appendChild(floor);
        }
    }
}

function getTempsForDay(sol) {
    var tempsForDay = '';
    return tempsForDay += sol.characteristics[0].value+'°C / '+sol.characteristics[1].value;
}

function redirectToHome() {
    window.location.href = "http://localhost:8080/mars";
}