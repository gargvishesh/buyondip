package com.buyondip.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaFallbackController {

    /**
     * Forward all non-API, non-static routes to the React SPA.
     */
    @RequestMapping(value = {
            "/watchlist", "/dips", "/stock/**", "/dashboard"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
