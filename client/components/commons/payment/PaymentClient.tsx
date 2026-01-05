"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
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

export default function PaymentClient() {
  const router = useRouter();
  const { toast } = useToast();

  const [paymentInfo, setPaymentInfo] = useState<{
    planId: string;
    planTitle: string;
    amount: number;
  } | null>(null);

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
    // Read payment info from localStorage
    const storedInfo = localStorage.getItem("paymentInfo");
    if (storedInfo) {
      try {
        const parsed = JSON.parse(storedInfo);
        setPaymentInfo(parsed);
      } catch (error) {
        console.error("Error parsing payment info:", error);
      }
    }
  }, []);

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
    if (
      !paymentInfo ||
      !paymentInfo.planId ||
      !paymentInfo.amount ||
      !userAuth
    ) {
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
          response = await createMoMoPayment(userAuth.id, paymentInfo.planId);
          break;
        case EPaymentMethod.VNPAY:
          response = await createVnPayPayment(userAuth.id, paymentInfo.planId);
          break;
        case EPaymentMethod.PAYPAL:
          response = await createPayPalPayment(userAuth.id, paymentInfo.planId);
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

  if (!paymentInfo || !paymentInfo.planId || !paymentInfo.amount) {
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
    <div className="min-h-screen flex">
      {/* Back Button - Absolute positioning */}
      <Button
        variant="ghost"
        onClick={() => router.back()}
        className="absolute top-4 left-4 z-10"
      >
        <ArrowLeft className="mr-2 h-4 w-4" />
        Quay lại
      </Button>

      {/* Left Side - Order Summary (Dark) */}
      <div className="hidden lg:flex lg:w-[45%] bg-gradient-to-br from-gray-900 to-gray-800 text-white p-12 flex-col justify-center">
        <div className="max-w-md mx-auto space-y-8">
          <div className="space-y-2">
            <h1 className="text-4xl font-bold">
              Đăng ký gói {paymentInfo.planTitle}
            </h1>
            <p className="text-5xl font-bold mt-4">
              {(paymentInfo.amount / 1000).toLocaleString("vi-VN")}k VNĐ
              <span className="text-xl font-normal text-gray-400">
                {" "}
                / tháng
              </span>
            </p>
          </div>

          <div className="space-y-4 pt-8">
            <div className="flex items-start gap-3">
              <div className="w-8 h-8 rounded-full bg-blue-500/20 flex items-center justify-center flex-shrink-0 mt-1">
                <svg
                  className="w-5 h-5 text-blue-400"
                  fill="currentColor"
                  viewBox="0 0 20 20"
                >
                  <path
                    fillRule="evenodd"
                    d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                    clipRule="evenodd"
                  />
                </svg>
              </div>
              <div>
                <p className="font-medium">{paymentInfo.planTitle}</p>
                <p className="text-sm text-gray-400">
                  Đăng ký hàng tháng • Hủy bất cứ lúc nào
                </p>
              </div>
            </div>
          </div>

          <div className="border-t border-gray-700 pt-6 space-y-3">
            <div className="flex justify-between text-sm">
              <span className="text-gray-400">Tổng phụ</span>
              <span>{paymentInfo.amount.toLocaleString("vi-VN")} VNĐ</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-400">Thuế</span>
              <span>0 VNĐ</span>
            </div>
            <div className="flex justify-between text-lg font-bold border-t border-gray-700 pt-3">
              <span>Tổng cộng</span>
              <span>{paymentInfo.amount.toLocaleString("vi-VN")} VNĐ</span>
            </div>
          </div>
        </div>
      </div>

      {/* Right Side - Payment Form (Light) */}
      <div className="flex-1 bg-white dark:bg-gray-950 p-8 lg:p-12 overflow-y-auto">
        <div className="max-w-xl mx-auto space-y-8">
          {/* Mobile Header */}
          <div className="lg:hidden space-y-2 mb-8 pt-12">
            <h1 className="text-2xl font-bold">
              Đăng ký gói {paymentInfo.planTitle}
            </h1>
            <p className="text-3xl font-bold text-primary">
              {(paymentInfo.amount / 1000).toLocaleString("vi-VN")}k VNĐ
              <span className="text-lg font-normal text-muted-foreground">
                {" "}
                / tháng
              </span>
            </p>
          </div>

          {/* Contact Information */}
          <div className="space-y-4">
            <h2 className="text-xl font-semibold">Thông tin liên hệ</h2>
            <div className="space-y-2">
              <label className="text-sm font-medium text-muted-foreground">
                Email
              </label>
              <input
                type="email"
                value={userAuth?.email || ""}
                disabled
                className="w-full px-4 py-3 border border-gray-300 dark:border-gray-700 rounded-lg bg-gray-50 dark:bg-gray-900 text-sm"
              />
            </div>
          </div>

          {/* Payment Method */}
          <div className="space-y-4">
            <h2 className="text-xl font-semibold">Phương thức thanh toán</h2>
            <RadioGroup
              value={selectedMethod}
              onValueChange={(value) =>
                setSelectedMethod(value as EPaymentMethod)
              }
              className="space-y-3"
            >
              {paymentMethods.map((method) => (
                <div key={method.id}>
                  <RadioGroupItem
                    value={method.id}
                    id={method.id}
                    className="peer sr-only"
                  />
                  <Label
                    htmlFor={method.id}
                    className="flex items-center justify-between p-4 border-2 border-gray-200 dark:border-gray-800 rounded-lg cursor-pointer hover:border-gray-300 dark:hover:border-gray-700 peer-data-[state=checked]:border-primary peer-data-[state=checked]:bg-primary/5 transition-all"
                  >
                    <div className="flex items-center gap-3">
                      <span className="text-2xl">{method.icon}</span>
                      <span className="font-medium">{method.name}</span>
                    </div>
                    <div className="w-5 h-5 rounded-full border-2 border-gray-300 dark:border-gray-700 peer-data-[state=checked]:border-primary peer-data-[state=checked]:bg-primary flex items-center justify-center">
                      {selectedMethod === method.id && (
                        <div className="w-2 h-2 rounded-full bg-white" />
                      )}
                    </div>
                  </Label>
                </div>
              ))}
            </RadioGroup>
          </div>

          {/* Subscribe Button */}
          <Button
            onClick={handlePayment}
            disabled={isProcessing}
            size="lg"
            className="w-full h-12 text-base font-semibold"
          >
            {isProcessing ? (
              <>
                <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                Đang xử lý...
              </>
            ) : (
              "Thanh toán"
            )}
          </Button>

          {/* Terms */}
          <p className="text-xs text-center text-muted-foreground">
            Bằng việc thanh toán, bạn đồng ý với{" "}
            <a
              href="/terms-of-service"
              className="underline hover:text-primary"
            >
              Điều khoản dịch vụ
            </a>{" "}
            và{" "}
            <a href="/privacy-policy" className="underline hover:text-primary">
              Chính sách bảo mật
            </a>
          </p>
        </div>
      </div>
    </div>
  );
}
