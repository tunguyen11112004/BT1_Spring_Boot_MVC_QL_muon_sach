# Repo inventory

Cập nhật theo index CodeGraph sau `codegraph init` (6 file, 20 node, 15 edge).

| Path | Vai trò |
| --- | --- |
| `pom.xml` | Spring Boot 4.1.1, Java 21, WAR, Thymeleaf, Web MVC, MySQL, DevTools |
| `src/main/java/org/fp/bt_ql_muon_sach/BtQlMuonSachApplication.java` | Entry point |
| `src/main/java/org/fp/bt_ql_muon_sach/ServletInitializer.java` | Deploy WAR |
| `src/main/resources/application.properties` | Chỉ `spring.application.name` |
| `src/test/java/org/fp/bt_ql_muon_sach/BtQlMuonSachApplicationTests.java` | Test mặc định |
| `HELP.md` | Link tài liệu Spring do Initializr sinh |
| `AGENTS.md`, `AI_RULES.md`, `.cursor/rules/` | Quy định vận hành agent |
| `.codegraph/` | Index local, không commit dữ liệu index |

Chưa có: API, screen, entity, migration, Docker, CI.
