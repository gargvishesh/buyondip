import { Link } from 'react-router-dom'
import { DipAnalysis } from '../../api/dips'
import DipCauseBadge from '../stock/DipCauseBadge'

interface Props {
  alert: DipAnalysis
  showScore?: boolean
}

export default function DipAlertCard({ alert, showScore }: Props) {
  return (
    <Link
      to={`/stock/${alert.symbol}`}
      className="block bg-white border border-gray-200 rounded-xl p-4 hover:shadow-md transition-shadow"
    >
      <div className="flex items-start justify-between mb-2">
        <div>
          <span className="font-bold text-gray-900">{alert.symbol}</span>
          <p className="text-xs text-gray-500 mt-0.5">{alert.companyName}</p>
        </div>
        <div className="flex items-center gap-1.5">
          {showScore && alert.compositeScore != null && (
            <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-semibold bg-indigo-100 text-indigo-700">
              Score: {alert.compositeScore.toFixed(2)}
            </span>
          )}
          <DipCauseBadge cause={alert.cause} />
        </div>
      </div>
      <div className="flex items-baseline gap-4 mt-3">
        <div>
          <p className="text-xs text-gray-400">Current</p>
          <p className="font-semibold text-gray-900">
            ₹{Number(alert.currentPrice).toLocaleString('en-IN')}
          </p>
        </div>
        <div>
          <p className="text-xs text-gray-400">Dip</p>
          <p className="font-semibold text-red-600">{Number(alert.dipPercent).toFixed(1)}%</p>
        </div>
        <div>
          <p className="text-xs text-gray-400">Prior Rise</p>
          <p className="font-semibold text-green-600">+{Number(alert.priorRisePercent).toFixed(1)}%</p>
        </div>
        {showScore && alert.fundamentalScore != null && (
          <div>
            <p className="text-xs text-gray-400">Fund.</p>
            <p className="font-semibold text-indigo-600">{Math.round(alert.fundamentalScore * 100)}%</p>
          </div>
        )}
      </div>
      {alert.annotation && (
        <p className="mt-2 text-xs text-gray-500 italic">{alert.annotation}</p>
      )}
    </Link>
  )
}
