import api from './axios'

export interface DipAnalysis {
  symbol: string
  companyName: string
  exchange: string
  currentPrice: number
  peakPrice: number
  peakDate: string
  dipPercent: number
  troughPrice: number
  troughDate: string
  priorRisePercent: number
  cause: 'GLOBAL' | 'SECTOR' | 'COMPANY_SPECIFIC'
  causeMessage: string
  annotation: string
  detectedAt: string
  source: 'WATCHLIST' | 'MARKET'
  compositeScore: number | null
  fundamentalScore: number | null
}

export const getActiveDips = () =>
  api.get<DipAnalysis[]>('/dips').then(r => r.data)

export const getMarketDips = () =>
  api.get<DipAnalysis[]>('/dips/market').then(r => r.data)

export const getDipForSymbol = (symbol: string) =>
  api.get<DipAnalysis>(`/dips/${symbol}`).then(r => r.data)

export const refreshDips = () =>
  api.post<{ alertsDetected: number; status: string }>('/dips/refresh').then(r => r.data)

export const refreshMarketDips = () =>
  api.post<{ alertsDetected: number; status: string }>('/dips/market/refresh').then(r => r.data)
