package io.rtiga.onlinepajak.domains.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ValidationResult {
    private String status;
    private String message;
    private List<Deviation> deviations;
    private FakturData validatedData;

    @Data
    @Builder
    public static class Deviation {
        private String field;
        private String pdfValue;
        private String djpApiValue;
        private String deviationType;
    }
}
