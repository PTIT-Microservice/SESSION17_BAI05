BÀI TẬP THỰC HÀNH 5

Fallback và silent-fail khi Redis gặp sự cố.

a) Cách đăng ký ErrorHandler vào CacheManager

1. `CustomCacheErrorHandler implements CacheErrorHandler` ghi đè bốn phương thức:
   - `handleCacheGetError`: log `Redis connection error, falling back to DB ...` ở mức WARN và **không ném lại** exception.
   - `handleCachePutError`, `handleCacheEvictError`, `handleCacheClearError`: log rồi không làm gì thêm.
2. `CacheConfig implements CachingConfigurer` và khai báo `@Bean @Override public CacheErrorHandler errorHandler()`
   trả về `new CustomCacheErrorHandler()`. Việc đăng ký nằm ở chính hàm `CachingConfigurer` chứ không ở
   `RedisCacheManager`: `@EnableCaching` lấy `CacheErrorHandler` từ bean của `CachingConfigurer` và truyền cho
   `CacheInterceptor`. RedisCacheManager không có setter cho error handler trong spring-data-redis 4.1.1
   (`javap` trên `RedisCache`/`RedisCacheConfiguration` không có tham số nào nhận `CacheErrorHandler`), nên
   "cấu hình RedisCacheManager với ErrorHandler" được hiểu là đăng ký ở tầng caching của Spring.

b) Vì sao dùng `CacheErrorHandler` thay vì try-catch trong service

`AbstractCacheInvoker` bọc mọi lời gọi cache. Với `@Cacheable` thường, lỗi `get` được chuyển thành cache miss
(đọc xuống DB); với `@Cacheable(sync = true)`, chính `valueLoader` được gọi như một cache read-through. Bước `put`
sau đó có thể lỗi riêng và đi vào `handleCachePutError` -\   được log và bỏ qua. Cuối cùng response vẫn là dữ liệu từ DB
với HTTP 200, không có lỗi 500 vì lý do cache. Không cần try-catch trong `MenuService`: bọc thủ công từng lời gọi
cache là không thể, vì lời gọi cache không nằm trong thân method mà nằm trong proxy AOP.

c) Kiểm chứng thứ tự theo assignment

```
docker run -d --name redis-lab -p 6379:6379 redis:7.2-alpine
./gradlew bootRun
curl http://localhost:8625/menu/1            # 200, ghi cache
redis-cli get 'menuCache::1'                # có dữ liệu
docker stop redis-lab
curl -i http://localhost:8625/menu/1        # vẫn 200 + dữ liệu từ DB, log WARN "Redis connection error, falling back to DB"
curl -X PUT http://localhost:8625/menu -H 'Content-Type: application/json' -d '{"id":1,"dishName":"Phở","price":80000}'
docker start redis-lab
curl http://localhost:8625/menu/1           # cache được nạp lại
```

d) Đối chiếu bảng kiểm

| Tiêu chí | Cách kiểm |
|---|---|
| Không lỗi 500 | `MenuFallbackRedisDownTest` chạy với `spring.data.redis.port=6390` (không có Redis): `getMenu` trả về bình thường |
| Fallback thành công | log `>>> [DB FALLBACK] SELECT menu id=1 tu database` + `org.hibernate.SQL` in `select ...` khi Redis đã tắt |
| CacheErrorHandler đúng | `CacheConfig` implements `CachingConfigurer`, `@Bean errorHandler()` trả `CustomCacheErrorHandler` |
| Log lỗi rõ ràng | WARN `Redis connection error, falling back to DB - cache=menuCache key=1 loi=...` |
| Không ảnh hưởng thao tác khác | `MenuFallbackRedisDownTest.capNhatVanThanhCongKhiEvictThatBai`: PUT ghi được DB dù evict cache thất bại |
| Handler chặn lỗi | `CustomCacheErrorHandlerTest`: bốn phương thức không ném lỗi, trong khi `SimpleCacheErrorHandler` mặc định của Spring thì ném lại chính exception đó |
| Timeout không kéo dài request | `spring.data.redis.timeout=200ms`, `connect-timeout=200ms` |

Giới hạn: nếu Redis chết hẳn, mọi request đều xuống DB nên DB là điểm chịu tải - cần rate limiter hoặc
circuit breaker quanh Redis cho production. Khi Redis sống lại, cache tự nạp lại theo từng key; nên alert trên
log WARN để biết hệ thống đang ở chế độ suy giảm.

Chưa chạy build/test trong môi trường này (theo yêu cầu), nên kết quả trên chưa được xác nhận bằng output thật.
