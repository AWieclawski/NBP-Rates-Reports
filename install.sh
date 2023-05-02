#!/bin/bash

PG_DIR="/nbpdata/pg12"
DC_FILE="./core/data-provider/docker-compose.yml"
if [[ ! -d "$PG_DIR"  && -f "$DC_FILE" ]]
  then
    echo "Preparing local data storage directory in ${PG_DIR}..."
    sudo mkdir -p "$PG_DIR"
    echo "About to build docker using ${DC_FILE} from ${PWD}" 
    sudo docker-compose -f "$DC_FILE" up -d
    echo "Presentation of active dockers:"
    sudo docker ps
    echo "Presentation of docker volumes:"
    sudo docker volume ls
  else
    echo "Installation aborted!"  
fi
test -d "$PG_DIR" && echo "${PG_DIR} exists." || echo "${PG_DIR} does not exist!"
test -f "$DC_FILE" && echo "${DC_FILE} exists." || echo "${DC_FILE} does not exist!"
