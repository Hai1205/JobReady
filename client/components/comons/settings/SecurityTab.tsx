"use client";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { EUserRole, EUserStatus } from "@/types/enum";

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
}

export default function SecurityTab({
  data,
  onChange,
  onChangePassword,
}: SecurityTabProps) {
  return (
    <form onSubmit={onChangePassword} className="space-y-6">
      <div className="space-y-2">
        <Label htmlFor="current-password">Mật Khẩu Hiện Tại</Label>
        <Input
          id="current-password"
          type="password"
          value={data?.currentPassword}
          onChange={(e) => onChange("currentPassword", e.target.value)}
          placeholder="Nhập mật khẩu hiện tại"
          className="border-border/50 focus:border-primary/50 focus:ring-primary/20"
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="new-password">Mật Khẩu Mới</Label>
        <Input
          id="new-password"
          type="password"
          value={data?.newPassword}
          onChange={(e) => onChange("newPassword", e.target.value)}
          placeholder="Nhập mật khẩu mới"
          className="border-border/50 focus:border-primary/50 focus:ring-primary/20"
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="confirm-password">Xác Nhận Mật Khẩu Mới</Label>
        <Input
          id="confirm-password"
          type="password"
          value={data?.confirmPassword}
          onChange={(e) => onChange("confirmPassword", e.target.value)}
          placeholder="Nhập lại mật khẩu mới"
          className="border-border/50 focus:border-primary/50 focus:ring-primary/20"
        />
      </div>

      <div className="flex justify-end pt-4">
        <Button
          type="submit"
          className="bg-gradient-to-r from-primary to-secondary hover:from-primary/90 hover:to-secondary/90 shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 transition-all duration-200"
        >
          Đổi Mật Khẩu
        </Button>
      </div>
    </form>
  );
}
