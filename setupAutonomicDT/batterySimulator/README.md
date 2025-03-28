# Computer Mouse Battery Simulator

This repository contains a Python-based simulator for monitoring and managing a computer mouse's battery status using MQTT messaging.

## 📌 Features

-   Publishes battery percentage, charging status, and timestamps via MQTT.
-   Listens for operations to start or stop battery charging.
-   Stores battery data and timestamps in CSV files after a set number of measurements.

## 🛠️ Requirements

Ensure you have the following dependencies installed:

```bash
pip install paho-mqtt pandas
```

## 🚀 How to Run

1. Ensure an MQTT broker (e.g., Mosquitto) is running on `localhost:1883`.
2. Run the simulator with:

    ```bash
    python simulator.py
    ```

## 🔄 MQTT Topics

### Published Topics:

-   **`computerMouse/batteryPercentage`** - Sends battery percentage and timestamp.
-   **`computerMouse/batteryIsCharging`** - Sends charging status.
-   **`computerMouse/batteryTimestamp`** - Sends battery timestamp.

### Subscribed Topics:

-   **`computerMouse/operations`** - Listens for `START_CHARGING` and `STOP_CHARGING` commands.
-   **`computerMouse/monitor`** - Listens for forwarded messages.

## 📝 Data Logging

After **500 measurements**, the simulator saves data to:

-   `messages_log.csv` - Logs received messages.
-   `messages_timestamp_log.csv` - Logs timestamps.
