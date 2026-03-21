import api from './axios'

export interface Candlestick {
  date: string
  open: number
  high: number
  low: number
  close: number
  volume: number
}

export interface PriceHistory {
  symbol: string
  candles: Candlestick[]
  peakPrice: number
  peakDate: string
  troughPrice: number
  troughDate: string
  currentPrice: number
  dipPercent: number
  priorRisePercent: number
}

export interface Fundamentals {
  symbol: string
  companyName: string
  exchange: string
  pe: number
  roe: number
  roce: number
  debtToEquity: number
  epsGrowth: number
  promoterHolding: number
  marketCap: number
  bookValue: number
  dividendYield: number
  sector: string
}

export const getPriceHistory = (symbol: string, range = '6mo') =>
  api.get<PriceHistory>(`/stocks/${symbol}/price-history`, { params: { range } }).then(r => r.data)

export const getFundamentals = (symbol: string, exchange = 'NSE') =>
  api.get<Fundamentals>(`/stocks/${symbol}/fundamentals`, { params: { exchange } }).then(r => r.data)

export const searchStocks = (q: string) =>
  api.get<{ symbol: string; name: string; exchange: string }[]>('/stocks/search', { params: { q } }).then(r => r.data)
