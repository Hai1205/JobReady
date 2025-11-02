"use client";

import { EUserRole, EUserStatus } from "@/types/enum";
import { FilterType, SharedFilter } from "../adminTable/SharedFilter";
import { capitalizeFirstLetter } from "@/lib/utils";

interface UserFilterProps {
  openMenuFilters: boolean;
  setOpenMenuFilters: (open: boolean) => void;
  activeFilters: { status: string[] };
  toggleFilter: (value: string, type: FilterType) => void;
  clearFilters: () => void;
  applyFilters: () => void;
  closeMenuMenuFilters: () => void;
}

export const UserFilter = ({
  openMenuFilters,
  setOpenMenuFilters,
  activeFilters,
  toggleFilter,
  clearFilters,
  applyFilters,
  closeMenuMenuFilters,
}: UserFilterProps) => {
  const handleToggleFilter = (value: string, type: FilterType) => {
    if (type === "status") {
      toggleFilter(value, "status");
    }

    if (type === "role") {
      toggleFilter(value, "role");
    }
  };

  const filterOptions = {
    status: Object.values(EUserStatus).map((status) => ({
      label: capitalizeFirstLetter(status),
      value: status,
    })),

    role: Object.values(EUserRole).map((role) => ({
      label: capitalizeFirstLetter(role),
      value: role,
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
