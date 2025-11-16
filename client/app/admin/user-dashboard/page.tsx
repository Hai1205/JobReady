"use client";

import { useCallback, useState, useEffect } from "react";
import dynamic from "next/dynamic";
import { RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle } from "@/components/ui/card";
import { EUserRole, EUserStatus } from "@/types/enum";
import { useUserStore } from "@/stores/userStore";
import { useAuthStore } from "@/stores/authStore";
import { toast } from "react-toastify";
import { DashboardHeader } from "@/components/comons/admin/DashboardHeader";
import CreateUserDialog from "@/components/comons/admin/userDashboard/CreateUserDialog";
import UpdateUserDialog from "@/components/comons/admin/userDashboard/UpdateUserDialog";
import { TableSearch } from "@/components/comons/admin/adminTable/TableSearch";
import { UserFilter } from "@/components/comons/admin/userDashboard/UserFilter";
import { UserTable } from "@/components/comons/admin/userDashboard/UserTable";
import { ExtendedUserData } from "@/components/comons/admin/userDashboard/constant";
import TableDashboardSkeleton from "@/components/comons/layout/TableDashboardSkeleton";
import ConfirmationDialog from "@/components/comons/layout/ConfirmationDialog";

export type UserFilterType = "status" | "role";
const userInitialFilters = { status: [] as string[], role: [] as string[] };

