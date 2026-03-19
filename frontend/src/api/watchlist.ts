import api from './axios'

export interface StockSummary {
  symbol: string
  companyName: string
  sector: string
  currentPrice: number
  change: number
  changePercent: number
  inDip: boolean
  dipPercent: number
  dipCause: string
}

export interface AddStockRequest {
  symbol: string
  companyName: string
  sector: string
}

export const getWatchlist = () => api.get<StockSummary[]>('/watchlist').then(r => r.data)

export const addStock = (req: AddStockRequest) =>
  api.post('/watchlist', req).then(r => r.data)

export const removeStock = (symbol: string) =>
  api.delete(`/watchlist/${symbol}`)
