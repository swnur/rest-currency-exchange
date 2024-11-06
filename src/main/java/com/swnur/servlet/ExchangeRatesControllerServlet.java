package com.swnur.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swnur.dao.ExchangeRateDAO;
import com.swnur.dao.ExchangeRateDAOImpl;
import com.swnur.dto.ExchangeRateRequestDTO;
import com.swnur.dto.ExchangeRateResponseDTO;
import com.swnur.entity.ExchangeRate;
import com.swnur.exception.InvalidParameterException;
import com.swnur.service.ExchangeRateService;
import com.swnur.utils.MappingUtils;
import com.swnur.utils.ValidationUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;

@WebServlet("/exchangeRates")
public class ExchangeRatesControllerServlet extends HttpServlet {

    private final ExchangeRateDAO exchangeRateDao = new ExchangeRateDAOImpl();
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRate> exchangeRates = exchangeRateDao.findAll();

        List<ExchangeRateResponseDTO> exchangeRateDTOResponses = exchangeRates.stream()
                .map(MappingUtils::convertToDTO)
                .collect(Collectors.toList());

        objectMapper.writeValue(resp.getWriter(), exchangeRateDTOResponses);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        ExchangeRateRequestDTO exchangeRateRequestDTO = new ExchangeRateRequestDTO(baseCurrencyCode, targetCurrencyCode, convertToNumber(rate));

        ValidationUtils.validate(exchangeRateRequestDTO);

        ExchangeRate exchangeRate = exchangeRateService.insert(exchangeRateRequestDTO);

        resp.setStatus(SC_CREATED);
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
}
