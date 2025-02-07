
# AAS Modelling with BaSyX

This repository provides:

- An AASX Model of a generic computer mouse.
- A *docker-compose* configuration used for setting up the BaSyX environment.

### Description of the AAS

The model is currently at its **first iteration** and serves as a generic template for potential future real-world digital twin implementations.\
The model was created entirely using AASX Package Explorer.

Currently, it is divided into two submodels:
- *Nameplate*: provides general information about the product and the manufacturer.
- *TechnicalData*: Contains technical data of the asset, along with its associated product classifications.

### Docker-Compose Setup for BaSyX
The provided docker-compose file can be used to set up the BaSyX environment for testing and deploying the AAS model.\
If you wish to generate your own compose file, it's recommended to visit the [Get Started](https://basyx.org/get-started/introduction) page on the official website, where a wizard will guide you through it.

**How to Setup**

1. Clone this repository and ensure you have Docker installed on your system.
2. Run the following command in the root of the repository:
    ```bash
    docker-compose up -d
    ```
3. Move mouse.aasx to the newly generated "aas" folder.
4. Access BaSyX via [localhost:3000](http://localhost:3000/)

