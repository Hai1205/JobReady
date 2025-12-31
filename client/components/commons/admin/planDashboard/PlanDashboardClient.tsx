"use client";

import { useCallback, useState, useEffect } from "react";
import { RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle } from "@/components/ui/card";
import { usePagination } from "@/hooks/use-pagination";
import { toast } from "react-toastify";
import { DashboardHeader } from "@/components/commons/admin/DashboardHeader";
import { TableSearch } from "@/components/commons/admin/adminTable/TableSearch";
import ConfirmationDialog from "@/components/commons/layout/ConfirmationDialog";
import TableDashboardSkeleton from "../../layout/TableDashboardSkeleton";
import CreatePlanDialog from "./CreatePlanDialog";
import UpdatePlanDialog from "./UpdatePlanDialog";
import { PlanTable } from "./PlanTable";
import { usePlanStore } from "@/stores/planStore";

export default function PlanDashboardClient() {
  const {
    plansTable,
    fetchAllPlansInBackground,
    createPlan,
    updatePlan,
    deletePlan,
    getAllPlans,
  } = usePlanStore();

  const [searchQuery, setSearchQuery] = useState("");
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isUpdateOpen, setIsUpdateOpen] = useState(false);

  const [filtered, setFiltered] = useState<IPlan[]>([]);

  // Pagination
  const { paginationState, paginationData, setPage } = usePagination({
    initialPage: 1,
    initialPageSize: 10,
  });

  useEffect(() => {
    // Fetch in background to update cache
    fetchAllPlansInBackground();
  }, []);

  const filterData = useCallback(
    (query: string) => {
      let results = [...plansTable];

      if (query.trim()) {
        const searchTerms = query.toLowerCase().trim();
        results = results.filter(
          (plan) =>
            plan.name.toLowerCase().includes(searchTerms) ||
            plan.type.toLowerCase().includes(searchTerms) ||
            plan.description.toLowerCase().includes(searchTerms)
        );
      }

      setFiltered(results);
    },
    [plansTable]
  );

  useEffect(() => {
    filterData(searchQuery);
  }, [plansTable, searchQuery, filterData]);

  // Update pagination when filtered plansTable change
  useEffect(() => {
    paginationData.totalElements = filtered.length;
    paginationData.totalPages = Math.ceil(
      filtered.length / paginationState.pageSize
    );
  }, [filtered.length, paginationState.pageSize]);

  // Paginate filtered plansTable
  const paginated = filtered.slice(
    (paginationState.page - 1) * paginationState.pageSize,
    paginationState.page * paginationState.pageSize
  );

  const handleSearch = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();

      filterData(searchQuery);
    },
    [searchQuery, filterData]
  );

  const handleRefresh = () => {
    setSearchQuery("");
    getAllPlans();
  };

  const [openMenuFilters, setOpenMenuFilters] = useState(false);
  const closeMenuMenuFilters = () => setOpenMenuFilters(false);

  const [dialogKey, setDialogKey] = useState(0);

  const [data, setData] = useState<IPlan | null>(null);

  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [planToDelete, setPlanToDelete] = useState<IPlan | null>(null);

  const [resetPasswordDialogOpen, setResetPasswordDialogOpen] = useState(false);
  const [userToResetPassword, setUserToResetPassword] = useState<IPlan | null>(
    null
  );

  const defaultPlan: IPlan = {
    id: "",
    type: "",
    name: "",
    price: 0,
    currency: "",
    period: "",
    description: "",
    features: [],
    isRecommended: false,
    isPopular: false,
  };

  const handleChange = (
    field: keyof IPlan,
    value: string | string[] | boolean
  ) => {
    setData((prev) => {
      if (!prev) {
        return { ...defaultPlan, [field]: value } as IPlan;
      }

      return { ...prev, [field]: value };
    });
  };

  const handleUpdate = async () => {
    if (!data) {
      return;
    }

    const res = await updatePlan(
      data.id,
      data.name,
      data.type,
      data.price,
      data.currency,
      data.period,
      data.description,
      data.features,
      data.isRecommended,
      data.isPopular
    );

    if (res?.data?.success) {
      toast.success("User updated successfully");
    } else {
      toast.error("Failed to update plan");
    }

    setIsUpdateOpen(false);
  };

  const handleCreate = async () => {
    if (!data) {
      return;
    }

    const res = await createPlan(
      data.name,
      data.type,
      data.price,
      data.currency,
      data.period,
      data.description,
      data.features,
      data.isRecommended,
      data.isPopular
    );

    if (res?.data?.success) {
      toast.success("User created successfully");
    } else {
      toast.error("Failed to create plan");
    }

    setIsCreateOpen(false);
  };

  const onDelete = (plan: IPlan) => {
    setPlanToDelete(plan);
    setDeleteDialogOpen(true);
  };

  const handleDialogClose = (open: boolean) => {
    if (!open) {
      setDeleteDialogOpen(false);
      setResetPasswordDialogOpen(false);
      setPlanToDelete(null);
      setUserToResetPassword(null);
    }
  };

  const handleDialogConfirm = async () => {
    if (planToDelete) {
      toast.success("Xóa gói thành công!");
      setDeleteDialogOpen(false);
      setPlanToDelete(null);
      await deletePlan(planToDelete.id);
    }
  };

  const onUpdate = async (plan: IPlan) => {
    setData(plan);
    setIsUpdateOpen(true);
  };

  if (plansTable === null) {
    return <TableDashboardSkeleton />;
  }

  return (
    <div className="space-y-4">
      <DashboardHeader
        title="User Dashboard"
        onCreateClick={() => {
          setData(defaultPlan);
          setIsCreateOpen(true);
        }}
        createButtonText="Create User"
      />

      {/* Use consistent key to avoid hydration issues */}
      <CreatePlanDialog
        key={`create-${dialogKey}-${isCreateOpen ? "open" : "closed"}`}
        isOpen={isCreateOpen}
        onOpenChange={(open) => {
          setIsCreateOpen(open);
          if (!open) {
            setData(null);
            setDialogKey((prev) => prev + 1);
          }
        }}
        onChange={handleChange}
        onCreated={handleCreate}
        data={data}
      />

      <UpdatePlanDialog
        key={`update-${dialogKey}-${isUpdateOpen ? "open" : "closed"}`}
        isOpen={isUpdateOpen}
        onOpenChange={(open) => {
          setIsUpdateOpen(open);
          if (!open) {
            setData(null);
            setDialogKey((prev) => prev + 1);
          }
        }}
        onChange={handleChange}
        data={data}
        onUpdated={handleUpdate}
      />

      <div className="space-y-4">
        <Card className="border-border/50 shadow-lg bg-linear-to-br from-card to-card/80 backdrop-blur-sm">
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
                  className="h-9 gap-2 px-4 bg-linear-to-br from-secondary/80 to-secondary hover:from-secondary hover:to-secondary/90 shadow-md hover:shadow-lg hover:shadow-secondary/20 transition-all duration-200 hover:scale-105"
                  onClick={async () => {
                    handleRefresh();
                  }}
                >
                  <RefreshCw className="h-4 w-4" />
                  Refresh
                </Button>
              </div>
            </div>
          </CardHeader>

          <PlanTable
            plans={paginated}
            isLoading={false}
            onUpdate={onUpdate}
            onDelete={onDelete}
            showPagination={filtered.length > 10}
            paginationData={paginationData}
            onPageChange={setPage}
          />
        </Card>
      </div>

      <ConfirmationDialog
        open={deleteDialogOpen}
        onOpenChange={handleDialogClose}
        title={"Delete Plan"}
        description={
          "Hành động này không thể hoàn tác. Điều này sẽ xóa vĩnh viễn người dùng và loại bỏ nó khỏi máy chủ của chúng tôi."
        }
        confirmText="Delete"
        cancelText="Hủy"
        isDestructive={true}
        onConfirm={handleDialogConfirm}
      />
    </div>
  );
}
