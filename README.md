# Assignment Four – Q3: Temperature Dashboard

## Overview
A **real-time temperature dashboard** built with **Jetpack Compose**, **ViewModel**, **StateFlow**, and **Kotlin Coroutines**.  
Simulates sensor readings every 2 seconds, shows a scrolling list, summary stats, and an optional mini chart.

---

## Features
- 🌡️ **Simulated Stream**: random readings **65°F–85°F** every **2s**  
- 🧠 **ViewModel + StateFlow**: unidirectional, reactive updates  
- 🗃️ **History Buffer**: stores the **last 20 readings**  
- 🧮 **Stats**: **current**, **average**, **min**, **max**  
- 📋 **List**: timestamp + value in a `LazyColumn`  
- 📈 **Chart**: simple sparkline using `Canvas` (or a lightweight third-party lib)  
- ⏯️ **Pause/Resume** data generation with a toggle
---

## How to Run
```bash
git clone https://github.com/cangokmen/CS501-AssignmentFourQ3
# Open in Android Studio and run on an emulator or device
```
