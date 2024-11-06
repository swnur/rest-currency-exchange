package com.swnur.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swnur.dto.ExchangeRequestDTO;
import com.swnur.dto.ExchangeResponseDTO;
import com.swnur.exception.InvalidParameterException;
import com.swnur.service.ExchangeService;
import com.swnur.utils.ValidationUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeControllerServlet extends HttpServlet {

    private final ExchangeService exchangeService = new ExchangeService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amount = req.getParameter("amount");

        if (amount == null || amount.isBlank()) {
            throw new InvalidParameterException("Missing parameter - amount");
        }

        ExchangeRequestDTO exchangeRequestDTO = new ExchangeRequestDTO(baseCurrencyCode, targetCurrencyCode, convertToNumber(amount));

        ValidationUtils.validate(exchangeRequestDTO);

        ExchangeResponseDTO exchangeResponseDTO = exchangeService.exchange(exchangeRequestDTO);

        objectMapper.writeValue(resp.getWriter(), exchangeResponseDTO);
    }

    private static BigDecimal convertToNumber(String amount) {
        try {
            return BigDecimal.valueOf(Double.parseDouble(amount));
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Parameter amount must be a number");
        }
    }
}
