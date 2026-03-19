import { useState } from 'react'
import { useWatchlist } from '../hooks/useWatchlist'
import { useDipAlerts } from '../hooks/useDipAlerts'
import { useMarketDips } from '../hooks/useMarketDips'
import MarketPulseBar from '../components/dashboard/MarketPulseBar'
import SectorHeatmap from '../components/dashboard/SectorHeatmap'
import DipAlertCard from '../components/dashboard/DipAlertCard'
import Spinner from '../components/shared/Spinner'

type Tab = 'watchlist' | 'market'

export default function DashboardPage() {
  const [activeTab, setActiveTab] = useState<Tab>('watchlist')
  const { stocks, loading: wLoading } = useWatchlist()
  const { alerts: watchlistAlerts, loading: wDipLoading, triggerRefresh: refreshWatchlist } = useDipAlerts()
  const { alerts: marketAlerts, loading: mLoading, triggerRefresh: refreshMarket } = useMarketDips()

  const isWatchlist = activeTab === 'watchlist'
  const alerts = isWatchlist ? watchlistAlerts : marketAlerts
  const alertsLoading = isWatchlist ? wDipLoading : mLoading

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        {isWatchlist ? (
          <button
            onClick={refreshWatchlist}
            className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors"
          >
            Refresh Dips
          </button>
        ) : (
          <button
            onClick={refreshMarket}
            className="flex items-center gap-2 bg-indigo-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors"
          >
            Run Market Scan
          </button>
        )}
      </div>

      {wLoading ? <Spinner /> : <MarketPulseBar stocks={stocks} />}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <div className="flex items-center justify-between mb-3">
            <div className="flex gap-1 bg-gray-100 rounded-lg p-1">
              <button
                onClick={() => setActiveTab('watchlist')}
                className={`px-4 py-1.5 rounded-md text-sm font-medium transition-colors ${
                  isWatchlist
                    ? 'bg-white text-gray-900 shadow-sm'
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                My Watchlist
              </button>
              <button
                onClick={() => setActiveTab('market')}
                className={`px-4 py-1.5 rounded-md text-sm font-medium transition-colors ${
                  !isWatchlist
                    ? 'bg-white text-gray-900 shadow-sm'
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                Market Opportunities
              </button>
            </div>
            {!alertsLoading && (
              <span className="text-sm text-gray-500">{alerts.length} alerts</span>
            )}
          </div>

          {alertsLoading ? (
            <Spinner />
          ) : alerts.length === 0 ? (
            <div className="text-center py-12 text-gray-400 bg-white rounded-xl border border-gray-200">
              <p className="text-3xl mb-2">{isWatchlist ? '' : ''}</p>
              <p>
                {isWatchlist
                  ? 'No active dip alerts right now'
                  : 'No market opportunities found — try running a market scan'}
              </p>
            </div>
          ) : (
            <div className="grid gap-3 sm:grid-cols-2">
              {alerts.map(a => (
                <DipAlertCard key={a.symbol} alert={a} showScore={!isWatchlist} />
              ))}
            </div>
          )}
        </div>

        <div>
          {wLoading ? <Spinner /> : <SectorHeatmap stocks={stocks} />}
        </div>
      </div>
    </div>
  )
}
