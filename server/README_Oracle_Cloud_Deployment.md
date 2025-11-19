# 🚀 JobReady Backend - Triển khai lên Oracle Cloud Infrastructure (OCI)

Hướng dẫn này cung cấp các bước chi tiết để triển khai kiến trúc microservices JobReady lên Oracle Cloud Infrastructure (OCI) sử dụng Oracle Kubernetes Engine (OKE) và các dịch vụ OCI khác.

## 📋 Điều kiện tiên quyết

### Thiết lập tài khoản OCI

1. **Tạo tài khoản OCI**: Đăng ký tại [oracle.com/cloud](https://www.oracle.com/cloud/)
2. **Tạo Compartment**: Tạo một compartment riêng cho dự án của bạn
3. **Tạo API Keys**:
   - Vào User Settings → API Keys
   - Tạo và tải xuống cặp khóa RSA
   - Ghi nhớ User OCID, Tenancy OCID, và Region của bạn

### Môi trường cục bộ

- **OCI CLI**: Cài đặt từ [docs.oracle.com](https://docs.oracle.com/en-us/iaas/Content/API/SDKDocs/cliinstall.htm)
- **kubectl**: Cài đặt Kubernetes CLI
- **Helm**: Cài đặt Helm 3.x
- **Docker**: Để build images
- **Maven**: Để build các service Java

### Dịch vụ OCI cần thiết

- **Oracle Kubernetes Engine (OKE)**
- **Oracle Container Registry (OCIR)**
- **Virtual Cloud Network (VCN)**
- **MySQL Database** (hoặc sử dụng containerized)
- **PostgreSQL Database** (hoặc sử dụng containerized)
- **Load Balancer**

## 🏗️ Tổng quan kiến trúc

```
Internet
    ↓
OCI Load Balancer (Public IP)
    ↓
OKE Cluster (Kubernetes Services)
    ↓
Microservices (Gateway → Auth/User/CV/AI/Mail/Stats)
    ↓
Databases (MySQL, PostgreSQL)
Message Queue (RabbitMQ)
Cache (Redis)
```

## 🚀 Các bước triển khai chi tiết

### Bước 1: Cấu hình OCI CLI

```bash
# Cấu hình OCI CLI
oci setup config

# Nhập thông tin của bạn:
# - User OCID
# - Tenancy OCID
# - Region (ví dụ: us-ashburn-1)
# - Đường dẫn đến private key
# - Passphrase (nếu có)
```

### Bước 2: Tạo VCN và Subnets

```bash
# Tạo VCN
oci network vcn create \
  --compartment-id <compartment-ocid> \
  --display-name jobready-vcn \
  --cidr-block 10.0.0.0/16

# Tạo Internet Gateway
oci network internet-gateway create \
  --compartment-id <compartment-ocid> \
  --vcn-id <vcn-ocid> \
  --display-name jobready-igw

# Tạo Route Table
oci network route-table create \
  --compartment-id <compartment-ocid> \
  --vcn-id <vcn-ocid> \
  --display-name jobready-rt \
  --route-rules '[{"cidrBlock":"0.0.0.0/0","networkEntityId":"<igw-ocid>"}]'

# Tạo Security List
oci network security-list create \
  --compartment-id <compartment-ocid> \
  --vcn-id <vcn-ocid> \
  --display-name jobready-sl \
  --egress-security-rules '[{"destination":"0.0.0.0/0","protocol":"6"}]' \
  --ingress-security-rules '[{"source":"0.0.0.0/0","protocol":"6","tcpOptions":{"destinationPortRange":{"max":80,"min":80}}},{"source":"0.0.0.0/0","protocol":"6","tcpOptions":{"destinationPortRange":{"max":443,"min":443}}}]'

# Tạo Subnets (Public và Private)
oci network subnet create \
  --compartment-id <compartment-ocid> \
  --vcn-id <vcn-ocid> \
  --display-name jobready-public-subnet \
  --cidr-block 10.0.1.0/24 \
  --route-table-id <rt-ocid> \
  --security-list-ids '["<sl-ocid>"]'

oci network subnet create \
  --compartment-id <compartment-ocid> \
  --vcn-id <vcn-ocid> \
  --display-name jobready-private-subnet \
  --cidr-block 10.0.2.0/24 \
  --prohibit-public-ip-on-vnic true
```

### Bước 3: Tạo OKE Cluster

```bash
# Tạo OKE Cluster
oci ce cluster create \
  --compartment-id <compartment-ocid> \
  --name jobready-cluster \
  --vcn-id <vcn-ocid> \
  --kubernetes-version v1.28.2 \
  --node-shape VM.Standard.E4.Flex \
  --node-count 3 \
  --subnet-ids '["<public-subnet-ocid>","<private-subnet-ocid>"]'

# Chờ tạo cluster (khoảng 10-15 phút)
oci ce cluster get --cluster-id <cluster-ocid>
```

### Bước 4: Cấu hình kubectl cho OKE

```bash
# Lấy kubeconfig của cluster
oci ce cluster create-kubeconfig \
  --cluster-id <cluster-ocid> \
  --file ~/.kube/config \
  --region <region> \
  --token-version 2.0.0

# Kiểm tra kết nối
kubectl get nodes
```

### Bước 5: Tạo OCIR Repository

```bash
# Tạo OCIR repositories cho từng service
oci artifacts container repository create \
  --compartment-id <compartment-ocid> \
  --display-name gateway-service \
  --is-public true

# Lặp lại cho: auth-service, user-service, cv-service, ai-service, mail-service, stats-service
```

### Bước 6: Build và Push Docker Images

```bash
# Đăng nhập vào OCIR
docker login <region>.ocir.io -u <tenancy-namespace>/<username>

# Build và push images
cd server

# Gateway Service
docker build -f gateway-service/Dockerfile -t <region>.ocir.io/<tenancy-namespace>/gateway-service:latest .
docker push <region>.ocir.io/<tenancy-namespace>/gateway-service:latest

# Auth Service
docker build -f auth-service/Dockerfile -t <region>.ocir.io/<tenancy-namespace>/auth-service:latest .
docker push <region>.ocir.io/<tenancy-namespace>/auth-service:latest

# User Service
docker build -f user-service/Dockerfile -t <region>.ocir.io/<tenancy-namespace>/user-service:latest .
docker push <region>.ocir.io/<tenancy-namespace>/user-service:latest

# CV Service
docker build -f cv-service/Dockerfile -t <region>.ocir.io/<tenancy-namespace>/cv-service:latest .
docker push <region>.ocir.io/<tenancy-namespace>/cv-service:latest

# AI Service
docker build -f ai-service/Dockerfile -t <region>.ocir.io/<tenancy-namespace>/ai-service:latest .
docker push <region>.ocir.io/<tenancy-namespace>/ai-service:latest

# Mail Service
docker build -f mail-service/Dockerfile -t <region>.ocir.io/<tenancy-namespace>/mail-service:latest .
docker push <region>.ocir.io/<tenancy-namespace>/mail-service:latest

# Stats Service
docker build -f stats-service/Dockerfile -t <region>.ocir.io/<tenancy-namespace>/stats-service:latest .
docker push <region>.ocir.io/<tenancy-namespace>/stats-service:latest
```

### Bước 7: Triển khai Infrastructure (Databases & Message Queue)

```bash
# Thêm Helm repositories
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

# Tạo namespace
kubectl create namespace jobready

# Triển khai MySQL
helm install mysql bitnami/mysql \
  --namespace jobready \
  --set auth.rootPassword=<mysql-root-password> \
  --set auth.database=jobready \
  --set persistence.enabled=true \
  --set persistence.size=50Gi

# Triển khai PostgreSQL với pgvector
helm install postgres bitnami/postgresql \
  --namespace jobready \
  --set auth.postgresPassword=<postgres-password> \
  --set auth.database=aidb \
  --set persistence.enabled=true \
  --set persistence.size=50Gi

# Kích hoạt pgvector extension
kubectl exec -it postgres-postgresql-0 -n jobready -- psql -U postgres -d aidb -c "CREATE EXTENSION vector;"

# Triển khai RabbitMQ
helm install rabbitmq bitnami/rabbitmq \
  --namespace jobready \
  --set auth.username=guest \
  --set auth.password=<rabbitmq-password> \
  --set persistence.enabled=true \
  --set persistence.size=20Gi

# Triển khai Redis
helm install redis bitnami/redis \
  --namespace jobready \
  --set auth.password=<redis-password> \
  --set persistence.enabled=true \
  --set persistence.size=10Gi
```

### Bước 8: Tạo Kubernetes Secrets

```bash
# Database secrets
kubectl create secret generic mysql-secret \
  --namespace jobready \
  --from-literal=url='jdbc:mysql://mysql:3306/jobready' \
  --from-literal=username='root' \
  --from-literal=password='<mysql-root-password>'

kubectl create secret generic postgres-secret \
  --namespace jobready \
  --from-literal=url='jdbc:postgresql://postgres:5432/aidb' \
  --from-literal=username='postgres' \
  --from-literal=password='<postgres-password>'

# Message queue secrets
kubectl create secret generic rabbitmq-secret \
  --namespace jobready \
  --from-literal=host='rabbitmq' \
  --from-literal=username='guest' \
  --from-literal=password='<rabbitmq-password>'

# Redis secret
kubectl create secret generic redis-secret \
  --namespace jobready \
  --from-literal=password='<redis-password>'

# JWT Keys (tạo cục bộ trước)
# cd config/keys && javac KeyGenerator.java && java KeyGenerator
kubectl create secret generic jwt-secret \
  --namespace jobready \
  --from-file=private-key=./config/keys/private_key.pem \
  --from-file=public-key=./config/keys/public_key.pem

# OpenRouter API Key
kubectl create secret generic openrouter-secret \
  --namespace jobready \
  --from-literal=api-key='<your-openrouter-api-key>'

# OAuth2 Secrets (nếu sử dụng)
kubectl create secret generic oauth2-secret \
  --namespace jobready \
  --from-literal=google-client-id='<google-client-id>' \
  --from-literal=google-client-secret='<google-client-secret>' \
  --from-literal=github-client-id='<github-client-id>' \
  --from-literal=github-client-secret='<github-client-secret>' \
  --from-literal=facebook-client-id='<facebook-client-id>' \
  --from-literal=facebook-client-secret='<facebook-client-secret>'

# SMTP Secret
kubectl create secret generic smtp-secret \
  --namespace jobready \
  --from-literal=username='<smtp-username>' \
  --from-literal=password='<smtp-password>' \
  --from-literal=host='<smtp-host>' \
  --from-literal=port='<smtp-port>'
```

### Bước 9: Cập nhật Kubernetes Manifests cho OCI

Tạo overlay dành riêng cho OCI trong `k8s/overlays/oci/`:

```yaml
# k8s/overlays/oci/kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

namespace: jobready

bases:
  - ../../base

images:
  - name: gateway-service
    newTag: latest
    newName: <region>.ocir.io/<tenancy-namespace>/gateway-service
  - name: auth-service
    newTag: latest
    newName: <region>.ocir.io/<tenancy-namespace>/auth-service
  - name: user-service
    newTag: latest
    newName: <region>.ocir.io/<tenancy-namespace>/user-service
  - name: cv-service
    newTag: latest
    newName: <region>.ocir.io/<tenancy-namespace>/cv-service
  - name: ai-service
    newTag: latest
    newName: <region>.ocir.io/<tenancy-namespace>/ai-service
  - name: mail-service
    newTag: latest
    newName: <region>.ocir.io/<tenancy-namespace>/mail-service
  - name: stats-service
    newTag: latest
    newName: <region>.ocir.io/<tenancy-namespace>/stats-service

patchesStrategicMerge:
  - replica-patch.yaml

commonLabels:
  environment: oci
```

```yaml
# k8s/overlays/oci/replica-patch.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: gateway-service
spec:
  replicas: 2
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-service
spec:
  replicas: 2
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  replicas: 2
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: cv-service
spec:
  replicas: 2
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ai-service
spec:
  replicas: 2
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mail-service
spec:
  replicas: 1
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: stats-service
spec:
  replicas: 1
```

### Bước 10: Triển khai Application lên OKE

```bash
# Triển khai lên OCI
kubectl apply -k k8s/overlays/oci/

# Chờ deployments
kubectl get pods -n jobready -w

# Kiểm tra services
kubectl get services -n jobready
```

### Bước 11: Cấu hình Load Balancer

```bash
# Lấy LoadBalancer IP
kubectl get svc gateway-service -n jobready

# Ghi chú EXTERNAL-IP (sẽ được gán bởi OCI Load Balancer)
```

### Bước 12: Cấu hình DNS (Tùy chọn)

```bash
# Trỏ domain của bạn đến LoadBalancer IP
# Ví dụ: api.jobready.com -> <load-balancer-ip>
```

## 🔧 Cấu hình

### Biến môi trường

Cập nhật ConfigMaps trong `k8s/configmaps/` với giá trị dành riêng cho OCI:

```yaml
# Ví dụ: gateway-configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: gateway-configmap
data:
  SPRING_PROFILES_ACTIVE: "oci"
  SERVER_PORT: "8080"
  # Thêm các config khác dành riêng cho gateway
```

### Giới hạn tài nguyên

Cập nhật deployments với giới hạn tài nguyên phù hợp:

```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "500m"
```

## 📊 Giám sát & Logging

### OCI Monitoring

```bash
# Kích hoạt OCI Monitoring cho cluster
oci ce cluster update \
  --cluster-id <cluster-ocid> \
  --is-monitoring-enabled true
```

### Prometheus & Grafana (Tùy chọn)

```bash
# Cài đặt Prometheus stack
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm install prometheus prometheus-community/kube-prometheus-stack --namespace monitoring --create-namespace
```

### Logging

```bash
# Xem logs
kubectl logs -f deployment/gateway-service -n jobready

# Tích hợp OCI Logging
oci logging log create \
  --compartment-id <compartment-ocid> \
  --display-name jobready-logs \
  --log-type SERVICE \
  --source-type OCISERVICE \
  --source-service OKE \
  --source-resource <cluster-ocid>
```

## 🔒 Các thực hành bảo mật tốt nhất

### Bảo mật mạng

- Sử dụng private subnets cho databases
- Cấu hình Security Lists đúng cách
- Kích hoạt OCI Web Application Firewall (WAF)

### Kiểm soát truy cập

- Sử dụng OCI Identity and Access Management (IAM)
- Triển khai Kubernetes RBAC
- Xoay vòng secrets thường xuyên

### Mã hóa dữ liệu

- Kích hoạt mã hóa at rest cho databases
- Sử dụng HTTPS cho tất cả communications
- Lưu trữ secrets trong OCI Vault

## 🚨 Khắc phục sự cố

### Các vấn đề phổ biến

1. **Pods không khởi động**

   ```bash
   kubectl describe pod <pod-name> -n jobready
   kubectl logs <pod-name> -n jobready
   ```

2. **Vấn đề giao tiếp service**

   ```bash
   kubectl exec -it <pod-name> -n jobready -- nslookup user-service
   ```

3. **Vấn đề kết nối database**

   ```bash
   kubectl exec -it <pod-name> -n jobready -- telnet mysql 3306
   ```

4. **Vấn đề pull image**
   ```bash
   kubectl describe pod <pod-name> -n jobready
   # Kiểm tra credentials OCIR có đúng không
   ```

### Các vấn đề dành riêng cho OCI

1. **Load Balancer không truy cập được**

   - Kiểm tra Security Lists cho phép traffic trên port 80/443
   - Xác minh cấu hình subnet

2. **OCIR push/pull thất bại**
   - Đảm bảo tenancy namespace đúng
   - Kiểm tra quyền API key

## 💰 Tối ưu hóa chi phí

### Right-sizing Resources

- Bắt đầu với node shapes tối thiểu (VM.Standard.E4.Flex)
- Sử dụng Horizontal Pod Autoscaler (HPA)
- Giám sát usage và điều chỉnh

### Auto-scaling

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: gateway-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: gateway-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
```

## 📞 Hỗ trợ

- **OCI Documentation**: [docs.oracle.com/en-us/iaas](https://docs.oracle.com/en-us/iaas)
- **Kubernetes Documentation**: [kubernetes.io/docs](https://kubernetes.io/docs)
- **JobReady Issues**: Tạo GitHub issues cho các vấn đề cụ thể của ứng dụng

## 🔄 Cập nhật & Bảo trì

### Rolling Updates

```bash
# Cập nhật images
kubectl set image deployment/gateway-service gateway-service=<new-image> -n jobready
kubectl rollout status deployment/gateway-service -n jobready
```

### Chiến lược Backup

- Database backups sử dụng OCI Object Storage
- Regular image backups
- Configuration backups

---

**Lưu ý**: Thay thế `<compartment-ocid>`, `<vcn-ocid>`, `<region>`, `<tenancy-namespace>`, và các placeholders khác với giá trị OCI thực tế của bạn.</content>
<parameter name="filePath">C:\Users\ASUS\OneDrive\Desktop\Learn\Backend\Microservice\projects\JobReady\server\README_Oracle_Cloud_Deployment.md
