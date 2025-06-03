import os
import json
import sys
import time
from datetime import datetime, timezone
import random

import paho.mqtt.client as mqtt

# Configurazione del broker MQTT
BROKER = "localhost"
PORT = 1883




def publish(client,topic_pre:str):
    operationalStates = [True,False]
    operationalState = random.choice(operationalStates)
    payload = json.dumps({"operationalStatus": operationalState})
    client.publish("/"+topic_pre+"-operational-status", payload)
    print(f"sent message: {payload}")

    powers = [5, 10, 20]
    power = random.choice(powers)
    payload = json.dumps({"power": power, "timestamp": datetime.now(timezone.utc).isoformat()})
    client.publish("/"+topic_pre+"-power", payload)
    print(f"sent message: {payload}")





def cleanup(client,topic_pre:str):
    payload = json.dumps({"power": False})
    client.publish("/"+topic_pre+"-power", payload)
    print(f"sent message: {payload}")

    payload = json.dumps({"operationalStatus": False})
    client.publish("/"+topic_pre+"-operational-status", payload)
    print(f"sent message: {payload}")





if __name__ == "__main__":
    try:
        client = mqtt.Client()

        client.connect(BROKER, PORT, 60)
        client.loop_start()
        while True:
            publish(client,"tv")
            publish(client, "pc")
            publish(client, "whashing-machine")
            publish(client, "vacum")
            time.sleep(10)


    except KeyboardInterrupt:
        cleanup(client,"tv")
        cleanup(client, "pc")
        cleanup(client, "whashing-machine")
        cleanup(client, "vacum")


        sys.exit(0)
