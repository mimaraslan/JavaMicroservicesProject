# 🐳 Docker Deployment Guide - Mikroservisler

Bu rehber, tüm mikroservisleri Docker container'larına deploy etmek için gerekli adımları içerir.

---

## 📋 Ön Gereksinimler

- Docker Desktop (Windows/Mac) veya Docker Engine (Linux) yüklü olmalı
- Docker Compose yüklü olmalı
- Java 21 ve Gradle yüklü olmalı (JAR build için)

---

## 🏗️ Adım 1: JAR Dosyalarını Build Etme

Önce tüm mikroservislerin JAR dosyalarını oluşturmanız gerekiyor:

```bash
# Windows PowerShell
.\gradlew.bat clean build -x test

# Linux/Mac
./gradlew clean build -x test
```

Bu komut her mikroservis için `build/libs/` dizininde JAR dosyaları oluşturur.

**Not:** `-x test` parametresi testleri çalıştırmadan build yapar (daha hızlı).

---

## 🐳 Adım 2: Docker Image'larını Build Etme

Docker Compose, Dockerfile'ları otomatik olarak build edecektir. Ancak manuel olarak build etmek isterseniz:

```bash
# Her servis için ayrı ayrı
docker build -t account-service:latest ./AccountService
docker build -t ledger-service:latest ./LedgerService
docker build -t fraud-service:latest ./FraudService
docker build -t notification-service:latest ./NotificationService
docker build -t config-server:latest ./ConfigServerLocal
docker build -t eureka-server:latest ./DashboardEurekaServer
docker build -t api-gateway:latest ./ApiGatewayService
```

---

## 🚀 Adım 3: Docker Compose ile Tüm Servisleri Başlatma

### Tüm servisleri başlat:

```bash
docker-compose up -d
```

### Sadece altyapı servislerini başlat (PostgreSQL, Redis, Kafka, Zipkin, Keycloak):

```bash
docker-compose up -d postgres redis redpanda zipkin keycloak
```

### Servisleri sırayla başlat (önerilen):

```bash
# 1. Altyapı servisleri
docker-compose up -d postgres redis redpanda zipkin keycloak

# 2. Veritabanı init (veritabanlarını oluştur)
docker-compose up -d postgres-init

# 3. Config Server ve Eureka
docker-compose up -d config-server eureka-server

# 4. Mikroservisler
docker-compose up -d account-service ledger-service fraud-service notification-service

# 5. API Gateway
docker-compose up -d api-gateway
```

---

## 📊 Servis Durumunu Kontrol Etme

### Tüm container'ları listele:

```bash
docker-compose ps
```

### Belirli bir servisin loglarını görüntüle:

```bash
docker-compose logs -f account-service
docker-compose logs -f ledger-service
# ... diğer servisler
```

### Tüm servislerin loglarını görüntüle:

```bash
docker-compose logs -f
```

---

## 🛑 Servisleri Durdurma

### Tüm servisleri durdur:

```bash
docker-compose down
```

### Servisleri durdur ve volume'ları sil:

```bash
docker-compose down -v
```

**⚠️ Dikkat:** Bu komut tüm veritabanı verilerini siler!

---

## 🔍 Servis Erişim Portları

| Servis | Port | URL |
|--------|------|-----|
| API Gateway | 80 | http://localhost |
| Eureka Dashboard | 8761 | http://localhost:8761 |
| Config Server | 8888 | http://localhost:8888 |
| Account Service | 9591 | http://localhost:9591 |
| Ledger Service | 9592 | http://localhost:9592 |
| Fraud Service | 9593 | http://localhost:9593 |
| Notification Service | 9594 | http://localhost:9594 |
| Keycloak | 8180 | http://localhost:8180 |
| Zipkin | 9411 | http://localhost:9411 |
| PostgreSQL | 9999 | localhost:9999 |
| Redis | 6379 | localhost:6379 |
| Kafka (Redpanda) | 9092 | localhost:9092 |

---

## 🔧 Environment Variables

Docker Compose, config dosyalarındaki `localhost` değerlerini otomatik olarak Docker servis isimleriyle override eder:

- `localhost:9999` → `postgres:5432` (PostgreSQL)
- `localhost:6379` → `redis:6379` (Redis)
- `localhost:9092` → `redpanda:9092` (Kafka)
- `localhost:9411` → `zipkin:9411` (Zipkin)
- `localhost:8180` → `keycloak:8080` (Keycloak)
- `localhost:8888` → `config-server:8888` (Config Server)
- `localhost:8761` → `eureka-server:8761` (Eureka)

---

## 🐛 Sorun Giderme

### 1. Container başlamıyor

```bash
# Container loglarını kontrol et
docker-compose logs <service-name>

# Container'ı yeniden başlat
docker-compose restart <service-name>
```

### 2. Veritabanı bağlantı hatası

```bash
# PostgreSQL'in çalıştığını kontrol et
docker-compose ps postgres

# Veritabanlarının oluşturulduğunu kontrol et
docker-compose exec postgres psql -U postgres -l
```

### 3. Kafka bağlantı hatası

```bash
# Redpanda'nın çalıştığını kontrol et
docker-compose ps redpanda

# Kafka topic'lerini kontrol et
docker-compose exec redpanda rpk topic list
```

### 4. Eureka'da servis görünmüyor

```bash
# Eureka dashboard'u kontrol et
# http://localhost:8761

# Servis loglarını kontrol et
docker-compose logs -f eureka-server
docker-compose logs -f <service-name>
```

### 5. JAR dosyası bulunamıyor

```bash
# JAR dosyalarının oluşturulduğunu kontrol et
ls AccountService/build/libs/
ls LedgerService/build/libs/
# ... diğer servisler

# Eğer yoksa, build et
.\gradlew.bat clean build -x test
```

---

## 📝 Notlar

1. **İlk Başlatma:** İlk başlatmada servislerin birbirini bulması biraz zaman alabilir. Eureka'da tüm servislerin kayıt olmasını bekleyin.

2. **Health Checks:** Bazı servisler health check kullanır. Servislerin tamamen hazır olması için birkaç saniye bekleyin.

3. **Network:** Tüm servisler `microservices-net` network'ünde birbirine erişebilir.

4. **Config Server:** Config Server, diğer servislerin config dosyalarını sağlar. Config Server başlamadan diğer servisler başlamamalı.

5. **Eureka:** Eureka, servis discovery için kullanılır. Eureka başlamadan diğer servisler kayıt olamaz.

---

## ✅ Başarılı Deployment Kontrol Listesi

- [ ] Tüm container'lar çalışıyor (`docker-compose ps`)
- [ ] PostgreSQL veritabanları oluşturuldu
- [ ] Config Server çalışıyor (http://localhost:8888)
- [ ] Eureka Dashboard açılıyor (http://localhost:8761)
- [ ] Tüm servisler Eureka'da görünüyor
- [ ] API Gateway çalışıyor (http://localhost)
- [ ] Keycloak çalışıyor (http://localhost:8180)
- [ ] Zipkin çalışıyor (http://localhost:9411)
- [ ] Redis çalışıyor
- [ ] Kafka (Redpanda) çalışıyor

---

## 🎯 Hızlı Başlangıç (Özet)

```bash
# 1. Build
.\gradlew.bat clean build -x test

# 2. Başlat
docker-compose up -d

# 3. Kontrol et
docker-compose ps
docker-compose logs -f
```

---

**Sorun yaşarsanız logları kontrol edin ve yukarıdaki sorun giderme adımlarını takip edin.**
