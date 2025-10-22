"use client";

import { useCallback, useState, useEffect } from "react";
import dynamic from "next/dynamic";
import { RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle } from "@/components/ui/card";
import { DashboardHeader } from "@/components/admin/DashboardHeader";
import CreateUserDialog from "@/components/admin/userDashboard/CreateUserDialog";
import UpdateUserDialog from "@/components/admin/userDashboard/UpdateUserDialog";
import { UserFilter } from "@/components/admin/userDashboard/UserFilter";
import { UserTable } from "@/components/admin/userDashboard/UserTable";
import { toast } from "react-toastify";
import { useUserStore } from "@/stores/userStore";
import { useAuthStore } from "@/stores/authStore";
import { EUserRole, EUserStatus } from "@/types/enum";
import { TableSearch } from "@/components/admin/adminTable/TableSearch";

// Initialize empty filters
const initialFilters = { status: [] as string[], role: [] as string[] };

function UserDashboardPage() {
  const { isLoading, getAllUsers, createUser, updateUser } = useUserStore();
  const { resetPassword } = useAuthStore();

  const [searchQuery, setSearchQuery] = useState("");

  const [isCreateUserOpen, setIsCreateUserOpen] = useState(false);
  const [isUpdateUserOpen, setIsUpdateUserOpen] = useState(false);
  // const [isResetPasswordOpen, setIsResetPasswordOpen] = useState(false);

  const [activeFilters, setActiveFilters] = useState<{
    status: string[];
    role: string[];
  }>(initialFilters);
  const [allUsers, setAllUsers] = useState<IUser[]>([]);
  const [filteredUsers, setFilteredUsers] = useState<IUser[]>([]);
  const [avatarFile, setAvatarFile] = useState<File | null>(null);
  const [previewAvatar, setPreviewAvatar] = useState<string>("");

  const defaultUser: ExtendedUserData = {
    id: "",
    username: "",
    email: "",
    password: "",
    fullname: "",
    role: EUserRole.USER,
    status: EUserStatus.PENDING,
  };

  const handleAvatarChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setAvatarFile(file);
      setPreviewAvatar(URL.createObjectURL(file));
    }
  };

  const fetchData = useCallback(() => {
    return async () => {
      const res = await getAllUsers();
      const data = res?.data?.users || [];
      setAllUsers(data);
      setFilteredUsers(data);
    };
  }, []);

  useEffect(() => {
    fetchData();
  }, [getAllUsers]);

  // Function to filter data based on query and activeFilters
  const filterData = useCallback(
    (query: string, filters: { status: string[] }) => {
      let results = [...allUsers];

      // Filter by search query
      if (query.trim()) {
        const searchTerms = query.toLowerCase().trim();
        results = results.filter(
          (user) =>
            user.fullname.toLowerCase().includes(searchTerms) ||
            user.email.toLowerCase().includes(searchTerms)
        );
      }

      // Filter by status
      if (filters.status.length > 0) {
        results = results.filter((user) =>
          filters.status.includes(user.status || "")
        );
      }

      setFilteredUsers(results);
    },
    [allUsers]
  );

  const handleSearch = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      filterData(searchQuery, activeFilters);
    },
    [searchQuery, activeFilters, filterData]
  );

  // Toggle filter without auto-filtering
  const toggleFilter = (value: string, type: "status" | "role") => {
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
    setFilteredUsers(allUsers); // Reset filtered data
    closeMenuMenuFilters();
  };

  const applyFilters = () => {
    filterData(searchQuery, activeFilters);
    closeMenuMenuFilters();
  };

  const [openMenuFilters, setOpenMenuFilters] = useState(false);
  const closeMenuMenuFilters = () => setOpenMenuFilters(false);

  const [dialogKey, setDialogKey] = useState(0);
  type ExtendedUserData = Omit<IUser, "status"> & {
    status: EUserStatus;
    role: EUserRole;
    password?: string;
  };

  const [data, setData] = useState<ExtendedUserData | null>(null);

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

  const handleUpdate = async () => {
    if (data) {
      await updateUser(
        data.id,
        data.fullname,
        avatarFile || null,
        data.role,
        data.status
      );

      setIsUpdateUserOpen(false);
    }
  };

  const handleResetPassword = async (user: IUser) => {
    if (user) {
      const result = await resetPassword(user.email);
      if (result?.data?.success) {
        toast.success("Password reset email sent successfully");
      } else {
        toast.error("Failed to send password reset email");
      }
    }
  };

  const handleCreate = async () => {
    if (data) {
      await createUser(
        data.email,
        data.password || "",
        data.fullname,
        avatarFile || null,
        data.role,
        data.status
      );

      setIsCreateUserOpen(false);
    }
  };

  const handleRefresh = () => {
    setActiveFilters(initialFilters);
    setSearchQuery("");
    fetchData();
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
            // Reset data to null when closing dialog
            setData(null);
            setDialogKey((prev) => prev + 1);
          }
        }}
        onChange={handleChange}
        onUserCreated={handleCreate}
        data={data}
        previewAvatar={previewAvatar}
        handleAvatarChange={handleAvatarChange}
        isLoading={isLoading}
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
        previewAvatar={previewAvatar}
        handleAvatarChange={handleAvatarChange}
        onUserUpdated={handleUpdate}
        isLoading={isLoading}
      />

      <div className="space-y-6">
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
            Users={filteredUsers}
            isLoading={isLoading}
            onEdit={(user) => {
              setData(user);
              setIsUpdateUserOpen(true);
            }}
            onResetPassword={(user) => {
              handleResetPassword(user);
            }}
          />
        </Card>
      </div>
    </div>
  );
}

export default dynamic(() => Promise.resolve(UserDashboardPage), {
  ssr: false,
});
