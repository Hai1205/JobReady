import { DataTable } from "@/components/admin/DataTable";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

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

const getRoleColor = (role: string) => {
  switch (role) {
    case "admin":
      return "bg-blue-500";
    case "user":
      return "bg-gray-500";
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
      header: "No",
      accessor: (_: IUser, index: number) => index + 1,
    },
    {
      header: "User",
      accessor: (user: IUser) => (
        <div className="flex items-center gap-3">
          <Avatar className="h-9 w-9">
            <AvatarImage src={user?.avatarUrl} alt={user?.fullname || "User"} />
            <AvatarFallback>
              {user?.fullname ? user.fullname.substring(0, 2) : "User"}
            </AvatarFallback>
          </Avatar>

          <div className="flex flex-col">
            <span className="font-medium hover:underline">
              {user?.fullname || "Unknown Artist"}
            </span>

            <span className="text-sm text-muted-foreground hover:underline">
              @{user?.username || "unknown"}
            </span>
            <span className="text-sm text-muted-foreground hover:underline">
              {user?.email || "unknown"}
            </span>
          </div>
        </div>
      ),
    },
    {
      header: "Role",
      accessor: (user: IUser) => (
        <div className="inline-flex items-center justify-center gap-2">
          <span className={`h-2 w-2 rounded-full ${getRoleColor(user.role)}`} />
          <span className="capitalize">{user.role}</span>
        </div>
      ),
    },
    {
      header: "Status",
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
