import { useDipAlerts } from '../hooks/useDipAlerts'
import DipAlertCard from '../components/dashboard/DipAlertCard'
import Spinner from '../components/shared/Spinner'

export default function DipAlertsPage() {
  const { alerts, loading, error, triggerRefresh } = useDipAlerts()

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dip Alerts</h1>
          <p className="text-sm text-gray-500 mt-1">
            Stocks that rose ≥15% then dipped ≥10% from peak
          </p>
        </div>
        <button
          onClick={triggerRefresh}
          disabled={loading}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 disabled:opacity-50 transition-colors"
        >
          🔄 Run Detection
        </button>
      </div>

      {error && (
        <div className="mb-4 bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 text-sm">
          {error}
        </div>
      )}

      {loading ? (
        <Spinner size="lg" />
      ) : alerts.length === 0 ? (
        <div className="text-center py-20 text-gray-400">
          <p className="text-5xl mb-3">✅</p>
          <p className="text-lg">No active dip alerts</p>
          <p className="text-sm mt-1">Try running detection or add more stocks to your watchlist.</p>
        </div>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {alerts.map(a => <DipAlertCard key={a.symbol} alert={a} />)}
        </div>
      )}
    </div>
  )
}
