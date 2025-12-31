interface CurrencyProps {
  amount: number;
  currency?: string;
  locale?: string;
  className?: string;
}

export default function Currency({
  amount,
  currency = "VNĐ",
  locale = "vi-VN",
  className = "",
}: CurrencyProps) {
  const formattedAmount = amount.toLocaleString(locale);

  return (
    <span className={className}>
      {formattedAmount} {currency}
    </span>
  );
}
