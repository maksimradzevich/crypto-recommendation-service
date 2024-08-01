# Overview
The Crypto Recommendation Service is a Spring Boot application that provides cryptocurrency recommendations based on price trends and other factors.

## Running locally
- Pass path to price storage in environment variable STORAGE_PATH
- Specify spring profile 'local' in running configuration

## Running inside Kubernetes cluster
  - Build docker image 
    ```sh
    mvn package docker:build
    ```
  - Install Ingress on kubernetes cluster
  - Configure **Ingress rate limiter** in 'kubernetes/ingress.yaml'
  - Deploy kubernetes resources in folder 'kubernetes'

# Contact
For any inquiries, please contact Maksim Radzevich at Maksim_Radzevich@epam.com.