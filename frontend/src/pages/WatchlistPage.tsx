import { useState } from 'react'
import { useWatchlist } from '../hooks/useWatchlist'
import WatchlistTable from '../components/watchlist/WatchlistTable'
import AddStockModal from '../components/watchlist/AddStockModal'
import Spinner from '../components/shared/Spinner'

export default function WatchlistPage() {
  const { stocks, loading, error, add, remove } = useWatchlist()
  const [showModal, setShowModal] = useState(false)

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Watchlist</h1>
          <p className="text-sm text-gray-500 mt-1">Track your Indian equities</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors"
        >
          + Add Stock
        </button>
      </div>

      {error && (
        <div className="mb-4 bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 text-sm">
          {error}
        </div>
      )}

      {loading ? (
        <Spinner size="lg" />
      ) : (
        <WatchlistTable stocks={stocks} onRemove={remove} />
      )}

      {showModal && (
        <AddStockModal onAdd={add} onClose={() => setShowModal(false)} />
      )}
    </div>
  )
}
