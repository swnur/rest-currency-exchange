package com.swnur.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swnur.dao.ExchangeRateDAO;
import com.swnur.dao.ExchangeRateDAOImpl;
import com.swnur.dto.ExchangeRateRequestDTO;
import com.swnur.entity.ExchangeRate;
import com.swnur.exception.InvalidParameterException;
import com.swnur.exception.NotFoundException;
import com.swnur.service.ExchangeRateService;
import com.swnur.utils.MappingUtils;
import com.swnur.utils.ValidationUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchangeRate/*")
public class ExchangeRateControllerServlet extends HttpServlet {

    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAOImpl();
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] currencyCodes = extractAndValidateCurrencyCodes(req, resp);

        ExchangeRate exchangeRate = exchangeRateDAO.findByCodes(currencyCodes[0], currencyCodes[1])
                .orElseThrow(() -> new NotFoundException(
                        "Exchange rate '" + currencyCodes[0] + "' - '" + currencyCodes[1] + "' not found")
                );

        objectMapper.writeValue(resp.getWriter(), MappingUtils.convertToDTO(exchangeRate));
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] currencyCodes = extractAndValidateCurrencyCodes(req, resp);

        String parameter = req.getReader().readLine();

        if (parameter == null || !parameter.contains("rate")) {
            throw new InvalidParameterException("Missing parameter - rate");
        }

        String rate = parameter.replace("rate=", "");

        if (rate.isBlank()) {
            throw new InvalidParameterException("Missing parameter - rate");
        }

        ExchangeRateRequestDTO exchangeRateRequestDto = new ExchangeRateRequestDTO(currencyCodes[0], currencyCodes[1], convertToNumber(rate));

        ExchangeRate exchangeRate = exchangeRateService.update(exchangeRateRequestDto);

        objectMapper.writeValue(resp.getWriter(), MappingUtils.convertToDTO(exchangeRate));
    }

    private static BigDecimal convertToNumber(String rate) {
        try {
            return BigDecimal.valueOf(Double.parseDouble(rate));
        }
        catch (NumberFormatException e) {
            throw new InvalidParameterException("Parameter rate must be a number");
        }
    }

    private String[] extractAndValidateCurrencyCodes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String currencyCodes = req.getPathInfo().substring(1);

        if (currencyCodes.length() != 6) {
            throw new InvalidParameterException("Currency codes are either not provided or provided in an incorrect format");
        }

        String baseCurrencyCode = currencyCodes.substring(0, 3);
        String targetCurrencyCode = currencyCodes.substring(3);

        ValidationUtils.validateCurrencyCode(baseCurrencyCode);
        ValidationUtils.validateCurrencyCode(targetCurrencyCode);

        return new String[]{baseCurrencyCode, targetCurrencyCode};
    }
}
