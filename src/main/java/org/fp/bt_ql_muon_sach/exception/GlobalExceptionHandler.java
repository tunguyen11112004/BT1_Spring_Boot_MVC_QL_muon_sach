package org.fp.bt_ql_muon_sach.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public String notFound(NotFoundException exception, Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "error/message";
    }

    @ExceptionHandler(BusinessException.class)
    public String business(BusinessException exception, Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "error/message";
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public String conflict(Model model) {
        model.addAttribute("errorMessage", "Dữ liệu sách vừa được cập nhật bởi người khác. Hãy thử lại.");
        return "error/message";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String conflictData(Model model) {
        model.addAttribute("errorMessage", "Dữ liệu bị trùng hoặc không hợp lệ.");
        return "error/message";
    }
}
