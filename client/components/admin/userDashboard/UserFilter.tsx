"use client";

import { SharedFilter } from "@/components/admin/SharedFilter";
import { capitalizeEnumValue } from "@/lib/utils";
import { EUserRole, EUserStatus } from "@/types/enum";

interface UserFilterProps {
  openMenuFilters: boolean;
  setOpenMenuFilters: (open: boolean) => void;
  activeFilters: { status: string[]; role: string[] };
  toggleFilter: (value: string, type: "status" | "role") => void;
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
  const handleToggleFilter = (value: string, type: "status" | "role") => {
    if (type === "status") {
      toggleFilter(value, "status");
    }
    if (type === "role") {
      toggleFilter(value, "role");
    }
  };

  const filterOptions = {
    status: Object.values(EUserStatus).map((status) => ({
      label: capitalizeEnumValue(status),
      value: status,
    })),
    role: Object.values(EUserRole).map((role) => ({
      label: capitalizeEnumValue(role),
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
