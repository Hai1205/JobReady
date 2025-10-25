"use client";

import { Fragment, useEffect, useState } from "react";
import { Dialog, Transition } from "@headlessui/react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ScrollArea } from "@/components/ui/scroll-area";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Save, UserIcon } from "lucide-react";
import { EUserRole, EUserStatus } from "@/types/enum";
import { capitalizeEnumValue } from "@/lib/utils";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

interface UpdateUserDialogProps {
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
  onChange: (field: keyof IUser, value: string | boolean) => void;
  data: IUser | null;
  previewAvatar: string;
  handleAvatarChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onUserUpdated: () => void;
  isLoading: boolean;
}

const UpdateUserDialog = ({
  isOpen,
  onOpenChange,
  onChange,
  data,
  previewAvatar,
  handleAvatarChange,
  onUserUpdated,
  isLoading,
}: UpdateUserDialogProps) => {
  const handleClose = () => {
    onOpenChange(false);
  };

  return (
    <Transition appear show={isOpen} as={Fragment}>
      <Dialog as="div" className="relative z-50" onClose={handleClose}>
        <Transition.Child
          as={Fragment}
          enter="ease-out duration-300"
          enterFrom="opacity-0"
          enterTo="opacity-100"
          leave="ease-in duration-200"
          leaveFrom="opacity-100"
          leaveTo="opacity-0"
        >
          <div className="fixed inset-0 bg-black bg-opacity-25" />
        </Transition.Child>

        <div className="fixed inset-0 overflow-y-auto">
          <div className="flex min-h-full items-center justify-center p-4">
            <Transition.Child
              as={Fragment}
              enter="ease-out duration-300"
              enterFrom="opacity-0 scale-95"
              enterTo="opacity-100 scale-100"
              leave="ease-in duration-200"
              leaveFrom="opacity-100 scale-100"
              leaveTo="opacity-0 scale-95"
            >
              <Dialog.Panel className="w-full max-w-lg transform overflow-hidden rounded-2xl bg-gradient-to-br from-card to-card/80 backdrop-blur-sm border border-border/50 shadow-2xl p-6 text-left align-middle transition-all">
                <Dialog.Title
                  as="h3"
                  className="text-xl font-bold bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent pb-4 border-b border-border/30"
                >
                  Cập nhật người dùng
                </Dialog.Title>

                <ScrollArea className="h-[50vh] pr-4 mt-6">
                  <div className="space-y-6">
                    {/* Avatar Section */}
                    <div className="flex items-center justify-center">
                      <div className="relative w-32 h-32 rounded-full overflow-hidden border-4 border-primary/20 shadow-lg hover:border-primary/40 transition-all duration-300">
                        <Avatar className="w-full h-full">
                          <AvatarImage
                            src={
                              previewAvatar ||
                              data?.avatarUrl ||
                              "/svgs/placeholder.svg"
                            }
                            alt={data?.fullname || "User"}
                            className="object-cover"
                          />
                          <AvatarFallback className="bg-gradient-to-br from-primary/20 to-secondary/20">
                            <UserIcon className="w-12 h-12 text-muted-foreground" />
                          </AvatarFallback>
                        </Avatar>

                        <div className="absolute inset-0 bg-black/60 opacity-0 hover:opacity-100 flex items-center justify-center transition-all duration-300 cursor-pointer">
                          <Button
                            variant="secondary"
                            size="sm"
                            className="bg-gradient-to-r from-primary to-secondary hover:from-primary/90 hover:to-secondary/90 text-primary-foreground shadow-lg"
                            onClick={() =>
                              document
                                .getElementById("avatar-input-update")
                                ?.click()
                            }
                          >
                            Thay đổi
                          </Button>
                        </div>

                        <input
                          id="avatar-input-update"
                          type="file"
                          accept="image/*"
                          className="hidden"
                          onChange={handleAvatarChange}
                        />
                      </div>
                    </div>

                    {/* Form Fields */}
                    <div className="grid gap-4">
                      <div className="space-y-2">
                        <Label
                          htmlFor="update-fullname"
                          className="text-sm font-medium"
                        >
                          Họ và tên
                        </Label>
                        <Input
                          id="update-fullname"
                          value={data?.fullname || ""}
                          onChange={(e) => onChange("fullname", e.target.value)}
                          placeholder="Nhập họ và tên"
                          className="bg-background/50 border-border/50 focus:border-primary transition-colors"
                        />
                      </div>

                      <div className="space-y-2">
                        <Label
                          htmlFor="update-email"
                          className="text-sm font-medium"
                        >
                          Email
                        </Label>
                        <Input
                          id="update-email"
                          value={data?.email || ""}
                          disabled
                          className="bg-muted/50 border-border/30 cursor-not-allowed opacity-60"
                        />
                      </div>

                      <div className="space-y-2">
                        <Label
                          htmlFor="update-username"
                          className="text-sm font-medium"
                        >
                          Username
                        </Label>
                        <Input
                          id="update-username"
                          value={data?.username || ""}
                          disabled
                          className="bg-muted/50 border-border/30 cursor-not-allowed opacity-60"
                        />
                      </div>

                      <div className="space-y-2">
                        <Label
                          htmlFor="update-role"
                          className="text-sm font-medium"
                        >
                          Vai trò
                        </Label>
                        <Select
                          value={data?.role || EUserRole.USER}
                          onValueChange={(value) =>
                            onChange("role", value as EUserRole)
                          }
                        >
                          <SelectTrigger
                            id="update-role"
                            className="bg-background/50 border-border/50"
                          >
                            <SelectValue placeholder="Chọn vai trò" />
                          </SelectTrigger>
                          <SelectContent>
                            {Object.values(EUserRole).map((role) => (
                              <SelectItem key={role} value={role}>
                                {capitalizeEnumValue(role)}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </div>

                      <div className="space-y-2">
                        <Label
                          htmlFor="update-status"
                          className="text-sm font-medium"
                        >
                          Trạng thái
                        </Label>
                        <Select
                          value={data?.status || EUserStatus.PENDING}
                          onValueChange={(value) =>
                            onChange("status", value as EUserStatus)
                          }
                        >
                          <SelectTrigger
                            id="update-status"
                            className="bg-background/50 border-border/50"
                          >
                            <SelectValue placeholder="Chọn trạng thái" />
                          </SelectTrigger>
                          <SelectContent>
                            {Object.values(EUserStatus).map((status) => (
                              <SelectItem key={status} value={status}>
                                {capitalizeEnumValue(status)}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </div>
                    </div>
                  </div>
                </ScrollArea>

                {/* Footer */}
                <div className="mt-6 flex justify-end gap-3 pt-4 border-t border-border/30">
                  <Button
                    variant="outline"
                    onClick={handleClose}
                    className="border-border/50 hover:bg-destructive/10 hover:text-destructive hover:border-destructive/50 transition-all duration-200"
                  >
                    Hủy
                  </Button>

                  <Button
                    onClick={onUserUpdated}
                    disabled={isLoading}
                    className="bg-gradient-to-r from-primary to-secondary hover:from-primary/90 hover:to-secondary/90 shadow-lg hover:shadow-xl hover:shadow-primary/30 transition-all duration-200"
                  >
                    {isLoading ? (
                      <>Đang lưu...</>
                    ) : (
                      <>
                        <Save className="h-4 w-4 mr-2" />
                        Lưu thay đổi
                      </>
                    )}
                  </Button>
                </div>
              </Dialog.Panel>
            </Transition.Child>
          </div>
        </div>
      </Dialog>
    </Transition>
  );
};

export default UpdateUserDialog;
