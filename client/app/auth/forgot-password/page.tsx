"use client";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Alert, AlertDescription } from "@/components/ui/alert";
import type React from "react";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { toast } from "react-toastify";
import { useAuthStore } from "@/stores/authStore";
import Link from "next/link";
import { Loader2, Mail, ArrowLeft, Send } from "lucide-react";

const ForgotPasswordPage: React.FC = () => {
  const router = useRouter();

  const { isLoading, sendOTP } = useAuthStore();
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setEmail(e.target.value);
    if (error) setError("");
  };

  const validate = () => {
    if (!email.trim()) {
      setError("Email is required");
      return false;
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      setError("Please enter a valid email");
      return false;
    }
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) {
      return;
    }

    const res = await sendOTP(email);

    if (!res) {
      return;
    }

    toast.success("Đã gửi mã OTP về email của bạn");

    router.push(
      `/auth/verification?email=${encodeURIComponent(
        email
      )}&isPasswordReset=true`
    );
  };

  return (
    <div className="space-y-6">
      <div className="space-y-2 text-center">
        <h1 className="text-2xl font-bold tracking-tight">Quên mật khẩu</h1>
        <p className="text-muted-foreground">
          Nhập email của bạn và chúng tôi sẽ gửi mã OTP để giúp bạn cài lại mật
          khẩu mới
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="space-y-2">
          <Label htmlFor="email">Email</Label>
          <div className="relative">
            <Mail className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              id="email"
              type="email"
              name="email"
              placeholder="Nhập email của bạn"
              value={email}
              onChange={handleChange}
              className="pl-10"
            />
          </div>
          {error && (
            <Alert variant="destructive">
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}
        </div>

        <Button type="submit" className="w-full" disabled={isLoading}>
          {isLoading ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Đang gửi...
            </>
          ) : (
            <>
              <Send className="mr-2 h-4 w-4" />
              Gửi mã OTP
            </>
          )}
        </Button>
      </form>

      <div className="text-center">
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

export default ForgotPasswordPage;
