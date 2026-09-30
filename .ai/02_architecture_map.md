# Architecture map

Hiện tại chỉ có một process Spring Boot.

```
Browser (chưa có view)
        |
        v
Spring Boot WAR
  BtQlMuonSachApplication
  ServletInitializer
        |
        v
application.properties
```

- Chưa có controller, service, repository, template Thymeleaf.
- `pom.xml` khai báo MySQL connector. Chưa có URL, user, schema.
- Chưa có tích hợp ngoài.

Khi thêm module, cập nhật file này và thêm summary trong `.ai/03_module_summaries/`.
