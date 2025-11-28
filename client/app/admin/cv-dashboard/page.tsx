"use client";

import { useCallback, useState, useEffect } from "react";
import { RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle } from "@/components/ui/card";
import { CVTable } from "@/components/commons/admin/cvDashboard/CVTable";
import { DashboardHeader } from "@/components/commons/admin/DashboardHeader";
import { useCVStore } from "@/stores/cvStore";
import { TableSearch } from "@/components/commons/admin/adminTable/TableSearch";
import { CVFilter } from "@/components/commons/admin/cvDashboard/CVFilter";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/authStore";
import TableDashboardSkeleton from "@/components/commons/layout/TableDashboardSkeleton";
import ConfirmationDialog from "@/components/commons/layout/ConfirmationDialog";
import ImportFileDialog from "@/components/commons/my-cvs/ImportFileDialog";
import { useAIStore } from "@/stores/aiStore";
import { EUserRole } from "@/types/enum";
import { toast } from "react-toastify";
import DraggingOnPage from "@/components/commons/layout/DraggingOnPage";

export type CvFilterType = "visibility";

export default function CVDashboardPage() {
  const {
    CVsTable,
    fetchAllCVsInBackground,
    createCV,
    deleteCV,
    handleGeneratePDF,
    handleSetCurrentCV,
    getAllCVs,
  } = useCVStore();
  const { userAuth } = useAuthStore();
  const { importCV } = useAIStore();

  const router = useRouter();

  // Security check: redirect to login if not authenticated or not admin
  useEffect(() => {
    if (!userAuth) {
      router.push("/auth/login");
    } else if (userAuth.role !== EUserRole.ADMIN) {
      router.push("/");
    }
  }, [userAuth]);

  if (!userAuth || userAuth.role !== EUserRole.ADMIN) return null;

  const [searchQuery, setSearchQuery] = useState("");
  const [filteredCVs, setFilteredCVs] = useState<ICV[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [cvToDelete, setCvToDelete] = useState<ICV | null>(null);

  const [isDraggingOnPage, setIsDraggingOnPage] = useState(false);
  const [droppedFile, setDroppedFile] = useState<File | null>(null);
  const [importDialogOpen, setImportDialogOpen] = useState(false);

  // Initialize empty filters
  const initialFilters = { visibility: [] as string[] };
  const [activeFilters, setActiveFilters] = useState<{
    visibility?: string[];
  }>(initialFilters);

  useEffect(() => {
    setIsLoading(true);
    fetchAllCVsInBackground().finally(() => setIsLoading(false));
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
      toast.success("Xóa CV thành công!");
      setDeleteDialogOpen(false);
      setCvToDelete(null);
      await deleteCV(cvToDelete.id);
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
    getAllCVs();
  };

  const handleImport = async (file: File | null) => {
    try {
      const res = await importCV(userAuth?.id || "", file);
      console.log("Import CV result:", res);

      if (res.data && res.data.success) {
        console.log("Navigating to cv-builder...");
        router.push("/cv-builder");
        return true;
      }

      console.log("Import failed or no CV data");
      return false;
    } catch (error) {
      console.error("Import error:", error);
      return false;
    }
  };

  // Page-level drag and drop handlers
  const handlePageDragEnter = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.dataTransfer.types.includes("Files")) {
      setIsDraggingOnPage(true);
    }
  };

  const handlePageDragLeave = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    // Only hide overlay if leaving the container entirely
    if (e.currentTarget === e.target) {
      setIsDraggingOnPage(false);
    }
  };

  const handlePageDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
  };

  const handlePageDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDraggingOnPage(false);

    const file = e.dataTransfer.files?.[0];
    if (file && file.type === "application/pdf") {
      setDroppedFile(file);
      setImportDialogOpen(true);
    } else if (file) {
      toast.error("Chỉ chấp nhận file PDF!");
    }
  };

  // Show skeleton loading when fetching (even if cache empty)
  if (isLoading && CVsTable.length === 0) {
    return <TableDashboardSkeleton />;
  }

  return (
    <div
      className="space-y-6"
      onDragEnter={handlePageDragEnter}
      onDragLeave={handlePageDragLeave}
      onDragOver={handlePageDragOver}
      onDrop={handlePageDrop}
    >
      {/* Drag Overlay */}
      {isDraggingOnPage && (
        <DraggingOnPage
          title="Thả file PDF vào đây"
          subtitle="để import CV của bạn"
        />
      )}

      <DashboardHeader
        title="CV Dashboard"
        onCreateClick={() => {
          createCV(userAuth?.id || "");
          router.push(`/cv-builder`);
        }}
        createButtonText="Create CV"
      >
        <ImportFileDialog
          onImport={handleImport}
          externalFile={droppedFile}
          isExternalOpen={importDialogOpen}
          onExternalOpenChange={setImportDialogOpen}
        />
      </DashboardHeader>

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
            isLoading={isLoading}
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
