import { useState, useEffect, useCallback } from 'react'
import { getActiveDips, refreshDips, DipAnalysis } from '../api/dips'
import { usePolling } from './usePolling'

export function useDipAlerts() {
  const [alerts, setAlerts] = useState<DipAnalysis[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const refresh = useCallback(async () => {
    try {
      const data = await getActiveDips()
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
    await refreshDips()
    await refresh()
  }

  return { alerts, loading, error, refresh, triggerRefresh }
}
