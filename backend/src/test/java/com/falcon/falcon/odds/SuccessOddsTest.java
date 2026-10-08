package com.falcon.falcon.odds;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class SuccessOddsTest {

    private static final double PRECISION = 1e-9;

    @ParameterizedTest(name = "{0} encounter(s) leave {1} odds of success")
    @CsvSource({
            "0, 1.0",
            "1, 0.9",
            "2, 0.81",
            "3, 0.729",
            "4, 0.6561"
    })
    void survives_each_encounter_with_nine_chances_out_of_ten(int encounters, double expected) {
        assertThat(SuccessOdds.afterEncounters(encounters)).isCloseTo(expected, within(PRECISION));
    }

    @Test
    void guarantees_success_when_no_hunter_is_ever_met() {
        assertThat(SuccessOdds.afterEncounters(0)).isEqualTo(1.0);
    }

    @Test
    void never_reaches_certain_capture_however_many_encounters() {
        // The odds tends to zero but stay strictly positive: escaping is
        // always conceivable, which is why the display only reaches 0% when
        // Endor cannot be reached at all.
        assertThat(SuccessOdds.afterEncounters(100)).isStrictlyBetween(0.0, 1.0);
    }

    @Test
    void shrinks_with_every_additional_encounter() {
        assertThat(SuccessOdds.afterEncounters(3)).isLessThan(SuccessOdds.afterEncounters(2));
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, -10 })
    void rejects_a_negative_number_of_encounters(int encounters) {
        assertThatThrownBy(() -> SuccessOdds.afterEncounters(encounters))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
