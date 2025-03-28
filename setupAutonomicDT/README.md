# Autonomic DT

This repository provides:

-   An **AASX model** of a generic computer mouse.
-   A **docker-compose** configuration for setting up the **BaSyX** environment.
-   A **docker-compose** configuration for setting up the **Apache StreamPipes** environment.
-   A **docker-run** command for setting up the **Node-RED** environment.

## 🔧 Environment Setup

### 1️⃣ Prerequisites

Make sure you have **Docker** installed on your system before proceeding.

### 2️⃣ Installing BaSyX

Run the following command in the `aas-digital-tiwn-modelling/setupAutonomicDT/basyxWithDataBridge` directory:

```bash
docker-compose up -d
```

After installation, move the `mouse.aasx` file to the newly generated "aas" folder.

🔗 **Access BaSyX**: [localhost:3000](http://localhost:3000/)

### 3️⃣ Installing Apache StreamPipes

Run the following command in the `aas-digital-tiwn-modelling/setupAutonomicDT/apacheStreamPipes` directory:

```bash
docker compose --env-file env.txt up -d
```

🔗 **Access Apache StreamPipes**: [localhost:8085](http://localhost:8085/)

**Default credentials:**

```
Username: admin@streampipes.apache.org
Password: admin
```

### 4️⃣ Installing Node-RED

Run the following command from any directory:

```bash
docker run -it -p 1880:1880 -v node_red_data:/data --name mynodered nodered/node-red
```

## 📡 Configuring the MQTT Data Source

To obtain data in the autonomic manager, it is necessary to configure an **MQTT Data Source**. BaSyX emits values on a topic based on the invoked HTTP REST API.

With the Camel routes configured in this project, the invoked AAS v3 API is:

```http
PATCH submodels/{submodelIdentifier}/submodel-elements/{idShortPath}/$value
```

🔹 **Example topic for the mouse model temperature property:**

```text
sm-repository/sm-repo/submodels/aHR0cHM6Ly9leGFtcGxlLmNvbS9pZHMvc20vNzQ2NF8xMTgxXzMwNTJfNTYzNg/submodelElements/BatteryStatus.BatteryPercentage/updated
```

📖 **BaSyX Eventing Documentation:**  
[BaSyX Eventing Documentation](https://wiki.basyx.org/en/latest/content/user_documentation/basyx_components/v2/submodel_repository/features/mqtt.html)
