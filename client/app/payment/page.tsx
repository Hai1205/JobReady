"use client";

import { useState, Suspense, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Label } from "@/components/ui/label";
import { Loader2, ArrowLeft, CreditCard, Wallet } from "lucide-react";
import { EPaymentMethod } from "@/types/enum";
import { useToast } from "@/hooks/use-toast";
import { usePaymentStore } from "@/stores/paymentStore";
import { useAuthStore } from "@/stores/authStore";

function PaymentContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { toast } = useToast();

  const planId = searchParams.get("planId");
  const planName = searchParams.get("planName");
  const amount = searchParams.get("amount");

  const userAuth = useAuthStore((state) => state.userAuth);
  const createMoMoPayment = usePaymentStore((state) => state.createMoMoPayment);
  const createVnPayPayment = usePaymentStore(
    (state) => state.createVnPayPayment
  );
  const createPayPalPayment = usePaymentStore(
    (state) => state.createPayPalPayment
  );

  const [selectedMethod, setSelectedMethod] = useState<EPaymentMethod>(
    EPaymentMethod.MOMO
  );
  const [isProcessing, setIsProcessing] = useState(false);

  useEffect(() => {
    // Check if user is logged in
    if (!userAuth) {
      toast({
        title: "Vui lòng đăng nhập",
        description: "Bạn cần đăng nhập để thực hiện thanh toán",
        variant: "destructive",
      });
      router.push("/auth/login");
    }
  }, [userAuth, router, toast]);

  const paymentMethods = [
    {
      id: EPaymentMethod.MOMO,
      name: "MoMo",
      description: "Thanh toán qua ví điện tử MoMo",
      icon: "💰",
      color: "bg-pink-50 border-pink-200 hover:border-pink-400",
    },
    {
      id: EPaymentMethod.VNPAY,
      name: "VNPay",
      description: "Thanh toán qua cổng VNPay",
      icon: "💳",
      color: "bg-blue-50 border-blue-200 hover:border-blue-400",
    },
    {
      id: EPaymentMethod.PAYPAL,
      name: "PayPal",
      description: "Thanh toán quốc tế qua PayPal",
      icon: "🌐",
      color: "bg-indigo-50 border-indigo-200 hover:border-indigo-400",
    },
  ];

  const handlePayment = async () => {
    if (!planId || !amount || !userAuth) {
      toast({
        title: "Lỗi",
        description:
          "Thông tin thanh toán không hợp lệ hoặc bạn chưa đăng nhập",
        variant: "destructive",
      });
      return;
    }

    setIsProcessing(true);

    try {
      let response;

      // Call appropriate payment method based on user selection
      switch (selectedMethod) {
        case EPaymentMethod.MOMO:
          response = await createMoMoPayment(userAuth.id, planId);
          break;
        case EPaymentMethod.VNPAY:
          response = await createVnPayPayment(userAuth.id, planId);
          break;
        case EPaymentMethod.PAYPAL:
          response = await createPayPalPayment(userAuth.id, planId);
          break;
        default:
          throw new Error("Phương thức thanh toán không hợp lệ");
      }

      // Check if payment creation was successful
      if (response.data && response.data.success && response.data.paymentUrl) {
        // Redirect to payment gateway
        window.location.href = response.data.paymentUrl;
      } else {
        throw new Error(response.message || "Không thể tạo thanh toán");
      }
    } catch (error: any) {
      toast({
        title: "Lỗi thanh toán",
        description:
          error.message ||
          error.response?.data?.message ||
          "Đã có lỗi xảy ra khi xử lý thanh toán",
        variant: "destructive",
      });
      setIsProcessing(false);
    }
  };

  if (!planId || !amount) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Card className="w-full max-w-md">
          <CardHeader>
            <CardTitle>Lỗi</CardTitle>
            <CardDescription>Thông tin thanh toán không hợp lệ</CardDescription>
          </CardHeader>
          <CardContent>
            <Button onClick={() => router.push("/plans")} className="w-full">
              <ArrowLeft className="mr-2 h-4 w-4" />
              Quay lại chọn gói
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-900 dark:to-gray-800 py-12 px-4">
      <div className="max-w-3xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <Button
            variant="ghost"
            onClick={() => router.back()}
            className="mb-4"
          >
            <ArrowLeft className="mr-2 h-4 w-4" />
            Quay lại
          </Button>
          <h1 className="text-3xl font-bold mb-2">Thanh toán</h1>
          <p className="text-muted-foreground">
            Chọn phương thức thanh toán phù hợp với bạn
          </p>
        </div>

        <div className="grid gap-6 md:grid-cols-3">
          {/* Payment Methods */}
          <Card className="md:col-span-2">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <CreditCard className="h-5 w-5" />
                Phương thức thanh toán
              </CardTitle>
              <CardDescription>
                Chọn một trong các phương thức thanh toán dưới đây
              </CardDescription>
            </CardHeader>
            <CardContent>
              <RadioGroup
                value={selectedMethod}
                onValueChange={(value) =>
                  setSelectedMethod(value as EPaymentMethod)
                }
                className="space-y-3"
              >
                {paymentMethods.map((method) => (
                  <div key={method.id} className="relative">
                    <RadioGroupItem
                      value={method.id}
                      id={method.id}
                      className="peer sr-only"
                    />
                    <Label
                      htmlFor={method.id}
                      className={`flex items-center gap-4 p-4 rounded-lg border-2 cursor-pointer transition-all ${
                        method.color
                      } ${
                        selectedMethod === method.id
                          ? "ring-2 ring-primary ring-offset-2"
                          : ""
                      }`}
                    >
                      <span className="text-3xl">{method.icon}</span>
                      <div className="flex-1">
                        <div className="font-semibold text-lg">
                          {method.name}
                        </div>
                        <div className="text-sm text-muted-foreground">
                          {method.description}
                        </div>
                      </div>
                      {selectedMethod === method.id && (
                        <div className="w-5 h-5 rounded-full bg-primary flex items-center justify-center">
                          <div className="w-2 h-2 rounded-full bg-white" />
                        </div>
                      )}
                    </Label>
                  </div>
                ))}
              </RadioGroup>
            </CardContent>
          </Card>

          {/* Order Summary */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Wallet className="h-5 w-5" />
                Tóm tắt đơn hàng
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <div className="text-sm text-muted-foreground mb-1">
                  Gói đăng ký
                </div>
                <div className="font-semibold text-lg">
                  {planName || "Premium Plan"}
                </div>
              </div>

              <div className="border-t pt-4">
                <div className="flex justify-between mb-2">
                  <span className="text-muted-foreground">Giá gốc</span>
                  <span className="font-medium">
                    {parseFloat(amount).toLocaleString("vi-VN")} VNĐ
                  </span>
                </div>
                <div className="flex justify-between mb-2">
                  <span className="text-muted-foreground">Phí xử lý</span>
                  <span className="font-medium">0 VNĐ</span>
                </div>
              </div>

              <div className="border-t pt-4">
                <div className="flex justify-between items-center">
                  <span className="text-lg font-semibold">Tổng cộng</span>
                  <span className="text-2xl font-bold text-primary">
                    {parseFloat(amount).toLocaleString("vi-VN")} VNĐ
                  </span>
                </div>
              </div>

              <Button
                onClick={handlePayment}
                disabled={isProcessing}
                className="w-full"
                size="lg"
              >
                {isProcessing ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Đang xử lý...
                  </>
                ) : (
                  "Thanh toán ngay"
                )}
              </Button>

              <p className="text-xs text-muted-foreground text-center">
                Bằng việc thanh toán, bạn đồng ý với{" "}
                <a
                  href="/terms-of-service"
                  className="underline hover:text-primary"
                >
                  Điều khoản dịch vụ
                </a>{" "}
                của chúng tôi
              </p>
            </CardContent>
          </Card>
        </div>

        {/* Security Notice */}
        <Card className="mt-6 bg-blue-50 dark:bg-blue-950 border-blue-200 dark:border-blue-800">
          <CardContent className="pt-6">
            <div className="flex gap-3">
              <div className="text-2xl">🔒</div>
              <div>
                <h3 className="font-semibold mb-1">Thanh toán an toàn</h3>
                <p className="text-sm text-muted-foreground">
                  Tất cả giao dịch được mã hóa và bảo mật bởi các cổng thanh
                  toán uy tín. Thông tin của bạn được bảo vệ tuyệt đối.
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

export default function PaymentPage() {
  return (
    <Suspense
      fallback={
        <div className="min-h-screen flex items-center justify-center">
          <Card className="w-full max-w-md">
            <CardContent className="flex flex-col items-center space-y-4 pt-6">
              <Loader2 className="h-16 w-16 text-blue-500 animate-spin" />
              <p className="text-center text-muted-foreground">Đang tải...</p>
            </CardContent>
          </Card>
        </div>
      }
    >
      <PaymentContent />
    </Suspense>
  );
}
