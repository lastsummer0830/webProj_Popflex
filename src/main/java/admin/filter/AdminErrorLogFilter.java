package admin.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import admin.logging.ErrorLogStore;

@WebFilter("/*")
public class AdminErrorLogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Throwable error) {
            if (request instanceof HttpServletRequest) {
                ErrorLogStore.add((HttpServletRequest) request, error);
            }

            if (error instanceof IOException) {
                throw (IOException) error;
            }
            if (error instanceof ServletException) {
                throw (ServletException) error;
            }
            if (error instanceof RuntimeException) {
                throw (RuntimeException) error;
            }
            if (error instanceof Error) {
                throw (Error) error;
            }

            throw new ServletException(error);
        }
    }

    @Override
    public void destroy() {
    }
}
