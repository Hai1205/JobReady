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
import {
  FilterType,
  initialFilters,
} from "@/components/comons/admin/adminTable/SharedFilter";
import { mockUsers } from "@/services/mockData";

function UserDashboardPage() {
  const { usersTable, getAllUsers, createUser, updateUser, deleteUser } =
    useUserStore();
  const { resetPassword } = useAuthStore();

  const [searchQuery, setSearchQuery] = useState("");
  const [avatarFile, setAvatarFile] = useState<File | null>(null);
  const [previewAvatar, setPreviewAvatar] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isCreateUserOpen, setIsCreateUserOpen] = useState(false);
  const [isUpdateUserOpen, setIsUpdateUserOpen] = useState(false);

  const [activeFilters, setActiveFilters] = useState<{
    status: string[];
    role: string[];
    privacy: string[];
  }>(initialFilters);
  const [filteredUsers, setFilteredUsers] = useState<IUser[] | []>(mockUsers);

  const fetchData = useCallback(async () => {
    setIsLoading(true);
    await getAllUsers();

    setIsLoading(false);
  }, [getAllUsers]);

  useEffect(() => {
    fetchData();
  }, [getAllUsers]);

  const filterData = useCallback(
    (
      query: string,
      filters: { status: string[]; role: string[]; privacy: string[] }
    ) => {
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

  const toggleFilter = (value: string, type: FilterType) => {
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
    setActiveFilters(initialFilters);
    setSearchQuery("");
    setFilteredUsers(usersTable);
    closeMenuMenuFilters();
  };

  const applyFilters = () => {
    filterData(searchQuery, activeFilters);
    closeMenuMenuFilters();
  };

  const handleRefresh = () => {
    setActiveFilters(initialFilters);
    setSearchQuery("");
    fetchData();
  };

  const [openMenuFilters, setOpenMenuFilters] = useState(false);
  const closeMenuMenuFilters = () => setOpenMenuFilters(false);

  const [dialogKey, setDialogKey] = useState(0);

  const [data, setData] = useState<ExtendedUserData | null>(null);

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

  const handleResetPassword = async (user: IUser) => {
    if (user) {
      const res = await resetPassword(user.email);

      if (res?.data?.success) {
        toast.success("Password reset email sent successfully");
      } else {
        toast.error("Failed to send password reset email");
      }
    }
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

  const onDelete = async (user: IUser) => {
    await deleteUser(user.id);
  };

  const onUpdate = async (user: IUser) => {
    setData(user);
    setIsUpdateUserOpen(true);
  };

  const onResetPassword = async (user: IUser) => {
    if (!user) {
      return;
    }

    await resetPassword(user?.email);
  };

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
            isLoading={isLoading}
            onUpdate={onUpdate}
            onDelete={onDelete}
            onResetPassword={onResetPassword}
          />
        </Card>
      </div>
    </div>
  );
}

export default dynamic(() => Promise.resolve(UserDashboardPage), {
  ssr: false,
});
