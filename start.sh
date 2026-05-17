#!/bin/bash

# ── Java 경로 설정 ─────────────────────────────────────────
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# ── 설정 ──────────────────────────────────────────────────
BASE_DIR="/home/bizline/springboot"
LOG_DIR="$BASE_DIR/logs"
LOG_FILE="$LOG_DIR/logs-$(date '+%Y%m%d_%H%M%S').log"
PROFILE="prod"

# ── 로그 디렉토리 생성 ─────────────────────────────────────
mkdir -p $LOG_DIR

# ── Graceful Shutdown ──────────────────────────────────────
EXISTING_PID=$(pgrep -f "back-springboot")
if [ -n "$EXISTING_PID" ]; then
    echo "[SpringBoot] Graceful shutdown 시작 (PID: $EXISTING_PID)..."
    kill -15 $EXISTING_PID

    for i in $(seq 1 30); do
        if ! kill -0 $EXISTING_PID 2>/dev/null; then
            echo "[SpringBoot] 정상 종료 완료"
            break
        fi
        echo "[SpringBoot] 종료 대기 중... ($i/30)"
        sleep 1
    done

    if kill -0 $EXISTING_PID 2>/dev/null; then
        echo "[SpringBoot] 강제 종료..."
        kill -9 $EXISTING_PID
        sleep 1
    fi
else
    echo "[SpringBoot] 실행 중인 프로세스 없음"
fi

# ── JAR 파일 확인 ──────────────────────────────────────────
JAR_FILE=$(ls $BASE_DIR/*.jar 2>/dev/null | grep -v plain | head -1)
if [ -z "$JAR_FILE" ]; then
    echo "[SpringBoot] ❌ JAR 파일 없음 - 종료"
    exit 1
fi

echo "[SpringBoot] JAR: $JAR_FILE"

# ── 실행 ──────────────────────────────────────────────────
nohup java -jar $JAR_FILE \
    --spring.profiles.active=$PROFILE \
    --spring.config.additional-location=file:$BASE_DIR/ \
    > $LOG_FILE 2>&1 &

echo "[SpringBoot] PID: $!"
echo "[SpringBoot] PROFILE: $PROFILE"
echo "[SpringBoot] 시작 완료"
echo "[SpringBoot] 로그: tail -f $LOG_FILE"
