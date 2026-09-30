package org.fp.bt_ql_muon_sach.service;

import org.fp.bt_ql_muon_sach.dto.BorrowingForm;
import org.fp.bt_ql_muon_sach.dto.BorrowingLineForm;
import org.fp.bt_ql_muon_sach.dto.BorrowingView;
import org.fp.bt_ql_muon_sach.entity.Book;
import org.fp.bt_ql_muon_sach.entity.BookStatus;
import org.fp.bt_ql_muon_sach.entity.Borrowing;
import org.fp.bt_ql_muon_sach.entity.BorrowingDetail;
import org.fp.bt_ql_muon_sach.entity.BorrowingStatus;
import org.fp.bt_ql_muon_sach.entity.Member;
import org.fp.bt_ql_muon_sach.entity.MemberStatus;
import org.fp.bt_ql_muon_sach.exception.BusinessException;
import org.fp.bt_ql_muon_sach.exception.NotFoundException;
import org.fp.bt_ql_muon_sach.repository.BookRepository;
import org.fp.bt_ql_muon_sach.repository.BorrowingRepository;
import org.fp.bt_ql_muon_sach.repository.MemberRepository;
import org.fp.bt_ql_muon_sach.util.FineCalculator;
import org.fp.bt_ql_muon_sach.util.PageRequests;
import org.fp.bt_ql_muon_sach.util.ViewMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class BorrowingService {

    public static final int LOAN_DAYS = 14;

    private final BorrowingRepository borrowingRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    public BorrowingService(
            BorrowingRepository borrowingRepository,
            MemberRepository memberRepository,
            BookRepository bookRepository
    ) {
        this.borrowingRepository = borrowingRepository;
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public Page<BorrowingView> list(int page, int size) {
        return borrowingRepository.findPage(PageRequests.of(page, size, "id", "id", "createdAt"))
                .map(ViewMapper::toView);
    }

    @Transactional(readOnly = true)
    public BorrowingView get(Long id) {
        return ViewMapper.toView(findWithDetails(id));
    }

    @Transactional
    public Long create(BorrowingForm form) {
        Member member = memberRepository.findById(form.getMemberId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thành viên."));
        if (member.getStatus() == MemberStatus.BLOCKED) {
            throw new BusinessException("Thành viên đang bị khóa, không được tạo phiếu mượn.");
        }
        Map<Long, Integer> lines = mergeLines(form);
        LocalDate borrowDate = form.getBorrowDate();
        Borrowing borrowing = new Borrowing();
        borrowing.setMember(member);
        borrowing.setBorrowDate(borrowDate);
        borrowing.setDueDate(borrowDate.plusDays(LOAN_DAYS));
        borrowing.setStatus(BorrowingStatus.BORROWING);
        borrowing.setTotalFine(BigDecimal.ZERO);

        for (Map.Entry<Long, Integer> entry : lines.entrySet()) {
            Book book = bookRepository.findByIdForUpdate(entry.getKey())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy sách."));
            if (book.getStatus() != BookStatus.ACTIVE) {
                throw new BusinessException("Sách \"" + book.getTitle() + "\" không được phép mượn.");
            }
            if (book.getAvailableQuantity() < entry.getValue()) {
                throw new BusinessException("Sách \"" + book.getTitle() + "\" không đủ số lượng. Còn "
                        + book.getAvailableQuantity() + ".");
            }
            book.setAvailableQuantity(book.getAvailableQuantity() - entry.getValue());
            BorrowingDetail detail = new BorrowingDetail();
            detail.setBook(book);
            detail.setQuantity(entry.getValue());
            detail.setReturnedQuantity(0);
            detail.setFineAmount(BigDecimal.ZERO);
            borrowing.addDetail(detail);
        }
        return borrowingRepository.save(borrowing).getId();
    }

    @Transactional
    public void returnCopies(Long borrowingId, Long detailId, int quantity, LocalDate returnDate) {
        Borrowing borrowing = findWithDetails(borrowingId);
        BorrowingDetail detail = borrowing.getDetails().stream()
                .filter(item -> item.getId().equals(detailId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Không tìm thấy dòng sách trong phiếu."));
        applyReturn(borrowing, detail, quantity, returnDate);
    }

    @Transactional
    public void returnAll(Long borrowingId, LocalDate returnDate) {
        Borrowing borrowing = findWithDetails(borrowingId);
        if (borrowing.getStatus() == BorrowingStatus.RETURNED) {
            throw new BusinessException("Phiếu đã trả hết.");
        }
        for (BorrowingDetail detail : borrowing.getDetails()) {
            int remaining = detail.remaining();
            if (remaining > 0) {
                applyReturn(borrowing, detail, remaining, returnDate);
            }
        }
    }

    private void applyReturn(Borrowing borrowing, BorrowingDetail detail, int quantity, LocalDate returnDate) {
        if (borrowing.getStatus() == BorrowingStatus.RETURNED) {
            throw new BusinessException("Phiếu đã trả hết.");
        }
        if (returnDate.isBefore(borrowing.getBorrowDate())) {
            throw new BusinessException("Ngày trả không được trước ngày mượn.");
        }
        int remaining = detail.remaining();
        if (quantity < 1 || quantity > remaining) {
            throw new BusinessException("Số lượng trả không hợp lệ. Còn " + remaining + " cuốn chưa trả.");
        }
        BigDecimal fine = FineCalculator.calculate(borrowing.getDueDate(), returnDate, quantity);
        detail.setReturnedQuantity(detail.getReturnedQuantity() + quantity);
        detail.setFineAmount(detail.getFineAmount().add(fine));
        Book book = bookRepository.findByIdForUpdate(detail.getBook().getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sách."));
        int restored = book.getAvailableQuantity() + quantity;
        if (restored > book.getTotalQuantity()) {
            throw new BusinessException("Số lượng tồn kho sau khi trả vượt quá tổng số lượng.");
        }
        book.setAvailableQuantity(restored);
        borrowing.setTotalFine(borrowing.getTotalFine().add(fine));
        boolean allReturned = borrowing.getDetails().stream().allMatch(item -> item.remaining() == 0);
        if (allReturned) {
            borrowing.setStatus(BorrowingStatus.RETURNED);
            borrowing.setReturnedDate(returnDate);
        }
    }

    private Map<Long, Integer> mergeLines(BorrowingForm form) {
        Map<Long, Integer> merged = new LinkedHashMap<>();
        if (form.getLines() != null) {
            for (BorrowingLineForm line : form.getLines()) {
                if (line.getBookId() == null) {
                    continue;
                }
                int quantity = line.getQuantity() == null ? 0 : line.getQuantity();
                if (quantity < 1) {
                    throw new BusinessException("Số lượng mượn phải lớn hơn 0.");
                }
                merged.merge(line.getBookId(), quantity, Integer::sum);
            }
        }
        if (merged.isEmpty()) {
            throw new BusinessException("Phiếu mượn phải có ít nhất một sách.");
        }
        return merged;
    }

    private Borrowing findWithDetails(Long id) {
        return borrowingRepository.findWithDetailsById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phiếu mượn."));
    }
}
