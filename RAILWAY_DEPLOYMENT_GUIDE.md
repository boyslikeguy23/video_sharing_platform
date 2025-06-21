# Hướng dẫn Deploy Backend lên Railway

## 1. Cấu hình đã hoàn thành

✅ **Spring Boot Actuator**: Đã thêm dependency cho health check
✅ **Railway.toml**: Đã cấu hình 2 dịch vụ (web + PostgreSQL)
✅ **CORS Configuration**: Đã cấu hình cho phép tất cả domain
✅ **Database Configuration**: Sử dụng biến môi trường

## 2. Các bước deploy lên Railway

### Bước 1: Truy cập Railway
- Vào [railway.app](https://railway.app)
- Đăng nhập bằng GitHub

### Bước 2: Tạo dự án mới
- Nhấn **"New Project"**
- Chọn **"Deploy from GitHub repo"**
- Chọn repository: `video_sharing_platform`

### Bước 3: Chờ deploy hoàn tất
Railway sẽ tự động:
- Build ứng dụng Spring Boot
- Tạo PostgreSQL database
- Cấu hình biến môi trường
- Deploy ứng dụng

## 3. Cấu hình Domain cho Frontend

### Sau khi deploy thành công:

1. **Lấy URL Backend**:
   - Vào tab **"Deployments"** trong Railway
   - Copy URL có dạng: `https://your-app-name.railway.app`

2. **Cấu hình Frontend**:
   ```javascript
   // Thay thế BASE_URL trong frontend
   const BASE_URL = "https://your-app-name.railway.app";
   ```

3. **CORS đã được cấu hình**:
   - Backend đã cho phép tất cả domain
   - Frontend có thể gọi API từ bất kỳ domain nào

## 4. Biến môi trường tự động

Railway sẽ tự động tạo các biến môi trường:
- `DB_HOST`: Địa chỉ PostgreSQL
- `DB_PORT`: 5432
- `DB_NAME`: Tên database
- `DB_USERNAME`: Username
- `DB_PASSWORD`: Password

## 5. Kiểm tra deploy

### Health Check:
```
GET https://your-app-name.railway.app/actuator/health
```

### Test API:
```
GET https://your-app-name.railway.app/api
```

## 6. Troubleshooting

### Nếu gặp lỗi CORS:
- Kiểm tra console browser
- Đảm bảo frontend đang gọi đúng URL backend
- CORS đã được cấu hình cho phép tất cả domain

### Nếu gặp lỗi database:
- Kiểm tra logs trong Railway
- Đảm bảo biến môi trường được set đúng
- Database sẽ được tạo tự động

## 7. Custom Domain (Tùy chọn)

Nếu muốn sử dụng domain tùy chỉnh:
1. Vào tab **"Settings"** trong Railway
2. Chọn **"Custom Domains"**
3. Thêm domain của bạn
4. Cấu hình DNS records

## 8. Monitoring

- **Logs**: Xem trong tab "Deployments"
- **Metrics**: Railway cung cấp monitoring cơ bản
- **Health Check**: Tự động restart nếu ứng dụng lỗi

---

**Lưu ý**: Sau khi deploy, hãy cập nhật URL backend trong frontend code và test lại các API calls. 