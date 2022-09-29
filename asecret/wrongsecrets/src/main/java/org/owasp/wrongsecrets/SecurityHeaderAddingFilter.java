package org.owasp.wrongsecrets;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SecurityHeaderAddingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        res.addHeader("Server", "WrongSecrets - Star us!");
        res.addHeader("X-Frame-Options", "SAMEORIGIN");
        res.addHeader("X-Content-Type-Options", "nosniff");
        res.addHeader("Content-Security-Policy", "default-src * 'self' https://wrongsecrets.herokuapp.com/; script-src  * 'self' 'unsafe-inline'; style-src * 'self' 'unsafe-inline'; img-src data:");
        chain.doFilter(request, res);
    }
}
