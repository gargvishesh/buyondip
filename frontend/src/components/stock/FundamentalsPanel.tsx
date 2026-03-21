import { Fundamentals } from '../../api/stocks'
import { currencySymbol, formatMarketCap } from '../../utils/currency'

interface Props {
  data: Fundamentals
  exchange?: string
}

function MetricRow({ label, value, good }: { label: string; value: string; good?: boolean | null }) {
  return (
    <div className="flex items-center justify-between py-2 border-b border-gray-100 last:border-0">
      <span className="text-sm text-gray-600">{label}</span>
      <span className={`text-sm font-semibold ${good === true ? 'text-green-600' : good === false ? 'text-red-600' : 'text-gray-900'}`}>
        {value}
      </span>
    </div>
  )
}

function fmt(n: number | null | undefined, decimals = 2, suffix = '') {
  if (n == null) return 'N/A'
  return n.toFixed(decimals) + suffix
}

export default function FundamentalsPanel({ data, exchange = 'NSE' }: Props) {
  const ccy = currencySymbol(exchange)
  return (
    <div className="bg-white rounded-xl border border-gray-200 p-4">
      <h3 className="font-semibold text-gray-900 mb-3">Fundamentals</h3>
      <div className="divide-y divide-gray-100">
        <MetricRow label="P/E Ratio" value={fmt(data.pe, 1, 'x')} good={data.pe != null ? data.pe < 25 : null} />
        <MetricRow label="ROE" value={fmt(data.roe, 1, '%')} good={data.roe != null ? data.roe > 15 : null} />
        <MetricRow label="ROCE" value={fmt(data.roce, 1, '%')} good={data.roce != null ? data.roce > 15 : null} />
        <MetricRow label="Debt / Equity" value={fmt(data.debtToEquity, 2, 'x')} good={data.debtToEquity != null ? data.debtToEquity < 1 : null} />
        <MetricRow label="EPS Growth" value={fmt(data.epsGrowth, 1, '%')} good={data.epsGrowth != null ? data.epsGrowth > 10 : null} />
        {exchange === 'NSE' && (
          <MetricRow label="Promoter Holding" value={fmt(data.promoterHolding, 1, '%')} good={data.promoterHolding != null ? data.promoterHolding > 50 : null} />
        )}
        <MetricRow label="Market Cap" value={data.marketCap != null ? formatMarketCap(data.marketCap, exchange) : 'N/A'} />
        <MetricRow label="Book Value" value={data.bookValue != null ? `${ccy}${fmt(data.bookValue, 0)}` : 'N/A'} />
        <MetricRow label="Dividend Yield" value={fmt(data.dividendYield, 2, '%')} />
      </div>
    </div>
  )
}
