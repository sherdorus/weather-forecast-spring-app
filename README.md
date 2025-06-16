
# 🌦️ Weather Forecast App

A simple and elegant Spring Boot weather forecast application that fetches real-time weather data for any city using OpenWeather and OpenStreetMap APIs.

## 🔧 Features

- 🔍 City search with real-time geocoding (via Nominatim API)
- 🌤️ Current weather with temperature, humidity, wind, and cloud cover
- ⚠️ Error handling with Bootstrap Toast notifications
- ⚡ Lightweight cache using Caffeine

## 🛠 Tech Stack

- Java 17
- Spring Boot 3
- Open-Meteo Weather API
- OpenStreetMap Nominatim API
- Caffeine Cache

## 🚀 Getting Started

### Requirements
- Java 17+
- Maven

### Run the app
```bash
git clone https://github.com/yourusername/weather-forecast-app.git
cd weather-forecast-app
./mvnw spring-boot:run
````

Then open: [http://localhost:8080](http://localhost:8080)

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/example/weather/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── dto/
│   │   ├── util/
│   └── resources/
│       ├── templates/
│       ├── static/
│       ├── messages.properties
│       └── application.properties
```

## 📸 Screenshots

*(Coming soon)*

---

## 📄 License

This project is licensed under the MIT License.

