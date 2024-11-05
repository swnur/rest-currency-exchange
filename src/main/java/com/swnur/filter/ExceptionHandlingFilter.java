package com.swnur.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swnur.dto.ErrorResponseDTO;
import com.swnur.exception.DBOperationException;
import com.swnur.exception.EntityExistsException;
import com.swnur.exception.InvalidParameterException;
import com.swnur.exception.NotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter("/*")
public class ExceptionHandlingFilter extends HttpFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            super.doFilter(req, res, chain);
        } catch (DBOperationException e) {
            writeErrorResponse(res, SC_INTERNAL_SERVER_ERROR, e);
        } catch (EntityExistsException e) {
            writeErrorResponse(res, SC_CONFLICT, e);
        } catch (InvalidParameterException e) {
            writeErrorResponse(res, SC_BAD_REQUEST, e);
        } catch (NotFoundException e) {
            writeErrorResponse(res, SC_NOT_FOUND, e);
        }
    }

    private void writeErrorResponse(HttpServletResponse response, int errorCode, RuntimeException e) throws IOException {
        response.setStatus(errorCode);

        objectMapper.writeValue(
                response.getWriter(),
                new ErrorResponseDTO(errorCode, e.getMessage())
        );
    }
}
