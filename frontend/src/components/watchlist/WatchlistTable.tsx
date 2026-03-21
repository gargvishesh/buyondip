import { Link } from 'react-router-dom'
import { StockSummary } from '../../api/watchlist'
import PercentBadge from '../shared/PercentBadge'
import Badge from '../shared/Badge'
import { formatPrice } from '../../utils/currency'

interface Props {
  stocks: StockSummary[]
  onRemove: (symbol: string) => void
}

export default function WatchlistTable({ stocks, onRemove }: Props) {
  if (stocks.length === 0) {
    return (
      <div className="text-center py-12 text-gray-400">
        <p className="text-4xl mb-2">📋</p>
        <p>Your watchlist is empty. Add some stocks to get started.</p>
      </div>
    )
  }

  return (
    <div className="overflow-x-auto rounded-xl border border-gray-200">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            {['Symbol', 'Company', 'Sector', 'Exchange', 'Price', 'Change', 'Status', ''].map(h => (
              <th key={h} className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                {h}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-100">
          {stocks.map(s => (
            <tr key={s.symbol} className="hover:bg-gray-50 transition-colors">
              <td className="px-4 py-3">
                <Link to={`/stock/${s.symbol}`} className="font-bold text-blue-600 hover:underline">
                  {s.symbol}
                </Link>
              </td>
              <td className="px-4 py-3 text-sm text-gray-700">{s.companyName}</td>
              <td className="px-4 py-3">
                <Badge variant="blue">{s.sector || '—'}</Badge>
              </td>
              <td className="px-4 py-3">
                <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                  s.exchange === 'NSE' ? 'bg-orange-100 text-orange-700' : 'bg-blue-100 text-blue-700'
                }`}>
                  {s.exchange || 'NSE'}
                </span>
              </td>
              <td className="px-4 py-3 text-sm font-semibold text-gray-900">
                {s.currentPrice ? formatPrice(s.currentPrice, s.exchange || 'NSE') : '—'}
              </td>
              <td className="px-4 py-3">
                {s.changePercent != null ? <PercentBadge value={s.changePercent} /> : '—'}
              </td>
              <td className="px-4 py-3">
                {s.inDip ? (
                  <span className="inline-flex items-center gap-1 text-xs font-medium text-red-600">
                    📉 Dip {Number(s.dipPercent).toFixed(1)}%
                  </span>
                ) : (
                  <Badge variant="green">Normal</Badge>
                )}
              </td>
              <td className="px-4 py-3 text-right">
                <button
                  onClick={() => onRemove(s.symbol)}
                  className="text-xs text-gray-400 hover:text-red-500 transition-colors"
                >
                  Remove
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
