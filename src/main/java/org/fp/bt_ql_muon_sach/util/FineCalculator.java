package org.fp.bt_ql_muon_sach.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class FineCalculator {

    public static final BigDecimal DAILY_RATE = new BigDecimal("5000");

    private FineCalculator() {
    }

    public static BigDecimal calculate(LocalDate dueDate, LocalDate returnDate, int copies) {
        if (copies < 1 || !returnDate.isAfter(dueDate)) {
            return BigDecimal.ZERO;
        }
        long days = ChronoUnit.DAYS.between(dueDate, returnDate);
        return DAILY_RATE.multiply(BigDecimal.valueOf(days)).multiply(BigDecimal.valueOf(copies));
    }
}
