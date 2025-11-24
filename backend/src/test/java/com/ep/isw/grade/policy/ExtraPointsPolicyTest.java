package com.ep.isw.grade.policy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ExtraPointsPolicyTest {

    private final ExtraPointsPolicy policy = new ExtraPointsPolicy();

    @Test
    void shouldGrantPointsWhenConsensusExists() {
        ExtraPointsPolicy.ExtraPointsResult result = policy.review(List.of(true, true, true));

        assertThat(result.granted()).isTrue();
        assertThat(result.value()).isEqualTo(ExtraPointsPolicy.EXTRA_POINTS);
    }

    @Test
    void shouldRejectWhenVotesAreMissing() {
        ExtraPointsPolicy.ExtraPointsResult result = policy.review(List.of(true, false));

        assertThat(result.granted()).isFalse();
        assertThat(result.reason()).contains("No hubo consenso");
    }
}
