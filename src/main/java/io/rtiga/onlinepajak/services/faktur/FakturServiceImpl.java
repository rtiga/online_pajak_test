package io.rtiga.onlinepajak.services.faktur;

import io.rtiga.onlinepajak.domains.dto.DJPResponse;
import io.rtiga.onlinepajak.domains.dto.FakturData;
import io.rtiga.onlinepajak.domains.dto.ValidationResult;
import io.rtiga.onlinepajak.services.DJPMockClient;
import io.rtiga.onlinepajak.utils.XmlParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Objects;

@Service
public class FakturServiceImpl implements FakturService {

    private final FakturParser fakturParser;
    private final Validator fakturValidator;
    private final DJPMockClient djpMockClient;

    public FakturServiceImpl(FakturParser fakturParser, Validator fakturValidator, DJPMockClient djpMockClient) {
        this.fakturParser = fakturParser;
        this.fakturValidator = fakturValidator;
        this.djpMockClient = djpMockClient;
    }

    @Override
    public ValidationResult processFaktur(MultipartFile file) {
        try {
            FakturData parsed;

            if(Objects.equals(file.getContentType(), "application/pdf")){
                parsed = fakturParser.parsePdf(file);
            } else {
                parsed = fakturParser.parseJpeg(file);
            }

            String fetchFakturXml = djpMockClient.fetchFakturXml();
            DJPResponse djpResponse = XmlParser.parseXml(fetchFakturXml);

            return fakturValidator.validate(parsed,djpResponse);
        } catch (Exception e) {
            return ValidationResult.builder()
                    .status("error")
                    .message("Failed to process Faktur: " + e.getMessage())
                    .build();
        }
    }
}
