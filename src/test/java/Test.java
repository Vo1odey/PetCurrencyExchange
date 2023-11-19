import com.example.petcurrencyexchange.models.DtoExchange;
import com.example.petcurrencyexchange.models.ExchangeRates;
import com.example.petcurrencyexchange.repositories.CurrencyRepository;
import com.example.petcurrencyexchange.repositories.ExchangeRatesRepository;
import com.example.petcurrencyexchange.utils.Calculation;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLOutput;

public class Test {
    public static void main(String[] args) throws SQLException {
        String from = "RUB";
        String to = "USD";
        BigDecimal amount = new BigDecimal(100);
        ExchangeRatesRepository exchangeRatesRepository = new ExchangeRatesRepository();
        if (exchangeRatesRepository.getExchangeRatesByCodes(from, to).isPresent()) {
            ExchangeRates exchangeRatesModel = exchangeRatesRepository.getExchangeRatesByCodes(from, to).get();
            BigDecimal convertedAmount = Calculation.convertedAmount(amount, exchangeRatesModel.getRate());
            DtoExchange DtoModel = new DtoExchange(exchangeRatesModel.getBaseCurrency(),
                    exchangeRatesModel.getTargetCurrency(), exchangeRatesModel.getRate(), amount, convertedAmount);
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(DtoModel);
        }
    }

}
