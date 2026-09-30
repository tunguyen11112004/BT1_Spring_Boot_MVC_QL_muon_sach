INSERT INTO category (id, name, description, active) VALUES
    (1, 'Văn học', 'Tiểu thuyết và truyện ngắn', TRUE),
    (2, 'Khoa học', 'Khoa học phổ thông', TRUE),
    (3, 'Lịch sử', 'Lịch sử Việt Nam và thế giới', TRUE),
    (4, 'Công nghệ', 'Lập trình và phần mềm', TRUE),
    (5, 'Thiếu nhi', 'Truyện cho thiếu nhi', TRUE);

INSERT INTO member (id, full_name, email, phone, status) VALUES
    (1, 'Nguyễn Văn An', 'an@thuvi.local', '0900000001', 'ACTIVE'),
    (2, 'Trần Thị Bình', 'binh@thuvi.local', '0900000002', 'ACTIVE'),
    (3, 'Lê Văn Cường', 'cuong@thuvi.local', '0900000003', 'ACTIVE'),
    (4, 'Phạm Thị Dung', 'dung@thuvi.local', '0900000004', 'ACTIVE'),
    (5, 'Hoàng Văn Em', 'em@thuvi.local', '0900000005', 'ACTIVE'),
    (6, 'Vũ Thị Phương', 'phuong@thuvi.local', '0900000006', 'ACTIVE'),
    (7, 'Đặng Văn Giang', 'giang@thuvi.local', '0900000007', 'ACTIVE'),
    (8, 'Bùi Thị Hoa', 'hoa@thuvi.local', '0900000008', 'ACTIVE'),
    (9, 'Ngô Văn Khoa', 'khoa@thuvi.local', '0900000009', 'BLOCKED'),
    (10, 'Lý Thị Lan', 'lan@thuvi.local', '0900000010', 'BLOCKED');

INSERT INTO book (id, isbn, title, author, category_id, total_quantity, available_quantity, status) VALUES
    (1, '9786040000001', 'Số đỏ', 'Vũ Trọng Phụng', 1, 5, 4, 'ACTIVE'),
    (2, '9786040000002', 'Truyện Kiều', 'Nguyễn Du', 1, 5, 5, 'ACTIVE'),
    (3, '9786040000003', 'Chí Phèo', 'Nam Cao', 1, 5, 5, 'ACTIVE'),
    (4, '9786040000004', 'Tắt đèn', 'Ngô Tất Tố', 1, 5, 4, 'ACTIVE'),
    (5, '9786040000005', 'Lược sử thời gian', 'Stephen Hawking', 2, 5, 4, 'ACTIVE'),
    (6, '9786040000006', 'Nguồn gốc các loài', 'Charles Darwin', 2, 5, 4, 'ACTIVE'),
    (7, '9786040000007', 'Vũ trụ trong vỏ hạt dẻ', 'Stephen Hawking', 2, 5, 5, 'ACTIVE'),
    (8, '9786040000008', 'Súng, vi trùng và thép', 'Jared Diamond', 2, 5, 5, 'ACTIVE'),
    (9, '9786040000009', 'Đại Việt sử ký', 'Ngô Sĩ Liên', 3, 5, 5, 'ACTIVE'),
    (10, '9786040000010', 'Việt Nam sử lược', 'Trần Trọng Kim', 3, 5, 5, 'ACTIVE'),
    (11, '9786040000011', 'Sapiens', 'Yuval Noah Harari', 3, 5, 5, 'ACTIVE'),
    (12, '9786040000012', 'Lịch sử thế giới', 'Susan Wise Bauer', 3, 5, 5, 'ACTIVE'),
    (13, '9786040000013', 'Clean Code', 'Robert C. Martin', 4, 5, 5, 'ACTIVE'),
    (14, '9786040000014', 'Design Patterns', 'Erich Gamma', 4, 5, 5, 'ACTIVE'),
    (15, '9786040000015', 'Java Core', 'Cay S. Horstmann', 4, 5, 5, 'ACTIVE'),
    (16, '9786040000016', 'Spring in Action', 'Craig Walls', 4, 5, 5, 'ACTIVE'),
    (17, '9786040000017', 'Dế Mèn phiêu lưu ký', 'Tô Hoài', 5, 5, 5, 'ACTIVE'),
    (18, '9786040000018', 'Hoàng tử bé', 'Antoine de Saint-Exupéry', 5, 5, 5, 'ACTIVE'),
    (19, '9786040000019', 'Alice ở xứ sở diệu kỳ', 'Lewis Carroll', 5, 5, 5, 'ACTIVE'),
    (20, '9786040000020', 'Cô bé Lọ Lem', 'Charles Perrault', 5, 5, 5, 'INACTIVE');

INSERT INTO borrowing (id, member_id, borrow_date, due_date, returned_date, status, total_fine) VALUES
    (1, 1, '2026-09-01', '2026-09-15', NULL, 'BORROWING', 0),
    (2, 2, '2026-08-01', '2026-08-15', '2026-08-10', 'RETURNED', 0),
    (3, 3, '2026-08-01', '2026-08-15', '2026-08-20', 'RETURNED', 25000),
    (4, 4, '2026-09-10', '2026-09-24', NULL, 'BORROWING', 0),
    (5, 5, '2026-09-20', '2026-10-04', NULL, 'BORROWING', 0);

INSERT INTO borrowing_detail (id, borrowing_id, book_id, quantity, returned_quantity, fine_amount) VALUES
    (1, 1, 1, 1, 0, 0),
    (2, 2, 2, 1, 1, 0),
    (3, 3, 3, 1, 1, 25000),
    (4, 4, 4, 2, 1, 0),
    (5, 5, 5, 1, 0, 0),
    (6, 5, 6, 1, 0, 0);
