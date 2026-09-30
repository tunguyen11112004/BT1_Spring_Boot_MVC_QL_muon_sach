package org.fp.bt_ql_muon_sach.repository;

import org.fp.bt_ql_muon_sach.entity.Member;
import org.fp.bt_ql_muon_sach.entity.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    Page<Member> findByStatus(MemberStatus status, Pageable pageable);

    @Query("""
            select m from Member m
            where m.status = org.fp.bt_ql_muon_sach.entity.MemberStatus.ACTIVE
            order by m.fullName
            """)
    List<Member> findActive();
}
