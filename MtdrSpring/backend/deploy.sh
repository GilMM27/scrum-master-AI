#!/bin/bash
SCRIPT_DIR=$(pwd)

#Validation
if [ -z "$DOCKER_REGISTRY" ]; then
    export DOCKER_REGISTRY=$(state_get DOCKER_REGISTRY)
    echo "DOCKER_REGISTRY set."
fi
if [ -z "$DOCKER_REGISTRY" ]; then
    echo "Error: DOCKER_REGISTRY env variable needs to be set!"
    exit 1
fi

if [ -z "$TODO_PDB_NAME" ]; then
    export TODO_PDB_NAME=$(state_get MTDR_DB_NAME)
    echo "TODO_PDB_NAME set."
fi
if [ -z "$TODO_PDB_NAME" ]; then
    echo "Error: TODO_PDB_NAME env variable needs to be set!"
    exit 1
fi

if [ -z "$OCI_REGION" ]; then
    echo "OCI_REGION not set. Will get it with state_get"
    export OCI_REGION=$(state_get REGION)
fi
if [ -z "$OCI_REGION" ]; then
    echo "Error: OCI_REGION env variable needs to be set!"
    exit 1
fi

if [ -z "$UI_USERNAME" ]; then
    echo "UI_USERNAME not set. Will get it with state_get"
    export UI_USERNAME=$(state_get UI_USERNAME)
fi
if [ -z "$UI_USERNAME" ]; then
    echo "Error: UI_USERNAME env variable needs to be set!"
    exit 1
fi

# New required vars
if [ -z "$TELEGRAM_BOT_NAME" ]; then
    echo "Error: TELEGRAM_BOT_NAME env variable needs to be set!"
    exit 1
fi

if [ -z "$TELEGRAM_BOT_TOKEN" ]; then
    echo "Error: TELEGRAM_BOT_TOKEN env variable needs to be set!"
    exit 1
fi

if [ -z "$JWT_SECRET" ]; then
    echo "Error: JWT_SECRET env variable needs to be set!"
    exit 1
fi

if [ -z "$JWT_EXPIRATION" ]; then
    export JWT_EXPIRATION=86400000
fi

if [ -z "$GEMINI_API_KEY" ]; then
    echo "Error: GEMINI_API_KEY env variable needs to be set!"
    exit 1
fi

echo "Creating springboot deployment, service, configmap and secrets"

export CURRENTTIME=$(date '+%F_%H:%M:%S')
echo "CURRENTTIME is $CURRENTTIME ...this will be appended to generated deployment yaml"

cp src/main/resources/todolistapp-springboot.yaml todolistapp-springboot-$CURRENTTIME.yaml

sed -e "s|%DOCKER_REGISTRY%|${DOCKER_REGISTRY}|g" \
    -e "s|%TODO_PDB_NAME%|${TODO_PDB_NAME}|g" \
    -e "s|%OCI_REGION%|${OCI_REGION}|g" \
    -e "s|%UI_USERNAME%|${UI_USERNAME}|g" \
    -e "s|%TELEGRAM_BOT_NAME%|${TELEGRAM_BOT_NAME}|g" \
    -e "s|%TELEGRAM_BOT_TOKEN%|${TELEGRAM_BOT_TOKEN}|g" \
    -e "s|%GEMINI_API_KEY%|${GEMINI_API_KEY}|g" \
    -e "s|%JWT_SECRET%|${JWT_SECRET}|g" \
    -e "s|%JWT_EXPIRATION%|${JWT_EXPIRATION}|g" \
    todolistapp-springboot-$CURRENTTIME.yaml > /tmp/todolistapp-springboot-$CURRENTTIME.yaml

mv -- /tmp/todolistapp-springboot-$CURRENTTIME.yaml todolistapp-springboot-$CURRENTTIME.yaml

if [ -z "$1" ]; then
    kubectl apply -f $SCRIPT_DIR/todolistapp-springboot-$CURRENTTIME.yaml -n mtdrworkshop
else
    kubectl apply -f <(istioctl kube-inject -f $SCRIPT_DIR/todolistapp-springboot-$CURRENTTIME.yaml) -n mtdrworkshop
fi
