
package io.rtiga.onlinepajak.domains.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class FakturData {
    private String namaPenjual;
    private String npwpPenjual;
    private String namaPembeli;
    private String npwpPembeli;
    private BigDecimal hargaTotal;
    private BigDecimal ppn;
    private BigDecimal ppnbm;
    private LocalDate tanggalFaktur;
    private String nomorFaktur;
    private String qrCodeValue;

}
