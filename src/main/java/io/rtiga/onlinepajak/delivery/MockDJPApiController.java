package io.rtiga.onlinepajak.delivery;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockDJPApiController {

    @GetMapping(value = "/djp-mock", produces = MediaType.APPLICATION_XML_VALUE)
    public String getMockDjpXml() {
        return """
                <resValidateFakturPm>
                    <kdJenisTransaksi>07</kdJenisTransaksi>
                    <fgPengganti>0</fgPengganti>
                    <nomorFaktur>0700002212345678</nomorFaktur>
                    <tanggalFaktur>01/04/2022</tanggalFaktur>
                    <npwpPenjual>012345678012000</npwpPenjual>
                    <namaPenjual>PT ABC</namaPenjual>
                    <alamatPenjual>Jalan Gatot Subroto No. 40A, Senayan, Kebayoran Baru,
                    Jakarta Selatan 12910</alamatPenjual>
                    <npwpLawanTransaksi>023456789217000</npwpLawanTransaksi>
                    <namaLawanTransaksi>PT XYZ</namaLawanTransaksi>
                    <alamatLawanTransaksi>Jalan Kuda Laut No. 1, Sungai Jodoh, Batu Ampar,
                    Batam 29444</alamatLawanTransaksi>
                    <jumlahDpp>15000000</jumlahDpp>
                    <jumlahPpn>1650000</jumlahPpn>
                    <jumlahPpnBm>0</jumlahPpnBm>
                    <statusApproval>Faktur Valid, Sudah Diapprove oleh DJP</statusApproval>
                    <statusFaktur>Faktur Pajak Normal</statusFaktur>
                    <referensi>123/ABC/IV/2022</referensi>
                    <detailTransaksi>
                        <nama>KOMPUTER MERK ABC, HS Code 84714110</nama>
                        <hargaSatuan>5000000</hargaSatuan>
                        <jumlahBarang>3</jumlahBarang>
                        <hargaTotal>15000000</hargaTotal>
                        <diskon>0</diskon>
                        <dpp>15000000</dpp>
                        <ppn>1650000</ppn>
                        <tarifPpnbm>0</tarifPpnbm>
                        <ppnbm>0</ppnbm>
                    </detailTransaksi>
                </resValidateFakturPm>
                """;
    }
}
