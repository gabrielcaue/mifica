export default function InputField({
  label,
  type,
  value,
  onChange,
  required = false,
  name,
  placeholder,
  className = '',
  error,
  autoComplete,
  maxLength,
}) {
  return (
    <div className="mb-4">
      <label className="block text-sm font-medium mb-1">
        {label}
        {required && <span className="text-red-500 ml-1">*</span>}
      </label>
      <input
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        autoComplete={autoComplete}
        maxLength={maxLength}
        required={required}
        aria-required={required}
        aria-invalid={Boolean(error)}
        className={className || 'w-full p-2 border rounded'}
      />
      {error && <p className="text-xs mt-1 text-red-500">{error}</p>}
    </div>
  );
}
