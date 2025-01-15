# 실행방법
# PASSWORD="your-password" ./deploy.sh

# 설정
SERVER_USER="jyp"                              # 서버 사용자 이름
SERVER_HOST="notice-crawler.ddns.net"          # 서버 주소
SERVER_PATH="dev/crawler"                      # 서버의 애플리케이션 경로
JAR_NAME="crawler-0.0.1-SNAPSHOT.jar"          # 빌드된 JAR 파일 이름
LOCAL_BUILD_PATH="build/libs"                  # 로컬 빌드 JAR 파일 경로
# 1. 로컬에서 JAR 파일 빌드
echo "로컬에서 애플리케이션 빌드 중..."
./gradlew bootJar || { echo "빌드 실패!"; exit 1; }

# 2. 서버의 기존 JAR 파일 삭제
echo "서버에서 기존 JAR 파일 삭제 중..."
sshpass -p "$PASSWORD" ssh $SERVER_USER@$SERVER_HOST "rm -f $SERVER_PATH/$JAR_NAME" || { echo "기존 JAR 파일 삭제 실패!"; exit 1; }

# 3. 새 JAR 파일 전송
echo "새 JAR 파일 전송 중..."
sshpass -p "$PASSWORD" scp $LOCAL_BUILD_PATH/$JAR_NAME $SERVER_USER@$SERVER_HOST:$SERVER_PATH || { echo "JAR 파일 전송 실패!"; exit 1; }

# 4. 서버에서 애플리케이션 재시작
echo "서버에서 애플리케이션 재시작 중..."
sshpass -p "$PASSWORD" ssh $SERVER_USER@$SERVER_HOST "cd $SERVER_PATH && ./restart.sh" || { echo "애플리케이션 재시작 실패!"; exit 1; }

echo "배포 완료!"