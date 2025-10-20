import { DataTable } from "@/components/admin/DataTable";
import { formatDateAgo } from "@/lib/utils";

interface ICVTableProps {
  CVs: ICV[];
  isLoading: boolean;
  onDownload?: (cv: ICV) => void;
}

export const CVTable = ({ CVs, isLoading, onDownload }: ICVTableProps) => {
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

  return (
    <DataTable
      data={CVs}
      isLoading={isLoading}
      columns={columns}
      actions={
        onDownload
          ? [
              {
                label: "Download",
                onClick: onDownload,
              },
            ]
          : []
      }
      emptyMessage="No CVs found"
    />
  );
};
