import os
import json
import time
import pandas as pd
import paho.mqtt.client as mqtt

# Configurazione del broker MQTT
BROKER = "localhost"
PORT = 1883
BATTERY_TOPIC = "computerMouse/batteryPercentage"
BATTERY_TOPIC_2 = "computerMouse/batteryIsCharging"
BATTERY_TOPIC_3 = "computerMouse/batteryTimestamp"

OPERATIONS_TOPIC = "computerMouse/operations"
MONITOR_TOPIC = "computerMouse/monitor"

isCharging = False
updateIsCharging = False

# Numero massimo di misurazioni prima di salvare e fermare il programma
MAX_MEASUREMENTS = 500
measure_count = 0

# DataFrame in-memory
df_messages = pd.DataFrame(columns=["finalTimestamp", "sentFromPipelineTimestamp"])
df_timestamps = pd.DataFrame(columns=["batteryTimestamp"])


def on_connect(client, userdata, flags, rc):
    print(f"Connected with result code {rc}")
    client.subscribe([(OPERATIONS_TOPIC, 0), (MONITOR_TOPIC, 0)])


def on_message(client, userdata, msg):
    global isCharging, updateIsCharging, df_messages, measure_count

    message = json.loads(msg.payload.decode("utf-8"))
    print(message)

    if msg.topic == OPERATIONS_TOPIC:
        if message["operation"] == "START_CHARGING":
            print("start charging")
            isCharging = True
            updateIsCharging = True
        elif message["operation"] == "STOP_CHARGING":
            print("stop charging")
            isCharging = False
            updateIsCharging = True
    else:
        print("forwarded message")
        message["finalTimestamp"] = round(time.time() * 1000)
        message["sentFromPipelineTimestamp"] = round(message["sentFromPipelineTimestamp"])

        # Aggiunge la riga al DataFrame
        df_messages = pd.concat([df_messages, pd.DataFrame([message])], ignore_index=True)
        measure_count += 1


def publish_battery():
    global isCharging, updateIsCharging, df_timestamps, measure_count

    client = mqtt.Client()
    client.on_connect = on_connect
    client.on_message = on_message

    client.connect(BROKER, PORT, 60)
    client.loop_start()

    battery = 100
    payload = json.dumps({"isCharging": isCharging})
    client.publish(BATTERY_TOPIC_2, payload)

    while measure_count < MAX_MEASUREMENTS:
        timestamp = round(time.time() * 1000)
        payload = json.dumps({"batteryPercentage": battery, "batteryTimestamp": timestamp})

        # Salva timestamp nel DataFrame
        df_timestamps = pd.concat([df_timestamps, pd.DataFrame([{"sentTimestamp": timestamp}])], ignore_index=True)

        client.publish(BATTERY_TOPIC, payload)
        client.publish(BATTERY_TOPIC_3, payload)

        if updateIsCharging:
            payload = json.dumps({"isCharging": isCharging})
            client.publish(BATTERY_TOPIC_2, payload)
            updateIsCharging = False

        if isCharging:
            battery += 1
            if battery >= 100:
                battery = 100
                isCharging = False
        else:
            battery -= 1
            if battery <= 0:
                battery = 0
                isCharging = True

        time.sleep(1)  # Invio del messaggio ogni secondo

    # **Dopo 500 misurazioni, salva i dati e ferma il codice**
    print("\n  Numero massimo di misurazioni raggiunto. Salvando i dati...")
    df_messages.to_csv("messages_log.csv", index=False)
    df_timestamps.to_csv("messages_timestamp_log.csv", index=False)
    print("✅ Dati salvati correttamente. Terminazione del programma.")


if __name__ == "__main__":
    publish_battery()