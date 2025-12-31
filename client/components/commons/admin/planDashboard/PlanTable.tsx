import { DataTable } from "../adminTable/DataTable";
import { Pencil, Trash2 } from "lucide-react";
import { PaginationData } from "@/components/commons/pagination/PaginationControls";

interface PlanTableProps {
  plans: IPlan[];
  isLoading: boolean;
  onUpdate?: (plan: IPlan) => void;
  onDelete?: (plan: IPlan) => void;
  paginationData?: PaginationData;
  onPageChange?: (page: number) => void;
  showPagination?: boolean;
}

export const PlanTable = ({
  plans,
  isLoading,
  onUpdate,
  onDelete,
  paginationData,
  onPageChange,
  showPagination = false,
}: PlanTableProps) => {
  const columns = [
    {
      header: "STT",
      accessor: (_: IPlan, index: number) => {
        // Calculate correct index based on current page
        const baseIndex = paginationData
          ? (paginationData.currentPage - 1) * paginationData.pageSize
          : 0;
        return baseIndex + index + 1;
      },
    },
    {
      header: "Tên gói",
      accessor: (plan: IPlan) => plan.name,
    },
    {
      header: "Loại",
      accessor: (plan: IPlan) => plan.type,
    },
    {
      header: "Giá",
      accessor: (plan: IPlan) => plan.price,
    },
  ];

  const actions = [];

  if (onUpdate) {
    actions.push({
      label: "Sửa",
      onClick: onUpdate,
      icon: Pencil,
    });
  }

  if (onDelete) {
    actions.push({
      label: "Xoá",
      onClick: onDelete,
      icon: Trash2,
      className: "hover:bg-destructive/10 hover:text-destructive",
    });
  }

  return (
    <DataTable
      data={plans}
      isLoading={isLoading}
      columns={columns}
      actions={actions}
      emptyMessage="Không tìm thấy người dùng nào"
      showPagination={showPagination}
      paginationData={paginationData}
      onPageChange={onPageChange}
    />
  );
};
