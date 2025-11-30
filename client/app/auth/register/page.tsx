"use client";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Alert, AlertDescription } from "@/components/ui/alert";
import type React from "react";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/authStore";
import Link from "next/link";
import { Loader2, Mail, Lock, Eye, EyeOff } from "lucide-react";
import { toast } from "react-toastify";

const RegisterPage: React.FC = () => {
  const { isLoading, register, sendOTP } = useAuthStore();
  const router = useRouter();

  const [formData, setFormData] = useState({
    fullname: "",
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    // Clear error when user types
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: "" }));
    }
  };

  const validate = () => {
    const newErrors: Record<string, string> = {};

    if (!formData.fullname) {
      newErrors.fullname = "Họ và tên là bắt buộc";
    }

    if (!formData.username) {
      newErrors.username = "Username là bắt buộc";
    }

    if (!formData.email.trim()) {
      newErrors.email = "Email là bắt buộc";
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = "Vui lòng nhập email hợp lệ";
    }

    if (!formData.password) {
      newErrors.password = "Mật khẩu là bắt buộc";
    } else if (formData.password.length < 8) {
      newErrors.password = "Mật khẩu phải có ít nhất 8 ký tự";
    }

    if (!formData.confirmPassword) {
      newErrors.confirmPassword = "Vui lòng xác nhận mật khẩu";
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Mật khẩu không trùng khớp";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) {
      return;
    }

    const response = await register(
      formData.fullname,
      formData.username,
      formData.email,
      formData.password
    );

    console.log("Register response:", response);

    if (response?.status === 200) {
      toast.success("Đăng ký thành công! Vui lòng kiểm tra email để xác nhận.");

      router.push(
        `/auth/verification?identifier=${encodeURIComponent(
          formData.email
        )}&isActivation=true`
      );

      await sendOTP(formData.email);
    }
  };

  return (
    <div className="space-y-6">
      <div className="space-y-2 text-center">
        <h1 className="text-2xl font-bold tracking-tight">Đăng ký tài khoản</h1>
        <p className="text-muted-foreground">
          Tạo tài khoản mới để bắt đầu xây dựng CV với AI
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="space-y-2">
          <Label htmlFor="fullname">fullname</Label>
          <div className="relative">
            <Mail className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              id="fullname"
              name="fullname"
              type="text"
              placeholder="Nhập họ và tên của bạn"
              value={formData.fullname}
              onChange={handleChange}
              className="pl-10"
            />
          </div>
          {errors.fullname && (
            <Alert variant="destructive">
              <AlertDescription>{errors.fullname}</AlertDescription>
            </Alert>
          )}
        </div>

        <div className="space-y-2">
          <Label htmlFor="username">Username</Label>
          <div className="relative">
            <Mail className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              id="username"
              name="username"
              type="text"
              placeholder="Nhập username của bạn"
              value={formData.username}
              onChange={handleChange}
              className="pl-10"
            />
          </div>
          {errors.username && (
            <Alert variant="destructive">
              <AlertDescription>{errors.username}</AlertDescription>
            </Alert>
          )}
        </div>

        <div className="space-y-2">
          <Label htmlFor="email">Email</Label>
          <div className="relative">
            <Mail className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              id="email"
              name="email"
              type="email"
              placeholder="Nhập email của bạn"
              value={formData.email}
              onChange={handleChange}
              className="pl-10"
            />
          </div>
          {errors.email && (
            <Alert variant="destructive">
              <AlertDescription>{errors.email}</AlertDescription>
            </Alert>
          )}
        </div>

        <div className="space-y-2">
          <Label htmlFor="password">Mật khẩu</Label>
          <div className="relative">
            <Lock className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              id="password"
              name="password"
              type={showPassword ? "text" : "password"}
              placeholder="Nhập mật khẩu"
              value={formData.password}
              onChange={handleChange}
              className="pl-10 pr-10"
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-2.5 text-muted-foreground hover:text-foreground"
            >
              {showPassword ? (
                <EyeOff className="h-4 w-4" />
              ) : (
                <Eye className="h-4 w-4" />
              )}
            </button>
          </div>
          {errors.password && (
            <Alert variant="destructive">
              <AlertDescription>{errors.password}</AlertDescription>
            </Alert>
          )}
          <p className="text-xs text-muted-foreground">
            Mật khẩu phải có ít nhất 8 ký tự
          </p>
        </div>

        <div className="space-y-2">
          <Label htmlFor="confirmPassword">Xác nhận mật khẩu</Label>
          <div className="relative">
            <Lock className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              id="confirmPassword"
              name="confirmPassword"
              type={showConfirmPassword ? "text" : "password"}
              placeholder="Nhập lại mật khẩu"
              value={formData.confirmPassword}
              onChange={handleChange}
              className="pl-10 pr-10"
            />
            <button
              type="button"
              onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              className="absolute right-3 top-2.5 text-muted-foreground hover:text-foreground"
            >
              {showConfirmPassword ? (
                <EyeOff className="h-4 w-4" />
              ) : (
                <Eye className="h-4 w-4" />
              )}
            </button>
          </div>
          {errors.confirmPassword && (
            <Alert variant="destructive">
              <AlertDescription>{errors.confirmPassword}</AlertDescription>
            </Alert>
          )}
        </div>

        <Button type="submit" className="w-full" disabled={isLoading}>
          {isLoading ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Đang tạo tài khoản...
            </>
          ) : (
            "Đăng ký"
          )}
        </Button>
      </form>

      <div className="text-center text-sm text-muted-foreground">
        Đã có tài khoản?{" "}
        <Link
          href="/auth/login"
          className="text-primary hover:underline font-medium"
        >
          Đăng nhập ngay
        </Link>
      </div>

      <div className="text-xs text-muted-foreground text-center">
        Bằng việc đăng ký, bạn đồng ý với{" "}
        <Link href="/terms-of-service" className="text-primary hover:underline">
          Điều khoản sử dụng
        </Link>{" "}
        và{" "}
        <Link href="/privacy-policy" className="text-primary hover:underline">
          Chính sách bảo mật
        </Link>{" "}
        của chúng tôi.
      </div>
    </div>
  );
};

export default RegisterPage;
