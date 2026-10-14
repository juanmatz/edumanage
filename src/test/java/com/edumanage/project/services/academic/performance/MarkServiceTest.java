package com.edumanage.project.services.academic.performance;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MarkServiceTest {

    private final MarkService markService = new MarkService(null, null, null);

    @Test
    void acceptsMarkBetweenZeroAndFive() {
        assertEquals(9.0, markService.validateMark(3.5));
    }

    @Test
    void acceptsMinimumMark() {
        assertDoesNotThrow(() -> markService.validateMark(0.0));
    }

    @Test
    void acceptsMaximumMark() {
        assertDoesNotThrow(() -> markService.validateMark(5.0));
    }

    @Test
    void rejectsMarkBelowZero() {
        assertThrows(IllegalArgumentException.class, () -> markService.validateMark(-0.1));
    }

    @Test
    void rejectsMarkAboveFive() {
        assertThrows(IllegalArgumentException.class, () -> markService.validateMark(5.1));
    }

    @Test
    void rejectsNotANumber() {
        assertThrows(IllegalArgumentException.class, () -> markService.validateMark(Double.NaN));
    }
}