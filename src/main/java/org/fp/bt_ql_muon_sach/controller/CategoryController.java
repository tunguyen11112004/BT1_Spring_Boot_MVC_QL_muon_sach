package org.fp.bt_ql_muon_sach.controller;

import jakarta.validation.Valid;
import org.fp.bt_ql_muon_sach.dto.CategoryForm;
import org.fp.bt_ql_muon_sach.exception.BusinessException;
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
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        model.addAttribute("categories", categoryService.list(page, size));
        return "categories/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("categoryForm", new CategoryForm());
        return "categories/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("categoryForm", categoryService.getForm(id));
        return "categories/form";
    }

    @PostMapping
    public String save(
            @Valid @ModelAttribute("categoryForm") CategoryForm form,
            BindingResult binding,
            Model model,
            RedirectAttributes redirect
    ) {
        if (binding.hasErrors()) {
            return "categories/form";
        }
        try {
            categoryService.save(form);
        } catch (BusinessException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            return "categories/form";
        }
        redirect.addFlashAttribute("successMessage", "Đã lưu thể loại.");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        categoryService.softDelete(id);
        redirect.addFlashAttribute("successMessage", "Đã ngừng dùng thể loại.");
        return "redirect:/categories";
    }
}
