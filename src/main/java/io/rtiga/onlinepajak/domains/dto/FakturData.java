
package io.rtiga.onlinepajak.domains.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class FakturData {
    private String namaPenjual;
    private String npwpPenjual;
    private String namaPembeli;
    private String npwpPembeli;
    private Double hargaTotal;
    private Double ppn;
    private Double ppnbm;
    private LocalDate tanggalFaktur;
    private String nomorFaktur;
    private String qrCodeValue;
}
