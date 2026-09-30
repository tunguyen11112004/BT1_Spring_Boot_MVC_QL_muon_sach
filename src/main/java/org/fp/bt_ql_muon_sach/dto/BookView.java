package org.fp.bt_ql_muon_sach.dto;

public record BookView(
        Long id,
        String isbn,
        String title,
        String author,
        Long categoryId,
        String categoryName,
        int totalQuantity,
        int availableQuantity,
        String status
) {
}
