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
import { Save } from "lucide-react";
import { EUserStatus } from "@/types/enum";

interface CreateUserDialogProps {
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
  onChange: (field: keyof IUser, value: string | boolean) => void;
  data: IUser | null;
  onUserCreated: () => void;
  isLoading: boolean;
}

const CreateUserDialog = ({
  isOpen,
  onOpenChange,
  onChange,
  data,
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
                      <Label htmlFor="create-name">Tên Quản trị viên</Label>
                      <Input
                        id="create-name"
                        value={data?.name || ""}
                        onChange={(e) => onChange("name", e.target.value)}
                      />
                    </div>
                  </div>

                  <div className="grid gap-4">
                    <div className="grid gap-2">
                      <Label htmlFor="create-email">Email</Label>
                      <Input
                        id="create-email"
                        type="email"
                        value={data?.email || ""}
                        onChange={(e) => onChange("email", e.target.value)}
                      />
                    </div>
                  </div>

                  {/* <div className="grid gap-4">
                    <div className="grid gap-2">
                      <Label htmlFor="create-password">Mật khẩu</Label>
                      <Input
                        id="create-password"
                        type="password"
                        value={data?.password || ""}
                        onChange={(e) =>
                          onChange("password" as keyof IUser, e.target.value)
                        }
                      />
                    </div>
                  </div>

                  <div className="grid gap-4">
                    <div className="grid gap-2">
                      <Label htmlFor="create-phone">Số điện thoại</Label>
                      <Input
                        id="create-phone"
                        value={data?.phone || ""}
                        onChange={(e) => onChange("phone", e.target.value)}
                      />
                    </div>
                  </div> */}

                  <div className="grid gap-2 mt-3">
                    <Label htmlFor="create-status">Trạng thái</Label>
                    <Select
                      value={data?.status || EUserStatus.PENDING}
                      onValueChange={(value) =>
                        onChange("status", value as EUserStatus)
                      }
                    >
                      <SelectTrigger id="create-status">
                        <SelectValue placeholder="Chọn trạng thái" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="active">Hoạt động</SelectItem>
                        <SelectItem value="inactive">Ngừng hoạt động</SelectItem>
                        <SelectItem value={EUserStatus.PENDING}>
                          Đang chờ
                        </SelectItem>
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
