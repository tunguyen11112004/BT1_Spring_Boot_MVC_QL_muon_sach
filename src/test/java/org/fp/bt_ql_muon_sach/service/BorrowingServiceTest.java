package org.fp.bt_ql_muon_sach.service;

import org.fp.bt_ql_muon_sach.dto.BorrowingForm;
import org.fp.bt_ql_muon_sach.dto.BorrowingLineForm;
import org.fp.bt_ql_muon_sach.entity.Book;
import org.fp.bt_ql_muon_sach.entity.BookStatus;
import org.fp.bt_ql_muon_sach.entity.Borrowing;
import org.fp.bt_ql_muon_sach.entity.BorrowingDetail;
import org.fp.bt_ql_muon_sach.entity.BorrowingStatus;
import org.fp.bt_ql_muon_sach.entity.Member;
import org.fp.bt_ql_muon_sach.entity.MemberStatus;
import org.fp.bt_ql_muon_sach.exception.BusinessException;
import org.fp.bt_ql_muon_sach.repository.BookRepository;
import org.fp.bt_ql_muon_sach.repository.BorrowingRepository;
import org.fp.bt_ql_muon_sach.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {

    @Mock
    private BorrowingRepository borrowingRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private BorrowingService borrowingService;

    @Test
    void createDecreasesAvailableQuantity() {
        Member member = activeMember();
        Book book = activeBook(3);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(book));
        when(borrowingRepository.save(any())).thenAnswer(invocation -> {
            Borrowing borrowing = invocation.getArgument(0);
            borrowing.setId(50L);
            return borrowing;
        });

        Long id = borrowingService.create(form(1L, 10L, 2, LocalDate.of(2026, 9, 1)));

        assertEquals(50L, id);
        assertEquals(1, book.getAvailableQuantity());
        ArgumentCaptor<Borrowing> captor = ArgumentCaptor.forClass(Borrowing.class);
        verify(borrowingRepository).save(captor.capture());
        assertEquals(LocalDate.of(2026, 9, 15), captor.getValue().getDueDate());
        assertEquals(2, captor.getValue().getDetails().get(0).getQuantity());
    }

    @Test
    void blockedMemberCannotBorrow() {
        Member member = activeMember();
        member.setStatus(MemberStatus.BLOCKED);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> borrowingService.create(form(1L, 10L, 1, LocalDate.of(2026, 9, 1))));

        assertEquals("Thành viên đang bị khóa, không được tạo phiếu mượn.", exception.getMessage());
        verify(borrowingRepository, never()).save(any());
    }

    @Test
    void insufficientStockIsRejected() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember()));
        when(bookRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(activeBook(1)));

        assertThrows(BusinessException.class,
                () -> borrowingService.create(form(1L, 10L, 2, LocalDate.of(2026, 9, 1))));
        verify(borrowingRepository, never()).save(any());
    }

    @Test
    void inactiveBookCannotBeBorrowed() {
        Book book = activeBook(5);
        book.setStatus(BookStatus.INACTIVE);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember()));
        when(bookRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(book));

        assertThrows(BusinessException.class,
                () -> borrowingService.create(form(1L, 10L, 1, LocalDate.of(2026, 9, 1))));
    }

    @Test
    void lateReturnAddsFineAndRestoresStock() {
        Book book = activeBook(2);
        book.setId(10L);
        Borrowing borrowing = openBorrowing(book, LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 15));
        when(borrowingRepository.findWithDetailsById(7L)).thenReturn(Optional.of(borrowing));
        when(bookRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(book));

        borrowingService.returnCopies(7L, 100L, 1, LocalDate.of(2026, 8, 20));

        assertEquals(3, book.getAvailableQuantity());
        assertEquals(new BigDecimal("25000"), borrowing.getTotalFine());
        assertEquals(BorrowingStatus.RETURNED, borrowing.getStatus());
        assertEquals(LocalDate.of(2026, 8, 20), borrowing.getReturnedDate());
    }

    @Test
    void partialReturnKeepsBorrowingOpen() {
        Book book = activeBook(1);
        book.setId(10L);
        Borrowing borrowing = openBorrowing(book, LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 24));
        borrowing.getDetails().get(0).setQuantity(2);
        when(borrowingRepository.findWithDetailsById(7L)).thenReturn(Optional.of(borrowing));
        when(bookRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(book));

        borrowingService.returnCopies(7L, 100L, 1, LocalDate.of(2026, 9, 20));

        assertEquals(BorrowingStatus.BORROWING, borrowing.getStatus());
        assertEquals(1, borrowing.getDetails().get(0).remaining());
        assertEquals(BigDecimal.ZERO, borrowing.getTotalFine());
    }

    private BorrowingForm form(Long memberId, Long bookId, int quantity, LocalDate borrowDate) {
        BorrowingLineForm line = new BorrowingLineForm();
        line.setBookId(bookId);
        line.setQuantity(quantity);
        BorrowingForm form = new BorrowingForm();
        form.setMemberId(memberId);
        form.setBorrowDate(borrowDate);
        form.setLines(List.of(line));
        return form;
    }

    private Member activeMember() {
        Member member = new Member();
        member.setId(1L);
        member.setFullName("An");
        member.setStatus(MemberStatus.ACTIVE);
        return member;
    }

    private Book activeBook(int available) {
        Book book = new Book();
        book.setId(10L);
        book.setTitle("Số đỏ");
        book.setStatus(BookStatus.ACTIVE);
        book.setTotalQuantity(5);
        book.setAvailableQuantity(available);
        return book;
    }

    private Borrowing openBorrowing(Book book, LocalDate borrowDate, LocalDate dueDate) {
        Member member = activeMember();
        Borrowing borrowing = new Borrowing();
        borrowing.setId(7L);
        borrowing.setMember(member);
        borrowing.setBorrowDate(borrowDate);
        borrowing.setDueDate(dueDate);
        borrowing.setStatus(BorrowingStatus.BORROWING);
        borrowing.setTotalFine(BigDecimal.ZERO);
        BorrowingDetail detail = new BorrowingDetail();
        detail.setId(100L);
        detail.setBook(book);
        detail.setQuantity(1);
        detail.setReturnedQuantity(0);
        detail.setFineAmount(BigDecimal.ZERO);
        borrowing.addDetail(detail);
        return borrowing;
    }
}
