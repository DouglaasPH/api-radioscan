package com.douglaasph.clinic_api.adapters.outbound.repository.xrayreport;

import com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport.GetXRayReportByIdAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport.XRayReportJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport.XRayReportRepository;
import com.douglaasph.clinic_api.domain.domain.XRayReport;
import com.douglaasph.clinic_api.domain.domain.enums.ProcessingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GetXRayReportByIdAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private XRayReportRepository xRayReportRepository;

    private GetXRayReportByIdAdapter getXRayReportByIdAdapter;

    @BeforeEach
    void setUp() { getXRayReportByIdAdapter = new GetXRayReportByIdAdapter(xRayReportRepository); }

    @Test
    @DisplayName("Must get xrayreport")
    void case1() {
        XRayReportJpaEntity report = new XRayReportJpaEntity(
                null,
                Instant.now(),
                "exams/abc-123.png",
                ProcessingStatus.PROCESSED_BY_IA.getCode(),
                null,
                null,
                false,
                Collections.emptyList()
        );
        XRayReportJpaEntity persisted = entityManager.persistAndFlush(report);

        Optional<XRayReport> result = getXRayReportByIdAdapter.get(persisted.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getS3Key()).isEqualTo("exams/abc-123.png");
        assertThat(result.get().getProcessingStatus()).isEqualTo(ProcessingStatus.PROCESSED_BY_IA.getCode());
        assertThat(result.get().isReleasedToPatient()).isFalse();
    }

    @Test
    @DisplayName("Should return an empty Optional when the id does not exists")
    void case2() {
        // act
        Optional<XRayReport> result = getXRayReportByIdAdapter.get(1L);

        // assert
        assertThat(result).isEmpty();
    }
}
