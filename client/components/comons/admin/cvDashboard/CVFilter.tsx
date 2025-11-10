"use client";

import { CvFilterType } from "@/app/admin/cv-dashboard/page";
import { FilterType, SharedFilter } from "../adminTable/SharedFilter";

interface CVFilterProps {
  openMenuFilters: boolean;
  setOpenMenuFilters: (open: boolean) => void;
  activeFilters: { visibility?: string[] };
  toggleFilter: (value: string, type: CvFilterType) => void;
  clearFilters: () => void;
  applyFilters: () => void;
  closeMenuMenuFilters: () => void;
}

export const CVFilter = ({
  openMenuFilters,
  setOpenMenuFilters,
  activeFilters,
  toggleFilter,
  clearFilters,
  applyFilters,
  closeMenuMenuFilters,
}: CVFilterProps) => {
  const handleToggleFilter = (
    value: string,
    type: FilterType
  ) => {
    if (type === "visibility") {
      toggleFilter(value, "visibility");
    }
  };

  const filterOptions = {
    visibility: [
      { label: "Công khai", value: "true" },
      { label: "Riêng tư", value: "false" },
    ],
  };

  return (
    <SharedFilter
      openMenuFilters={openMenuFilters}
      setOpenMenuFilters={setOpenMenuFilters}
      activeFilters={activeFilters}
      toggleFilter={handleToggleFilter}
      clearFilters={clearFilters}
      applyFilters={applyFilters}
      closeMenuMenuFilters={closeMenuMenuFilters}
      filterOptions={filterOptions}
    />
  );
};
