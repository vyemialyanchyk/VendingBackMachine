package com.vending.back.machine.helper.history;

import com.vending.back.machine.domain.oauth.VbmUserDetails;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;

/**
 * vyemialyanchyk on 1/30/2017.
 */
@SuppressWarnings("unchecked")
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
})
public class CurrentUserProvider implements Interceptor {
    private static final int PARAM_INDEX = 1;

    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        addUpdater(invocation.getArgs());
        return invocation.proceed();
    }

    private void addUpdater(final Object[] queryArgs) {
        final Object param = queryArgs[PARAM_INDEX];
        if (param instanceof Map) {
            Map<String, Object> paramsMap = (Map<String, Object>) param;
            if (!paramsMap.containsKey("updater")) {
                paramsMap.put("updater", getUserId());
            }
        }
    }

    private Long getUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = 1L;
        if (authentication != null && authentication.getPrincipal() != null && !"anonymousUser".equals(authentication.getName())
                && authentication.getPrincipal() instanceof VbmUserDetails) {
            id = ((VbmUserDetails) authentication.getPrincipal()).getUserId();
        }
        return id;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
