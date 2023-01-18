#!/bin/bash

DOCKER_USER_ID="wer080"
echo "> DOCKER_USER_ID = ${DOCKER_USER_ID}"

APP_VERSION="1.0"
echo "> APP VERSION = ${APP_VERSION}"

WORK_DIR="/home/ec2-user/app/mentoree-webservice/"
echo "> WORK_DIR = ${WORK_DIR}"

echo "> 디렉토리 이동"
cd $WORK_DIR
pwd


echo "> 기존 컨테이너 종료 ..."
sudo docker stop $(sudo docker ps -qa -f "name=mentoree-backend")
sudo docker rm $(sudo docker ps -qa -f "name=mentoree-backend")

echo "> 기존 이미지 삭제 ..."
sudo docker rmi ${DOCKER_USER_ID}/mentoree-backend:${APP_VERSION}

echo "> 이미지 Build ..."
sudo docker build -t ${DOCKER_USER_ID}/mentoree-backend:${APP_VERSION}

echo "> Docker compose 파일 실행"
sudo docker-compose -f docker-compose.yml up -d

