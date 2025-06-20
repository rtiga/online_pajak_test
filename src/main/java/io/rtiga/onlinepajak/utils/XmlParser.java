package io.rtiga.onlinepajak.utils;

import io.rtiga.onlinepajak.domains.dto.DJPResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import java.io.StringReader;

public class XmlParser {

    public static DJPResponse parseXml(String xml) {
        try {
            JAXBContext context = JAXBContext.newInstance(DJPResponse.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (DJPResponse) unmarshaller.unmarshal(new StringReader(xml));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML", e);
        }
    }
}
