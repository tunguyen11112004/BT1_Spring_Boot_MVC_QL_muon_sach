package org.fp.bt_ql_muon_sach.service;

import org.fp.bt_ql_muon_sach.dto.BorrowingView;
import org.fp.bt_ql_muon_sach.dto.MemberBorrowCountView;
import org.fp.bt_ql_muon_sach.dto.TopBookView;
import org.fp.bt_ql_muon_sach.repository.BorrowingRepository;
import org.fp.bt_ql_muon_sach.util.ViewMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final BorrowingRepository borrowingRepository;

    public ReportService(BorrowingRepository borrowingRepository) {
        this.borrowingRepository = borrowingRepository;
    }

    @Transactional(readOnly = true)
    public List<BorrowingView> overdue() {
        return borrowingRepository.findOverdue(LocalDate.now()).stream().map(ViewMapper::toView).toList();
    }

    @Transactional(readOnly = true)
    public List<TopBookView> topBooks() {
        return borrowingRepository.findTopBooks(PageRequest.of(0, 5));
    }

    @Transactional(readOnly = true)
    public List<MemberBorrowCountView> memberCounts() {
        return borrowingRepository.countByMember();
    }
}
