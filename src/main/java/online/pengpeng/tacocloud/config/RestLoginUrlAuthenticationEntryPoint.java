package online.pengpeng.tacocloud.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.*;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author pengpeng
 * @date 2022/4/24
 */
public class RestLoginUrlAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {


    private static final Log logger = LogFactory.getLog(RestLoginUrlAuthenticationEntryPoint.class);


    private PortMapper portMapper = new PortMapperImpl();
    private PortResolver portResolver = new PortResolverImpl();
    private String loginFormUrl;
    private boolean forceHttps = false;
    private boolean useForward = false;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public RestLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        Assert.notNull(loginFormUrl, "loginFormUrl cannot be null");
        this.loginFormUrl = loginFormUrl;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(StringUtils.hasText(loginFormUrl) && UrlUtils
                .isValidRedirectUrl(loginFormUrl), "loginFromUrl must be specified and must be valid redirect URL");
        if (useForward && UrlUtils.isAbsoluteUrl(loginFormUrl)){
            throw  new IllegalArgumentException(
                    "useForward must be false if using an absolute loginFormURL");
        }
        Assert.notNull(portMapper, "portMapper must be specified");
        Assert.notNull(portResolver, "portResolver must be specified");
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse  response, AuthenticationException e) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        PrintWriter out = response.getWriter();
        String sb = "{\"status\":\"error\", \"msg\":\"" +
                "未登录" +
                "\"}";
        out.write(sb);
        out.flush();
        out.close();
    }
}
