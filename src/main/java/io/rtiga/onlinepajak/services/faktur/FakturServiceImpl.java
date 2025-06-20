package io.rtiga.onlinepajak.services.faktur;

import io.rtiga.onlinepajak.domains.dto.FakturData;
import io.rtiga.onlinepajak.domains.dto.ValidationResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class FakturServiceImpl implements FakturService {

    private final FakturParser fakturParser;
    private final Validator fakturValidator;

    public FakturServiceImpl(FakturParser fakturParser, Validator fakturValidator) {
        this.fakturParser = fakturParser;
        this.fakturValidator = fakturValidator;
    }

    @Override
    public ValidationResult processFaktur(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {

            FakturData parsed = fakturParser.parsePdf(inputStream);

            FakturData official = parsed; // TODO: Replace with actual API integration or a mocked one

            return fakturValidator.validate(parsed,official);
        } catch (Exception e) {
            return ValidationResult.builder()
                    .status("error")
                    .message("Failed to process Faktur: " + e.getMessage())
                    .build();
        }
    }
}
