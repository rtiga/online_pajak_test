package io.rtiga.onlinepajak.services.faktur;

import io.rtiga.onlinepajak.domains.dto.FakturData;
import io.rtiga.onlinepajak.domains.dto.ValidationResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class Validator {
    public ValidationResult validate(FakturData pdf, FakturData djp) {
        List<ValidationResult.Deviation> deviations = new ArrayList<>();

        if (!Objects.equals(pdf.getNamaPembeli(), djp.getNamaPembeli())) {
            deviations.add(ValidationResult.Deviation.builder()
                    .field("namaPembeli")
                    .pdfValue(pdf.getNamaPembeli())
                    .djpApiValue(djp.getNamaPembeli())
                    .deviationType("mismatch")
                    .build());
        }

        return ValidationResult.builder()
                .status(deviations.isEmpty() ? "validated_successfully" : "validated_with_deviations")
                .message(deviations.isEmpty() ? "All fields match." : "Found mismatched fields.")
                .deviations(deviations)
                .validatedData(djp)
                .build();
    }
}
