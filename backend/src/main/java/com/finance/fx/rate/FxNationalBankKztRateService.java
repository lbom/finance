package com.finance.fx.rate;

import com.finance.fx.pair.Pair;
import com.finance.fx.pair.PairRepo;
import com.finance.fx.pair.PairType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FxNationalBankKztRateService {

    private final PairRepo pairRepo;
    private final FxRateRepo fxRateRepo;
    private RestTemplate restTemplate = new RestTemplate();

    void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${fx-kzt.url:https://nationalbank.kz/rss/rates_all.xml}")
    private String url;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public record KztRate(BigDecimal price, LocalDate date) {}

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 0,12 * * *")
    public void refreshRates() {
        log.info("Starting National Bank KZT rates refresh");
        try {
            String xmlResponse = restTemplate.getForObject(url, String.class);
            if (xmlResponse == null || xmlResponse.isBlank()) {
                log.warn("National Bank returned empty XML response");
                return;
            }

            Map<String, KztRate> ratesMap = parseRatesXml(xmlResponse);
            if (ratesMap.isEmpty()) {
                log.warn("No KZT rates parsed from National Bank XML");
                return;
            }

            List<Pair> forexPairs = pairRepo.findAllByType(PairType.FOREX);
            List<FxRate> updatedRates = forexPairs.stream()
                    .filter(pair -> ratesMap.containsKey(pair.getSymbol()))
                    .map(pair -> {
                        KztRate rateInfo = ratesMap.get(pair.getSymbol());
                        FxRate fxRate = new FxRate();
                        fxRate.setPairId(pair.getId());
                        fxRate.setPrice(rateInfo.price());
                        fxRate.setCandleType(FxCandleType.D);
                        fxRate.setCandleDate(rateInfo.date());
                        return fxRate;
                    })
                    .toList();

            if (!updatedRates.isEmpty()) {
                fxRateRepo.saveAll(updatedRates);
            }

            log.info("Completed National Bank KZT rates refresh. Updated {} symbols", updatedRates.size());
        } catch (Exception e) {
            log.error("Failed to refresh National Bank KZT rates", e);
        }
    }

    public Map<String, KztRate> parseRatesXml(String xml) {
        Map<String, KztRate> map = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));
            NodeList items = doc.getElementsByTagName("item");

            for (int i = 0; i < items.getLength(); i++) {
                Node node = items.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;
                Element element = (Element) node;

                String title = getTagValue("title", element);
                String pubDate = getTagValue("pubDate", element);
                String description = getTagValue("description", element);
                String quantStr = getTagValue("quant", element);

                if (title == null || description == null) continue;

                BigDecimal amount = new BigDecimal(description.replace(",", ".").trim());
                BigDecimal quant = (quantStr != null && !quantStr.isBlank())
                        ? new BigDecimal(quantStr.trim())
                        : BigDecimal.ONE;

                BigDecimal price = amount.divide(quant, 4, RoundingMode.HALF_UP);
                LocalDate date = parseDate(pubDate);

                String currencyCode = title.trim().toUpperCase();
                map.put(currencyCode + "KZT", new KztRate(price, date));
                if (price.compareTo(BigDecimal.ZERO) > 0) {
                    map.put("KZT" + currencyCode, new KztRate(BigDecimal.ONE.divide(price, 8, RoundingMode.HALF_UP), date));
                }
            }
        } catch (Exception e) {
            log.error("Error parsing National Bank KZT XML", e);
        }
        return map;
    }

    private String getTagValue(String tag, Element element) {
        NodeList list = element.getElementsByTagName(tag);
        if (list != null && list.getLength() > 0) {
            Node node = list.item(0);
            if (node != null) {
                return node.getTextContent();
            }
        }
        return null;
    }

    private LocalDate parseDate(String pubDateStr) {
        if (pubDateStr == null || pubDateStr.isBlank()) {
            return LocalDate.now(ZoneOffset.UTC);
        }
        try {
            return LocalDate.parse(pubDateStr.trim(), DATE_FORMATTER);
        } catch (Exception e) {
            log.warn("Could not parse pubDate '{}', using today", pubDateStr);
            return LocalDate.now(ZoneOffset.UTC);
        }
    }
}
