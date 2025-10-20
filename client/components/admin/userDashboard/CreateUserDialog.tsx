"use client";

import { Fragment } from "react";
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

interface CreateUserDialogProps {
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
  onChange: (field: keyof IUser, value: string | boolean) => void;
  data: IUser | null;
  previewAvatar: string;
  handleAvatarChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onUserCreated: () => void;
  isLoading: boolean;
}

const CreateUserDialog = ({
  isOpen,
  onOpenChange,
  onChange,
  data,
  previewAvatar,
  handleAvatarChange,
  onUserCreated,
  isLoading,
}: CreateUserDialogProps) => {
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
              <Dialog.Panel className="w-full max-w-md transform overflow-hidden rounded-2xl bg-white dark:bg-gray-800 p-6 text-left align-middle shadow-xl transition-all">
                <Dialog.Title
                  as="h3"
                  className="text-lg font-medium leading-6 text-gray-900 dark:text-white"
                >
                  Tạo quản trị viên
                </Dialog.Title>

                <ScrollArea className="h-[42vh] pr-4 mt-4">
                  <div className="grid gap-4">
                    <div className="grid gap-2">
                      <Label htmlFor="update-fullname">Fullname</Label>
                      <Input
                        id="update-fullname"
                        value={data?.fullname || ""}
                        onChange={(e) => onChange("fullname", e.target.value)}
                      />
                    </div>
                  </div>

                  <div className="grid gap-4">
                    <div className="grid gap-2">
                      <Label htmlFor="update-email">Email</Label>
                      <Input
                        id="update-email"
                        value={data?.email || ""}
                        onChange={(e) => onChange("email", e.target.value)}
                      />
                    </div>
                  </div>

                  <div className="grid gap-4">
                    <div className="grid gap-2">
                      <Label htmlFor="update-username">Username</Label>
                      <Input
                        id="update-username"
                        value={data?.username || ""}
                        onChange={(e) => onChange("username", e.target.value)}
                      />
                    </div>
                  </div>

                  <div className="flex items-center justify-center col-span-1 row-span-3">
                    <div className="relative w-40 h-40 border border-gray-700 rounded-full overflow-hidden flex items-center justify-center bg-[#282828]">
                      <Avatar className="rounded-full object-cover w-full h-full">
                        <AvatarImage
                          src={
                            previewAvatar ? previewAvatar : "/placeholder.svg"
                          }
                          alt={data?.fullname}
                        />
                        <AvatarFallback>
                          <UserIcon />
                        </AvatarFallback>
                      </Avatar>

                      <div className="absolute inset-0 bg-black/50 opacity-0 hover:opacity-100 flex items-center justify-center transition-opacity">
                        <Button
                          variant="secondary"
                          size="sm"
                          className="bg-[#1DB954] text-white hover:bg-[#1ed760]"
                          onClick={() =>
                            document.getElementById("avatar-input")?.click()
                          }
                        >
                          Change
                        </Button>

                        <input
                          id="avatar-input"
                          type="file"
                          accept="image/*"
                          className="hidden"
                          onChange={handleAvatarChange}
                        />
                      </div>
                    </div>
                  </div>

                  <div className="grid gap-2 mt-3">
                    <Label htmlFor="update-role">Role</Label>
                    <Select
                      value={data?.role || EUserRole.USER}
                      onValueChange={(value) =>
                        onChange("role", value as EUserRole)
                      }
                    >
                      <SelectTrigger id="update-role">
                        <SelectValue placeholder="Select role" />
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

                  <div className="grid gap-2 mt-3">
                    <Label htmlFor="update-status">Status</Label>
                    <Select
                      value={data?.status || EUserStatus.PENDING}
                      onValueChange={(value) =>
                        onChange("status", value as EUserStatus)
                      }
                    >
                      <SelectTrigger id="update-status">
                        <SelectValue placeholder="Select status" />
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
                </ScrollArea>

                {/* Footer */}
                <div className="mt-4 flex justify-end gap-2 pt-4 border-t border-gray-800">
                  <Button
                    variant="outline"
                    onClick={handleClose}
                    className="bg-gray-200 border-gray-300 text-gray-700 hover:bg-red-200 hover:text-red-600 hover:border-red-200 dark:bg-transparent dark:border-gray-700 dark:text-white dark:hover:bg-red-900 dark:hover:text-white"
                  >
                    Hủy
                  </Button>

                  <Button onClick={onUserCreated} disabled={isLoading}>
                    {isLoading ? (
                      <>Đang lưu...</>
                    ) : (
                      <>
                        <Save className="h-4 w-4" />
                        Lưu
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

export default CreateUserDialog;
