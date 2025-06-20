
package io.rtiga.onlinepajak.services.faktur;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import io.rtiga.onlinepajak.domains.dto.FakturData;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class FakturParser {

    private final Tesseract tesseract;

    public FakturParser() {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath(getTessdataPath());
        this.tesseract.setLanguage("ind");
    }

    private String getTessdataPath() {
        return new File(Objects.requireNonNull(getClass().getClassLoader().getResource("tesseract")).getFile()).getAbsolutePath();
    }

    public FakturData parseJpeg(MultipartFile file) throws Exception {
        BufferedImage image = ImageIO.read(file.getInputStream());
        String ocrText = tesseract.doOCR(image);

        log.info(ocrText);

        List<String> npwps = extractAllNpwp(ocrText);
        String sellerName = extract(ocrText, "Nama\\s*:\\s*(PT\\s+[\\w\\s]+)");

        return FakturData.builder()
                .namaPenjual(sellerName)
                .npwpPenjual(npwps.get(1))
                .namaPembeli(extract(ocrText, "Pembeli.*?Nama\\s*:\\s*(.*?)\\n"))
                .npwpPembeli(npwps.get(0))
                .hargaTotal(parseMoney(ocrText, "Harga Jual.*?([\\d\\.\\,]+)"))
                .ppn(parseMoney(ocrText, "Total PPN.*?([\\d\\.\\,]+)"))
                .ppnbm(parseMoney(ocrText, "Total PPnBM.*?([\\d\\.\\,]+)"))
                .tanggalFaktur(parseDate(ocrText,
                        "(\\d{2})\\s+(Januari|Februari|Maret|April|Mei|Juni|Juli|Agustus|September|Oktober|November|Desember)\\s+(\\d{4})"))
                .nomorFaktur(extract(ocrText, "Nomor Seri Faktur Pajak\\s*:\\s*(\\d{3}\\.\\d{3}-\\d{2}\\.\\d{8})"))
                .build();
    }

    public FakturData parsePdf(MultipartFile file) throws Exception {

        PDDocument document = Loader.loadPDF(file.getBytes());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);

        log.info(text);

        String buyerBlock = extractBlock(text, "Pembeli.*?(Nama\\s*:\\s*.*?NPWP\\s*:\\s*.*?)(\\n|$)");
        String buyerName = extract(buyerBlock, "Nama\\s*:\\s*(.*)");
        String sellerName = extract(text, "Nama\\s*:\\s*(PT\\s+[\\w\\s]+)").replace("\r\nAlamat","");

        List<String> npwps = extractAllNpwp(text);

        return FakturData.builder()
                .namaPembeli(buyerName)
                .npwpPembeli(npwps.get(0))
                .namaPenjual(sellerName)
                .npwpPenjual(npwps.get(1))
                .hargaTotal(parseMoney(text, "Dasar Pengenaan Pajak\\s*([\\d\\.\\,]+)"))
                .ppn(parseMoney(text, "Total PPN\\s*([\\d\\.\\,]+)"))
                .ppnbm(parseMoney(text, "Total PPnBM.*?([\\d\\.\\,]+)"))
                .tanggalFaktur(parseDate(text, "(\\d{2})\\s+(Januari|Februari|Maret|April|Mei|Juni|Juli|Agustus|September|Oktober|November|Desember)\\s+(\\d{4})"))
                .nomorFaktur(extract(text, "Faktur Pajak.*?(\\d{3}\\.\\d{3}-\\d{2}\\.\\d{8})"))
                .qrCodeValue(parseQrCode(document))
                .build();
    }

    private List<String> extractAllNpwp(String text) {
        Pattern pattern = Pattern.compile("NPWP\\s*:\\s*(\\d{2}\\.\\d{3}\\.\\d{3}\\.\\d-\\d{3}\\.\\d{3})");
        Matcher matcher = pattern.matcher(text);

        List<String> npwpList = new ArrayList<>();
        while (matcher.find()) {
            npwpList.add(matcher.group(1).trim());
        }
        return npwpList;
    }

    private String extract(String text, String pattern) {
        Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private String extractBlock(String text, String pattern) {
        Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }

    private BigDecimal parseMoney(String text, String regex) {
        String value = extract(text, regex);
        return value != null ? BigDecimal.valueOf(Double.parseDouble(value.replace(".", "").replace(",", "."))) : new BigDecimal(0);
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
            return "";
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
