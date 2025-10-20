"use client";

import { useCallback, useState, useEffect } from "react";
import { RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle } from "@/components/ui/card";
import { CVFilter } from "@/components/admin/cvDashboard/CVFilter";
import { CVTable } from "@/components/admin/cvDashboard/CVTable";
import { TableSearch } from "@/components/admin/TableSearch";
import { DashboardHeader } from "@/components/admin/DashboardHeader";
import { useCVStore } from "@/stores/cvStore";

export default function CVDashboardPage() {
  const { isLoading, getAllCVs } = useCVStore();

  const [isViewCVOpen, setIsViewCVOpen] = useState(false);
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
    <div className="space-y-4">
      <DashboardHeader title="CV Dashboard" />

      <div className="space-y-4">
        <Card className="bg-white dark:bg-gray-800">
          <CardHeader className="pb-3">
            <div className="flex items-center justify-between">
              <CardTitle />

              <div className="flex items-center gap-2">
                <TableSearch
                  handleSearch={handleSearch}
                  searchQuery={searchQuery}
                  setSearchQuery={setSearchQuery}
                  placeholder="Search CVs..."
                />

                <Button
                  variant="secondary"
                  size="sm"
                  className="h-8 gap-1"
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
            onView={(cv) => {
              setData(cv);
              setIsViewCVOpen(true);
            }}
          />
        </Card>
      </div>
    </div>
  );
}
