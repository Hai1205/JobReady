"use client";

import { Button } from "@/components/ui/button";
import { InputWithIcon } from "@/components/ui/input-with-icon";
import { Label } from "@/components/ui/label";
import { EUserRole, EUserStatus } from "@/types/enum";
import { Eye, EyeOff, Lock, Loader2, Trash2 } from "lucide-react";
import { useState } from "react";
import ConfirmationDialog from "@/components/commons/layout/ConfirmationDialog";
import { toast } from "react-toastify";

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
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);

  const handleDeleteAccount = () => {
    // Implement actual delete logic here
    toast.success("Yêu cầu xóa tài khoản đã được gửi!");
    setShowDeleteDialog(false);
  };

  return (
    <>
      <form onSubmit={onChangePassword} className="space-y-6">
        <InputWithIcon
          id="current-password"
          name="current-password"
          type={showCurrentPassword ? "text" : "password"}
          placeholder="Nhập mật khẩu hiện tại của bạn"
          value={data?.currentPassword || ""}
          onChange={(e) => onChange("currentPassword", e.target.value)}
          leftIcon={Lock}
          rightIcon={showCurrentPassword ? EyeOff : Eye}
          onRightIconClick={() => setShowCurrentPassword(!showCurrentPassword)}
        />

        <InputWithIcon
          id="new-password"
          name="new-password"
          type={showNewPassword ? "text" : "password"}
          placeholder="Nhập mật khẩu mới của bạn"
          value={data?.newPassword || ""}
          onChange={(e) => onChange("newPassword", e.target.value)}
          leftIcon={Lock}
          rightIcon={showNewPassword ? EyeOff : Eye}
          onRightIconClick={() => setShowNewPassword(!showNewPassword)}
        />

        <InputWithIcon
          id="confirm-password"
          name="confirm-password"
          type={showConfirmPassword ? "text" : "password"}
          placeholder="Nhập lại mật khẩu mới"
          value={data?.confirmPassword || ""}
          onChange={(e) => onChange("confirmPassword", e.target.value)}
          leftIcon={Lock}
          rightIcon={showConfirmPassword ? EyeOff : Eye}
          onRightIconClick={() => setShowConfirmPassword(!showConfirmPassword)}
        />

        <div className="flex justify-end pt-4">
          <Button
            type="submit"
            disabled={isLoading}
            className="bg-linear-to-br from-primary to-secondary hover:from-primary/90 hover:to-secondary/90 shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
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

      {/* Danger Zone */}
      <div className="mt-8 rounded-lg border border-destructive/50 bg-destructive/5 p-6">
        <div className="space-y-4">
          <div>
            <h3 className="text-lg font-semibold text-destructive">
              Vùng nguy hiểm
            </h3>
            <p className="mt-1 text-sm text-muted-foreground">
              Hành động này không thể hoàn tác. Vui lòng cân nhắc kỹ trước khi
              thực hiện.
            </p>
          </div>
          <Button
            variant="destructive"
            onClick={() => setShowDeleteDialog(true)}
            className="w-full sm:w-auto"
          >
            <Trash2 className="mr-2 h-4 w-4" />
            Xóa tài khoản vĩnh viễn
          </Button>
        </div>
      </div>

      <ConfirmationDialog
        open={showDeleteDialog}
        onOpenChange={setShowDeleteDialog}
        onConfirm={handleDeleteAccount}
        title="Xóa tài khoản vĩnh viễn?"
        description="Tất cả dữ liệu của bạn bao gồm CV, thông tin cá nhân và lịch sử thanh toán sẽ bị xóa vĩnh viễn. Hành động này không thể hoàn tác!"
        confirmText="Xóa tài khoản"
        cancelText="Hủy bỏ"
        isDestructive={true}
      />
    </>
  );
}
