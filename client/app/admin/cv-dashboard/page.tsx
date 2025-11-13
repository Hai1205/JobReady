"use client";

import { useCallback, useState, useEffect } from "react";
import { RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle } from "@/components/ui/card";
import { CVTable } from "@/components/comons/admin/cvDashboard/CVTable";
import { DashboardHeader } from "@/components/comons/admin/DashboardHeader";
import { useCVStore } from "@/stores/cvStore";
import { TableSearch } from "@/components/comons/admin/adminTable/TableSearch";
import { CVFilter } from "@/components/comons/admin/cvDashboard/CVFilter";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/authStore";
import TableDashboardSkeleton from "@/components/comons/layout/TableDashboardSkeleton";
import ConfirmationDialog from "@/components/comons/layout/ConfirmationDialog";

export type CvFilterType = "visibility";

export default function CVDashboardPage() {
  const {
    CVsTable,
    fetchAllCVsInBackground,
    createCV,
    deleteCV,
    handleGeneratePDF,
    handleSetCurrentCV,
    isLoadingAllCVs,
  } = useCVStore();
  const { userAuth } = useAuthStore();

  const router = useRouter();

  const [searchQuery, setSearchQuery] = useState("");
  const [filteredCVs, setFilteredCVs] = useState<ICV[]>([]);

  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [cvToDelete, setCvToDelete] = useState<ICV | null>(null);

  // Initialize empty filters
  const initialFilters = { visibility: [] as string[] };
  const [activeFilters, setActiveFilters] = useState<{
    visibility?: string[];
  }>(initialFilters);

  useEffect(() => {
    // Fetch CVs in background
    fetchAllCVsInBackground();
  }, []);

  // Function to filter data based on query and activeFilters
  const filterData = useCallback(
    (query: string, filters: { visibility?: string[] }) => {
      let results = [...CVsTable];

      // Filter by search query
      if (query.trim()) {
        const searchTerms = query.toLowerCase().trim();
        results = results.filter((cv) =>
          cv.title.toLowerCase().includes(searchTerms)
        );
      }

      // Filter by visibility
      if (filters.visibility && filters.visibility.length > 0) {
        results = results.filter((cv) => {
          const isVisibility = cv.isVisibility;
          return filters.visibility!.some((filterValue) => {
            if (filterValue === "true") return isVisibility === true;
            if (filterValue === "false") return isVisibility === false;
            return false;
          });
        });
      }

      setFilteredCVs(results);
    },
    [CVsTable]
  );

  useEffect(() => {
    filterData(searchQuery, activeFilters);
  }, [CVsTable, searchQuery, activeFilters, filterData]);

  const onDelete = (cv: ICV) => {
    setCvToDelete(cv);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (cvToDelete) {
      await deleteCV(cvToDelete.id);
      setDeleteDialogOpen(false);
      setCvToDelete(null);
    }
  };

  const onUpdate = (cv: ICV) => {
    handleSetCurrentCV(cv);
    router.push(`/cv-builder`);
  };

  const onDownload = async (cv: ICV) => {
    handleGeneratePDF(cv);
  };

  const handleSearch = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      filterData(searchQuery, activeFilters);
    },
    [searchQuery, activeFilters, filterData]
  );

  const [openMenuFilters, setOpenMenuFilters] = useState(false);
  const closeMenuMenuFilters = () => setOpenMenuFilters(false);

  // Toggle filter without auto-filtering
  const toggleFilter = (value: string, type: CvFilterType) => {
    if (type === "visibility") {
      setActiveFilters((prev) => {
        const updated = { ...prev };
        if (updated.visibility?.includes(value)) {
          updated.visibility = updated.visibility.filter(
            (item) => item !== value
          );
        } else {
          updated.visibility = [...(updated.visibility || []), value];
        }
        return updated;
      });
    }
  };

  const clearFilters = () => {
    setActiveFilters(initialFilters);
    setSearchQuery("");
    setFilteredCVs(CVsTable);
    closeMenuMenuFilters();
  };

  const applyFilters = () => {
    filterData(searchQuery, activeFilters);
    closeMenuMenuFilters();
  };

  const handleRefresh = () => {
    setActiveFilters(initialFilters);
    setSearchQuery("");
    fetchAllCVsInBackground();
  };

  // Show skeleton loading when there's no cached data
  if (isLoadingAllCVs && CVsTable.length === 0) {
    return <TableDashboardSkeleton />;
  }

  return (
    <div className="space-y-6">
      <DashboardHeader
        title="CV Dashboard"
        onCreateClick={() => {
          createCV(userAuth?.id || "");
          router.push(`/cv-builder`);
        }}
        createButtonText="Create CV"
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
                  placeholder="Search CVs..."
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

                <CVFilter
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

          <CVTable
            CVs={filteredCVs}
            isLoading={isLoadingAllCVs && CVsTable.length === 0}
            onUpdate={onUpdate}
            onDownload={onDownload}
            onDelete={onDelete}
          />
        </Card>
      </div>

      <ConfirmationDialog
        open={deleteDialogOpen}
        description="Hành động này không thể hoàn tác. Điều này sẽ xóa vĩnh viễn CV và loại bỏ nó khỏi máy chủ của chúng tôi."
        onOpenChange={setDeleteDialogOpen}
        onConfirm={handleDeleteConfirm}
      />
    </div>
  );
}
