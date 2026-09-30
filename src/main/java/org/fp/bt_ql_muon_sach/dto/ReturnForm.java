package org.fp.bt_ql_muon_sach.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ReturnForm {

    @NotNull(message = "Chọn dòng sách cần trả")
    private Long detailId;

    @NotNull(message = "Nhập số lượng trả")
    @Min(value = 1, message = "Số lượng trả phải lớn hơn 0")
    private Integer quantity;

    @NotNull(message = "Chọn ngày trả")
    private LocalDate returnDate = LocalDate.now();

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}
