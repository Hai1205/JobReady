"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Camera, Loader2 } from "lucide-react";
import { useAuthStore } from "@/stores/authStore";
import { useUserStore } from "@/stores/userStore";
import { EUserRole, EUserStatus } from "@/types/enum";

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

  return (
    <div className="space-y-6">
      {/* Avatar Section */}
      <div className="flex flex-col items-center gap-4 pb-6 border-b border-border/50">
        <div className="relative group">
          <Avatar className="h-32 w-32 border-4 border-primary/20 shadow-lg">
            {previewAvatar && (
              <AvatarImage src={previewAvatar} className="object-cover" />
            )}
            <AvatarFallback className="text-3xl font-bold bg-gradient-to-br from-primary to-secondary text-primary-foreground">
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
          <Label htmlFor="username">Username</Label>
          <Input
            id="username"
            type="username"
            value={data?.username}
            disabled
            className="border-border/50 bg-muted/50"
          />
          <p className="text-xs text-muted-foreground">
            Username không thể thay đổi
          </p>
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
          <p className="text-xs text-muted-foreground">
            Email không thể thay đổi
          </p>
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
          <Label htmlFor="role">Vai Trò</Label>
          <Input
            id="role"
            value={userAuth?.role}
            disabled
            className="border-border/50 bg-muted/50"
          />
        </div>
      </div>

      <div className="flex justify-end gap-3 pt-4">
        <Button
          type="submit"
          disabled={isLoading}
          onClick={onUpdate}
          className="bg-gradient-to-r from-primary to-secondary hover:from-primary/90 hover:to-secondary/90 shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 transition-all duration-200"
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
