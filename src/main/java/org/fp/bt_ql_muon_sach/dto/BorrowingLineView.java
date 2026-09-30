package org.fp.bt_ql_muon_sach.dto;

import java.math.BigDecimal;

public record BorrowingLineView(
        Long detailId,
        String bookTitle,
        String isbn,
        int quantity,
        int returnedQuantity,
        int remaining,
        BigDecimal fineAmount
) {
}
