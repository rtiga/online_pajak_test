package io.rtiga.onlinepajak.services.faktur;

import io.rtiga.onlinepajak.domains.dto.DJPResponse;
import io.rtiga.onlinepajak.domains.dto.FakturData;
import io.rtiga.onlinepajak.domains.dto.ValidationResult;
import jakarta.validation.Validation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class Validator {
    public ValidationResult validate(FakturData faktur, DJPResponse djp) {
        List<ValidationResult.Deviation> deviations = new ArrayList<>();

        compareField("npwpPenjual", faktur.getNpwpPenjual(), djp.getNpwpPenjual(), deviations);
        compareField("namaPenjual", faktur.getNamaPenjual(), djp.getNamaPenjual(), deviations);
        compareField("npwpPembeli", faktur.getNpwpPembeli(), djp.getNpwpLawanTransaksi(), deviations);
        compareField("namaPembeli", faktur.getNamaPembeli(), djp.getNamaLawanTransaksi(), deviations);
        compareField("hargaTotal", faktur.getHargaTotal(), BigDecimal.valueOf(djp.getJumlahDpp()), deviations);
        compareField("ppn", faktur.getPpn(), BigDecimal.valueOf(djp.getJumlahPpn()), deviations);

        ValidationResult.ValidationDetails details = new ValidationResult.ValidationDetails();
        details.setDeviations(deviations);
        details.setValidatedData(djp);

        ValidationResult result = ValidationResult.builder()
                .validationResults(details)
                .build();

        if (deviations.isEmpty()) {
            result.setStatus("validated_successfully");
            result.setMessage("All fields match");
        } else {
            result.setStatus("validated_with_deviations");
            result.setMessage("Some fields differ from DJP data");
        }

        return result;
    }

    private void compareField(String field, Object pdfVal, Object apiVal,
                              List<ValidationResult.Deviation> deviations) {
        if (pdfVal == null && apiVal == null) return;

        if (!Objects.equals(String.valueOf(pdfVal).trim(), String.valueOf(apiVal).trim())) {
            ValidationResult.Deviation dev =  ValidationResult.Deviation.builder()
                            .field(field)
                    .pdfValue(String.valueOf(pdfVal))
                    .djpApiValue(apiVal.toString())
                    .deviationType(pdfVal == null ? "missing_in_pdf" :
                            apiVal == null ? "missing_in_api" : "mismatch")
                    .build();
            dev.setField(field);
            deviations.add(dev);
        }
    }
}
