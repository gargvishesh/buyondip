import api from './axios'

export interface NewsItem {
  title: string
  url: string
  description: string
  source: string
  publishedAt: string
}

export const getStockNews = (symbol: string) =>
  api.get<NewsItem[]>(`/news/${symbol}`).then(r => r.data)

export const getMarketNews = () =>
  api.get<NewsItem[]>('/news/market').then(r => r.data)
