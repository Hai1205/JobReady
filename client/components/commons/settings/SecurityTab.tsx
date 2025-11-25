"use client";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { EUserRole, EUserStatus } from "@/types/enum";
import { Eye, EyeOff, Lock, Loader2 } from "lucide-react";
import { useState } from "react";

type ExtendedUserData = Omit<IUser, "status"> & {
  status: EUserStatus;
  role: EUserRole;
  password?: string;
  newPassword?: string;
  currentPassword?: string;
  confirmPassword?: string;
};

interface SecurityTabProps {
  data: ExtendedUserData | null;
  onChange: (
    field: keyof ExtendedUserData,
    value: string | string[] | boolean
  ) => void;
  onChangePassword: (e: React.FormEvent) => void;
  isLoading?: boolean;
}

export default function SecurityTab({
  data,
  onChange,
  onChangePassword,
  isLoading = false,
}: SecurityTabProps) {
  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  return (
    <form onSubmit={onChangePassword} className="space-y-6">
      <div className="relative">
        <Lock className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
        <Input
          id="current-password"
          name="current-password"
          type={showCurrentPassword ? "text" : "password"}
          placeholder="Nhập mật khẩu hiện tại của bạn"
          value={data?.currentPassword || ""}
          onChange={(e) => onChange("currentPassword", e.target.value)}
          className="pl-10"
        />
        <button
          type="button"
          onClick={() => setShowCurrentPassword(!showCurrentPassword)}
          className="absolute right-3 top-2.5 text-muted-foreground hover:text-foreground"
        >
          {showCurrentPassword ? (
            <EyeOff className="h-4 w-4" />
          ) : (
            <Eye className="h-4 w-4" />
          )}
        </button>
      </div>

      <div className="relative">
        <Lock className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
        <Input
          id="new-password"
          name="new-password"
          type={showNewPassword ? "text" : "password"}
          placeholder="Nhập mật khẩu mới của bạn"
          value={data?.newPassword || ""}
          onChange={(e) => onChange("newPassword", e.target.value)}
          className="pl-10"
        />
        <button
          type="button"
          onClick={() => setShowNewPassword(!showNewPassword)}
          className="absolute right-3 top-2.5 text-muted-foreground hover:text-foreground"
        >
          {showNewPassword ? (
            <EyeOff className="h-4 w-4" />
          ) : (
            <Eye className="h-4 w-4" />
          )}
        </button>
      </div>

      <div className="relative">
        <Lock className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
        <Input
          id="confirm-password"
          name="confirm-password"
          type={showConfirmPassword ? "text" : "password"}
          placeholder="Nhập lại mật khẩu mới"
          value={data?.confirmPassword || ""}
          onChange={(e) => onChange("confirmPassword", e.target.value)}
          className="pl-10"
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

      <div className="flex justify-end pt-4">
        <Button
          type="submit"
          disabled={isLoading}
          className="bg-gradient-to-r from-primary to-secondary hover:from-primary/90 hover:to-secondary/90 shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isLoading ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Đang xử lý...
            </>
          ) : (
            "Đổi Mật Khẩu"
          )}
        </Button>
      </div>
    </form>
  );
}
