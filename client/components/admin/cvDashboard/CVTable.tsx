import { DataTable } from "@/components/admin/DataTable";

interface ICVTableProps {
  CVs: ICV[];
  isLoading: boolean;
  onView?: (cv: ICV) => void;
}

export const CVTable = ({ CVs, isLoading, onView }: ICVTableProps) => {
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
        onView
          ? [
              {
                label: "View",
                onClick: onView,
              },
            ]
          : []
      }
      emptyMessage="No CVs found"
    />
  );
};
