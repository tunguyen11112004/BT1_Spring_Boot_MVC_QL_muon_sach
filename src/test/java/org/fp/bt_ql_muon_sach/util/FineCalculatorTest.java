package org.fp.bt_ql_muon_sach.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FineCalculatorTest {

    @Test
    void noFineWhenReturnedOnOrBeforeDueDate() {
        LocalDate due = LocalDate.of(2026, 9, 15);
        assertEquals(BigDecimal.ZERO, FineCalculator.calculate(due, due, 2));
        assertEquals(BigDecimal.ZERO, FineCalculator.calculate(due, due.minusDays(1), 2));
    }

    @Test
    void fineIsFiveThousandPerDayPerCopy() {
        LocalDate due = LocalDate.of(2026, 8, 15);
        LocalDate returned = LocalDate.of(2026, 8, 20);
        assertEquals(new BigDecimal("25000"), FineCalculator.calculate(due, returned, 1));
        assertEquals(new BigDecimal("50000"), FineCalculator.calculate(due, returned, 2));
    }
}
