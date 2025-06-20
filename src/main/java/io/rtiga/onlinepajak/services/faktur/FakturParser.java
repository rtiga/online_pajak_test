
package io.rtiga.onlinepajak.services.faktur;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import io.rtiga.onlinepajak.domains.dto.FakturData;
import org.apache.pdfbox.io.RandomAccessStreamCache;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FakturParser {

    public FakturData parsePdf(InputStream inputStream) throws Exception {
        PDDocument document = new PDDocument((RandomAccessStreamCache.StreamCacheCreateFunction) inputStream);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);

        return FakturData.builder()
                .namaPenjual(extract(text, "Nama\\s*:\\s*(PT\\s+[\\w\\s]+)"))
                .npwpPenjual(extract(text, "NPWP\\s*:\\s*(\\d{2}\\.\\d{3}\\.\\d{3}\\.\\d-\\d{3}\\.\\d{3})"))
                .namaPembeli(extract(text, "Pembeli.*?Nama\\s*:\\s*([\\w\\s]+)"))
                .npwpPembeli(extract(text, "Pembeli.*?NPWP\\s*:\\s*(\\d{2}\\.\\d{3}\\.\\d{3}\\.\\d-\\d{3}\\.\\d{3})"))
                .hargaTotal(parseMoney(text, "Harga Jual / Penggantian\\s*([\\d\\.\\,]+)"))
                .ppn(parseMoney(text, "Total PPN\\s*([\\d\\.\\,]+)"))
                .ppnbm(parseMoney(text, "Total PPnBM.*?([\\d\\.\\,]+)"))
                .tanggalFaktur(parseDate(text, "(\\d{2})\\s+(Januari|Februari|Maret|April|Mei|Juni|Juli|Agustus|September|Oktober|November|Desember)\\s+(\\d{4})"))
                .nomorFaktur(extract(text, "Faktur Pajak.*?(\\d{3}\\.\\d{3}-\\d{2}\\.\\d{8})"))
                .qrCodeValue(parseQrCode(document))
                .build();
    }

    private String extract(String text, String pattern) {
        Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private Double parseMoney(String text, String regex) {
        String value = extract(text, regex);
        return value != null ? Double.parseDouble(value.replace(".", "").replace(",", ".")) : null;
    }

    private String parseQrCode(PDDocument document) throws Exception {
        PDFRenderer renderer = new PDFRenderer(document);
        BufferedImage image = renderer.renderImageWithDPI(0, 300);
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            return null;
        }
    }

    private LocalDate parseDate(String text, String pattern) {
        Matcher matcher = Pattern.compile(pattern).matcher(text);
        if (matcher.find()) {
            String day = matcher.group(1);
            String month = convertMonthName(matcher.group(2));
            String year = matcher.group(3);
            return LocalDate.parse(String.format("%s-%s-%s", year, month, day));
        }
        return null;
    }

    // TODO: Handle wrong month name
    private String convertMonthName(String indoMonth) {
        return switch (indoMonth.toLowerCase()) {
            case "januari" -> "01";
            case "februari" -> "02";
            case "maret" -> "03";
            case "april" -> "04";
            case "mei" -> "05";
            case "juni" -> "06";
            case "juli" -> "07";
            case "agustus" -> "08";
            case "september" -> "09";
            case "oktober" -> "10";
            case "november" -> "11";
            case "desember" -> "12";
            default -> "01";
        };
    }
}
