import Badge from '../shared/Badge'

interface DipCauseBadgeProps {
  cause: 'GLOBAL' | 'SECTOR' | 'COMPANY_SPECIFIC'
  message?: string
}

const config = {
  GLOBAL: { variant: 'green' as const, label: 'Global' },
  SECTOR: { variant: 'amber' as const, label: 'Sector' },
  COMPANY_SPECIFIC: { variant: 'red' as const, label: 'Company' },
}

export default function DipCauseBadge({ cause, message }: DipCauseBadgeProps) {
  const { variant, label } = config[cause] || config.COMPANY_SPECIFIC
  return (
    <div className="flex flex-col gap-1">
      <Badge variant={variant}>{label}</Badge>
      {message && <p className="text-xs text-gray-500">{message}</p>}
    </div>
  )
}
