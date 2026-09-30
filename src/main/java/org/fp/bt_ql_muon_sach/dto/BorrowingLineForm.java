package org.fp.bt_ql_muon_sach.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BorrowingLineForm {

    private Long bookId;

    @NotNull(message = "Nhập số lượng")
    @Min(value = 1, message = "Số lượng mượn phải lớn hơn 0")
    private Integer quantity = 1;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
