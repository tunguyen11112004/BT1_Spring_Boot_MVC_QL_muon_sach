package org.fp.bt_ql_muon_sach.controller;

import jakarta.validation.Valid;
import org.fp.bt_ql_muon_sach.dto.BorrowingForm;
import org.fp.bt_ql_muon_sach.dto.BorrowingLineForm;
import org.fp.bt_ql_muon_sach.dto.ReturnForm;
import org.fp.bt_ql_muon_sach.exception.BusinessException;
import org.fp.bt_ql_muon_sach.service.BookService;
import org.fp.bt_ql_muon_sach.service.BorrowingService;
import org.fp.bt_ql_muon_sach.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/borrowings")
public class BorrowingController {

    private final BorrowingService borrowingService;
    private final MemberService memberService;
    private final BookService bookService;

    public BorrowingController(
            BorrowingService borrowingService,
            MemberService memberService,
            BookService bookService
    ) {
        this.borrowingService = borrowingService;
        this.memberService = memberService;
        this.bookService = bookService;
    }

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        model.addAttribute("borrowings", borrowingService.list(page, size));
        return "borrowings/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        if (!model.containsAttribute("borrowingForm")) {
            model.addAttribute("borrowingForm", emptyForm());
        }
        loadChoices(model);
        return "borrowings/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("borrowingForm") BorrowingForm form,
            BindingResult binding,
            Model model,
            RedirectAttributes redirect
    ) {
        if (binding.hasErrors()) {
            loadChoices(model);
            return "borrowings/form";
        }
        try {
            Long id = borrowingService.create(form);
            redirect.addFlashAttribute("successMessage", "Đã tạo phiếu mượn. Hạn trả sau 14 ngày.");
            return "redirect:/borrowings/" + id + "/return";
        } catch (BusinessException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            loadChoices(model);
            return "borrowings/form";
        }
    }

    @GetMapping("/{id}/return")
    public String returnForm(@PathVariable Long id, Model model) {
        model.addAttribute("borrowing", borrowingService.get(id));
        if (!model.containsAttribute("returnForm")) {
            model.addAttribute("returnForm", new ReturnForm());
        }
        return "borrowings/return";
    }

    @PostMapping("/{id}/return")
    public String returnBooks(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean all,
            @Valid @ModelAttribute("returnForm") ReturnForm form,
            BindingResult binding,
            Model model,
            RedirectAttributes redirect
    ) {
        try {
            if (all) {
                borrowingService.returnAll(id, form.getReturnDate() == null ? LocalDate.now() : form.getReturnDate());
            } else {
                if (binding.hasErrors()) {
                    model.addAttribute("borrowing", borrowingService.get(id));
                    return "borrowings/return";
                }
                borrowingService.returnCopies(id, form.getDetailId(), form.getQuantity(), form.getReturnDate());
            }
        } catch (BusinessException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("borrowing", borrowingService.get(id));
            return "borrowings/return";
        }
        redirect.addFlashAttribute("successMessage", "Đã ghi nhận trả sách.");
        return "redirect:/borrowings/" + id + "/return";
    }

    private void loadChoices(Model model) {
        model.addAttribute("members", memberService.activeMembers());
        model.addAttribute("books", bookService.borrowableBooks());
    }

    private BorrowingForm emptyForm() {
        BorrowingForm form = new BorrowingForm();
        for (int i = 0; i < 5; i++) {
            form.getLines().add(new BorrowingLineForm());
        }
        return form;
    }
}
