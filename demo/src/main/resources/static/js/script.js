document.addEventListener("DOMContentLoaded", function () {
    getLast7DaysInfo();
});

function getLast7DaysInfo() {
    //fillInfoAboutWeather();
    fetch("http://localhost:8080/get7days")
            .then((response) => response.json())
            .then((data) => {
                createAndAppendDivForEachDay(data);
            })
            .catch((error) => console.error("Błąd pobierania danych:", error));

    function createAndAppendDivForEachDay(data) {
        for (var i = 0; i <= 6; i++) {
            var list = document.querySelector('.forecast');
            var day = document.createElement("div");

            var {
                dayName
            } = formatDate(data[i].characteristics[2].value);
            var {
                minTemp, maxTemp
            } = getMinMaxTemp(i,data);
            var {
                sol
            } = getSol(i,data);
            day.innerHTML = `
                            <div class="day" onclick="provideDetailsByCurrentDay(${sol})">
                                <div class="day-temp" style="float: left;">
                                    <div class="day-name">${dayName}</div>
                                    <div class="day-temperature">${maxTemp}°C / ${minTemp}°C</div>
                                </div>
                                <div class="day-sol" style="text-align: right;">Sol: ${sol}</div>
                            </div>
            `;
            list.appendChild(day);
        }
    }
}

function fillInfoAboutWeather() {
    var {
        warmDate,
        hottestDayTemp,
        month,
        pressure,
        coldDate,
        coldestDayTemp,
        hottestSol
    } = checkWhichDayWasHottestAndColdest();

    var detailsBottomDiv = document.querySelector(".details-bottom");
    var aboutWeather = document.createElement("div");
    aboutWeather.innerHTML = `
                                W dniu ${warmDate}, na Marsie w rejonie Gale Crater zanotowano rekordową dotychczasową temperaturę dodatnią, dochodzącą do około <a style="color:red; font-weight: bold;">${hottestDayTemp}°C</a>. Był to <a style="color:#FF6347; font-weight: bold;">sol ${hottestSol}</a>, miesiąc ${month.replace(/^Month\s+/i, '')}. Wówczas ciśnienie atmosferyczne oscylowało w granicach ${pressure} paskali, a atmosfera charakteryzowała się wyjątkową przejrzystością, intensywność promieniowania ultrafioletowego była wysoka. Natomiast najzimniejszy dzień to ${coldDate}, wówczas temperatura spadła do<a style="color:rgb(54, 171, 255); font-weight: bold;"> ${coldestDayTemp}°C</a>. Marsjański rok ma 687 dni.
    `;
    detailsBottomDiv.appendChild(aboutWeather);
}

function checkWhichDayWasHottestAndColdest() {
    var hottestSol;
    var warmDate;
    var month;
    var pressure;
    var coldDate;

    var coldestDayTemp;
    var hottestDayTemp;

    // Tutaj dodaj kod, który wyśle zapytanie do aplikacji i pobierze charakterystyki dla najcieplejszego oraz nazimniejszego dnia
    // Następnie, wystarczy podstawić te wartości pod zmienne -> hottestSol - warmDate - month - pressure - coldDate - coldestDayTemp - hottesDayTemp

    return {
        warmDate,
        hottestDayTemp,
        month,
        pressure,
        coldDate,
        coldestDayTemp,
        hottestSol
    };
}

function formatDate(date) {
    var date = new Date(date);
    var options = {
        weekday: "long",
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
    };
    var dayName = new Intl.DateTimeFormat("pl-PL", options).format(date);
    return {
        dayName
    };
}

function getMinMaxTemp(i,data) {
    var minTemp = data[i].characteristics[1].value;
    var maxTemp = data[i].characteristics[0].value;
    return {
        minTemp,
        maxTemp
    };
}

function getSol(i,data) {
    var sol = data[i].sol;
    return {
        sol
    };
}

