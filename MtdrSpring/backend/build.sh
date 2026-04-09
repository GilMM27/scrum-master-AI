#!/bin/bash

export IMAGE_NAME=todolistapp-springboot
export IMAGE_VERSION=0.1


DEV_MODE=false
if [ "$1" = "--dev" ]; then
    DEV_MODE=true
fi
if [ "$DEV_MODE" = true ]; then
    echo "=== Dev Mode: Cleaning up containers and images ==="
    docker stop agilecontainer 2>/dev/null
    docker rm -f agilecontainer 2>/dev/null
    docker rmi agileimage 2>/dev/null
    
    echo "=== Running Maven verify ==="
    mvn clean verify
    
    echo "=== Building Dev Docker image ==="
    docker build -f DockerfileDev --platform linux/amd64 -t agileimage:0.1 .
    
    echo "=== Starting container ==="
    docker run --name agilecontainer --volume "$PWD/target:/tmp/target:rw" -p 8080:8080 -d agileimage:0.1
    exit 0
fi



if [ -z "$DOCKER_REGISTRY" ]; then
    export DOCKER_REGISTRY=$(state_get DOCKER_REGISTRY)
    echo "DOCKER_REGISTRY set."
fi
if [ -z "$DOCKER_REGISTRY" ]; then
    echo "Error: DOCKER_REGISTRY env variable needs to be set!"
    exit 1
fi

export IMAGE=${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_VERSION}

mvn clean package spring-boot:repackage
docker build -f Dockerfile -t $IMAGE .

docker push $IMAGE
if [  $? -eq 0 ]; then
    docker rmi "$IMAGE" #local
fi