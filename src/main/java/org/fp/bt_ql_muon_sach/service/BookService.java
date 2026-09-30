package org.fp.bt_ql_muon_sach.service;

import org.fp.bt_ql_muon_sach.dto.BookForm;
import org.fp.bt_ql_muon_sach.dto.BookView;
import org.fp.bt_ql_muon_sach.entity.Book;
import org.fp.bt_ql_muon_sach.entity.BookStatus;
import org.fp.bt_ql_muon_sach.entity.Category;
import org.fp.bt_ql_muon_sach.exception.BusinessException;
import org.fp.bt_ql_muon_sach.exception.NotFoundException;
import org.fp.bt_ql_muon_sach.repository.BookRepository;
import org.fp.bt_ql_muon_sach.repository.CategoryRepository;
import org.fp.bt_ql_muon_sach.util.IsbnNormalizer;
import org.fp.bt_ql_muon_sach.util.PageRequests;
import org.fp.bt_ql_muon_sach.util.ViewMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<BookView> search(String keyword, Long categoryId, int page, int size, String sort) {
        return bookRepository.search(
                keyword == null ? "" : keyword.trim(),
                categoryId,
                BookStatus.DELETED,
                PageRequests.of(page, size, sort, "id", "createdAt", "title")
        ).map(ViewMapper::toView);
    }

    @Transactional(readOnly = true)
    public List<BookView> borrowableBooks() {
        return bookRepository.findBorrowable().stream().map(ViewMapper::toView).toList();
    }

    @Transactional(readOnly = true)
    public BookForm getForm(Long id) {
        return ViewMapper.toForm(findVisible(id));
    }

    @Transactional
    public void save(BookForm form) {
        String isbn = IsbnNormalizer.normalize(form.getIsbn());
        if (!IsbnNormalizer.isValid(isbn)) {
            throw new BusinessException("ISBN phải gồm 10 hoặc 13 ký tự số.");
        }
        boolean duplicated = form.getId() == null
                ? bookRepository.existsByIsbn(isbn)
                : bookRepository.existsByIsbnAndIdNot(isbn, form.getId());
        if (duplicated) {
            throw new BusinessException("ISBN đã tồn tại.");
        }
        if (form.getStatus() == BookStatus.DELETED) {
            throw new BusinessException("Dùng chức năng xóa để ngừng sử dụng sách.");
        }
        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thể loại."));
        Book book = form.getId() == null ? new Book() : findVisible(form.getId());
        int newTotal = form.getTotalQuantity();
        if (form.getId() == null) {
            book.setAvailableQuantity(newTotal);
        } else {
            int borrowed = book.getTotalQuantity() - book.getAvailableQuantity();
            if (newTotal < borrowed) {
                throw new BusinessException("Tổng số lượng không được nhỏ hơn số sách đang được mượn (" + borrowed + ").");
            }
            book.setAvailableQuantity(newTotal - borrowed);
        }
        book.setIsbn(isbn);
        book.setTitle(form.getTitle().trim());
        book.setAuthor(form.getAuthor().trim());
        book.setCategory(category);
        book.setTotalQuantity(newTotal);
        book.setStatus(form.getStatus());
        bookRepository.save(book);
    }

    @Transactional
    public void softDelete(Long id) {
        Book book = findVisible(id);
        if (bookRepository.hasOutstandingLoan(id)) {
            throw new BusinessException("Không thể xóa sách đang có lượt mượn chưa trả.");
        }
        book.setStatus(BookStatus.DELETED);
    }

    private Book findVisible(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sách."));
        if (book.getStatus() == BookStatus.DELETED) {
            throw new NotFoundException("Không tìm thấy sách.");
        }
        return book;
    }
}
