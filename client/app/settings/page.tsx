"use client";

import { useState } from "react";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { User, Lock, Bell, Shield } from "lucide-react";
import { useAuthStore } from "@/stores/authStore";
import { useUserStore } from "@/stores/userStore";
import { toast } from "react-toastify";
import { EUserRole, EUserStatus } from "@/types/enum";
// Import settings components
import ProfileTab from "@/components/settings/ProfileTab";
import SecurityTab from "@/components/settings/SecurityTab";
import NotificationsTab from "@/components/settings/NotificationsTab";
import PrivacyTab from "@/components/settings/PrivacyTab";

export default function SettingsPage() {
  const { userAuth, changePassword } = useAuthStore();
  const { updateUser, isLoading } = useUserStore();

  const [avatarFile, setAvatarFile] = useState<File | null>(null);
  const [previewAvatar, setPreviewAvatar] = useState<string>("");

  const tabs = [
    { value: "profile", icon: User, label: "Hồ Sơ" },
    { value: "security", icon: Lock, label: "Bảo Mật" },
    { value: "notifications", icon: Bell, label: "Thông Báo" },
    { value: "privacy", icon: Shield, label: "Quyền Riêng Tư" },
  ];

  type ExtendedUserData = Omit<IUser, "status"> & {
    status: EUserStatus;
    role: EUserRole;
    password?: string;
    newPassword?: string;
    currentPassword?: string;
    confirmPassword?: string;
  };
  const defaultUser: ExtendedUserData = {
    id: "",
    username: "",
    email: "",
    password: "",
    newPassword: "",
    confirmPassword: "",
    fullname: "",
    role: EUserRole.USER,
    status: EUserStatus.PENDING,
  };

  const [data, setData] = useState<ExtendedUserData | null>(userAuth);

  const handleChange = (
    field: keyof ExtendedUserData,
    value: string | string[] | boolean
  ) => {
    setData((prev) => {
      if (!prev) {
        return { ...defaultUser, [field]: value } as ExtendedUserData;
      }

      return { ...prev, [field]: value };
    });
  };

  const handleUpdate = async () => {
    if (data) {
      await updateUser(
        data.id,
        data.fullname,
        avatarFile || null,
        data.role,
        data.status
      );
    }
  };

  const handleAvatarChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setAvatarFile(file);
      setPreviewAvatar(URL.createObjectURL(file));
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();

    if (data?.newPassword !== data?.confirmPassword) {
      toast.error("New passwords do not match!");
      return;
    }

    if (data?.newPassword && data.newPassword.length < 6) {
      toast.error("Password must be at least 6 characters!");
      return;
    }

    if (
      data?.email &&
      data?.currentPassword &&
      data?.newPassword &&
      data?.confirmPassword
    ) {
      changePassword(
        data.email,
        data.currentPassword,
        data.newPassword,
        data.confirmPassword
      );

      toast.info("Change password successfully!");
    }
  };

  if (!userAuth) {
    return null;
  }

  return (
    <div className="min-h-screen flex items-center justify-center py-12 bg-gradient-to-br from-background to-muted/20">
      <div className="container max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
            Cài Đặt
          </h1>
          <p className="text-muted-foreground mt-2">
            Quản lý thông tin tài khoản và cài đặt của bạn
          </p>
        </div>

        <Tabs defaultValue="profile" className="space-y-6">
          <TabsList className="grid w-full grid-cols-2 lg:grid-cols-4 bg-card border border-border/50">
            {tabs.map(({ value, icon: Icon, label }) => (
              <TabsTrigger
                key={value}
                value={value}
                className="gap-2 data-[state=active]:bg-gradient-to-r data-[state=active]:from-primary data-[state=active]:to-secondary data-[state=active]:text-primary-foreground"
              >
                <Icon className="h-4 w-4" />
                <span className="hidden sm:inline">{label}</span>
              </TabsTrigger>
            ))}
          </TabsList>

          {/* Profile Tab */}
          <TabsContent value="profile">
            <Card className="border-border/50 shadow-lg bg-gradient-to-br from-card to-card/80 backdrop-blur-sm">
              <CardHeader>
                <CardTitle className="text-xl bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
                  Thông Tin Cá Nhân
                </CardTitle>
                <CardDescription>
                  Cập nhật thông tin hồ sơ của bạn
                </CardDescription>
              </CardHeader>
              <CardContent>
                <ProfileTab
                  data={data}
                  onChange={handleChange}
                  onUpdate={handleUpdate}
                  previewAvatar={previewAvatar}
                  onAvatarChange={handleAvatarChange}
                />
              </CardContent>
            </Card>
          </TabsContent>

          {/* Security Tab */}
          <TabsContent value="security">
            <Card className="border-border/50 shadow-lg bg-gradient-to-br from-card to-card/80 backdrop-blur-sm">
              <CardHeader>
                <CardTitle className="text-xl bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
                  Bảo Mật
                </CardTitle>
                <CardDescription>
                  Thay đổi mật khẩu và cài đặt bảo mật
                </CardDescription>
              </CardHeader>
              <CardContent>
                <SecurityTab
                  data={data}
                  onChange={handleChange}
                  onChangePassword={handleChangePassword}
                />
              </CardContent>
            </Card>
          </TabsContent>

          {/* Notifications Tab */}
          <TabsContent value="notifications">
            <Card className="border-border/50 shadow-lg bg-gradient-to-br from-card to-card/80 backdrop-blur-sm">
              <CardHeader>
                <CardTitle className="text-xl bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
                  Thông Báo
                </CardTitle>
                <CardDescription>
                  Quản lý cách bạn nhận thông báo
                </CardDescription>
              </CardHeader>
              <CardContent>
                <NotificationsTab />
              </CardContent>
            </Card>
          </TabsContent>

          {/* Privacy Tab */}
          <TabsContent value="privacy">
            <Card className="border-border/50 shadow-lg bg-gradient-to-br from-card to-card/80 backdrop-blur-sm">
              <CardHeader>
                <CardTitle className="text-xl bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
                  Quyền Riêng Tư
                </CardTitle>
                <CardDescription>
                  Kiểm soát quyền riêng tư và dữ liệu của bạn
                </CardDescription>
              </CardHeader>
              <CardContent>
                <PrivacyTab />
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
}
