package org.fp.bt_ql_muon_sach.repository;

import jakarta.persistence.LockModeType;
import org.fp.bt_ql_muon_sach.entity.Book;
import org.fp.bt_ql_muon_sach.entity.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    boolean existsByIsbn(String isbn);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.id = :id")
    Optional<Book> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = "category")
    @Query("""
            select b from Book b
            where b.status <> :deleted
              and (:keyword is null or :keyword = ''
                   or lower(b.title) like lower(concat('%', :keyword, '%'))
                   or lower(b.author) like lower(concat('%', :keyword, '%'))
                   or lower(b.isbn) like lower(concat('%', :keyword, '%'))
                   or lower(b.category.name) like lower(concat('%', :keyword, '%')))
              and (:categoryId is null or b.category.id = :categoryId)
            """)
    Page<Book> search(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("deleted") BookStatus deleted,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "category")
    @Query("""
            select b from Book b
            where b.status = org.fp.bt_ql_muon_sach.entity.BookStatus.ACTIVE
            order by b.title
            """)
    java.util.List<Book> findBorrowable();

    @Query("""
            select count(d) > 0 from BorrowingDetail d
            where d.book.id = :bookId and d.returnedQuantity < d.quantity
            """)
    boolean hasOutstandingLoan(@Param("bookId") Long bookId);
}
