"use client";

import { SharedFilter } from "../adminTable/SharedFilter";
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

interface InvoiceFilterSection {
  key: InvoiceFilterType;
  label: string;
  options: { label: string; value: string }[];
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
  const filterSections: InvoiceFilterSection[] = [
    {
      key: "status",
      label: "Trạng thái",
      options: Object.values(EInvoiceStatus).map((status) => ({
        label: capitalizeFirstLetter(status),
        value: status,
      })),
    },
  ];

  return (
    <SharedFilter<InvoiceFilterType, InvoiceFilterSection>
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
