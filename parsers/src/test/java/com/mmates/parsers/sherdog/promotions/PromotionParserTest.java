package com.mmates.parsers.sherdog.promotions;

import com.mmates.core.model.promotion.Promotion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PromotionParserTest {

    @Test
    void shouldRetrievePromotionsFromRecentEventPage() {
        PromotionParser parser = new PromotionParser();
        List<Promotion> promotions = assertDoesNotThrow(() -> parser.retrievePromotionsFromRecentEventPage(1, 2));
        assertFalse(promotions.isEmpty());
    }

}