function UserDashboardPage() {
  const {
    usersTable,
    fetchAllUsersInBackground,
    createUser,
    updateUser,
    deleteUser,
    isLoading: storeLoading,
  } = useUserStore();
  const { resetPassword } = useAuthStore();

  const [searchQuery, setSearchQuery] = useState("");
  const [avatarFile, setAvatarFile] = useState<File | null>(null);
  const [previewAvatar, setPreviewAvatar] = useState<string>("");
  const [isCreateUserOpen, setIsCreateUserOpen] = useState(false);
  const [isUpdateUserOpen, setIsUpdateUserOpen] = useState(false);

  const [activeFilters, setActiveFilters] = useState<{
    status: string[];
    role: string[];
  }>(userInitialFilters);
  const [filteredUsers, setFilteredUsers] = useState<IUser[]>([]);

  useEffect(() => {
    // Fetch users in background
    fetchAllUsersInBackground();
  }, []);

  const filterData = useCallback(
    (query: string, filters: { status: string[]; role: string[] }) => {
      let results = [...usersTable];

      if (query.trim()) {
        const searchTerms = query.toLowerCase().trim();
        results = results.filter(
          (user) =>
            user.fullname.toLowerCase().includes(searchTerms) ||
            user.email.toLowerCase().includes(searchTerms)
        );
      }

      if (filters.status.length > 0) {
        results = results.filter((user) =>
          filters.status.includes(user.status || "")
        );
      }

      if (filters.role.length > 0) {
        results = results.filter((user) =>
          filters.role.includes(user.role || "")
        );
      }

      setFilteredUsers(results);
    },
    [usersTable]
  );

  useEffect(() => {
    filterData(searchQuery, activeFilters);
  }, [usersTable, searchQuery, activeFilters, filterData]);

  const handleSearch = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();

      filterData(searchQuery, activeFilters);
    },
    [searchQuery, activeFilters, filterData]
  );

  const toggleFilter = (value: string, type: UserFilterType) => {
    setActiveFilters((prev) => {
      const updated = { ...prev };
      if (updated[type]?.includes(value)) {
        updated[type] = updated[type].filter((item) => item !== value);
      } else {
        updated[type] = [...(updated[type] || []), value];
      }
      return updated;
    });
  };

  const clearFilters = () => {
    setActiveFilters(userInitialFilters);
    setSearchQuery("");
    setFilteredUsers(usersTable);
    closeMenuMenuFilters();
  };

  const applyFilters = () => {
    filterData(searchQuery, activeFilters);
    closeMenuMenuFilters();
  };

  const handleRefresh = () => {
    setActiveFilters(userInitialFilters);
    setSearchQuery("");
    fetchAllUsersInBackground();
  };

  const [openMenuFilters, setOpenMenuFilters] = useState(false);
  const closeMenuMenuFilters = () => setOpenMenuFilters(false);

  const [dialogKey, setDialogKey] = useState(0);

  const [data, setData] = useState<ExtendedUserData | null>(null);

  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [userToDelete, setUserToDelete] = useState<IUser | null>(null);

  const [resetPasswordDialogOpen, setResetPasswordDialogOpen] = useState(false);
  const [userToResetPassword, setUserToResetPassword] = useState<IUser | null>(
    null
  );

  // Combined dialog state
  const isDialogOpen = deleteDialogOpen || resetPasswordDialogOpen;
  const isDeleteDialog = deleteDialogOpen;

  const defaultUser: ExtendedUserData = {
    id: "",
    username: "",
    email: "",
    password: "",
    fullname: "",
    role: EUserRole.USER,
    status: EUserStatus.PENDING,
  };

  const handleChange = (
    field: keyof ExtendedUserData,
    value: string | string[] | boolean
  ) => {
    setData((prev) => {
      if (!prev) {
        return { ...defaultUser, [field]: value } as ExtendedUserData;
      }

      return { ...prev, [field]: value };
    });
  };

  const handleAvatarChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setAvatarFile(file);
      setPreviewAvatar(URL.createObjectURL(file));
    }
  };

  const handleUpdate = async () => {
    if (!data) {
      return;
    }

    const res = await updateUser(
      data.id,
      data.fullname,
      avatarFile || null,
      data.role,
      data.status
    );

    if (res?.data?.success) {
      toast.success("User updated successfully");
    } else {
      toast.error("Failed to update user");
    }

    setIsUpdateUserOpen(false);
  };

  const handleCreate = async () => {
    if (!data) {
      return;
    }

    const res = await createUser(
      data.email,
      data.password || "",
      data.fullname,
      avatarFile || null,
      data.role,
      data.status
    );

    if (res?.data?.success) {
      toast.success("User created successfully");
    } else {
      toast.error("Failed to create user");
    }

    setIsCreateUserOpen(false);
  };

  const onDelete = (user: IUser) => {
    setUserToDelete(user);
    setDeleteDialogOpen(true);
  };

  const onResetPassword = async (user: IUser) => {
    setUserToResetPassword(user);
    setResetPasswordDialogOpen(true);
  };

  const handleDialogClose = (open: boolean) => {
    if (!open) {
      setDeleteDialogOpen(false);
      setResetPasswordDialogOpen(false);
      setUserToDelete(null);
      setUserToResetPassword(null);
    }
  };

  const handleDialogConfirm = async () => {
    if (isDeleteDialog && userToDelete) {
      await deleteUser(userToDelete.id);
      setDeleteDialogOpen(false);
      setUserToDelete(null);
    } else if (!isDeleteDialog && userToResetPassword) {
      await resetPassword(userToResetPassword.email);
      toast.success("Password reset email sent successfully");
      setResetPasswordDialogOpen(false);
      setUserToResetPassword(null);
    }
  };

  const onUpdate = async (user: IUser) => {
    setData(user);
    setIsUpdateUserOpen(true);
  };

  // Show skeleton loading when there's no cached data
  if (storeLoading && usersTable.length === 0) {
    return <TableDashboardSkeleton />;
  }

  return (
    <div className="space-y-4">
      <DashboardHeader
        title="User Dashboard"
        onCreateClick={() => {
          setData(defaultUser);
          setIsCreateUserOpen(true);
        }}
        createButtonText="Create User"
      />

      {/* Use consistent key to avoid hydration issues */}
      <CreateUserDialog
        key={`create-${dialogKey}-${isCreateUserOpen ? "open" : "closed"}`}
        isOpen={isCreateUserOpen}
        onOpenChange={(open) => {
          setIsCreateUserOpen(open);
          if (!open) {
            setData(null);
            setDialogKey((prev) => prev + 1);
          }
        }}
        onChange={handleChange}
        onUserCreated={handleCreate}
        data={data}
        previewAvatar={previewAvatar}
        handleAvatarChange={handleAvatarChange}
      />

      <UpdateUserDialog
        key={`update-${dialogKey}-${isUpdateUserOpen ? "open" : "closed"}`}
        isOpen={isUpdateUserOpen}
        onOpenChange={(open) => {
          setIsUpdateUserOpen(open);
          if (!open) {
            setData(null);
            setDialogKey((prev) => prev + 1);
          }
        }}
        onChange={handleChange}
        data={data}
        onUserUpdated={handleUpdate}
        previewAvatar={previewAvatar}
        handleAvatarChange={handleAvatarChange}
      />

      <div className="space-y-4">
        <Card className="border-border/50 shadow-lg bg-gradient-to-br from-card to-card/80 backdrop-blur-sm">
          <CardHeader className="pb-4 border-b border-border/30">
            <div className="flex items-center justify-between">
              <CardTitle />

              <div className="flex items-center gap-3">
                <TableSearch
                  handleSearch={handleSearch}
                  searchQuery={searchQuery}
                  setSearchQuery={setSearchQuery}
                  placeholder="Search Users..."
                />

                <Button
                  variant="secondary"
                  size="sm"
                  className="h-9 gap-2 px-4 bg-gradient-to-r from-secondary/80 to-secondary hover:from-secondary hover:to-secondary/90 shadow-md hover:shadow-lg hover:shadow-secondary/20 transition-all duration-200 hover:scale-105"
                  onClick={async () => {
                    handleRefresh();
                  }}
                >
                  <RefreshCw className="h-4 w-4" />
                  Refresh
                </Button>

                <UserFilter
                  openMenuFilters={openMenuFilters}
                  setOpenMenuFilters={setOpenMenuFilters}
                  activeFilters={activeFilters}
                  toggleFilter={toggleFilter}
                  clearFilters={clearFilters}
                  applyFilters={applyFilters}
                  closeMenuMenuFilters={closeMenuMenuFilters}
                />
              </div>
            </div>
          </CardHeader>

          <UserTable
            users={filteredUsers}
            isLoading={storeLoading && usersTable.length === 0}
            onUpdate={onUpdate}
            onDelete={onDelete}
            onResetPassword={onResetPassword}
          />
        </Card>
      </div>

      <ConfirmationDialog
        open={deleteDialogOpen || resetPasswordDialogOpen}
        onOpenChange={handleDialogClose}
        title={isDeleteDialog ? "Delete User" : "Đặt lại mật khẩu"}
        description={
          isDeleteDialog
            ? "Hành động này không thể hoàn tác. Điều này sẽ xóa vĩnh viễn người dùng và loại bỏ nó khỏi máy chủ của chúng tôi."
            : `Bạn có muốn gửi email đặt lại mật khẩu cho ${
                userToResetPassword?.fullname || userToResetPassword?.email
              }?`
        }
        confirmText={isDeleteDialog ? "Delete" : "Gửi email"}
        cancelText="Hủy"
        isDestructive={isDeleteDialog}
        onConfirm={handleDialogConfirm}
      />
    </div>
  );
}

export default dynamic(() => Promise.resolve(UserDashboardPage), {
  ssr: false,
});
