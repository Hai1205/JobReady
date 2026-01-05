"use client";

import { SharedFilter } from "../adminTable/SharedFilter";
import { ContactFilterType } from "./ContactDashboardClient";

interface ContactFilterProps {
  openMenuFilters: boolean;
  setOpenMenuFilters: (open: boolean) => void;
  activeFilters: { status?: string[] };
  toggleFilter: (value: string, type: ContactFilterType) => void;
  clearFilters: () => void;
  applyFilters: () => void;
  closeMenuMenuFilters: () => void;
}

interface ContactFilterSection {
  key: ContactFilterType;
  label: string;
  options: { label: string; value: string }[];
}

export const ContactFilter = ({
  openMenuFilters,
  setOpenMenuFilters,
  activeFilters,
  toggleFilter,
  clearFilters,
  applyFilters,
  closeMenuMenuFilters,
}: ContactFilterProps) => {
  const filterSections: ContactFilterSection[] = [
    {
      key: "status",
      label: "Trạng thái",
      options: [
        { label: "Công khai", value: "true" },
        { label: "Riêng tư", value: "false" },
      ],
    },
  ];

  return (
    <SharedFilter<ContactFilterType, ContactFilterSection>
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
