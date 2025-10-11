import { DataTable } from "@/components/admin/DataTable";

interface UserTableProps {
  Users: IUser[];
  isLoading: boolean;
  onEdit?: (user: IUser) => void;
  onResetPassword?: (user: IUser) => void;
}

const getStatusColor = (status: string) => {
  switch (status) {
    case "active":
      return "bg-green-500";
    case "inactive":
      return "bg-red-500";
    case "pending":
      return "bg-yellow-500";
    default:
      return "bg-gray-500";
  }
};

export const UserTable = ({
  Users,
  isLoading,
  onEdit,
  onResetPassword,
}: UserTableProps) => {
  const columns = [
    {
      header: "STT",
      accessor: (_: IUser, index: number) => index + 1,
    },
    {
      header: "Tên Quản trị viên",
      accessor: (user: IUser) => user.name,
    },
    {
      header: "Email",
      accessor: (user: IUser) => user.email,
    },
    // {
    //   header: "Số điện thoại",
    //   accessor: (user: IUser) => user.phone,
    // },
    {
      header: "Trạng thái",
      accessor: (user: IUser) => (
        <div className="inline-flex items-center justify-center gap-2">
          <span
            className={`h-2 w-2 rounded-full ${getStatusColor(user.status)}`}
          />
          <span className="capitalize">{user.status}</span>
        </div>
      ),
    },
  ];

  const actions = [];

  if (onEdit) {
    actions.push({
      label: "Edit",
      onClick: onEdit,
    });
  }

  if (onResetPassword) {
    actions.push({
      label: "Reset Password",
      onClick: onResetPassword,
    });
  }

  return (
    <DataTable
      data={Users}
      isLoading={isLoading}
      columns={columns}
      actions={actions}
      emptyMessage="No users found"
    />
  );
};
