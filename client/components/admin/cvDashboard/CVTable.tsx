import { DataTable } from "@/components/admin/DataTable";

interface ICVTableProps {
  CVs: ICV[];
  isLoading: boolean;
  onDownload?: (cv: ICV) => void;
}

export const CVTable = ({ CVs, isLoading, onDownload }: ICVTableProps) => {
  const columns = [
    {
      header: "No",
      accessor: (_: ICV, index: number) => index + 1,
    },
    {
      header: "Title",
      accessor: (cv: ICV) => cv.title,
    },
    {
      header: "Created At",
      accessor: (cv: ICV) => cv.createdAt,
    },
    {
      header: "Updated At",
      accessor: (cv: ICV) => cv.updatedAt,
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
