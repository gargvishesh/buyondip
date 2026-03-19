import { useState, useEffect, useCallback } from 'react'
import { getWatchlist, addStock, removeStock, StockSummary, AddStockRequest } from '../api/watchlist'
import { usePolling } from './usePolling'

export function useWatchlist() {
  const [stocks, setStocks] = useState<StockSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const refresh = useCallback(async () => {
    try {
      const data = await getWatchlist()
      setStocks(data)
      setError(null)
    } catch (e: any) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { refresh() }, [refresh])
  usePolling(refresh)

  const add = async (req: AddStockRequest) => {
    await addStock(req)
    await refresh()
  }

  const remove = async (symbol: string) => {
    await removeStock(symbol)
    await refresh()
  }

  return { stocks, loading, error, refresh, add, remove }
}
