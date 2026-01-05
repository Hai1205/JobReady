import { EyeIcon } from "lucide-react";
import { DataTable } from "../adminTable/DataTable";
import { PaginationData } from "@/components/commons/pagination/PaginationControls";

interface InvoiceTableProps {
  invoices: IInvoice[];
  isLoading: boolean;
  paginationData?: PaginationData;
  onPageChange?: (page: number) => void;
  showPagination?: boolean;
  onView?: (invoice: IInvoice) => void;
}

const getStatusColor = (status: string) => {
  switch (status) {
    case "active":
      return "bg-green-500";
    case "banned":
      return "bg-red-500";
    case "pending":
      return "bg-yellow-500";
    default:
      return "bg-gray-500";
  }
};

export const InvoiceTable = ({
  invoices,
  isLoading,
  paginationData,
  onPageChange,
  showPagination = false,
  onView,
}: InvoiceTableProps) => {
  const columns = [
    {
      header: "STT",
      accessor: (_: IInvoice, index: number) => {
        // Calculate correct index based on current page
        const baseIndex = paginationData
          ? (paginationData.currentPage - 1) * paginationData.pageSize
          : 0;
        return baseIndex + index + 1;
      },
    },
    {
      header: "Tên gói",
      accessor: (invoice: IInvoice) => invoice.planTitle,
    },
    {
      header: "Giá tiền",
      accessor: (invoice: IInvoice) => invoice.amount,
    },
    {
      header: "Phương thức thanh toán",
      accessor: (invoice: IInvoice) => invoice.paymentMethod,
    },
    {
      header: "Trạng thái",
      accessor: (invoice: IInvoice) => (
        <div className="inline-flex items-center justify-center gap-2">
          <span
            className={`h-2 w-2 rounded-full ${getStatusColor(invoice.status)}`}
          />
          <span className="capitalize">{invoice.status}</span>
        </div>
      ),
    },
  ];

  const actions = [];

  if (onView) {
    actions.push({
      label: "Xem",
      onClick: onView,
      icon: EyeIcon,
    });
  }

  return (
    <DataTable
      data={invoices}
      isLoading={isLoading}
      columns={columns}
      actions={actions}
      emptyMessage="Không tìm thấy hóa đơn nào"
      showPagination={showPagination}
      paginationData={paginationData}
      onPageChange={onPageChange}
    />
  );
};
