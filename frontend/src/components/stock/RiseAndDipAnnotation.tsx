interface Props {
  annotation: string
}

export default function RiseAndDipAnnotation({ annotation }: Props) {
  if (!annotation) return null
  return (
    <div className="flex items-start gap-2 rounded-lg bg-blue-50 border border-blue-200 px-4 py-3 text-sm text-blue-800">
      <span className="mt-0.5 shrink-0">💡</span>
      <span>{annotation}</span>
    </div>
  )
}
