# 빌드 환경 설정 (jdk)
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# gradle wrapper의 필요한 파일 먼저 복사
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY src ./src

# 캐시를 활용해 의존성 다운받기
RUN ./gradlew build -x test --no-daemon

# 실행에 필요한 JAR 추출
RUN cp build/libs/$(ls build/libs | grep -v plain) app.jar

# 런타임 환경 (JRE만 포함, 더 가벼움)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/app.jar .

# 포트 노출 (spring 앱이 실행될 포트)
EXPOSE 8080

# 애플리케이션 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]