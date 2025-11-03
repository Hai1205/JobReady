"use client";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Alert, AlertDescription } from "@/components/ui/alert";
import type React from "react";
import { useEffect, useRef, useState } from "react";
import { useRouter } from "next/navigation";
import { toast } from "react-toastify";
import { useAuthStore } from "@/stores/authStore";
import Link from "next/link";
import { Loader2, ArrowLeft, Shield, Clock } from "lucide-react";

const VerificationPage: React.FC = () => {
  const { isLoading, verifyOTP } = useAuthStore();

  const router = useRouter();
  const [identifier, setIdentifier] = useState("");
  const [isActivation, setIsActivation] = useState(false);
  const [otp, setOtp] = useState<string[]>(Array(6).fill(""));

  const [isExpired, setIsExpired] = useState(false);
  const [timeLeft, setTimeLeft] = useState(300); // 5 minutes in seconds
  const [isClient, setIsClient] = useState(false);
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

  // Get email from URL params on client side only
  useEffect(() => {
    setIsClient(true);
    const urlParams = new URLSearchParams(window.location.search);
    const isActivationParam = urlParams.get("isActivation") === "true";
    const identifierParam = urlParams.get("identifier");

    if (identifierParam) {
      setIdentifier(identifierParam);
    }

    if (identifierParam) {
      setIsActivation(isActivationParam);
    }
  }, []);

  useEffect(() => {
    if (timeLeft <= 0) {
      setIsExpired(true);
      return;
    }

    const timer = setInterval(() => {
      setTimeLeft((prevTime) => prevTime - 1);
    }, 1000);

    return () => clearInterval(timer);
  }, [timeLeft]);

  const handleChange = (index: number, value: string) => {
    if (!/^\d*$/.test(value)) return;

    const newOtp = [...otp];
    newOtp[index] = value.substring(0, 1);
    setOtp(newOtp);

    // Auto-focus next input
    if (value && index < 5) {
      inputRefs.current[index + 1]?.focus();
    }
  };

  const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
    e.preventDefault();

    const pastedData = e.clipboardData.getData("text/plain").trim();

    // Check if pasted content is a 6-digit number
    if (/^\d{6}$/.test(pastedData)) {
      const newOtp = pastedData.split("");
      setOtp(newOtp);

      // Focus the last input
      inputRefs.current[5]?.focus();
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent) => {
    if (e.key === "Backspace" && otp[index] === "" && index > 0) {
      const prevInput = document.getElementById(`digit-${index - 1}`);
      if (prevInput) prevInput.focus();
    }
  };

  const validate = () => {
    if (otp.some((digit) => digit === "")) {
      return false;
    }
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) {
      return;
    }

    const res = await verifyOTP(identifier, otp.join(""), isActivation);

    if (!res) {
      setOtp(Array(6).fill(""));
      return;
    }

    if (isExpired) {
      setOtp(Array(6).fill(""));
      return;
    }

    if (res?.status && res.status !== 200) {
      return;
    }

    if (!isActivation) {
      router.push(`/auth/reset-password/?identifier=${encodeURIComponent(identifier)}`);
    } else {
      toast.success("Xác thực tài khoản thành công");
      router.push("/auth/login");
    }
  };

  const handleResend = async () => {
    const { sendOTP } = useAuthStore.getState();
    const result = await sendOTP(identifier);

    if (result) {
      toast.success("Mã OTP đã được gửi lại");
      setOtp(Array(6).fill(""));
      setTimeLeft(300);
      setIsExpired(false);
    }
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs < 10 ? "0" : ""}${secs}`;
  };

  // Don't render anything on server side
  if (!isClient) {
    return null;
  }

  return (
    <div className="space-y-6">
      <div className="space-y-2 text-center">
        <div className="flex justify-center mb-4">
          <div className="h-12 w-12 rounded-full bg-primary/10 flex items-center justify-center">
            <Shield className="h-6 w-6 text-primary" />
          </div>
        </div>
        <h1 className="text-2xl font-bold tracking-tight">Nhập mã xác thực</h1>
        <p className="text-muted-foreground">
          Chúng tôi đã gửi mã OTP gồm 6 chữ số về tài khoản <strong>{identifier}</strong>
        </p>
        <p className="text-sm text-muted-foreground">
          Vui lòng nhập mã để{" "}
          {isActivation ? "đặt lại mật khẩu" : "xác thực tài khoản"}
        </p>
      </div>

      {!isExpired && (
        <div className="flex items-center justify-center gap-2 p-3 bg-primary/5 rounded-lg border">
          <Clock className="h-4 w-4 text-primary" />
          <span className="text-sm font-medium">
            Mã hết hạn trong: {formatTime(timeLeft)}
          </span>
        </div>
      )}

      {isExpired && (
        <Alert variant="destructive">
          <AlertDescription>
            Mã OTP đã hết hạn. Vui lòng yêu cầu gửi lại mã mới.
          </AlertDescription>
        </Alert>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="flex justify-center gap-2">
          {[0, 1, 2, 3, 4, 5].map((index) => (
            <Input
              key={index}
              ref={(el) => {
                inputRefs.current[index] = el;
              }}
              type="text"
              value={otp[index]}
              onChange={(e) => handleChange(index, e.target.value)}
              onKeyDown={(e) => handleKeyDown(index, e)}
              onPaste={index === 0 ? handlePaste : undefined}
              className={`w-12 h-12 text-center text-xl font-bold ${
                isExpired ? "opacity-50" : ""
              }`}
              maxLength={1}
              disabled={isExpired}
            />
          ))}
        </div>

        <Button
          type="submit"
          className="w-full"
          disabled={isLoading || isExpired || !validate()}
        >
          {isLoading ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Đang xác thực...
            </>
          ) : (
            "Xác thực"
          )}
        </Button>
      </form>

      <div className="space-y-4 text-center">
        <p className="text-sm text-muted-foreground">
          Không nhận được mã?{" "}
          <button
            onClick={handleResend}
            className="text-primary hover:underline font-medium"
            disabled={isLoading}
          >
            Gửi lại mã
          </button>
        </p>

        <Link
          href="/auth/login"
          className="inline-flex items-center gap-2 text-sm text-primary hover:underline"
        >
          <ArrowLeft className="h-3 w-3" />
          Quay lại trang đăng nhập
        </Link>
      </div>
    </div>
  );
};

export default VerificationPage;
