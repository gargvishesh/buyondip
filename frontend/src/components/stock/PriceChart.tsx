import {
  ComposedChart, Line, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, ReferenceLine, ReferenceArea,
} from 'recharts'
import { PriceHistory } from '../../api/stocks'

interface Props {
  data: PriceHistory
}

function fmtDate(d: string) {
  const dt = new Date(d)
  return dt.toLocaleDateString('en-IN', { day: '2-digit', month: 'short' })
}

export default function PriceChart({ data }: Props) {
  if (!data.candles || data.candles.length === 0) {
    return <div className="flex items-center justify-center h-48 text-gray-400">No price data</div>
  }

  const chartData = data.candles.map(c => ({
    date: c.date,
    close: Number(c.close),
    open: Number(c.open),
    high: Number(c.high),
    low: Number(c.low),
  }))

  const inDip = data.dipPercent != null && Number(data.dipPercent) <= -10

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-4">
      <div className="flex items-center justify-between mb-4">
        <h3 className="font-semibold text-gray-900">Price History</h3>
        {data.peakPrice && (
          <span className="text-xs text-gray-500">
            Peak ₹{Number(data.peakPrice).toLocaleString('en-IN')}
          </span>
        )}
      </div>
      <ResponsiveContainer width="100%" height={300}>
        <ComposedChart data={chartData} margin={{ top: 4, right: 8, bottom: 4, left: 8 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
          <XAxis
            dataKey="date"
            tickFormatter={fmtDate}
            tick={{ fontSize: 11 }}
            interval="preserveStartEnd"
          />
          <YAxis
            domain={['auto', 'auto']}
            tick={{ fontSize: 11 }}
            tickFormatter={v => `₹${Number(v).toLocaleString('en-IN', { maximumFractionDigits: 0 })}`}
            width={80}
          />
          <Tooltip
            formatter={(v: unknown) => [`₹${Number(v).toLocaleString('en-IN')}`, 'Price']}
            labelFormatter={(d: unknown) => fmtDate(String(d))}
          />

          {/* Green rise zone: trough → peak */}
          {data.troughDate && data.peakDate && (
            <ReferenceArea
              x1={data.troughDate}
              x2={data.peakDate}
              fill="#22c55e"
              fillOpacity={0.08}
              stroke="#22c55e"
              strokeOpacity={0.3}
            />
          )}

          {/* Red dip zone: peak → last candle */}
          {data.peakDate && inDip && (
            <ReferenceArea
              x1={data.peakDate}
              x2={chartData[chartData.length - 1].date}
              fill="#ef4444"
              fillOpacity={0.08}
              stroke="#ef4444"
              strokeOpacity={0.3}
            />
          )}

          {/* Dashed peak line */}
          {data.peakPrice && (
            <ReferenceLine
              y={Number(data.peakPrice)}
              stroke="#ef4444"
              strokeDasharray="6 3"
              label={{ value: 'Peak', position: 'insideTopRight', fontSize: 11, fill: '#ef4444' }}
            />
          )}

          <Line
            type="monotone"
            dataKey="close"
            stroke="#2563eb"
            strokeWidth={2}
            dot={false}
            activeDot={{ r: 4 }}
          />
        </ComposedChart>
      </ResponsiveContainer>
    </div>
  )
}
