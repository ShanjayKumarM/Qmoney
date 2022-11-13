
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  //  private RestTemplate restTemplate;
  private StockQuotesService service;
  // Caution: Do not delete or modify the constructor, or else your build will
  // break!
  // This is absolutely necessary for backward compatibility
  // private RestTemplate restTemplate;

  protected PortfolioManagerImpl(String provider,RestTemplate restTemplate) {
    //this.restTemplate = restTemplate;
  }

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Now we want to convert our code into a module, so we will not call it from
  // main anymore.
  // Copy your code from Module#3
  // PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and make sure that it
  // follows the method signature.
  // Logic to read Json file and convert them into Objects will not be required
  // further as our
  // clients will take care of it, going forward.
  // Test your code using Junits provided.
  // Make sure that all of the tests inside PortfolioManagerTest using command
  // below -
  // ./gradlew test --tests PortfolioManagerTest
  // This will guard you against any regressions.
  // run ./gradlew build in order to test yout code, and make sure that
  // the tests and static code quality pass.

  // CHECKSTYLE:OFF

  public PortfolioManagerImpl(StockQuotesService service2, RestTemplate restTemplate2) {
  //  this.restTemplate = restTemplate2;
    this.service = service2; 
  }

  public PortfolioManagerImpl(StockQuotesService service){
  //  this.restTemplate = new RestTemplate();
    this.service = service;
  }

public PortfolioManagerImpl(RestTemplate restTemplate2) {
  // this.restTemplate =  restTemplate2;
}

private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  // CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Extract the logic to call Tiingo thirdparty APIs to a separate function.
  // It should be split into fto parts.
  // Part#1 - Prepare the Url to call Tiingo based on a template constant,
  // by replacing the placeholders.
  // Constant should look like
  // https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=?&endDate=?&token=?
  // Where ? are replaced with something similar to <ticker> and then actual url
  // produced by
  // replacing the placeholders with actual parameters.

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException, StockQuoteServiceException {
  //  ObjectMapper om = getObjectMapper();
  //  String result = restTemplate.getForObject(buildUri(symbol, from, to), String.class);
  //  List<TiingoCandle> collection = om.readValue(result, new TypeReference<ArrayList<TiingoCandle>>() {
  //  });
  //  return new ArrayList<Candle>(collection);
  return this.service.getStockQuote(symbol, from, to);

  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?" + "startDate="
        + startDate.toString() + "&endDate=" + endDate.toString() + "&token="
        + "56ced5d79d2bfeedeb35aa03a11d6d68a33074f7";
    return uriTemplate;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate)
      throws JsonMappingException, JsonProcessingException, StockQuoteServiceException {
    //  ObjectMapper om = getObjectMapper();
  //  RestTemplate restTemplate = new RestTemplate();
    List<AnnualizedReturn> anreturns = new ArrayList<AnnualizedReturn>();
    for (PortfolioTrade symbol : portfolioTrades) {
      // String result =
      // this.restTemplate.getForObject("https://api.tiingo.com/tiingo/daily/"
      // + symbol.getSymbol() + "/prices?startDate=" + symbol.getPurchaseDate()
      // + "&endDate=" + endDate.toString() + "&token="
      // + "56ced5d79d2bfeedeb35aa03a11d6d68a33074f7", String.class);
      List<Candle> collection = getStockQuote(symbol.getSymbol(), symbol.getPurchaseDate(), endDate);
      // collection = om.readValue(result,
      // om.getTypeFactory().constructCollectionType(List.class, TiingoCandle.class));
      AnnualizedReturn x = calculateAnnualizedReturns(endDate, symbol, collection.get(0).getOpen(),
          collection.get(collection.size() - 1).getClose());
      anreturns.add(x);
      Collections.sort(anreturns, getComparator());
    }
    return anreturns;
    // tickerWithCloseValues.put(collection.get(collection.size() - 1).getClose(),
    // symbol.getSymbol());
    // }

  }

  //private static ObjectMapper getObjectMapper() {
    // ObjectMapper objectMapper = new ObjectMapper();
    //objectMapper.registerModule(new JavaTimeModule());
    //return objectMapper;
  // }

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, PortfolioTrade trade, Double buyPrice,
      Double sellPrice) {
    Double totalReturn = (sellPrice - buyPrice) / buyPrice;
    long daysBetween = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
    double years = (double) daysBetween / 365;
    Double annualret = Math.pow(1 + totalReturn, 1 / years) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualret, totalReturn);
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate, int numThreads) throws InterruptedException, StockQuoteServiceException {
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    List<AnnualizedReturn> anreturns = new ArrayList<AnnualizedReturn>();
    List<Future<AnnualizedReturn>> list = new ArrayList<Future<AnnualizedReturn>>();

    for (PortfolioTrade symbol : portfolioTrades) {
      Callable<AnnualizedReturn> callable = new PortfolioCallable(symbol,endDate,this.service);
      Future<AnnualizedReturn> future = executor.submit(callable);
      list.add(future);
    }

    for (Future<AnnualizedReturn> fut : list) {
      try {
        anreturns.add(fut.get());
      } catch (ExecutionException e) {
        throw new StockQuoteServiceException("Execution exception");
      }
    }
    Collections.sort(anreturns, getComparator());

    executor.shutdown();

    return anreturns;


    //return null;
  }

}
