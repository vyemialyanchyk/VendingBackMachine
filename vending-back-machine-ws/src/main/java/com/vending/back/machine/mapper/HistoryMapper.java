package com.vending.back.machine.mapper;

import org.apache.ibatis.annotations.Insert;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * vyemialyanchyk on 1/30/2017.
 */
public interface HistoryMapper {

    @Insert("INSERT INTO ${_table}_hst select t.* FROM ${_table} t WHERE ${_where}")
    @Transactional(propagation = Propagation.MANDATORY)
    void writeHistory(Map<String, Object> params);
}