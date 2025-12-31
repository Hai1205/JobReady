"use client";

import { FilterType, SharedFilter } from "../adminTable/SharedFilter";
import { capitalizeFirstLetter } from "@/lib/utils";
import { InvoiceFilterType } from "./InvoiceDashboardClient";
import { EInvoiceStatus } from "@/types/enum";

interface InvoiceFilterProps {
  openMenuFilters: boolean;
  setOpenMenuFilters: (open: boolean) => void;
  activeFilters: { status: string[] };
  toggleFilter: (value: string, type: InvoiceFilterType) => void;
  clearFilters: () => void;
  applyFilters: () => void;
  closeMenuMenuFilters: () => void;
}

export const InvoiceFilter = ({
  openMenuFilters,
  setOpenMenuFilters,
  activeFilters,
  toggleFilter,
  clearFilters,
  applyFilters,
  closeMenuMenuFilters,
}: InvoiceFilterProps) => {
  const handleToggleFilter = (value: string, type: FilterType) => {
    if (type === "status") {
      toggleFilter(value, "status");
    }
  };

  const filterOptions = {
    status: Object.values(EInvoiceStatus).map((status) => ({
      label: capitalizeFirstLetter(status),
      value: status,
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
