package com.swnur.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swnur.dao.CurrencyDAO;
import com.swnur.dao.CurrencyDAOImpl;
import com.swnur.dto.CurrencyRequestDTO;
import com.swnur.dto.CurrencyResponseDTO;
import com.swnur.entity.Currency;
import com.swnur.utils.MappingUtils;
import com.swnur.utils.ValidationUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;

@WebServlet("/currencies")
public class CurrenciesControllerServlet extends HttpServlet {

    private final CurrencyDAO currencyDao = new CurrencyDAOImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Currency> currencies = currencyDao.findAll();

        List<CurrencyResponseDTO> currenciesDto = currencies.stream()
                .map(MappingUtils::convertToDTO)
                .collect(Collectors.toList());

        objectMapper.writeValue(resp.getWriter(), currenciesDto);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        System.out.println(name + " " + code + " " + sign);

        CurrencyRequestDTO currencyRequestDto = new CurrencyRequestDTO(code, name, sign);

        ValidationUtils.validate(currencyRequestDto);

        Currency currency = currencyDao.insert(MappingUtils.convertToEntity(currencyRequestDto));

        resp.setStatus(SC_CREATED);
        objectMapper.writeValue(resp.getWriter(), MappingUtils.convertToDTO(currency));
    }
}
