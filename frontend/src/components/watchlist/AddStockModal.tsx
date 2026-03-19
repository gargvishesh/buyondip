import { useState, useRef, useEffect } from 'react'
import { searchStocks } from '../../api/stocks'
import { AddStockRequest } from '../../api/watchlist'

interface Props {
  onAdd: (req: AddStockRequest) => Promise<void>
  onClose: () => void
}

const SECTORS = ['IT', 'Bank', 'Pharma', 'Auto', 'Energy', 'FMCG', 'Metal', 'Realty', 'Other']

export default function AddStockModal({ onAdd, onClose }: Props) {
  const [symbol, setSymbol] = useState('')
  const [companyName, setCompanyName] = useState('')
  const [sector, setSector] = useState('')
  const [suggestions, setSuggestions] = useState<{ symbol: string; name: string }[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const ref = useRef<HTMLDivElement>(null)

  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) onClose()
    }
    document.addEventListener('mousedown', handler)
    return () => document.removeEventListener('mousedown', handler)
  }, [onClose])

  const search = async (q: string) => {
    if (q.length < 2) { setSuggestions([]); return }
    try {
      const res = await searchStocks(q)
      setSuggestions(res.slice(0, 6))
    } catch {}
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!symbol || !companyName) { setError('Symbol and company name required'); return }
    setLoading(true)
    try {
      await onAdd({ symbol: symbol.toUpperCase(), companyName, sector })
      onClose()
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to add stock')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div ref={ref} className="bg-white rounded-2xl shadow-xl w-full max-w-md p-6">
        <h2 className="text-lg font-bold text-gray-900 mb-4">Add Stock to Watchlist</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="relative">
            <label className="block text-sm font-medium text-gray-700 mb-1">Symbol</label>
            <input
              value={symbol}
              onChange={e => { setSymbol(e.target.value); search(e.target.value) }}
              placeholder="RELIANCE, TCS, INFY..."
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            {suggestions.length > 0 && (
              <ul className="absolute z-10 w-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg max-h-48 overflow-y-auto">
                {suggestions.map(s => (
                  <li
                    key={s.symbol}
                    className="px-3 py-2 hover:bg-blue-50 cursor-pointer text-sm"
                    onClick={() => {
                      setSymbol(s.symbol.replace('.NS', '').replace('.BO', ''))
                      setCompanyName(s.name)
                      setSuggestions([])
                    }}
                  >
                    <span className="font-medium">{s.symbol}</span>
                    <span className="text-gray-500 ml-2">{s.name}</span>
                  </li>
                ))}
              </ul>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Company Name</label>
            <input
              value={companyName}
              onChange={e => setCompanyName(e.target.value)}
              placeholder="Reliance Industries Ltd"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Sector</label>
            <select
              value={sector}
              onChange={e => setSector(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Select sector</option>
              {SECTORS.map(s => <option key={s} value={s}>{s}</option>)}
            </select>
          </div>

          {error && <p className="text-sm text-red-600">{error}</p>}

          <div className="flex gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 border border-gray-300 text-gray-700 rounded-lg py-2 text-sm font-medium hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 bg-blue-600 text-white rounded-lg py-2 text-sm font-medium hover:bg-blue-700 disabled:opacity-50 transition-colors"
            >
              {loading ? 'Adding…' : 'Add Stock'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
