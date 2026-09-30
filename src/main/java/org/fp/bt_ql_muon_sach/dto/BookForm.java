package org.fp.bt_ql_muon_sach.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.fp.bt_ql_muon_sach.entity.BookStatus;

public class BookForm {

    private Long id;

    @NotBlank(message = "ISBN không được để trống")
    @Size(max = 20, message = "ISBN tối đa 20 ký tự")
    private String isbn;

    @NotBlank(message = "Tên sách không được để trống")
    @Size(max = 255, message = "Tên sách tối đa 255 ký tự")
    private String title;

    @NotBlank(message = "Tác giả không được để trống")
    @Size(max = 255, message = "Tên tác giả tối đa 255 ký tự")
    private String author;

    @NotNull(message = "Chọn thể loại")
    private Long categoryId;

    @NotNull(message = "Nhập tổng số lượng")
    @Min(value = 0, message = "Tổng số lượng không được âm")
    private Integer totalQuantity;

    @NotNull(message = "Chọn trạng thái")
    private BookStatus status = BookStatus.ACTIVE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }
}
