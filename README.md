# Quản lý mượn sách

Ứng dụng Spring Boot MVC (Thymeleaf) quản lý thể loại, sách, thành viên và phiếu mượn/trả.

Stack của project: Java 21, Spring Boot 4.1.1, Spring Data JPA, Bean Validation, Flyway, MySQL, Thymeleaf.

## Chạy với Laragon

1. Bật Laragon và start MySQL.
2. Tài khoản mặc định: `root`, mật khẩu để trống. Database `bt_ql_muon_sach` được tạo khi ứng dụng khởi động.
3. Nếu MySQL có mật khẩu, sửa `spring.datasource.password` trong `src/main/resources/application.properties`.
4. Trong thư mục project:

```
mvnw spring-boot:run
```

5. Mở http://localhost:8080

Flyway chạy `V1__schema.sql` rồi `V2__seed.sql` (5 thể loại, 20 sách, 10 thành viên, 5 phiếu mượn).

## Luồng chính

- `/books` tìm kiếm, phân trang, tạo và sửa sách
- `/categories` thể loại, xóa mềm
- `/members` lọc trạng thái, khóa / mở khóa
- `/borrowings/new` tạo phiếu, hạn trả = ngày mượn + 14 ngày, trừ tồn kho
- `/borrowings/{id}/return` trả một phần hoặc trả hết. Trả muộn: 5.000đ / ngày / cuốn
- `/reports/overdue`, `/reports/top-books`, `/reports/members`

Thành viên `BLOCKED` và sách không ở trạng thái `ACTIVE` không tạo được phiếu mới. Sách còn lượt mượn chưa trả thì không xóa được.

## Test

```
mvnw test
```

Unit test nằm ở `BorrowingServiceTest` và `FineCalculatorTest`.

## Ghi chú thiết kế

- Mượn và trả nằm trong `@Transactional` để tồn kho, chi tiết phiếu và tiền phạt cùng thành công hoặc cùng hủy.
- Form và view dùng DTO. Entity chỉ nằm ở service và repository.
- Hai người mượn cuốn cuối cùng cùng lúc: dòng sách bị khóa `PESSIMISTIC_WRITE`, người sau thấy tồn kho đã giảm. `Book` có `@Version` nếu cập nhật bị ghi đè.
- Danh sách dùng lazy loading và chỉ join dữ liệu cần hiển thị trong transaction của service. Không bật `open-in-view`.
