"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Camera, Loader2 } from "lucide-react";
import { useAuthStore } from "@/stores/authStore";
import { useUserStore } from "@/stores/userStore";
import { EUserRole, EUserStatus } from "@/types/enum";
import { validatePhoneNumber } from "@/lib/utils";

type ExtendedUserData = Omit<IUser, "status"> & {
  status: EUserStatus;
  role: EUserRole;
  password?: string;
  newPassword?: string;
  currentPassword?: string;
  confirmPassword?: string;
};

interface ProfileTabProps {
  data: ExtendedUserData | null;
  onChange: (
    field: keyof ExtendedUserData,
    value: string | string[] | boolean
  ) => void;
  onUpdate: () => void;
  previewAvatar: string;
  onAvatarChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

export default function ProfileTab({
  data,
  onChange,
  onUpdate,
  previewAvatar,
  onAvatarChange,
}: ProfileTabProps) {
  const { userAuth } = useAuthStore();
  const { isLoading } = useUserStore();
  const [phoneError, setPhoneError] = useState<string>("");

  // Phone validation function
  const validatePhone = (phone: string): boolean => {
    if (!phone.trim()) {
      setPhoneError("");
      return true; // Empty phone is allowed
    }

    const isValid = validatePhoneNumber(phone);
    if (!isValid) {
      setPhoneError(
        "Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam (10 số, bắt đầu bằng 03, 05, 07, 08, 09)"
      );
      return false;
    }

    setPhoneError("");
    return true;
  };

  // Handle phone change with validation
  const handlePhoneChange = (value: string) => {
    onChange("phone", value);
    validatePhone(value);
  };

  // Validate phone on mount and when data changes
  useEffect(() => {
    if (data?.phone) {
      validatePhone(data.phone);
    }
  }, [data?.phone]);

  return (
    <div className="space-y-6">
      {/* Avatar Section */}
      <div className="flex flex-col items-center gap-4 pb-6 border-b border-border/50">
        <div className="relative group">
          <Avatar className="h-32 w-32 border-4 border-primary/20 shadow-lg">
            {(previewAvatar || userAuth?.avatarUrl) && (
              <AvatarImage
                src={previewAvatar || userAuth?.avatarUrl}
                className="object-cover"
              />
            )}
            <AvatarFallback className="text-3xl font-bold bg-linear-to-br from-primary to-secondary text-primary-foreground">
              {data?.fullname?.charAt(0).toUpperCase() || "U"}
            </AvatarFallback>
          </Avatar>
          <label
            htmlFor="avatar-upload"
            className="absolute inset-0 flex items-center justify-center bg-black/50 rounded-full opacity-0 group-hover:opacity-100 transition-opacity cursor-pointer"
          >
            <Camera className="h-8 w-8 text-white" />
          </label>
          <input
            id="avatar-upload"
            type="file"
            accept="image/*"
            onChange={onAvatarChange}
            className="hidden"
          />
        </div>
        <div className="text-center">
          <p className="text-sm text-muted-foreground">
            Click vào ảnh để thay đổi
          </p>
          <p className="text-xs text-muted-foreground mt-1">
            JPG, PNG hoặc GIF (tối đa 5MB)
          </p>
        </div>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="username">Tên đăng nhập</Label>
          <Input
            id="username"
            type="username"
            value={data?.username}
            disabled
            className="border-border/50 bg-muted/50"
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="email">Email</Label>
          <Input
            id="email"
            type="email"
            value={data?.email}
            disabled
            className="border-border/50 bg-muted/50"
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="fullname">Họ và Tên</Label>
          <Input
            id="fullname"
            value={data?.fullname}
            onChange={(e) => onChange("fullname", e.target.value)}
            placeholder="Nhập họ và tên"
            className="border-border/50 focus:border-primary/50 focus:ring-primary/20"
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="phone">Số điện thoại</Label>
          <Input
            id="phone"
            value={data?.phone || ""}
            onChange={(e) => handlePhoneChange(e.target.value)}
            placeholder="Nhập số điện thoại"
            className={`border-border/50 focus:border-primary/50 focus:ring-primary/20 ${
              phoneError ? "border-red-500 focus:border-red-500" : ""
            }`}
          />
          {phoneError && (
            <p className="text-sm text-red-500 mt-1">{phoneError}</p>
          )}
        </div>

        <div className="space-y-2">
          <Label htmlFor="location">Địa chỉ</Label>
          <Input
            id="location"
            value={data?.location || ""}
            onChange={(e) => onChange("location", e.target.value)}
            placeholder="Nhập địa chỉ"
            className="border-border/50 focus:border-primary/50 focus:ring-primary/20"
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="birth">Ngày sinh</Label>
          <Input
            id="birth"
            type="date"
            value={data?.birth || ""}
            onChange={(e) => onChange("birth", e.target.value)}
            className="border-border/50 focus:border-primary/50 focus:ring-primary/20"
          />
        </div>
      </div>

      <div className="space-y-2">
        <div className="flex items-center justify-between">
          <Label htmlFor="summary">Giới thiệu bản thân</Label>
          <span className="text-xs text-muted-foreground">
            {data?.summary?.length || 0}/2000
          </span>
        </div>
        <ScrollArea className="h-32 w-full border border-border/50 rounded-md bg-background">
          <textarea
            id="summary"
            value={data?.summary || ""}
            onChange={(e) => {
              // Truncate to 2000 characters if exceeded
              const truncatedValue =
                e.target.value.length > 2000
                  ? e.target.value.slice(0, 2000)
                  : e.target.value;
              onChange("summary", truncatedValue);
            }}
            placeholder="Viết vài dòng giới thiệu về bản thân..."
            className="w-full min-h-32 px-3 py-2 text-sm bg-transparent focus:outline-none transition-colors resize-none border-0 overflow-hidden"
            style={{ height: "auto" }}
            onInput={(e) => {
              e.currentTarget.style.height = "auto";
              e.currentTarget.style.height =
                e.currentTarget.scrollHeight + "px";
            }}
          />
        </ScrollArea>
        <p className="text-xs text-muted-foreground">
          Viết vài dòng giới thiệu về bản thân và mục tiêu nghề nghiệp của bạn
        </p>
      </div>

      <div className="flex justify-end gap-3 pt-4">
        <Button
          type="submit"
          disabled={isLoading || !!phoneError}
          onClick={onUpdate}
          className="bg-linear-to-br from-primary to-secondary hover:from-primary/90 hover:to-secondary/90 shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isLoading ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Đang lưu...
            </>
          ) : (
            "Lưu Thay Đổi"
          )}
        </Button>
      </div>
    </div>
  );
}
