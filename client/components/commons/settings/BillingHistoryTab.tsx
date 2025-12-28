"use client";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { mockInvoices } from "@/services/mockData";
import { useAuthStore } from "@/stores/authStore";
import {
  CreditCard,
  Download,
  FileText,
  Calendar,
  DollarSign,
  CheckCircle2,
  Clock,
  XCircle,
  RefreshCw,
} from "lucide-react";
import { toast } from "react-toastify";

const statusConfig = {
  paid: {
    label: "Đã thanh toán",
    variant: "default" as const,
    icon: CheckCircle2,
    className:
      "bg-green-500/10 text-green-700 hover:bg-green-500/20 border-green-500/20",
  },
  pending: {
    label: "Đang xử lý",
    variant: "secondary" as const,
    icon: Clock,
    className:
      "bg-yellow-500/10 text-yellow-700 hover:bg-yellow-500/20 border-yellow-500/20",
  },
  failed: {
    label: "Thất bại",
    variant: "destructive" as const,
    icon: XCircle,
    className:
      "bg-red-500/10 text-red-700 hover:bg-red-500/20 border-red-500/20",
  },
  refunded: {
    label: "Đã hoàn tiền",
    variant: "outline" as const,
    icon: RefreshCw,
    className:
      "bg-blue-500/10 text-blue-700 hover:bg-blue-500/20 border-blue-500/20",
  },
};

export default function BillingHistoryTab() {
  const { userAuth } = useAuthStore();

  // Filter invoices for current user
  const userInvoices = mockInvoices.filter(
    (invoice) => invoice.userId === userAuth?.id
  );

  const handleDownloadInvoice = (invoiceId: string) => {
    toast.success(`Đang tải xuống hóa đơn ${invoiceId}...`);
    // Implement actual download logic here
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  return (
    <div className="space-y-6">
      {/* Current Payment Method */}
      <Card className="overflow-hidden border-primary/20 bg-linear-to-br from-primary/5 via-transparent to-secondary/5">
        <CardContent className="p-6">
          <div className="flex items-start justify-between">
            <div className="flex gap-4">
              <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-primary/10">
                <CreditCard className="h-6 w-6 text-primary" />
              </div>
              <div className="space-y-1">
                <h3 className="font-semibold">Phương thức thanh toán</h3>
                <p className="text-sm text-muted-foreground">
                  {userInvoices[0]?.paymentMethod || "Chưa có thông tin"}
                </p>
                <p className="text-xs text-muted-foreground">
                  Hết hạn: 12/2026
                </p>
              </div>
            </div>
            <Button variant="outline" size="sm">
              Thay đổi
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* Billing Summary */}
      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                <DollarSign className="h-5 w-5 text-primary" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Tổng chi tiêu</p>
                <p className="text-2xl font-bold">
                  $
                  {userInvoices
                    .filter((inv) => inv.status === "paid")
                    .reduce((sum, inv) => sum + inv.amount, 0)}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-green-500/10">
                <CheckCircle2 className="h-5 w-5 text-green-600" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Đã thanh toán</p>
                <p className="text-2xl font-bold">
                  {userInvoices.filter((inv) => inv.status === "paid").length}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-blue-500/10">
                <Calendar className="h-5 w-5 text-blue-600" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">
                  Chu kỳ tiếp theo
                </p>
                <p className="text-sm font-semibold">
                  {userInvoices[0]?.periodEnd
                    ? formatDate(userInvoices[0].periodEnd)
                    : "N/A"}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Invoice History */}
      <div>
        <div className="mb-4 flex items-center justify-between">
          <h3 className="text-lg font-semibold">Lịch sử hóa đơn</h3>
          <Button variant="outline" size="sm">
            <Download className="mr-2 h-4 w-4" />
            Tải tất cả
          </Button>
        </div>

        {userInvoices.length === 0 ? (
          <Card>
            <CardContent className="flex flex-col items-center justify-center p-12 text-center">
              <FileText className="mb-4 h-12 w-12 text-muted-foreground/50" />
              <h3 className="mb-2 font-semibold">Chưa có hóa đơn</h3>
              <p className="text-sm text-muted-foreground">
                Bạn chưa có bất kỳ hóa đơn nào. Nâng cấp plan để bắt đầu!
              </p>
            </CardContent>
          </Card>
        ) : (
          <div className="space-y-3">
            {userInvoices.map((invoice) => {
              const StatusIcon = statusConfig[invoice.status].icon;
              return (
                <Card
                  key={invoice.id}
                  className="transition-all hover:shadow-md"
                >
                  <CardContent className="p-4">
                    <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                      <div className="flex items-start gap-4">
                        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                          <FileText className="h-5 w-5 text-primary" />
                        </div>
                        <div className="min-w-0 flex-1 space-y-1">
                          <div className="flex items-center gap-2">
                            <h4 className="font-semibold">
                              {invoice.planName}
                            </h4>
                            <Badge
                              variant={statusConfig[invoice.status].variant}
                              className={statusConfig[invoice.status].className}
                            >
                              <StatusIcon className="mr-1 h-3 w-3" />
                              {statusConfig[invoice.status].label}
                            </Badge>
                          </div>
                          <p className="text-sm text-muted-foreground">
                            {invoice.description}
                          </p>
                          <div className="flex flex-wrap gap-4 text-xs text-muted-foreground">
                            <span className="flex items-center gap-1">
                              <Calendar className="h-3 w-3" />
                              {formatDate(invoice.billingDate)}
                            </span>
                            <span className="flex items-center gap-1">
                              <CreditCard className="h-3 w-3" />
                              {invoice.paymentMethod}
                            </span>
                            <span className="flex items-center gap-1">
                              ID: {invoice.transactionId}
                            </span>
                          </div>
                        </div>
                      </div>

                      <div className="flex items-center gap-3 sm:flex-col sm:items-end">
                        <div className="text-right">
                          <p className="text-2xl font-bold">
                            {invoice.currency}
                            {invoice.amount}
                          </p>
                          <p className="text-xs text-muted-foreground">
                            {formatDate(invoice.periodStart)} -{" "}
                            {formatDate(invoice.periodEnd)}
                          </p>
                        </div>
                        {invoice.status === "paid" && invoice.downloadUrl && (
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => handleDownloadInvoice(invoice.id)}
                            className="shrink-0"
                          >
                            <Download className="h-4 w-4" />
                          </Button>
                        )}
                        {invoice.status === "failed" && (
                          <Button
                            size="sm"
                            variant="outline"
                            className="shrink-0"
                          >
                            Thử lại
                          </Button>
                        )}
                      </div>
                    </div>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
