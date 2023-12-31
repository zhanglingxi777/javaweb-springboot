package top.zhanglingxi.security.filter;

import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import top.zhanglingxi.utils.RedisUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {
    @Autowired
    private RedisUtils redisUtils;

    @Override
    @Autowired
    @Lazy
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    @Autowired
    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
        super.setAuthenticationSuccessHandler(successHandler);
    }

    @Override
    @Autowired
    public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        super.setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    @Value("username")
    public void setUsernameParameter(String usernameParameter) {
        super.setUsernameParameter(usernameParameter);
    }

    @Override
    @Value("password")
    public void setPasswordParameter(String passwordParameter) {
        super.setPasswordParameter(passwordParameter);
    }

    @Override
    @Value("/doLogin")
    public void setFilterProcessesUrl(String filterProcessesUrl) {
        super.setFilterProcessesUrl(filterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try (ServletInputStream is = request.getInputStream()) {
            FastByteArrayOutputStream read = IoUtil.read(is);
            String json = read.toString();
            JSONObject jsonObject = JSONObject.parseObject(json);
            Object uuidObj = jsonObject.get("uuid");
            Object verifyCodeObj = jsonObject.get("verifyCode");
            if (Objects.isNull(uuidObj)) {
                throw new AuthenticationServiceException("uuid不能为空");
            }
            if (Objects.isNull(verifyCodeObj)) {
                throw new AuthenticationServiceException("验证码不能为空");
            }
            String verifyCode = verifyCodeObj.toString();
            String uuid = uuidObj.toString();
            Object cacheObject = redisUtils.getCacheObject("ebao:image-code:" + uuid);
            if (Objects.isNull(cacheObject)) {
                throw new AuthenticationServiceException("验证码已过期");
            }
            if (!verifyCode.equals(cacheObject.toString())) {
                throw new AuthenticationServiceException("验证码错误");
            }
            Object usernameObj = jsonObject.get("username");
            Object passwordObj = jsonObject.get("password");
            Object rememberMeObj = jsonObject.get("rememberMe");
            if (Objects.isNull(usernameObj)) {
                throw new AuthenticationServiceException("用户名不能为空");
            }
            if (Objects.isNull(passwordObj)) {
                throw new AuthenticationServiceException("密码不能为空");
            }
            if (Objects.nonNull(rememberMeObj)) {
                request.setAttribute("rememberMe", rememberMeObj.toString());
            }
            String username = usernameObj.toString();
            String password = passwordObj.toString();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            setDetails(request, authenticationToken);
            return getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException("读取请求体中的数据失败");
        }
    }
}
