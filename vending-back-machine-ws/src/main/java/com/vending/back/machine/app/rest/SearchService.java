package com.vending.back.machine.app.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * vyemialyanchyk on 12/21/2016.
 */
@Slf4j
@RestController
@RequestMapping(value = "/rest/public/search", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE, method = RequestMethod.GET)
public class SearchService {

    public static class SearchRes {
        public String indexCreateTime = "0";
        public String time = "0";
        public String items = "0";
        public String[] result;
    }

    @RequestMapping(value = "/{str}", consumes = ALL_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public SearchRes searchParts(@PathVariable("str") String str) {
        return searchPartsIntern(str, 6, 10);
    }

    @RequestMapping(value = "/{str}/col/{col}", consumes = ALL_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public SearchRes searchParts(@PathVariable("str") String str, @PathVariable("col") Integer col) {
        return searchPartsIntern(str, col, 10);
    }

    @RequestMapping(value = "/{str}/col/{col}/max/{max}", consumes = ALL_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public SearchRes searchParts(@PathVariable("str") String str, @PathVariable("col") Integer col, @PathVariable("max") Integer max) {
        return searchPartsIntern(str, col, max);
    }

    private SearchRes searchPartsIntern(String str, Integer col, Integer max) {
        SearchRes res = new SearchRes();
        return res;
    }
}