function provideDetailsByCurrentDay(sol) {
    toggleClickedDayColor(sol);
    document.querySelector('.details-container').scrollIntoView({ behavior: 'smooth' });

    fetch(`http://localhost:8080/weather/details?sol=${sol}`)
        .then(response => response.json())
        .then(data => {
            const elements = {
                sol: document.querySelector('.details-sol'),
                day: document.querySelector('.details-day'),
                dayTemp: document.querySelector('.day-temp-details'),
                groundTemp: document.querySelector('.ground-temp'),
                pressure: document.querySelector('.pressure'),
                uv: document.querySelector('.uv'),
                opacity: document.querySelector('.opacity'),
                sunrise: document.querySelector('.sunrise'),
                sunset: document.querySelector('.sunset'),
                month: document.querySelector('.month'),
                season: document.querySelector('.season')
            };

            const getPolishNameUV = index => ({ Low: 'Niskie', Moderate: 'Umiarkowane', High: 'Wysokie' }[index] || 'Brak danych');
            const getPolishNameWeather = name => name === 'Sunny' ? 'Słonecznie' : 'Brak danych';

            let date = '';
            let tempsMaxMin = 'Temperatura powietrza: ';
            let gtsTempsMaxMin = 'Temperatura przy powierzchni: ';

            elements.sol.innerHTML = `Sol(dzień): ${data.sol}`;

            data.characteristics.forEach(({ name, value }) => {
                switch (name) {
                    case 'Date':
                        date = formatDate(value).dayName;
                        break;
                    case 'MaxTemp':
                        tempsMaxMin += `${value}°C / `;
                        break;
                    case 'MinTemp':
                        tempsMaxMin += `${value}°C`;
                        break;
                    case 'MaxGtsTemp':
                        gtsTempsMaxMin += `${value}°C / `;
                        break;
                    case 'MinGtsTemp':
                        gtsTempsMaxMin += `${value}°C`;
                        break;
                    case 'Pressure':
                        elements.pressure.innerHTML = `Ciśnienie: ${value}Pa`;
                        break;
                    case 'UV':
                        elements.uv.innerHTML = `Promieniowanie UV: ${getPolishNameUV(value)}`;
                        break;
                    case 'Opacity':
                        elements.opacity.innerHTML = `Pogoda: ${getPolishNameWeather(value)}`;
                        break;
                    case 'Sunrise':
                        elements.sunrise.innerHTML = `Wschód słońca: ${value}`;
                        break;
                    case 'Sunset':
                        elements.sunset.innerHTML = `Zachód słońca: ${value}`;
                        break;
                    case 'Month':
                        elements.month.innerHTML = `Miesiąc: ${value}`;
                        break;
                    case 'Season':
                        elements.season.innerHTML = `Pora roku: ${value}`;
                        break;
                }
            });

            elements.day.innerHTML = date;
            elements.dayTemp.innerHTML = tempsMaxMin;
            elements.groundTemp.innerHTML = gtsTempsMaxMin;
        })
        .catch(error => console.error("Błąd pobierania danych:", error));
}


let lastClickedSol;
function toggleClickedDayColor(sol) {
    const daySolElements = document.querySelectorAll('.day');
    let nowClickedSol = sol;

    if (nowClickedSol !== lastClickedSol) {
        setClickedColor(nowClickedSol, daySolElements);
        restoreLastClickedColorToDefault(lastClickedSol, daySolElements);
        lastClickedSol = nowClickedSol;
    }
}

function setClickedColor(sol, daySolElements) {
    daySolElements.forEach(element => {
        if (element.textContent.includes(sol)) {
            element.style.backgroundColor = 'rgba(255, 99, 71, 0.1)';
        }
    });
}

function restoreLastClickedColorToDefault(lastClickedSol, daySolElements) {
    if (!lastClickedSol) return; 
    daySolElements.forEach(element => {
        if (element.textContent.includes(lastClickedSol)) {
            element.style.backgroundColor = 'rgba(255, 255, 255, 0.7)';
        }
    });
}

function intoSelectWeahterDay() {
    document.querySelector('.weather-app').scrollIntoView({
        behavior: 'smooth'
    });
}

function redirectTo20DaysWeather() {
    window.location.href = "http://localhost:8080/mars20days";
}