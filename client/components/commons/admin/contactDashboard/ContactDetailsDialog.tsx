import {
  CheckCircle,
  User,
  Mail,
  Phone,
  MessageSquare,
  Calendar,
  Tag,
  Eye,
} from "lucide-react";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Label } from "@/components/ui/label";
import { formatDateAgo } from "@/lib/utils";
import { useState } from "react";
import { EContactStatus } from "@/types/enum";
import { EnhancedDialog } from "../EnhancedDialog";

interface IContactDetailsDialogProps {
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
  selectedContact: IContact | null;
  onResolveContact: () => void;
}

const ContactDetailsDialog = ({
  isOpen,
  onOpenChange,
  selectedContact,
  onResolveContact,
}: IContactDetailsDialogProps) => {
  const [isLoading, setIsLoading] = useState(false);

  if (!selectedContact) return null;

  const getStatusColor = (status?: string) => {
    switch (status) {
      case "resolved":
        return "bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300";
      default:
        return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-300";
    }
  };

  const handleResolveContact = async () => {
    setIsLoading(true);
    try {
      await Promise.resolve(onResolveContact());
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <EnhancedDialog
      isOpen={isOpen}
      onOpenChange={onOpenChange}
      title="Chi tiết liên hệ"
      description={`Thông tin liên hệ từ ${selectedContact.name}`}
      icon={Eye}
      isLoading={isLoading}
      selectedContact={selectedContact}
      onResolveContact={handleResolveContact}
      className="max-w-3xl"
      showCloseButton={false}
    >
      <ScrollArea className="h-[calc(70vh-8rem)] pr-4">
        <div className="grid gap-6 py-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-4">
              <div className="flex items-center space-x-3">
                <div className="p-2 bg-blue-100 dark:bg-blue-900/30 rounded-lg">
                  <User className="h-5 w-5 text-blue-600 dark:text-blue-400" />
                </div>
                <div>
                  <Label className="text-sm font-medium text-gray-700 dark:text-gray-300">
                    Tên người gửi
                  </Label>
                  <p className="text-gray-900 dark:text-white font-medium">
                    {selectedContact.name}
                  </p>
                </div>
              </div>

              <div className="flex items-center space-x-3">
                <div className="p-2 bg-green-100 dark:bg-green-900/30 rounded-lg">
                  <Mail className="h-5 w-5 text-green-600 dark:text-green-400" />
                </div>
                <div>
                  <Label className="text-sm font-medium text-gray-700 dark:text-gray-300">
                    Email
                  </Label>
                  <p className="text-gray-900 dark:text-white">
                    {selectedContact.email}
                  </p>
                </div>
              </div>

              <div className="flex items-center space-x-3">
                <div className="p-2 bg-purple-100 dark:bg-purple-900/30 rounded-lg">
                  <Phone className="h-5 w-5 text-purple-600 dark:text-purple-400" />
                </div>
                <div>
                  <Label className="text-sm font-medium text-gray-700 dark:text-gray-300">
                    Số điện thoại
                  </Label>
                  <p className="text-gray-900 dark:text-white">
                    {selectedContact.phone}
                  </p>
                </div>
              </div>

              <div className="flex items-center space-x-3">
                <div className="p-2 bg-orange-100 dark:bg-orange-900/30 rounded-lg">
                  <Tag className="h-5 w-5 text-orange-600 dark:text-orange-400" />
                </div>
                <div>
                  <Label className="text-sm font-medium text-gray-700 dark:text-gray-300">
                    Chương trình
                  </Label>
                  <p className="text-gray-900 dark:text-white">
                    {selectedContact.program || "N/A"}
                  </p>
                </div>
              </div>

              <div className="flex items-center space-x-3">
                <div className="p-2 bg-indigo-100 dark:bg-indigo-900/30 rounded-lg">
                  <Calendar className="h-5 w-5 text-indigo-600 dark:text-indigo-400" />
                </div>
                <div>
                  <Label className="text-sm font-medium text-gray-700 dark:text-gray-300">
                    Thời gian gửi
                  </Label>
                  <p className="text-gray-900 dark:text-white">
                    {formatDateAgo(selectedContact?.createdAt || "")}
                  </p>
                </div>
              </div>

              <div className="flex items-center space-x-3">
                <div>
                  <span
                    className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(
                      selectedContact.status
                    )}`}
                  >
                    {selectedContact.status === EContactStatus.RESOLVED
                      ? "Đã xử lý"
                      : "Chưa xử lý"}
                  </span>
                </div>
              </div>
            </div>

            <div className="space-y-4">
              <div className="flex items-start space-x-3">
                <div className="p-2 bg-teal-100 dark:bg-teal-900/30 rounded-lg mt-1">
                  <MessageSquare className="h-5 w-5 text-teal-600 dark:text-teal-400" />
                </div>
                <div className="flex-1">
                  <Label className="text-sm font-medium text-gray-700 dark:text-gray-300">
                    Nội dung
                  </Label>
                  <div className="mt-1 p-3 bg-gray-50 dark:bg-gray-700 rounded-md">
                    <p className="text-gray-900 dark:text-white whitespace-pre-wrap">
                      {selectedContact.message}
                    </p>
                  </div>
                </div>
              </div>

              {selectedContact.resolvedAt && (
                <div className="flex items-center space-x-3">
                  <div className="p-2 bg-green-100 dark:bg-green-900/30 rounded-lg">
                    <CheckCircle className="h-5 w-5 text-green-600 dark:text-green-400" />
                  </div>
                  <div>
                    <Label className="text-sm font-medium text-gray-700 dark:text-gray-300">
                      Ngày xử lý
                    </Label>
                    <p className="text-gray-900 dark:text-white">
                      {formatDateAgo(selectedContact.resolvedAt || "")}
                    </p>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </ScrollArea>
    </EnhancedDialog>
  );
};

export default ContactDetailsDialog;
