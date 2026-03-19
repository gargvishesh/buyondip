import { useState, useEffect, useCallback } from 'react'
import { getMarketDips, refreshMarketDips, DipAnalysis } from '../api/dips'
import { usePolling } from './usePolling'

export function useMarketDips() {
  const [alerts, setAlerts] = useState<DipAnalysis[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const refresh = useCallback(async () => {
    try {
      const data = await getMarketDips()
      setAlerts(data)
      setError(null)
    } catch (e: any) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { refresh() }, [refresh])
  usePolling(refresh)

  const triggerRefresh = async () => {
    setLoading(true)
    await refreshMarketDips()
    await refresh()
  }

  return { alerts, loading, error, refresh, triggerRefresh }
}
