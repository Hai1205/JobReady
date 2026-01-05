"use client";

import { CvFilterType } from "./CVDashboardClient";
import { SharedFilter } from "../adminTable/SharedFilter";

interface CVFilterProps {
  openMenuFilters: boolean;
  setOpenMenuFilters: (open: boolean) => void;
  activeFilters: { visibility?: string[] };
  toggleFilter: (value: string, type: CvFilterType) => void;
  clearFilters: () => void;
  applyFilters: () => void;
  closeMenuMenuFilters: () => void;
}

interface CVFilterSection {
  key: CvFilterType;
  label: string;
  options: { label: string; value: string }[];
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
  const filterSections: CVFilterSection[] = [
    {
      key: "visibility",
      label: "Chế độ hiển thị",
      options: [
        { label: "Công khai", value: "true" },
        { label: "Riêng tư", value: "false" },
      ],
    },
  ];

  return (
    <SharedFilter<CvFilterType, CVFilterSection>
      openMenuFilters={openMenuFilters}
      setOpenMenuFilters={setOpenMenuFilters}
      activeFilters={activeFilters}
      toggleFilter={toggleFilter}
      clearFilters={clearFilters}
      applyFilters={applyFilters}
      closeMenuMenuFilters={closeMenuMenuFilters}
      filterSections={filterSections}
    />
  );
};
