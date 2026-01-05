"use client";

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { ScrollArea } from "@/components/ui/scroll-area";
import {
  FileText,
  Calendar,
  CreditCard,
  DollarSign,
  User,
  Package,
  CheckCircle2,
  Clock,
  XCircle,
  RefreshCw,
  Download,
  Printer,
} from "lucide-react";
import { exportInvoiceToPDF } from "@/lib/invoicePdfExporter";
import { toast } from "react-toastify";

interface InvoiceDialogProps {
  invoice: IInvoice | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

const statusConfig = {
  paid: {
    label: "Đã thanh toán",
    icon: CheckCircle2,
    className: "bg-green-500/10 text-green-700 border-green-500/20",
  },
  pending: {
    label: "Đang xử lý",
    icon: Clock,
    className: "bg-yellow-500/10 text-yellow-700 border-yellow-500/20",
  },
  failed: {
    label: "Thất bại",
    icon: XCircle,
    className: "bg-red-500/20 text-red-800 dark:text-red-400 border-red-500/30",
  },
  refunded: {
    label: "Đã hoàn tiền",
    icon: RefreshCw,
    className: "bg-blue-500/10 text-blue-700 border-blue-500/20",
  },
};

export function InvoiceDialog({ invoice, open, onOpenChange }: InvoiceDialogProps) {
  if (!invoice) return null;

  const StatusIcon = statusConfig[invoice.status].icon;

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const handleDownload = () => {
    try {
      const result = exportInvoiceToPDF(invoice);
      toast.success(`Đã tải xuống ${result.filename}`);
    } catch (error) {
      toast.error("Không thể tải xuống hóa đơn PDF");
      console.error(error);
    }
  };

  const handlePrint = () => {
    window.print();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl max-h-[90vh]">
        <DialogHeader>
          <div className="flex items-start justify-between">
            <div>
              <DialogTitle className="text-2xl font-bold flex items-center gap-2">
                <FileText className="h-6 w-6 text-primary" />
                Chi tiết hóa đơn
              </DialogTitle>
              <DialogDescription className="mt-2">
                Thông tin chi tiết về giao dịch thanh toán
              </DialogDescription>
            </div>
            <Badge className={statusConfig[invoice.status].className}>
              <StatusIcon className="mr-1 h-3 w-3" />
              {statusConfig[invoice.status].label}
            </Badge>
          </div>
        </DialogHeader>

        <ScrollArea className="h-[calc(90vh-120px)] pr-4">
          <div className="space-y-6 mt-4">
          {/* Invoice IDs */}
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-1">
              <p className="text-sm text-muted-foreground flex items-center gap-2">
                <FileText className="h-4 w-4" />
                Mã hóa đơn
              </p>
              <p className="font-mono text-sm font-semibold bg-muted px-3 py-2 rounded-md">
                {invoice.id}
              </p>
            </div>
            <div className="space-y-1">
              <p className="text-sm text-muted-foreground flex items-center gap-2">
                <CreditCard className="h-4 w-4" />
                Mã giao dịch
              </p>
              <p className="font-mono text-sm font-semibold bg-muted px-3 py-2 rounded-md">
                {invoice.transactionId}
              </p>
            </div>
          </div>

          <Separator />

          {/* Service Info */}
          <div className="space-y-3">
            <h3 className="font-semibold flex items-center gap-2">
              <Package className="h-5 w-5 text-primary" />
              Thông tin dịch vụ
            </h3>
            <div className="bg-primary/5 rounded-lg p-4 space-y-2">
              <div className="flex justify-between items-start">
                <div>
                  <p className="font-semibold text-lg">{invoice.planTitle}</p>
                  <p className="text-sm text-muted-foreground">
                    {invoice.description}
                  </p>
                </div>
                <div className="text-right">
                  <p className="text-2xl font-bold text-primary">
                    {invoice.currency}
                    {invoice.amount.toLocaleString("vi-VN")}
                  </p>
                  <p className="text-xs text-muted-foreground">Đơn giá</p>
                </div>
              </div>
            </div>
          </div>

          <Separator />

          {/* Payment Details */}
          <div className="space-y-3">
            <h3 className="font-semibold flex items-center gap-2">
              <DollarSign className="h-5 w-5 text-primary" />
              Chi tiết thanh toán
            </h3>
            <div className="grid gap-3">
              <div className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
                <div className="flex items-center gap-2">
                  <CreditCard className="h-4 w-4 text-muted-foreground" />
                  <span className="text-sm">Phương thức thanh toán</span>
                </div>
                <Badge variant="outline" className="font-mono">
                  {invoice.paymentMethod.toUpperCase()}
                </Badge>
              </div>

              <div className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
                <div className="flex items-center gap-2">
                  <User className="h-4 w-4 text-muted-foreground" />
                  <span className="text-sm">Mã người dùng</span>
                </div>
                <span className="font-mono text-sm">{invoice.userId}</span>
              </div>

              <div className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
                <div className="flex items-center gap-2">
                  <Package className="h-4 w-4 text-muted-foreground" />
                  <span className="text-sm">Mã gói</span>
                </div>
                <span className="font-mono text-sm">{invoice.planId}</span>
              </div>

              <div className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
                <div className="flex items-center gap-2">
                  <Calendar className="h-4 w-4 text-muted-foreground" />
                  <span className="text-sm">Ngày thanh toán</span>
                </div>
                <span className="text-sm font-semibold">
                  {formatDate(invoice.billingDate)}
                </span>
              </div>
            </div>
          </div>

          <Separator />

          {/* Period Info */}
          <div className="space-y-3">
            <h3 className="font-semibold flex items-center gap-2">
              <Calendar className="h-5 w-5 text-primary" />
              Chu kỳ sử dụng
            </h3>
            <div className="grid grid-cols-2 gap-4">
              <div className="p-4 bg-gradient-to-br from-blue-50 to-blue-100/50 dark:from-blue-950/30 dark:to-blue-900/20 rounded-lg border border-blue-200/50 dark:border-blue-800/50">
                <p className="text-xs text-muted-foreground mb-1">Bắt đầu</p>
                <p className="font-semibold text-sm">
                  {formatDate(invoice.periodStart)}
                </p>
              </div>
              <div className="p-4 bg-gradient-to-br from-purple-50 to-purple-100/50 dark:from-purple-950/30 dark:to-purple-900/20 rounded-lg border border-purple-200/50 dark:border-purple-800/50">
                <p className="text-xs text-muted-foreground mb-1">Kết thúc</p>
                <p className="font-semibold text-sm">
                  {formatDate(invoice.periodEnd)}
                </p>
              </div>
            </div>
          </div>

          {/* Total Amount */}
          <div className="bg-gradient-to-r from-primary/10 via-primary/5 to-primary/10 rounded-lg p-4 border-2 border-primary/20">
            <div className="flex justify-between items-center">
              <span className="text-lg font-semibold">Tổng cộng</span>
              <div className="text-right">
                <p className="text-3xl font-bold text-primary">
                  {invoice.currency}
                  {invoice.amount.toLocaleString("vi-VN")}
                </p>
                <p className="text-xs text-muted-foreground">
                  Đã bao gồm thuế VAT
                </p>
              </div>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex gap-2 pt-2">
            <Button
              onClick={handleDownload}
              variant="outline"
              className="flex-1"
            >
              <Download className="mr-2 h-4 w-4" />
              Tải xuống
            </Button>
            <Button onClick={handlePrint} variant="outline" className="flex-1">
              <Printer className="mr-2 h-4 w-4" />
              In hóa đơn
            </Button>
          </div>

          {/* Footer Note */}
          <div className="text-center text-xs text-muted-foreground pt-4 border-t">
            <p>
              Hóa đơn này được tạo tự động bởi hệ thống. Vui lòng liên hệ bộ
              phận hỗ trợ nếu có bất kỳ thắc mắc nào.
            </p>
          </div>
        </div>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
