# Assignment Four – Q3: Temperature Dashboard

## Overview
A **real-time temperature dashboard** built with **Jetpack Compose**, **ViewModel**, **StateFlow**, and **Kotlin Coroutines**.  
The app simulates live temperature sensor data, updating every 2 seconds, and visualizes readings through a dynamic list and summary statistics.

---

## Features
- 🌡️ **Simulated Temperature Stream** – Generates random readings between **65°F–85°F** every **2 seconds**  
- 📊 **Reactive Updates** – Uses **StateFlow** for real-time UI synchronization  
- 🧮 **Summary Statistics** – Displays **current**, **average**, **minimum**, and **maximum** temperature values  
- 📅 **Reading History** – Stores and shows the **last 20 readings** with timestamps  
- ⏯️ **Pause/Resume** – Toggle simulation on and off using a simple control button  

---

## How It Works
- A coroutine running in the **ViewModel** simulates temperature readings and emits them via a `MutableStateFlow<List<TemperatureReading>>`.  
- The **UI** collects this flow using `collectAsStateWithLifecycle()` and automatically recomposes with every update.  
- The app keeps only the **20 most recent readings** in memory for display.  
- Summary values (min, max, average) are calculated reactively whenever the list updates.  
- The user can **pause or resume** data generation, controlling the coroutine execution.

---

## How to Run
```bash
git clone https://github.com/cangokmen/CS501-AssignmentFourQ3
# Open in Android Studio and run on an emulator or device
```
