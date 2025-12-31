"use client";

import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { ArrowRight, Loader2 } from "lucide-react";
import { useState } from "react";
import { useToast } from "@/hooks/use-toast";

interface PaymentButtonProps {
  planId: string;
  planName: string;
  amount: number;
  disabled?: boolean;
  className?: string;
  children?: React.ReactNode;
}

export default function PaymentButton({
  planId,
  planName,
  amount,
  disabled = false,
  className = "",
  children = "Nâng cấp ngay",
}: PaymentButtonProps) {
  const router = useRouter();
  const { toast } = useToast();
  const [isLoading, setIsLoading] = useState(false);

  const handlePayment = async () => {
    setIsLoading(true);
    
    try {
      // Validate user authentication
      // const token = localStorage.getItem("token");
      // if (!token) {
      //   toast({
      //     title: "Chưa đăng nhập",
      //     description: "Vui lòng đăng nhập để tiếp tục",
      //     variant: "destructive",
      //   });
      //   router.push("/auth/login");
      //   return;
      // }

      // Navigate to payment page with plan details
      const params = new URLSearchParams({
        planId,
        planName,
        amount: amount.toString(),
      });

      router.push(`/payment?${params.toString()}`);
    } catch (error) {
      toast({
        title: "Lỗi",
        description: "Đã có lỗi xảy ra. Vui lòng thử lại sau.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Button
      onClick={handlePayment}
      disabled={disabled || isLoading}
      className={className}
      size="lg"
    >
      {isLoading ? (
        <>
          <Loader2 className="mr-2 h-4 w-4 animate-spin" />
          Đang xử lý...
        </>
      ) : (
        <>
          {children}
          <ArrowRight className="ml-2 h-4 w-4" />
        </>
      )}
    </Button>
  );
}
