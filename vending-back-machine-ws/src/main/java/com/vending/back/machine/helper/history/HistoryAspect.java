package com.vending.back.machine.helper.history;

import com.vending.back.machine.mapper.HistoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.TreeMap;

/**
 * vyemialyanchyk on 1/30/2017.
 */
@Slf4j
@Aspect
@Component
public class HistoryAspect {
    @Autowired
    private HistoryMapper historyMapper;

    @Pointcut("execution(* com.vending.back.machine.mapper.*.update*(..))")
    public void updateMethod() {
    }

    @Pointcut("execution(* com.vending.back.machine.mapper.*.delete*(..))")
    public void deleteMethod() {
    }

    @Before("deleteMethod()")
    public void deleteAdvice(JoinPoint joinPoint) {
        updateAdvice(joinPoint);
    }

    @Before("updateMethod()")
    public void updateAdvice(JoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Trackable tracking = signature.getMethod().getDeclaredAnnotation(Trackable.class);
        if (tracking != null) {
            final Map<String, Object> params = new TreeMap<>();
            String table = tracking.table().trim();
            String where = tracking.where().trim();
            if (table != null && table.length() > 0 && where != null && where.length() > 0) {
                params.put("_table", table);
                params.put("_where", where);
                writeHistory(joinPoint, params, signature, where);
            }
            table = tracking.table02().trim();
            where = tracking.where02().trim();
            if (table != null && table.length() > 0 && where != null && where.length() > 0) {
                params.put("_table", table);
                params.put("_where", where);
                writeHistory(joinPoint, params, signature, where);
            }
        }
    }

    public void writeHistory(JoinPoint joinPoint, Map<String, Object> params, MethodSignature signature, String where) {
        int i = 0;
        Object[] args = joinPoint.getArgs();
        for (Annotation[] annotations : signature.getMethod().getParameterAnnotations()) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == Param.class) {
                    Param param = AnnotationUtils.getAnnotation(annotation, Param.class);
                    if (where.contains("#{" + param.value() + "}")) {
                        // parameter re-used in where clause - pass it to the query
                        params.put(param.value(), args[i]);
                    }
                }
            }
            i++;
        }
        historyMapper.writeHistory(params);
    }
}
