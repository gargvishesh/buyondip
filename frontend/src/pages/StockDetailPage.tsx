import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getPriceHistory, getFundamentals, PriceHistory, Fundamentals } from '../api/stocks'
import { getDipForSymbol, DipAnalysis } from '../api/dips'
import { getStockNews, NewsItem } from '../api/news'
import PriceChart from '../components/stock/PriceChart'
import FundamentalsPanel from '../components/stock/FundamentalsPanel'
import DipCauseBadge from '../components/stock/DipCauseBadge'
import RiseAndDipAnnotation from '../components/stock/RiseAndDipAnnotation'
import Spinner from '../components/shared/Spinner'
import { formatPrice } from '../utils/currency'

const RANGES = ['1mo', '3mo', '6mo', '1y', '2y']

export default function StockDetailPage() {
  const { symbol } = useParams<{ symbol: string }>()
  const [range, setRange] = useState('6mo')
  const [history, setHistory] = useState<PriceHistory | null>(null)
  const [fundamentals, setFundamentals] = useState<Fundamentals | null>(null)
  const [dip, setDip] = useState<DipAnalysis | null>(null)
  const [news, setNews] = useState<NewsItem[]>([])
  const [loading, setLoading] = useState(true)

  const exchange = dip?.exchange || fundamentals?.exchange || 'NSE'

  useEffect(() => {
    if (!symbol) return
    setLoading(true)
    let resolvedExchange = 'NSE'
    Promise.allSettled([
      getPriceHistory(symbol, range).then(setHistory),
      getDipForSymbol(symbol).then(d => { setDip(d); resolvedExchange = d?.exchange || 'NSE' }).catch(() => setDip(null)),
    ]).then(() =>
      Promise.allSettled([
        getFundamentals(symbol, resolvedExchange).then(setFundamentals),
        getStockNews(symbol, resolvedExchange).then(setNews).catch(() => setNews([])),
      ])
    ).finally(() => setLoading(false))
  }, [symbol, range])

  if (!symbol) return <div className="p-8 text-gray-500">Invalid symbol</div>

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      <div className="flex items-center gap-3">
        <Link to="/" className="text-gray-400 hover:text-gray-600 text-sm">← Back</Link>
        <h1 className="text-2xl font-bold text-gray-900">{symbol}</h1>
        {dip && <DipCauseBadge cause={dip.cause} />}
      </div>

      {dip?.annotation && <RiseAndDipAnnotation annotation={dip.annotation} />}

      {/* Range selector */}
      <div className="flex gap-1 bg-gray-100 rounded-lg p-1 w-fit">
        {RANGES.map(r => (
          <button
            key={r}
            onClick={() => setRange(r)}
            className={`px-3 py-1 rounded text-sm font-medium transition-colors ${
              range === r ? 'bg-white shadow text-blue-600' : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            {r}
          </button>
        ))}
      </div>

      {loading ? (
        <Spinner size="lg" />
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            {history && <PriceChart data={history} exchange={exchange} />}

            {/* Dip details */}
            {dip && (
              <div className="bg-white rounded-xl border border-gray-200 p-4">
                <h3 className="font-semibold text-gray-900 mb-3">Dip Analysis</h3>
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 text-center">
                  {[
                    { label: 'Current', value: formatPrice(Number(dip.currentPrice), exchange) },
                    { label: 'Peak', value: formatPrice(Number(dip.peakPrice), exchange) },
                    { label: 'Dip %', value: `${Number(dip.dipPercent).toFixed(1)}%`, color: 'text-red-600' },
                    { label: 'Prior Rise', value: `+${Number(dip.priorRisePercent).toFixed(1)}%`, color: 'text-green-600' },
                  ].map(m => (
                    <div key={m.label} className="bg-gray-50 rounded-lg p-3">
                      <p className="text-xs text-gray-500">{m.label}</p>
                      <p className={`font-bold text-sm mt-1 ${m.color || 'text-gray-900'}`}>{m.value}</p>
                    </div>
                  ))}
                </div>
                <div className="mt-3 text-sm text-gray-600 bg-gray-50 rounded-lg px-3 py-2">
                  <span className="font-medium">Cause: </span>{dip.causeMessage}
                </div>
              </div>
            )}

            {/* News */}
            {news.length > 0 && (
              <div className="bg-white rounded-xl border border-gray-200 p-4">
                <h3 className="font-semibold text-gray-900 mb-3">Latest News</h3>
                <div className="space-y-3">
                  {news.slice(0, 5).map((n, i) => (
                    <a
                      key={i}
                      href={n.url}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="block hover:bg-gray-50 rounded-lg p-2 -mx-2 transition-colors"
                    >
                      <p className="text-sm font-medium text-gray-900 line-clamp-2">{n.title}</p>
                      <p className="text-xs text-gray-400 mt-1">{n.source} · {new Date(n.publishedAt).toLocaleDateString('en-IN')}</p>
                    </a>
                  ))}
                </div>
              </div>
            )}
          </div>

          <div>
            {fundamentals && <FundamentalsPanel data={fundamentals} exchange={exchange} />}
          </div>
        </div>
      )}
    </div>
  )
}
