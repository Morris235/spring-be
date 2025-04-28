### ✅ Spring Boot

**1. `Dockerfile`**

```dockerfile
FROM eclipse-temurin:17-jre-bookworm  # 베이스 이미지 설정
WORKDIR /app  # 작업 디렉토리 설정
COPY build/libs/*.jar app.jar  # 파일 복사
ENTRYPOINT ["java", "-jar", "app.jar"]  # 컨테이너 실행 명령
```

**2. 빌드 및 푸시**

```bash
cd spring-be
./gradlew build
docker build -t morris235/spring-be:latest .  # 도커 이미지 빌드
docker push morris235/spring-be:latest  # 도커 이미지 푸시
```

---

## ☸️ Kubernetes 리소스 생성

### ✅ 서비스 설정 (ClusterIP)

**spring-be-service.yaml**
```yaml
apiVersion: v1  # 리소스 API 버전 지정
kind: Service  # 리소스 종류 (Service, Ingress 등)
metadata:  # 메타데이터 정의 시작
  name: spring-be-service  # 리소스 이름 설정
spec:  # 실제 설정 시작
  selector:  # 연결할 Pod의 라벨 선택
    app: spring-be
  ports:  # 포트 정의 블록 시작
    - port: 8080  # 서비스가 노출할 포트
      targetPort: 8080  # 실제 컨테이너 내부 포트
```

**vue-fe-service.yaml**
```yaml
apiVersion: v1  # 리소스 API 버전 지정
kind: Service  # 리소스 종류 (Service, Ingress 등)
metadata:  # 메타데이터 정의 시작
  name: vue-fe-service  # 리소스 이름 설정
spec:  # 실제 설정 시작
  selector:  # 연결할 Pod의 라벨 선택
    app: vue-fe
  ports:  # 포트 정의 블록 시작
    - port: 80  # 서비스가 노출할 포트
      targetPort: 80  # 실제 컨테이너 내부 포트
```

**적용:**
```bash
kubectl apply -f spring-be-service.yaml  # 쿠버네티스 리소스 적용
kubectl apply -f vue-fe-service.yaml  # 쿠버네티스 리소스 적용
```

---

### ✅ Ingress 설정 (CORS + 최적화)

**app-ingress.yaml**
```yaml
apiVersion: networking.k8s.io/v1  # 리소스 API 버전 지정
kind: Ingress  # 리소스 종류 (Service, Ingress 등)
metadata:  # 메타데이터 정의 시작
  name: app-ingress  # 리소스 이름 설정
  annotations:
    nginx.ingress.kubernetes.io/enable-cors: "true"
    nginx.ingress.kubernetes.io/cors-allow-origin: "http://vue.localtest.me"
    nginx.ingress.kubernetes.io/cors-allow-methods: "GET, POST, OPTIONS"
    nginx.ingress.kubernetes.io/cors-allow-headers: "Content-Type"
    nginx.ingress.kubernetes.io/cors-allow-credentials: "true"
    nginx.ingress.kubernetes.io/proxy-buffering: "off"
spec:  # 실제 설정 시작
  ingressClassName: nginx
  rules:  # 인그레스 도메인/경로 라우팅 규칙
    - host: vue.localtest.me
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: vue-fe-service  # 리소스 이름 설정
                port:  # 서비스가 노출할 포트
                  number: 80
    - host: api.localtest.me
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: spring-be-service  # 리소스 이름 설정
                port:  # 서비스가 노출할 포트
                  number: 8080
```

**적용:**
```bash
kubectl apply -f app-ingress.yaml  # 쿠버네티스 리소스 적용
```

---

## 🔄 배포 이미지 적용 (Rollout)

```bash
kubectl rollout restart deployment vue-fe-deployment  # 디플로이먼트 재시작
kubectl rollout restart deployment spring-be-deployment  # 디플로이먼트 재시작
```

---

## 🌐 접속 및 테스트

> 반드시 `Ingress Controller`가 정상 설치되어 있어야 함

### ✅ Ingress 주소 확인
```bash
kubectl get ingress
```

### ✅ 브라우저 접속

- [http://vue.localtest.me](http://vue.localtest.me)
- [http://api.localtest.me/plus](http://api.localtest.me/plus)

### ✅ API 직접 테스트

```bash
curl -X POST http://api.localtest.me/plus \  # API 호출 테스트
  -H "Content-Type: application/json" \
  -d '{"num1":1,"num2":2}'
```

---

## ✅ 결과 정리

| 항목 | 처리 방식 |
|------|-----------|
| Docker 빌드 | `Dockerfile`로 multi-stage 빌드 |
| Vue 정적 리소스 최적화 | Nginx 설정 (`expires`, `cache-control`, `gzip`) |
| API 요청 속도 개선 | Ingress proxy-buffering 비활성화 |
| CORS 문제 | Ingress에서 해결 (`nginx.ingress.kubernetes.io/enable-cors`) |
| 배포 방식 | Docker Hub → Kubernetes Rollout |
| 도메인 테스트 | `vue.localtest.me`, `api.localtest.me` 사용 (127.0.0.1 자동 연결) |

---

> 이 문서는 Apple Silicon 환경에서 Kubernetes 기반으로 Vue + Spring Boot를 배포하려는 개발자를 위한 최적화된 실전 배포 가이드입니다.