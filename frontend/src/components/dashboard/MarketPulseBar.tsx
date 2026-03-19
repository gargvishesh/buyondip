import { StockSummary } from '../../api/watchlist'
import PercentBadge from '../shared/PercentBadge'

interface Props {
  stocks: StockSummary[]
}

export default function MarketPulseBar({ stocks }: Props) {
  const dipping = stocks.filter(s => s.inDip).length
  return (
    <div className="bg-gray-900 text-white rounded-xl p-4 flex flex-wrap items-center gap-6">
      <div>
        <p className="text-xs text-gray-400">Watchlist</p>
        <p className="text-lg font-bold">{stocks.length} stocks</p>
      </div>
      <div>
        <p className="text-xs text-gray-400">In Dip</p>
        <p className="text-lg font-bold text-red-400">{dipping}</p>
      </div>
      <div className="flex-1 overflow-x-auto">
        <div className="flex gap-4">
          {stocks.slice(0, 8).map(s => (
            <div key={s.symbol} className="text-center shrink-0">
              <p className="text-xs text-gray-400">{s.symbol}</p>
              <PercentBadge value={s.changePercent} />
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
