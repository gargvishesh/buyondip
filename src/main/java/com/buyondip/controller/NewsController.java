package com.buyondip.controller;

import com.buyondip.dto.NewsItemDto;
import com.buyondip.service.NewsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/{symbol}")
    public List<NewsItemDto> getStockNews(@PathVariable String symbol) {
        return newsService.getStockNews(symbol.toUpperCase());
    }

    @GetMapping("/market")
    public List<NewsItemDto> getMarketNews() {
        return newsService.getMarketNews();
    }
}
