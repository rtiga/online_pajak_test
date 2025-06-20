package io.rtiga.onlinepajak.domains.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "resValidateFakturPm")
@XmlAccessorType(XmlAccessType.FIELD)
public class DJPResponse {
    private String kdJenisTransaksi;
    private String fgPengganti;
    private String nomorFaktur;
    private String tanggalFaktur;
    private String npwpPenjual;
    private String namaPenjual;
    private String alamatPenjual;
    private String npwpLawanTransaksi;
    private String namaLawanTransaksi;
    private String alamatLawanTransaksi;
    private Long jumlahDpp;
    private Long jumlahPpn;
    private Long jumlahPpnBm;
    private String statusApproval;
    private String statusFaktur;
    private String referensi;

    @XmlElement(name = "detailTransaksi")
    private DetailTransaction detailTransaksi;
}
