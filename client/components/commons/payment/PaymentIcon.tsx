import { EPaymentMethod } from "@/types/enum";
import Image from "next/image";

interface PaymentIconProps {
  method: EPaymentMethod;
  size?: number;
  className?: string;
}

export default function PaymentIcon({ method, size = 40, className = "" }: PaymentIconProps) {
  const icons = {
    [EPaymentMethod.MOMO]: {
      emoji: "💰",
      alt: "MoMo",
      bgColor: "bg-pink-100 dark:bg-pink-900",
    },
    [EPaymentMethod.VNPAY]: {
      emoji: "💳",
      alt: "VNPay",
      bgColor: "bg-blue-100 dark:bg-blue-900",
    },
    [EPaymentMethod.PAYPAL]: {
      emoji: "🌐",
      alt: "PayPal",
      bgColor: "bg-indigo-100 dark:bg-indigo-900",
    },
  };

  const icon = icons[method];

  return (
    <div
      className={`flex items-center justify-center rounded-lg ${icon.bgColor} ${className}`}
      style={{ width: size, height: size }}
    >
      <span style={{ fontSize: size * 0.6 }}>{icon.emoji}</span>
    </div>
  );
}
