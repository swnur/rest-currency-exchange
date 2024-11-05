package com.swnur.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swnur.dao.CurrencyDAO;
import com.swnur.dao.CurrencyDAOImpl;
import com.swnur.entity.Currency;
import com.swnur.exception.NotFoundException;
import com.swnur.utils.MappingUtils;
import com.swnur.utils.ValidationUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyControllerServlet extends HttpServlet {
    private final CurrencyDAO currencyDAO = new CurrencyDAOImpl();;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getPathInfo().substring(1);

        ValidationUtils.validateCurrencyCode(code);
        Currency currency = currencyDAO.findByCode(code)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Currency with code '%s' not found.", code)));

        objectMapper.writeValue(resp.getWriter(), MappingUtils.convertToDTO(currency));
    }
}
