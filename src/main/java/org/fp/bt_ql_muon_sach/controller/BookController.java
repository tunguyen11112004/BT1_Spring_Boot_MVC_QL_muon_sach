package org.fp.bt_ql_muon_sach.controller;

import jakarta.validation.Valid;
import org.fp.bt_ql_muon_sach.dto.BookForm;
import org.fp.bt_ql_muon_sach.exception.BusinessException;
import org.fp.bt_ql_muon_sach.service.BookService;
import org.fp.bt_ql_muon_sach.service.CategoryService;
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

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;

    public BookController(BookService bookService, CategoryService categoryService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            Model model
    ) {
        model.addAttribute("books", bookService.search(keyword, categoryId, page, size, sort));
        model.addAttribute("categories", categoryService.activeCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sort", sort);
        return "books/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("bookForm", new BookForm());
        model.addAttribute("categories", categoryService.activeCategories());
        return "books/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("bookForm", bookService.getForm(id));
        model.addAttribute("categories", categoryService.activeCategories());
        return "books/form";
    }

    @PostMapping
    public String save(
            @Valid @ModelAttribute("bookForm") BookForm form,
            BindingResult binding,
            Model model,
            RedirectAttributes redirect
    ) {
        if (binding.hasErrors()) {
            model.addAttribute("categories", categoryService.activeCategories());
            return "books/form";
        }
        try {
            bookService.save(form);
        } catch (BusinessException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("categories", categoryService.activeCategories());
            return "books/form";
        }
        redirect.addFlashAttribute("successMessage", "Đã lưu sách.");
        return "redirect:/books";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            bookService.softDelete(id);
            redirect.addFlashAttribute("successMessage", "Đã ngừng sử dụng sách.");
        } catch (BusinessException exception) {
            redirect.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/books";
    }
}
