package org.fp.bt_ql_muon_sach.util;

import org.fp.bt_ql_muon_sach.dto.BookForm;
import org.fp.bt_ql_muon_sach.dto.BookView;
import org.fp.bt_ql_muon_sach.dto.BorrowingLineView;
import org.fp.bt_ql_muon_sach.dto.BorrowingView;
import org.fp.bt_ql_muon_sach.dto.CategoryForm;
import org.fp.bt_ql_muon_sach.dto.CategoryView;
import org.fp.bt_ql_muon_sach.dto.MemberForm;
import org.fp.bt_ql_muon_sach.dto.MemberView;
import org.fp.bt_ql_muon_sach.entity.Book;
import org.fp.bt_ql_muon_sach.entity.Borrowing;
import org.fp.bt_ql_muon_sach.entity.BorrowingDetail;
import org.fp.bt_ql_muon_sach.entity.BorrowingStatus;
import org.fp.bt_ql_muon_sach.entity.Category;
import org.fp.bt_ql_muon_sach.entity.Member;

import java.time.LocalDate;
import java.util.List;

public final class ViewMapper {

    private ViewMapper() {
    }

    public static CategoryView toView(Category category) {
        return new CategoryView(category.getId(), category.getName(), category.getDescription(), category.isActive());
    }

    public static CategoryForm toForm(Category category) {
        CategoryForm form = new CategoryForm();
        form.setId(category.getId());
        form.setName(category.getName());
        form.setDescription(category.getDescription());
        form.setActive(category.isActive());
        return form;
    }

    public static BookView toView(Book book) {
        return new BookView(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory().getId(),
                book.getCategory().getName(),
                book.getTotalQuantity(),
                book.getAvailableQuantity(),
                book.getStatus().name()
        );
    }

    public static BookForm toForm(Book book) {
        BookForm form = new BookForm();
        form.setId(book.getId());
        form.setIsbn(book.getIsbn());
        form.setTitle(book.getTitle());
        form.setAuthor(book.getAuthor());
        form.setCategoryId(book.getCategory().getId());
        form.setTotalQuantity(book.getTotalQuantity());
        form.setStatus(book.getStatus());
        return form;
    }

    public static MemberView toView(Member member) {
        return new MemberView(
                member.getId(),
                member.getFullName(),
                member.getEmail(),
                member.getPhone(),
                member.getStatus().name()
        );
    }

    public static MemberForm toForm(Member member) {
        MemberForm form = new MemberForm();
        form.setId(member.getId());
        form.setFullName(member.getFullName());
        form.setEmail(member.getEmail());
        form.setPhone(member.getPhone());
        form.setStatus(member.getStatus());
        return form;
    }

    public static BorrowingView toView(Borrowing borrowing) {
        List<BorrowingLineView> lines = borrowing.getDetails().stream().map(ViewMapper::toLine).toList();
        boolean overdue = borrowing.getStatus() != BorrowingStatus.RETURNED
                && borrowing.getDueDate().isBefore(LocalDate.now());
        return new BorrowingView(
                borrowing.getId(),
                borrowing.getMember().getId(),
                borrowing.getMember().getFullName(),
                borrowing.getBorrowDate(),
                borrowing.getDueDate(),
                borrowing.getReturnedDate(),
                borrowing.getStatus().name(),
                borrowing.getTotalFine(),
                overdue,
                lines
        );
    }

    private static BorrowingLineView toLine(BorrowingDetail detail) {
        return new BorrowingLineView(
                detail.getId(),
                detail.getBook().getTitle(),
                detail.getBook().getIsbn(),
                detail.getQuantity(),
                detail.getReturnedQuantity(),
                detail.remaining(),
                detail.getFineAmount()
        );
    }
}
