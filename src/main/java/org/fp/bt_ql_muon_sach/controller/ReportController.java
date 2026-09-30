package org.fp.bt_ql_muon_sach.controller;

import org.fp.bt_ql_muon_sach.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/overdue")
    public String overdue(Model model) {
        model.addAttribute("borrowings", reportService.overdue());
        return "reports/overdue";
    }

    @GetMapping("/top-books")
    public String topBooks(Model model) {
        model.addAttribute("books", reportService.topBooks());
        return "reports/top-books";
    }

    @GetMapping("/members")
    public String members(Model model) {
        model.addAttribute("rows", reportService.memberCounts());
        return "reports/members";
    }
}
