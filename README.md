# Product Platform - Microservices

Platform microservices berbasis Spring Boot 4 dan Spring Cloud untuk manajemen produk, autentikasi JWT, gateway routing, dan impor otomatis DummyJSON.

---

## Arsitektur & Port

```
Client / Postman
   │
   ▼
api-gateway :8089 ── cek JWT, route lb://
   │                  │                        │
   ▼                  ▼                        ▼
auth-service :8081   product-service :8082 ◄─Feign─ scheduler-service :8083 ──Feign──► DummyJSON
   │                  │   (cek duplikat + aturan bisnis)
   └────────┬─────────┘
            ▼
     PostgreSQL :5432 & Redis :6379 (Docker)

Semua service terdaftar di eureka-server :8761
```

| Service | Port | Context-Path | Keterangan |
| :--- | :---: | :---: | :--- |
| `eureka-server` | 8761 | `/` | Service registry |
| `api-gateway` | 8089 | `/` | Entry point, verifikasi JWT, blokir akses internal |
| `auth-service` | 8081 | `/auth` | Register (BCrypt), Login, JWT issuer |
| `product-service` | 8082 | `/product` | CRUD produk, validasi rating, Redis cache, AOP logging |
| `scheduler-service` | 8083 | `/scheduler` | Impor DummyJSON (startup & cron per 2 menit) |
| PostgreSQL | 5432 | - | Database (`product_db`) |
| Redis | 6379 | - | Cache detail produk (TTL 10 menit) |

---

## Perintah Docker

Jalankan database dan Redis sebelum menyalakan service:

```bash
# PostgreSQL
docker run -d --name postgres-batch19 -p 5432:5432 \
  -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=root -e POSTGRES_DB=product_db \
  postgres:16

# Redis
docker compose -f docker-compose-redis.yaml up -d
```

---

## Urutan Start Service

Jalankan service sesuai urutan berikut:

1. **Docker Container** (PostgreSQL & Redis)
2. **`eureka-server`** (`:8761`) - pastikan dashboard aktif sebelum service lain start
3. **`auth-service`** (`:8081`)
4. **`product-service`** (`:8082`)
5. **`scheduler-service`** (`:8083`) - dijalankan setelah `product-service` aktif agar impor awal berhasil
6. **`api-gateway`** (`:8089`)

Jalankan tiap service dengan:
```bash
./mvnw spring-boot:run
```

---

## Kontrak Endpoint (via Gateway :8089)

Format response seragam:
* Sukses: `{"status": "success", "message": "...", "data": ...}`
* Gagal: `{"status": "failed", "message": "...", "data": null}`

| Method | Endpoint | Auth | Deskripsi |
| :---: | :--- | :---: | :--- |
| `POST` | `/auth/register` | Publik | Register user baru |
| `POST` | `/auth/login` | Publik | Login & dapatkan token JWT |
| `POST` | `/product` | JWT | Buat produk (201, audit log header `X-User-Id`) |
| `GET` | `/product/{id}` | JWT | Detail produk (Redis cache hit di panggilan ke-2) |
| `GET` | `/product` | JWT | Ambil semua produk |
| `GET` | `/product?huruf={kw}` | JWT | Filter nama produk (case-insensitive) |
| `PUT` | `/product/{id}` | JWT | Update produk & evict cache |
| `DELETE` | `/product/{id}` | JWT | Hapus produk & evict cache |
| `POST` | `/scheduler/import` | JWT | Picu impor manual DummyJSON |
| `POST` | `/product/internal/import` | Internal | Ditolak Gateway (`403 Forbidden`) |

---

## Catatan Keputusan

1. **Umur Token JWT (W1.3):**
   Ditetapkan **1 jam (`3600000 ms`)**. Mengikuti standar keamanan aplikasi web/API tanpa refresh token untuk meminimalkan celah penyalahgunaan token jika terjadi kebocoran (window of vulnerability yang sempit), namun tetap memberikan waktu yang cukup bagi pengguna untuk menyelesaikan satu sesi pekerjaan tanpa terputus.

2. **Context-Path & Proteksi Endpoint Internal di Gateway (Kontrak Gateway):**
   Tiap service memiliki context-path mandiri (`/auth`, `/product`, `/scheduler`) yang diteruskan apa adanya oleh Gateway via `lb://`. Endpoint `/product/internal/**` diblokir langsung di Gateway menggunakan `.denyAll()` (HTTP 403) agar endpoint penerima data batch impor hanya dapat diakses antar-service lewat Eureka/Feign.

3. **Deteksi Duplikat DummyJSON (W5.5):**
   Menggunakan pengecekan `productRepository.existsByNameIgnoreCase(name)` di service layer, didukung unique constraint pada kolom `name` di database. Jika nama produk sudah ada, data dilewati (skip) tanpa error.

4. **Kegagalan Sinkronisasi DummyJSON Tidak Membatalkan Transaksi (B4.2):**
   Pemanggilan DummyJSON (`POST`, `PUT`, `DELETE`) dibungkus blok try-catch terpisah. Karena DummyJSON hanya simulasi eksternal, error pada DummyJSON tidak membatalkan (rollback) penyimpanan data di database lokal agar ketersediaan sistem tetap terjaga.

5. **Pagination Feign DummyJSON Menggunakan limit dan skip:**
   Pemanggilan DummyJSON memanfaatkan query param `?limit=5&skip={offset}` langsung ke API eksternal, sehingga scheduler hanya mengunduh 5 produk per putaran sesuai batch yang diproses, bukan mengunduh seluruh data ke memori.
