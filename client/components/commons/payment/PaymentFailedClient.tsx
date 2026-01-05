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
import {
  XCircle,
  RefreshCcw,
  ArrowLeft,
  AlertTriangle,
  MessageCircle,
} from "lucide-react";

export default function PaymentFailedClient() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [countdown, setCountdown] = useState(15);

  // Get error details from query params
  const error = searchParams.get("error");
  const orderId = searchParams.get("orderId");
  const paymentId = searchParams.get("paymentId");
  const txnRef = searchParams.get("txnRef");
  const paymentMethod = searchParams.get("paymentMethod");
  const planTitle = searchParams.get("planTitle");
  const amount = searchParams.get("amount");

  // Determine transaction ID based on payment method
  const transactionId = orderId || paymentId || txnRef;

  useEffect(() => {
    // Countdown for auto redirect
    if (countdown > 0) {
      const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
      return () => clearTimeout(timer);
    } else {
      router.push("/plans");
    }
  }, [countdown, router]);

  const getErrorMessage = (errorCode: string | null) => {
    switch (errorCode) {
      case "insufficient_funds":
        return "Số dư trong tài khoản của bạn không đủ để thực hiện giao dịch này.";
      case "card_declined":
        return "Thẻ của bạn đã bị từ chối. Vui lòng liên hệ ngân hàng của bạn.";
      case "expired_card":
        return "Thẻ của bạn đã hết hạn. Vui lòng sử dụng thẻ khác.";
      case "invalid_card":
        return "Thông tin thẻ không hợp lệ. Vui lòng kiểm tra lại.";
      case "transaction_timeout":
        return "Giao dịch đã hết thời gian chờ. Vui lòng thử lại.";
      case "bank_error":
        return "Đã xảy ra lỗi từ phía ngân hàng. Vui lòng thử lại sau.";
      case "cancelled":
        return "Bạn đã hủy giao dịch thanh toán.";
      default:
        return "Đã có lỗi xảy ra trong quá trình xử lý thanh toán. Vui lòng thử lại sau.";
    }
  };

  const handleRetry = () => {
    const params = new URLSearchParams();
    if (planTitle) params.set("planTitle", planTitle);
    if (amount) params.set("amount", amount);

    router.push(`/payment?${params.toString()}`);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-red-50 via-orange-50 to-yellow-50 dark:from-gray-900 dark:via-red-950 dark:to-gray-900 flex items-center justify-center p-4">
      <div className="max-w-2xl w-full space-y-6">
        {/* Error Icon */}
        <div className="flex justify-center">
          <div className="relative">
            <div className="absolute inset-0 bg-red-500 rounded-full blur-2xl opacity-20 animate-pulse"></div>
            <div className="relative bg-white dark:bg-gray-800 rounded-full p-6 shadow-2xl">
              <XCircle className="h-20 w-20 text-red-500" />
            </div>
          </div>
        </div>

        {/* Main Card */}
        <Card className="border-2 border-red-200 dark:border-red-800 shadow-xl">
          <CardHeader className="text-center space-y-2">
            <CardTitle className="text-3xl font-bold text-red-600 dark:text-red-400 flex items-center justify-center gap-2">
              <AlertTriangle className="h-8 w-8" />
              Thanh toán thất bại
            </CardTitle>
            <CardDescription className="text-lg">
              Rất tiếc, giao dịch của bạn không thể hoàn thành
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            {/* Error Message */}
            <div className="bg-red-50 dark:bg-red-950 border-2 border-red-200 dark:border-red-800 rounded-lg p-4">
              <div className="flex gap-3">
                <AlertTriangle className="h-5 w-5 text-red-500 flex-shrink-0 mt-0.5" />
                <div>
                  <h3 className="font-semibold text-red-900 dark:text-red-100 mb-1">
                    Lý do thất bại
                  </h3>
                  <p className="text-sm text-red-800 dark:text-red-200">
                    {getErrorMessage(error)}
                  </p>
                </div>
              </div>
            </div>

            {/* Transaction Details */}
            {(transactionId || planTitle || amount) && (
              <div className="bg-gray-50 dark:bg-gray-900 rounded-lg p-6 space-y-3">
                <h3 className="font-semibold text-lg mb-4">
                  Chi tiết giao dịch
                </h3>

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
                    <span className="text-muted-foreground">Gói đã chọn</span>
                    <span className="font-semibold">{planTitle}</span>
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
                  <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full bg-red-100 dark:bg-red-900 text-red-700 dark:text-red-300 font-semibold">
                    <XCircle className="h-4 w-4" />
                    Thất bại
                  </span>
                </div>

                <div className="flex justify-between items-center">
                  <span className="text-muted-foreground">Thời gian</span>
                  <span className="font-medium">
                    {new Date().toLocaleString("vi-VN")}
                  </span>
                </div>
              </div>
            )}

            {/* Troubleshooting Tips */}
            <div className="space-y-3">
              <h3 className="font-semibold text-lg">Một số gợi ý:</h3>
              <ul className="space-y-2">
                {[
                  "Kiểm tra lại số dư tài khoản hoặc hạn mức thẻ",
                  "Đảm bảo thông tin thẻ được nhập chính xác",
                  "Thử lại với một phương thức thanh toán khác",
                  "Liên hệ ngân hàng để kiểm tra giao dịch",
                  "Thử lại sau vài phút",
                ].map((tip, index) => (
                  <li key={index} className="flex items-start gap-2">
                    <div className="w-5 h-5 rounded-full bg-blue-100 dark:bg-blue-900 flex items-center justify-center flex-shrink-0 mt-0.5">
                      <span className="text-xs font-bold text-blue-600 dark:text-blue-400">
                        {index + 1}
                      </span>
                    </div>
                    <span>{tip}</span>
                  </li>
                ))}
              </ul>
            </div>

            {/* Actions */}
            <div className="space-y-3 pt-4">
              <Button
                onClick={handleRetry}
                size="lg"
                className="w-full bg-gradient-to-r from-blue-600 to-cyan-600 hover:from-blue-700 hover:to-cyan-700"
              >
                <RefreshCcw className="mr-2 h-5 w-5" />
                Thử lại thanh toán
              </Button>

              <Button
                onClick={() => router.push("/plans")}
                variant="outline"
                size="lg"
                className="w-full"
              >
                <ArrowLeft className="mr-2 h-5 w-5" />
                Quay lại chọn gói
              </Button>
            </div>

            {/* Auto Redirect */}
            <div className="text-center pt-4 border-t">
              <p className="text-sm text-muted-foreground">
                Tự động chuyển về trang chọn gói trong{" "}
                <span className="font-bold text-red-600 dark:text-red-400">
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
              <MessageCircle className="h-6 w-6 text-blue-600 dark:text-blue-400 flex-shrink-0" />
              <div>
                <h3 className="font-semibold mb-1">Cần hỗ trợ?</h3>
                <p className="text-sm text-muted-foreground mb-3">
                  Nếu bạn tiếp tục gặp vấn đề với thanh toán, vui lòng liên hệ
                  đội ngũ hỗ trợ của chúng tôi. Chúng tôi sẵn sàng giúp bạn
                  24/7.
                </p>
                <div className="flex gap-2">
                  <Button variant="link" className="p-0 h-auto font-semibold">
                    Gửi email hỗ trợ →
                  </Button>
                  <span className="text-muted-foreground">hoặc</span>
                  <Button variant="link" className="p-0 h-auto font-semibold">
                    Chat ngay →
                  </Button>
                </div>
              </div>
            </div>
          </CardContent>
        </Card> */}
      </div>
    </div>
  );
}
