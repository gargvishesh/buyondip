import { StockSummary } from '../../api/watchlist'

interface Props {
  stocks: StockSummary[]
}

function sectorColor(stocks: StockSummary[]) {
  const avg = stocks.reduce((s, x) => s + (x.changePercent || 0), 0) / stocks.length
  if (avg >= 1) return 'bg-green-100 border-green-300 text-green-800'
  if (avg >= 0) return 'bg-green-50 border-green-200 text-green-700'
  if (avg >= -1) return 'bg-red-50 border-red-200 text-red-700'
  return 'bg-red-100 border-red-300 text-red-800'
}

export default function SectorHeatmap({ stocks }: Props) {
  const bySector: Record<string, StockSummary[]> = {}
  for (const s of stocks) {
    const sec = s.sector || 'Other'
    if (!bySector[sec]) bySector[sec] = []
    bySector[sec].push(s)
  }

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-4">
      <h3 className="font-semibold text-gray-900 mb-3">Sector Overview</h3>
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-2">
        {Object.entries(bySector).map(([sector, sStocks]) => {
          const avg = sStocks.reduce((s, x) => s + (x.changePercent || 0), 0) / sStocks.length
          return (
            <div key={sector} className={`rounded-lg border p-3 ${sectorColor(sStocks)}`}>
              <p className="text-xs font-medium">{sector}</p>
              <p className="text-sm font-bold mt-1">{avg >= 0 ? '+' : ''}{avg.toFixed(2)}%</p>
              <p className="text-xs opacity-60">{sStocks.length} stocks</p>
            </div>
          )
        })}
      </div>
    </div>
  )
}
