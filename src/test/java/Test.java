import com.example.petcurrencyexchange.dto.Exchange;
import com.example.petcurrencyexchange.models.Currency;
import com.example.petcurrencyexchange.models.ExchangeRates;
import com.example.petcurrencyexchange.repositories.CurrencyRepository;
import com.example.petcurrencyexchange.repositories.ExchangeRatesRepository;
import com.example.petcurrencyexchange.service.Calculation;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) throws SQLException, IOException {
        CurrencyRepository currencyRepository = new CurrencyRepository();
        System.out.println(currencyRepository.getCurrencies());
    }


}
