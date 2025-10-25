import { formatDateAgo } from "@/lib/utils";
import { DataTable } from "../adminTable/DataTable";

interface ICVTableProps {
  CVs: ICV[];
  isLoading: boolean;
  onEdit?: (cv: ICV) => void;
  onDownload?: (cv: ICV) => void;
}

export const CVTable = ({
  CVs,
  isLoading,
  onEdit,
  onDownload,
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
      header: "Ngày tạo",
      accessor: (cv: ICV) => formatDateAgo(cv.createdAt || ""),
    },
    {
      header: "Ngày cập nhật",
      accessor: (cv: ICV) => formatDateAgo(cv.updatedAt || ""),
    },
  ];

  const actions = [];

  if (onEdit) {
    actions.push({
      label: "Edit",
      onClick: onEdit,
    });
  }

  if (onDownload) {
    actions.push({
      label: "Download",
      onClick: onDownload,
    });
  }

  return (
    <DataTable
      data={CVs}
      isLoading={isLoading}
      columns={columns}
      actions={actions}
      emptyMessage="No CVs found"
    />
  );
};
