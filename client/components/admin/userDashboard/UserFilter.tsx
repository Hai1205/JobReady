"use client";

import { SharedFilter } from "@/components/admin/SharedFilter";
import { EUserStatus } from "@/types/enum";

interface UserFilterProps {
  openMenuFilters: boolean;
  setOpenMenuFilters: (open: boolean) => void;
  activeFilters: { status: string[] };
  toggleFilter: (value: string, type: "status") => void;
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
  const handleToggleFilter = (
    value: string,
    type: "status"
  ) => {
    if (type === "status") {
      toggleFilter(value, "status");
    }
  };

  const filterOptions = {
    status: [
      { label: "Hoạt động", value: EUserStatus.ACTIVE },
      { label: "Ngừng hoạt động", value: EUserStatus.INACTIVE },
      { label: "Đang chờ", value: EUserStatus.PENDING },
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
