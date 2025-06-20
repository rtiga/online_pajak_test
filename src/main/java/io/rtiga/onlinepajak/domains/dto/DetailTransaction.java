package io.rtiga.onlinepajak.domains.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DetailTransaction {
    private String nama;
    private Long hargaSatuan;
    private Integer jumlahBarang;
    private Long hargaTotal;
    private Long diskon;
    private Long dpp;
    private Long ppn;
    private Long tarifPpnbm;
    private Long ppnbm;
}
