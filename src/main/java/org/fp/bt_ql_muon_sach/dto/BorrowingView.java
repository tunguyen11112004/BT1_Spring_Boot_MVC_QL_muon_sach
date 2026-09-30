package org.fp.bt_ql_muon_sach.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BorrowingView(
        Long id,
        Long memberId,
        String memberName,
        LocalDate borrowDate,
        LocalDate dueDate,
        LocalDate returnedDate,
        String status,
        BigDecimal totalFine,
        boolean overdue,
        List<BorrowingLineView> lines
) {
}
