"use client";

import { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { CheckCircle2, Download, ArrowRight, Sparkles } from "lucide-react";
import confetti from "canvas-confetti";

export default function PaymentSuccessClient() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [countdown, setCountdown] = useState(10);

  // Get transaction details from query params
  const orderId = searchParams.get("orderId");
  const paymentId = searchParams.get("paymentId");
  const txnRef = searchParams.get("txnRef");
  const paymentMethod = searchParams.get("paymentMethod");
  const planTitle = searchParams.get("planTitle");
  const amount = searchParams.get("amount");

  // Determine transaction ID based on payment method
  const transactionId = orderId || paymentId || txnRef || "N/A";

  useEffect(() => {
    // Trigger confetti animation
    const duration = 3 * 1000;
    const animationEnd = Date.now() + duration;
    const defaults = { startVelocity: 30, spread: 360, ticks: 60, zIndex: 0 };

    function randomInRange(min: number, max: number) {
      return Math.random() * (max - min) + min;
    }

    const interval: any = setInterval(function () {
      const timeLeft = animationEnd - Date.now();

      if (timeLeft <= 0) {
        return clearInterval(interval);
      }

      const particleCount = 50 * (timeLeft / duration);
      confetti({
        ...defaults,
        particleCount,
        origin: { x: randomInRange(0.1, 0.3), y: Math.random() - 0.2 },
      });
      confetti({
        ...defaults,
        particleCount,
        origin: { x: randomInRange(0.7, 0.9), y: Math.random() - 0.2 },
      });
    }, 250);

    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    // Countdown for auto redirect
    if (countdown > 0) {
      const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
      return () => clearTimeout(timer);
    } else {
      router.push("/my-cvs");
    }
  }, [countdown, router]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 dark:from-gray-900 dark:via-blue-950 dark:to-gray-900 flex items-center justify-center p-4">
      <div className="max-w-2xl w-full space-y-6">
        {/* Success Icon */}
        <div className="flex justify-center">
          <div className="relative">
            <div className="absolute inset-0 bg-blue-500 rounded-full blur-2xl opacity-20 animate-pulse"></div>
            <div className="relative bg-white dark:bg-gray-800 rounded-full p-6 shadow-2xl">
              <CheckCircle2 className="h-20 w-20 text-blue-500" />
            </div>
          </div>
        </div>

        {/* Main Card */}
        <Card className="border-2 border-blue-200 dark:border-blue-800 shadow-xl">
          <CardHeader className="text-center space-y-2">
            <CardTitle className="text-3xl font-bold text-blue-600 dark:text-blue-400 flex items-center justify-center gap-2">
              <Sparkles className="h-8 w-8" />
              Thanh toán thành công!
            </CardTitle>
            <CardDescription className="text-lg">
              Cảm ơn bạn đã nâng cấp tài khoản. Bạn đã mở khóa nhiều tính năng
              tuyệt vời!
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            {/* Transaction Details */}
            <div className="bg-gradient-to-br from-blue-50 to-indigo-50 dark:from-blue-950 dark:to-gray-900 rounded-lg p-6 space-y-4">
              <h3 className="font-semibold text-lg mb-4">Chi tiết giao dịch</h3>

              <div className="grid gap-3">
                {transactionId && (
                  <div className="flex justify-between items-center">
                    <span className="text-muted-foreground">Mã giao dịch</span>
                    <span className="font-mono font-semibold">
                      {transactionId}
                    </span>
                  </div>
                )}

                {paymentMethod && (
                  <div className="flex justify-between items-center">
                    <span className="text-muted-foreground">Phương thức</span>
                    <span className="font-semibold uppercase">
                      {paymentMethod}
                    </span>
                  </div>
                )}

                {planTitle && (
                  <div className="flex justify-between items-center">
                    <span className="text-muted-foreground">Gói đã mua</span>
                    <span className="font-semibold text-blue-600 dark:text-blue-400">
                      {planTitle}
                    </span>
                  </div>
                )}

                {amount && (
                  <div className="flex justify-between items-center">
                    <span className="text-muted-foreground">Số tiền</span>
                    <span className="font-bold text-lg">
                      {parseFloat(amount).toLocaleString("vi-VN")} VNĐ
                    </span>
                  </div>
                )}

                <div className="flex justify-between items-center">
                  <span className="text-muted-foreground">Trạng thái</span>
                  <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-300 font-semibold">
                    <CheckCircle2 className="h-4 w-4" />
                    Thành công
                  </span>
                </div>

                <div className="flex justify-between items-center">
                  <span className="text-muted-foreground">Thời gian</span>
                  <span className="font-medium">
                    {new Date().toLocaleString("vi-VN")}
                  </span>
                </div>
              </div>
            </div>

            {/* Benefits */}
            <div className="space-y-3">
              <h3 className="font-semibold text-lg">Quyền lợi của bạn:</h3>
              <ul className="space-y-2">
                {[
                  "Tạo không giới hạn CV chuyên nghiệp",
                  "Truy cập đầy đủ tất cả mẫu CV cao cấp",
                  "AI gợi ý thông minh không giới hạn",
                  "Xuất CV định dạng PDF chất lượng cao",
                  "Hỗ trợ ưu tiên 24/7",
                ].map((benefit, index) => (
                  <li key={index} className="flex items-start gap-2">
                    <CheckCircle2 className="h-5 w-5 text-blue-500 flex-shrink-0 mt-0.5" />
                    <span>{benefit}</span>
                  </li>
                ))}
              </ul>
            </div>

            {/* Actions */}
            <div className="space-y-3 pt-4">
              <Button
                onClick={() => router.push("/my-cvs")}
                size="lg"
                className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700"
              >
                Bắt đầu tạo CV ngay
                <ArrowRight className="ml-2 h-5 w-5" />
              </Button>

              <Button
                onClick={() => window.print()}
                variant="outline"
                size="lg"
                className="w-full"
              >
                <Download className="mr-2 h-5 w-5" />
                Tải hóa đơn
              </Button>
            </div>

            {/* Auto Redirect */}
            <div className="text-center pt-4 border-t">
              <p className="text-sm text-muted-foreground">
                Tự động chuyển hướng trong{" "}
                <span className="font-bold text-blue-600 dark:text-blue-400">
                  {countdown}
                </span>{" "}
                giây...
              </p>
            </div>
          </CardContent>
        </Card>

        {/* Support Card */}
        {/* <Card className="bg-blue-50 dark:bg-blue-950 border-blue-200 dark:border-blue-800">
          <CardContent className="pt-6">
            <div className="flex gap-3">
              <div className="text-2xl">💬</div>
              <div>
                <h3 className="font-semibold mb-1">Cần hỗ trợ?</h3>
                <p className="text-sm text-muted-foreground mb-3">
                  Nếu bạn có bất kỳ câu hỏi nào về giao dịch hoặc tài khoản, đội
                  ngũ hỗ trợ của chúng tôi luôn sẵn sàng giúp đỡ.
                </p>
                <Button variant="link" className="p-0 h-auto font-semibold">
                  Liên hệ hỗ trợ →
                </Button>
              </div>
            </div>
          </CardContent>
        </Card> */}
      </div>
    </div>
  );
}
