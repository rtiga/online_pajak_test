package io.rtiga.onlinepajak.services.faktur;

import io.rtiga.onlinepajak.domains.dto.ValidationResult;
import org.springframework.web.multipart.MultipartFile;

public interface FakturService {
    ValidationResult processFaktur(MultipartFile file);
}
