
package com.crio.warmup.stock.quotes;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {

  private RestTemplate restTemplate;

  protected AlphavantageService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) 
      throws JsonProcessingException, StockQuoteServiceException {
    try {
      ObjectMapper om = getObjectMapper();
      String url = buildUri(symbol);
      String result = restTemplate.getForObject(url, String.class);
      if (result == null || result.isEmpty()) {
        throw new StockQuoteServiceException("No response");
      }
      System.out.println(result);
      AlphavantageDailyResponse alphavantageDailyResponse = om
          .readValue(result, AlphavantageDailyResponse.class);
      Map<LocalDate, AlphavantageCandle> candles = alphavantageDailyResponse.getCandles();
      Map<LocalDate, AlphavantageCandle> filteredCandles = candles.entrySet().stream()
          .filter(x -> x.getKey().compareTo(from) >= 0 && x
            .getKey().compareTo(to) <= 0).sorted((a, b) -> {
              return a.getKey().compareTo(b.getKey());
            }).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
      filteredCandles.forEach((k, v) -> v.setDate(k));
      List<Candle> answer = new ArrayList<Candle>(filteredCandles.values());
      Collections.reverse(answer);
      return answer;
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  protected String buildUri(String symbol) {
    String uriTemplate = "https://www.alphavantage.co/query?function="
        + "TIME_SERIES_DAILY&symbol=" + symbol + "&outputsize=full&apikey=" + "VZ32Q7J33SGVHDKG";
    return uriTemplate;
  }
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Inplement the StockQuoteService interface as per the contracts.
  //  The implementation of this functions will be doing following tasks
  //  1. Build the appropriate url to communicate with thirdparty.
  //  The url should consider startDate and endDate if it is supported by the provider.
  //  2. Perform thirdparty communication with the Url prepared in step#1
  //  3. Map the response and convert the same to List<Candle>
  //  4. If the provider does not support startDate and endDate, then the implementation
  //  should also filter the dates based on startDate and endDate.
  //  Make sure that result contains the records for for startDate and endDate after filtering.
  //  5. return a sorted List<Candle> sorted ascending based on Candle#getDate
  //  Call alphavantage service to fetch daily adjusted data for last 20 years. Refer to
  //  documentation here - https://www.alphavantage.co/documentation/
  //  Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  //  Run the tests using command below and make sure it passes
  //  ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  //TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Write a method to create appropriate url to call alphavantage service. Method should
  // be using configurations provided in the {@link @application.properties}.
  // Use thie method in #getStockQuote.
  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  Update the method signature to match the signature change in the interface.
  //  Start throwing new StockQuoteServiceException when you get some invalid response from
  //  Alphavangate, or you encounter a runtime exception during Json parsing.
  //  Make sure that the exception propagates all the way from PortfolioManager,
  //  so that the external user's of our API are able to explicitly handle this exception upfront.
  //CHECKSTYLE:OFF

}

