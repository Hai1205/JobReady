"use client";

import { useCallback, useState, useEffect } from "react";
import { RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle } from "@/components/ui/card";
import { CVTable } from "@/components/admin/cvDashboard/CVTable";
import { DashboardHeader } from "@/components/admin/DashboardHeader";
import { useCVStore } from "@/stores/cvStore";
import { TableSearch } from "@/components/admin/adminTable/TableSearch";

export default function CVDashboardPage() {
  const { isLoading, getAllCVs, handleGeneratePDF } = useCVStore();

  const [searchQuery, setSearchQuery] = useState("");
  const [allCVs, setAllCVs] = useState<ICV[]>([]);
  const [filteredCVs, setFilteredCVs] = useState<ICV[]>([]);

  const fetchData = useCallback(() => {
    return async () => {
      const res = await getAllCVs();
      const data = res?.data?.cvs || [];
      setAllCVs(data);
      setFilteredCVs(data);
    };
  }, []);

  useEffect(() => {
    fetchData();
  }, [getAllCVs]);

  // Function to filter data based on query and activeFilters
  const filterData = useCallback(
    (query: string) => {
      let results = [...allCVs];

      // Filter by search query
      if (query.trim()) {
        const searchTerms = query.toLowerCase().trim();
        results = results.filter((cv) =>
          cv.title.toLowerCase().includes(searchTerms)
        );
      }

      setFilteredCVs(results);
    },
    [allCVs]
  );

  const handleSearch = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      filterData(searchQuery);
    },
    [searchQuery, filterData]
  );

  const clearFilters = () => {
    setSearchQuery("");
    closeMenuMenuFilters();
  };

  const applyFilters = () => {
    filterData(searchQuery);
    closeMenuMenuFilters();
  };

  const [openMenuFilters, setOpenMenuFilters] = useState(false);
  const closeMenuMenuFilters = () => setOpenMenuFilters(false);

  const [data, setData] = useState<ICV | null>(null);

  const handleRefresh = () => {
    setSearchQuery("");
    fetchData();
  };

  return (
    <div className="space-y-6">
      <DashboardHeader title="CV Dashboard" />

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

                {/* <CVFilter
                  openMenuFilters={openMenuFilters}
                  setOpenMenuFilters={setOpenMenuFilters}
                  clearFilters={clearFilters}
                  applyFilters={applyFilters}
                  closeMenuMenuFilters={closeMenuMenuFilters}
                /> */}
              </div>
            </div>
          </CardHeader>

          <CVTable
            CVs={filteredCVs}
            isLoading={isLoading}
            onDownload={(cv) => {
              handleGeneratePDF(cv);
            }}
          />
        </Card>
      </div>
    </div>
  );
}
