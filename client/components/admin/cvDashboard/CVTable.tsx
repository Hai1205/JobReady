import { DataTable } from "@/components/admin/DataTable";

interface ICVTableProps {
  CVs: ICV[];
  isLoading: boolean;
  onEdit?: (cv: ICV) => void;
}

const getStatusColor = (status: string) => {
  switch (status) {
    case "active":
      return "bg-green-500";
    case "inactive":
      return "bg-red-500";
    default:
      return "bg-gray-500";
  }
};

const getCategoryColor = (status: string) => {
  switch (status) {
    case "Hồ sơ du học":
      return "bg-green-500";
    case "Chi phí":
      return "bg-yellow-500";
    case "Visa":
      return "bg-green-500";
    case "Ngôn ngữ":
      return "bg-yellow-500";
    case "Định cư":
      return "bg-green-500";
    case "Dịch vụ":
      return "bg-yellow-500";
    default:
      return "bg-gray-500";
  }
};

export const CVTable = ({ CVs, isLoading, onEdit }: ICVTableProps) => {
  const columns = [
    {
      header: "STT",
      accessor: (_: ICV, index: number) => index + 1,
    },
    // {
    //   header: "Câu hỏi",
    //   accessor: (cv: ICV) => cv.question,
    // },
    // {
    //   header: "Câu trả lời",
    //   accessor: (cv: ICV) => cv.answer,
    // },
    // {
    //   header: "Danh mục",
    //   accessor: (cv: ICV) => (
    //     <div className="inline-flex items-center justify-center gap-2">
    //       <span
    //         className={`h-2 w-2 rounded-full ${getCategoryColor(
    //           cv?.category || ""
    //         )}`}
    //       />
    //       <span className="capitalize">{cv?.category}</span>
    //     </div>
    //   ),
    // },
    // {
    //   header: "Trạng thái",
    //   accessor: (cv: ICV) => (
    //     <div className="inline-flex items-center justify-center gap-2">
    //       <span
    //         className={`h-2 w-2 rounded-full ${getStatusColor(
    //           cv?.status || ""
    //         )}`}
    //       />
    //       <span className="capitalize">{cv.status}</span>
    //     </div>
    //   ),
    // },
  ];

  return (
    <DataTable
      data={CVs}
      isLoading={isLoading}
      columns={columns}
      actions={
        onEdit
          ? [
              {
                label: "Chỉnh sửa",
                onClick: onEdit,
              },
            ]
          : []
      }
      emptyMessage="Không tìm thấy câu hỏi thường gặp nào"
    />
  );
};
