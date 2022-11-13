
package com.crio.warmup.stock.portfolio;

import com.crio.warmup.stock.quotes.StockQuoteServiceFactory;
import com.crio.warmup.stock.quotes.StockQuotesService;
import java.lang.Deprecated;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerFactory {

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Implement the method in such a way that it will return new Instance of
  // PortfolioManager using RestTemplate provided.

  @Deprecated
  public static PortfolioManager getPortfolioManager(RestTemplate restTemplate) {
     
    return new PortfolioManagerImpl(restTemplate);
    
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the method in such a way that it will return new Instance of PortfolioManager
  //  using RestTemplate and provider name.
  //  As a first step, Create appropriate instance of StoockQuoteService using
  //  StockQuoteServiceFactory and then use the same instance of StockQuoteService to create the
  //  instance of PortfolioManager.
  //  Mark the earlier constructor of PortfolioManager as @Deprecated.
  //  Test your changes using gradle command and make sur all of the tests pass.
  //  ./gradlew test --tests PortfolioManagerFactory

  public static PortfolioManager getPortfolioManager(String provider,
      RestTemplate restTemplate) {
    StockQuotesService service = StockQuoteServiceFactory.getService(provider, restTemplate);
    return new PortfolioManagerImpl(service,restTemplate);
  }

}
