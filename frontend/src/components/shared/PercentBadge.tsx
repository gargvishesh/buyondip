interface PercentBadgeProps {
  value: number
  className?: string
}

export default function PercentBadge({ value, className = '' }: PercentBadgeProps) {
  const isPos = value >= 0
  return (
    <span className={`inline-flex items-center gap-0.5 text-sm font-semibold ${isPos ? 'text-green-600' : 'text-red-600'} ${className}`}>
      {isPos ? '▲' : '▼'} {Math.abs(value).toFixed(2)}%
    </span>
  )
}
