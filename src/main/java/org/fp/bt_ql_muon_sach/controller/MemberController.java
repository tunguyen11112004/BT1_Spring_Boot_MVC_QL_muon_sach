package org.fp.bt_ql_muon_sach.controller;

import jakarta.validation.Valid;
import org.fp.bt_ql_muon_sach.dto.MemberForm;
import org.fp.bt_ql_muon_sach.entity.MemberStatus;
import org.fp.bt_ql_muon_sach.exception.BusinessException;
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

@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) MemberStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        model.addAttribute("members", memberService.list(status, page, size));
        model.addAttribute("status", status);
        model.addAttribute("statuses", MemberStatus.values());
        return "members/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        model.addAttribute("statuses", MemberStatus.values());
        return "members/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("memberForm", memberService.getForm(id));
        model.addAttribute("statuses", MemberStatus.values());
        return "members/form";
    }

    @PostMapping
    public String save(
            @Valid @ModelAttribute("memberForm") MemberForm form,
            BindingResult binding,
            Model model,
            RedirectAttributes redirect
    ) {
        if (binding.hasErrors()) {
            model.addAttribute("statuses", MemberStatus.values());
            return "members/form";
        }
        try {
            memberService.save(form);
        } catch (BusinessException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("statuses", MemberStatus.values());
            return "members/form";
        }
        redirect.addFlashAttribute("successMessage", "Đã lưu thành viên.");
        return "redirect:/members";
    }

    @PostMapping("/{id}/lock")
    public String toggleLock(@PathVariable Long id, RedirectAttributes redirect) {
        memberService.toggleLock(id);
        redirect.addFlashAttribute("successMessage", "Đã đổi trạng thái thành viên.");
        return "redirect:/members";
    }
}
