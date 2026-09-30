package org.fp.bt_ql_muon_sach.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowingForm {

    @NotNull(message = "Chọn thành viên")
    private Long memberId;

    @NotNull(message = "Chọn ngày mượn")
    private LocalDate borrowDate = LocalDate.now();

    @Valid
    private List<BorrowingLineForm> lines = new ArrayList<>();

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public List<BorrowingLineForm> getLines() {
        return lines;
    }

    public void setLines(List<BorrowingLineForm> lines) {
        this.lines = lines;
    }
}
