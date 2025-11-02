"use client";

import { capitalizeFirstLetter } from "@/lib/utils";
import { EPrivacy } from "@/types/enum";
import { SharedFilter } from "../adminTable/SharedFilter";

interface CVFilterProps {
  openMenuFilters: boolean;
  setOpenMenuFilters: (open: boolean) => void;
  activeFilters: { privacy?: string[] };
  toggleFilter: (value: string, type: "status" | "privacy" | "role") => void;
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
    type: "status" | "privacy" | "role"
  ) => {
    if (type === "privacy") {
      toggleFilter(value, "privacy");
    }
  };

  const filterOptions = {
    privacy: Object.values(EPrivacy).map((privacy) => ({
      label: capitalizeFirstLetter(privacy),
      value: privacy,
    })),
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
