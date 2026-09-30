package org.fp.bt_ql_muon_sach.repository;

import org.fp.bt_ql_muon_sach.dto.MemberBorrowCountView;
import org.fp.bt_ql_muon_sach.dto.TopBookView;
import org.fp.bt_ql_muon_sach.entity.Borrowing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    @EntityGraph(attributePaths = "member")
    @Query("select b from Borrowing b")
    Page<Borrowing> findPage(Pageable pageable);

    @EntityGraph(attributePaths = {"member", "details", "details.book"})
    @Query("select b from Borrowing b where b.id = :id")
    Optional<Borrowing> findWithDetailsById(@Param("id") Long id);

    @EntityGraph(attributePaths = "member")
    @Query("""
            select b from Borrowing b
            where b.status <> org.fp.bt_ql_muon_sach.entity.BorrowingStatus.RETURNED
              and b.dueDate < :today
            order by b.dueDate
            """)
    List<Borrowing> findOverdue(@Param("today") LocalDate today);

    @Query("""
            select new org.fp.bt_ql_muon_sach.dto.TopBookView(d.book.title, sum(d.quantity))
            from BorrowingDetail d
            group by d.book.id, d.book.title
            order by sum(d.quantity) desc
            """)
    List<TopBookView> findTopBooks(Pageable pageable);

    @Query("""
            select new org.fp.bt_ql_muon_sach.dto.MemberBorrowCountView(
                m.fullName, m.email, count(b.id))
            from Member m
            left join m.borrowings b
            group by m.id, m.fullName, m.email
            order by count(b.id) desc
            """)
    List<MemberBorrowCountView> countByMember();
}
