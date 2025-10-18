"use client";

import { useCallback, useState, useEffect } from "react";
import { RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle } from "@/components/ui/card";
import UpdateCVDialog from "@/components/admin/cvDashboard/UpdateCVDialog";
import CreateCVDialog from "@/components/admin/cvDashboard/CreateCVDialog";
import { CVFilter } from "@/components/admin/cvDashboard/CVFilter";
import { CVTable } from "@/components/admin/cvDashboard/CVTable";
import { TableSearch } from "@/components/admin/TableSearch";
import { DashboardHeader } from "@/components/admin/DashboardHeader";
// import { CVCategory } from "@/components/admin/cvDashboard/constant";
import { useCVStore } from "@/stores/cvStore";

// Initialize empty filters
const initialFilters = { status: [] as string[], contentType: [] as string[] };

export default function CVDashboardPage() {
  const { isLoading, getAllCVs, updateCV, createCV } = useCVStore();

  const [searchQuery, setSearchQuery] = useState("");
  const [isCreateCVOpen, setIsCreateCVOpen] = useState(false);
  const [isUpdateCVOpen, setIsUpdateCVOpen] = useState(false);
  const [activeFilters, setActiveFilters] = useState(initialFilters);
  const [allCVs, setAllCVs] = useState<ICV[] | []>([]);
  const [filteredCVs, setFilteredCVs] = useState<ICV[] | []>([]);

  useEffect(() => {
    const fetchData = async () => {
      const res = await getAllCVs();
      const data = res?.data?.cvs || [];
      setAllCVs(data);
      setFilteredCVs(data);
    };

    fetchData();
  }, [getAllCVs]);

  // Function to filter data based on query and activeFilters
  const filterData = useCallback(
    (query: string, filters: { status: string[]; contentType: string[] }) => {
      let results = [...allCVs];

      // Filter by search query
      if (query.trim()) {
        const searchTerms = query.toLowerCase().trim();
        results = results.filter((cv) =>
          // cv.question.toLowerCase().includes(searchTerms) ||
          // cv.answer.toLowerCase().includes(searchTerms) ||
          cv.tittle.toLowerCase().includes(searchTerms)
        );
      }

      // Filter by status
      // if (filters.status.length > 0) {
      //   results = results.filter((cv) =>
      //     filters.status.includes(cv.status || "")
      //   );
      // }

      // Filter by contentType (category)
      // if (filters.contentType.length > 0) {
      //   results = results.filter((cv) =>
      //     filters.contentType.includes(cv.category || "")
      //   );
      // }

      setFilteredCVs(results);
    },
    [allCVs]
  );

  const handleSearch = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      // Filter data based on current searchQuery and activeFilters
      filterData(searchQuery, activeFilters);
    },
    [searchQuery, activeFilters, filterData]
  );

  // Toggle filter without auto-filtering
  const toggleFilter = (value: string, type: "status" | "contentType") => {
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
    setFilteredCVs(allCVs); // Reset filtered data
    closeMenuMenuFilters();
  };

  const applyFilters = () => {
    // Filter data based on current activeFilters and searchQuery
    filterData(searchQuery, activeFilters);
    closeMenuMenuFilters();
  };

  const [openMenuFilters, setOpenMenuFilters] = useState(false);
  const closeMenuMenuFilters = () => setOpenMenuFilters(false);

  const [data, setData] = useState<ICV | null>(null);

  const handleChange = (field: keyof ICV, value: string) => {
    // setData((prev) => {
    //   // If prev is null, create a new object with default values
    //   if (!prev) {
    //     const defaultData = {
    //       _id: "",
    //       tittle: "",
    //       answer: "",
    //       category: CVCategory[0].value,
    //     };
    //     return { ...defaultData, [field]: value };
    //   }
    //   // If prev is not null, update the current value
    //   return { ...prev, [field]: value };
    // });
  };

  const handleUpdate = async () => {
    // if (data) {
    //   await updateCV(
    //     data._id,
    //     data.question,
    //     data.answer,
    //     data.category,
    //     data.status
    //   );
    //   // Refresh the CVs list after update
    //   const res = await getAllCVs();
    //   const updatedData = res?.data?.CVs || [];
    //   setAllCVs(updatedData);
    //   // Apply current filters
    //   filterData(searchQuery, activeFilters);
    //   setIsUpdateCVOpen(false);
    // }
  };

  const handleCreate = async () => {
    // if (data) {
    //   await createCV(data.question, data.answer, data.category, data.status);
    //   // Refresh the CVs list after create
    //   const res = await getAllCVs();
    //   const updatedData = res?.data?.CVs || [];
    //   setAllCVs(updatedData);
    //   // Apply current filters
    //   filterData(searchQuery, activeFilters);
    //   setIsCreateCVOpen(false);
    //   setData(null);
    // }
  };

  return (
    <div className="space-y-4">
      <DashboardHeader
        tittle="CV Dashboard"
        onCreateClick={() => setIsCreateCVOpen(true)}
        createButtonText="Create CV"
      />

      <CreateCVDialog
        isOpen={isCreateCVOpen}
        onOpenChange={setIsCreateCVOpen}
        onChange={handleChange}
        onCVCreated={handleCreate}
        data={data}
        isLoading={isLoading}
      />

      <UpdateCVDialog
        isOpen={isUpdateCVOpen}
        onOpenChange={setIsUpdateCVOpen}
        onChange={handleChange}
        data={data}
        onCVUpdated={handleUpdate}
        isLoading={isLoading}
      />

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
                    // Reset filters
                    setActiveFilters(initialFilters);
                    setSearchQuery("");

                    // Refresh data from API
                    const res = await getAllCVs();
                    const data = res?.data?.cvs || [];
                    setAllCVs(data);
                    setFilteredCVs(data);
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
              setData(cv);
              setIsUpdateCVOpen(true);
            }}
          />
        </Card>
      </div>
    </div>
  );
}
