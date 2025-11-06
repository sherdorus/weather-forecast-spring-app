
# 🌦️ Weather Forecast App

A simple and elegant Spring Boot weather forecast application that fetches real-time weather data for any city using OpenWeather and OpenStreetMap APIs.


## 🌍 Live Demo

🚀 **Try it live:** [Weather Forecast App on Render](https://weather-forecast-spring-app.onrender.com)  
*(It may take up to 30 seconds to wake up if the Render free instance is asleep.)*

## 🧰 Deployment Details

- **Platform:** Render
- **Runtime:** Java 17
- **Containerization:** Docker
- **CI/CD:** GitHub Actions (automatic build & deploy on push to `master`)
- **Build Command:** `./mvnw clean package -DskipTests`
- **Start Command:** `java -jar target/weatherSpringApp-0.0.1-SNAPSHOT.jar`

## 🔧 Features

- 🔍 City search with real-time geocoding (via Nominatim API)
- 🌤️ Current weather with temperature, humidity, wind, and cloud cover
- ⚠️ Error handling with Bootstrap Toast notifications
- ⚡ Lightweight cache using Caffeine
- 🐳 Dockerized for easy deployment
- 🔁 Automated CI/CD via GitHub Actions

## 🛠 Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Open-Meteo Weather API**
- **OpenStreetMap Nominatim API**
- **Caffeine Cache**
- **Docker**
- **GitHub Actions**
- **Render Cloud Platform**
- 
## 🚀 Getting Started

### Requirements
- Java 17+
- Maven

### Run the app
```bash
git clone https://github.com/sherdorus/weather-forecast-spring-app.git
cd weather-forecast-spring-app
./mvnw spring-boot:run
````

Then open: [http://localhost:8080](http://localhost:8080)

## 📁 Project Structure

```
src/
├── main/
│   ├── java/io/sherdor/weatherspringapp/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── dto/
│   │   ├── config/
│   └── resources/
│       ├── templates/
│       ├── static/
│       ├── messages.properties
│       └── application.properties
```

## 📸 Screenshots

### ☀️ Light Theme

![Weather App Light](https://github.com/user-attachments/assets/3a4124bc-25e5-4d8d-92ee-1dffec2ede21)

![Weather App Light](https://github.com/user-attachments/assets/61e14369-10f4-4973-8573-6929dc8ffd90)

### 🌙 Dark Theme
![Weather App Dark](https://github.com/user-attachments/assets/1b19b324-9a94-4d1a-84f4-4d3e5698e78c)

---

## 📄 License

This project is licensed under the MIT License.

