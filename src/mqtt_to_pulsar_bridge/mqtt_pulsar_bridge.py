import paho.mqtt.client as mqtt
import pulsar
import time

# --- Config ---
MQTT_BROKER_HOST = "localhost"
MQTT_BROKER_PORT = 1883
MQTT_TOPIC_SUBSCRIBE = "test/data/#"
MQTT_CLIENT_ID = "bridge"

PULSAR_SERVICE_URL = "pulsar://localhost:6650"
PULSAR_TOPIC_PUBLISH = "persistent://public/default/test-data"

mqtt_client = None
pulsar_client = None
pulsar_producer = None
# --- End Config ---

def on_connect_mqtt(client, userdata, flags, rc, properties=None):
    if rc == 0:
        print(f"Connected to MQTT broker: {MQTT_BROKER_HOST}:{MQTT_BROKER_PORT}")
        client.subscribe(MQTT_TOPIC_SUBSCRIBE)
        print(f"Subscribed to topic: {MQTT_TOPIC_SUBSCRIBE}")
    else:
        print(f"MQTT connection failed with code: {rc}. Retry in 5 seconds...")
        time.sleep(5)
        try_connect_mqtt()

def on_message_mqtt(client, userdata, msg):
    global pulsar_producer
    try:
        payload_str = msg.payload.decode('utf-8', errors='replace')
        print(f"MQTT < Topic: {msg.topic} | Payload: {payload_str}")

        if pulsar_producer:
            pulsar_producer.send(msg.payload)
            print(f"Pulsar > Topic: {PULSAR_TOPIC_PUBLISH} | Message sent")
        else:
            print("Error - Producer Pulsar not initialized.")

    except Exception as e:
        print(f"Error while processing MQTT message or sending to Pulsar: {e}")

def setup_pulsar_client_and_producer():
    global pulsar_client, pulsar_producer
    try:
        pulsar_client = pulsar.Client(PULSAR_SERVICE_URL)
        pulsar_producer = pulsar_client.create_producer(PULSAR_TOPIC_PUBLISH)
        print(f"Connected to Pulsar and producer linked to topic: {PULSAR_TOPIC_PUBLISH}")
        return True
    except Exception as e:
        print(f"Couldn't connect to Pulsar or create producer at this time: {e}")
        pulsar_client = None
        pulsar_producer = None
        return False

def try_connect_mqtt():
    global mqtt_client
    try:
        mqtt_client.connect(MQTT_BROKER_HOST, MQTT_BROKER_PORT, 60)
        return True
    except Exception as e:
        print(f"Exception during MQTT connection: {e}")
        return False

def main():
    global mqtt_client, pulsar_client, pulsar_producer

    print("Started...")

    if not setup_pulsar_client_and_producer():
        print("Exiting cuz of Pulsar initialization error...")
        return

    mqtt_client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2, client_id=MQTT_CLIENT_ID)
    mqtt_client.on_connect = on_connect_mqtt
    mqtt_client.on_message = on_message_mqtt

    if not try_connect_mqtt():
        print("Couldn't connect to the MQTT Broker. Check the address and restart.")
        if pulsar_producer and pulsar_client:
            pulsar_producer.close()
            pulsar_client.close()
        return

    try:
        mqtt_client.loop_forever()
    except KeyboardInterrupt:
        print("Keyboard interruption on main loop. Closing connections...")
        if mqtt_client:
            mqtt_client.loop_stop()
            mqtt_client.disconnect()
            print("MQTT client closed.")
        if pulsar_producer:
            pulsar_producer.close()
            print("Pulsar producer closed.")
        if pulsar_client:
            pulsar_client.close()
            print("Pulsar client closed.")
    except Exception as e:
        print(f"MQTT main loop error: {e}")


if __name__ == "__main__":
    main()