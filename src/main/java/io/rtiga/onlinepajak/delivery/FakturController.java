package io.rtiga.onlinepajak.delivery;


import io.rtiga.onlinepajak.domains.dto.ValidationResult;
import io.rtiga.onlinepajak.services.faktur.FakturService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/faktur")
@RequiredArgsConstructor
public class FakturController {

    private final FakturService fakturService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ValidationResult> upload(@RequestParam("file") MultipartFile file) {

        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equalsIgnoreCase("application/pdf") &&
                        !contentType.equalsIgnoreCase("image/jpeg"))) {
            return ResponseEntity.badRequest().body(
                    ValidationResult.builder()
                            .status("error")
                            .message("File is not JPG or PDF")
                            .build()
            );
        }

        return ResponseEntity.ok(fakturService.processFaktur(file));
    }
}

