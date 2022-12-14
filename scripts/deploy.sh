# !/bin/bash

DOCKER_USER_ID="wer080"
APP_VERSION"1.0"

REPOSITORY = /home/ec2-user/app/mentoree-service/zip
PROJECT_NAME = mentoree-webservice

echo "> build 파일 압축 및 이동"
zip -r mentoree-webservice *
mkdir -p deploy
mv mentoree-webservice.zip deploy/mentoree-webservice.zip

echo "> 현재 실행중인 Docker 컨테이너 pid 확인"
CURRENT_PID=$(docker container ls -qa)

if [ -z "${CURRENT_PID}"]
then
  echo "> 현재 구동 중인 컨테이너가 없습니다."
else
  echo "> 현재 구동 중인 컨테이너를 종료합니다. PID = ${CURRENT_PID}"
  sudo docker stop "${CURRENT_PID}"
  sleep 5
  sudo docker rm "${CURRENT_PID}"
  sleep 5
fi

echo "> 이미지 Build ..."
sudo docker rmi ${DOCKER_USER_ID}/mentoree-backend:${APP_VERSION}
sudo docker build -t ${DOCKER_USER_ID}/mentoree-backend:${APP_VERSION}

echo "> Docker compose 파일 실행"
docker-compose -f docker-compose.yml up -d

