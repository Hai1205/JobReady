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

export default function CVDashboardPage() {
  const { isLoading, getAllCVs, createCV, handleGeneratePDF } =
    useCVStore();
    const { userAuth } = useAuthStore();

  const router = useRouter();

  const [searchQuery, setSearchQuery] = useState("");
  const [allCVs, setAllCVs] = useState<ICV[]>([]);
  const [filteredCVs, setFilteredCVs] = useState<ICV[]>([]);

  // Initialize empty filters
  const initialFilters = { privacy: [] as string[] };
  const [activeFilters, setActiveFilters] = useState<{
    privacy?: string[];
  }>(initialFilters);

  const fetchData = useCallback(async () => {
    try {
      const res = await getAllCVs();
      const data = res?.data?.cvs || [];

      setAllCVs(data);
      setFilteredCVs(data);
    } catch (err) {
      console.error("Error fetching CVs:", err);
    }
  }, [getAllCVs]);

  useEffect(() => {
    fetchData();
  }, [getAllCVs]);

  // Function to filter data based on query and activeFilters
  const filterData = useCallback(
    (query: string, filters: { privacy?: string[] }) => {
      let results = [...allCVs];

      // Filter by search query
      if (query.trim()) {
        const searchTerms = query.toLowerCase().trim();
        results = results.filter((cv) =>
          cv.title.toLowerCase().includes(searchTerms)
        );
      }

      // Filter by privacy
      if (filters.privacy && filters.privacy.length > 0) {
        results = results.filter((cv) =>
          filters.privacy!.includes(cv.privacy || "")
        );
      }

      setFilteredCVs(results);
    },
    [allCVs]
  );

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
  const toggleFilter = (value: string, type: "status" | "privacy" | "role") => {
    if (type === "privacy") {
      setActiveFilters((prev) => {
        const updated = { ...prev };
        if (updated.privacy?.includes(value)) {
          updated.privacy = updated.privacy.filter((item) => item !== value);
        } else {
          updated.privacy = [...(updated.privacy || []), value];
        }
        return updated;
      });
    }
  };

  const clearFilters = () => {
    setActiveFilters(initialFilters);
    setSearchQuery("");
    setFilteredCVs(allCVs); // Reset filtered data
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
            isLoading={isLoading}
            onEdit={(cv) => {
              router.push(`/cv-builder/${cv.id}`);
            }}
            onDownload={(cv) => {
              handleGeneratePDF(cv);
            }}
          />
        </Card>
      </div>
    </div>
  );
}
