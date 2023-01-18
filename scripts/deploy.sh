#!/bin/bash

DOCKER_USER_ID="wer080"
APP_VERSION="1.0"

WORK_DIR="/home/ec2-user/app/mentoree-webservice/"

echo "> 디렉토리 이동"
cd $WORK_DIR

echo "> 기존 컨테이너 종료 ..."
sudo docker stop $(sudo docker ps -qa -f "name=mentoree-backend")
sudo docker rm $(sudo docker ps -qa -f "name=mentoree-backend")

echo "> 기존 이미지 삭제 ..."
sudo docker rmi ${DOCKER_USER_ID}/mentoree-backend:${APP_VERSION}

echo "> 이미지 Build ..."
sudo docker build -t ${DOCKER_USER_ID}/mentoree-backend:${APP_VERSION}

echo "> Docker compose 파일 실행"
sudo docker-compose -f docker-compose.yml up -d

