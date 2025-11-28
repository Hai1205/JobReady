import { formatDateAgo } from "@/lib/utils";
import { DataTable } from "../adminTable/DataTable";
import { Pencil, Download, Trash2 } from "lucide-react";

interface ICVTableProps {
  CVs: ICV[];
  isLoading: boolean;
  onUpdate?: (cv: ICV) => void;
  onDownload?: (cv: ICV) => void;
  onDelete?: (cv: ICV) => void;
}

const getPrivacyColor = (isVisibility: boolean) => {
  return isVisibility ? "bg-green-500" : "bg-red-500";
};

export const CVTable = ({
  CVs,
  isLoading,
  onUpdate,
  onDownload,
  onDelete,
}: ICVTableProps) => {
  const columns = [
    {
      header: "STT",
      accessor: (_: ICV, index: number) => index + 1,
    },
    {
      header: "Tiêu đề",
      accessor: (cv: ICV) => cv.title,
    },
    {
      header: "Chế độ",
      accessor: (cv: ICV) => (
        <div className="inline-flex items-center justify-center gap-2">
          <span
            className={`h-2 w-2 rounded-full ${getPrivacyColor(
              cv.isVisibility
            )}`}
          />
          <span className="capitalize">
            {cv.isVisibility ? "Công khai" : "Riêng tư"}
          </span>
        </div>
      ),
    },
    {
      header: "Ngày tạo",
      accessor: (cv: ICV) => formatDateAgo(cv.createdAt || ""),
    },
    {
      header: "Ngày cập nhật",
      accessor: (cv: ICV) => formatDateAgo(cv.updatedAt || ""),
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

  if (onDownload) {
    actions.push({
      label: "Tải xuống",
      onClick: onDownload,
      icon: Download,
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
      data={CVs}
      isLoading={isLoading}
      columns={columns}
      actions={actions}
      emptyMessage="Không tìm thấy CV nào"
    />
  );
};
