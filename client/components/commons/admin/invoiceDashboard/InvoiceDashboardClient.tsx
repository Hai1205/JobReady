"use client";

import { useCallback, useState, useEffect } from "react";
import { RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle } from "@/components/ui/card";
import { EUserRole, EUserStatus } from "@/types/enum";
import { usePagination } from "@/hooks/use-pagination";
import { DashboardHeader } from "@/components/commons/admin/DashboardHeader";
import { TableSearch } from "@/components/commons/admin/adminTable/TableSearch";
import { ExtendedUserData } from "@/components/commons/admin/userDashboard/constant";
import TableDashboardSkeleton from "../../layout/TableDashboardSkeleton";
import { InvoiceFilter } from "./InvoiceFilter";
import { InvoiceTable } from "./InvoiceTable";
import { usePaymentStore } from "@/stores/paymentStore";

export type InvoiceFilterType = "status";
const initialFilters = { status: [] as string[] };


export default function InvoiceDashboardClient() {
  const {
    invoicesTable,
    fetchAllInvoicesInBackground,
    getAllInvoices,
  } = usePaymentStore();

  const [searchQuery, setSearchQuery] = useState("");

  const [activeFilters, setActiveFilters] = useState<{
    status: string[];
  }>(initialFilters);
  const [filtered, setFiltered] = useState<IInvoice[]>([]);

  // Pagination
  const { paginationState, paginationData, setPage } = usePagination({
    initialPage: 1,
    initialPageSize: 10,
  });

  useEffect(() => {
    // Fetch in background to update cache
    fetchAllInvoicesInBackground();
  }, []);

  const filterData = useCallback(
    (query: string, filters: { status: string[] }) => {
      let results = [...invoicesTable];

      if (query.trim()) {
        const searchTerms = query.toLowerCase().trim();
        results = results.filter(
          (invoice) =>
            invoice.planName.toLowerCase().includes(searchTerms) ||
            invoice.paymentMethod.toLowerCase().includes(searchTerms)
        );
      }

      if (filters.status.length > 0) {
        results = results.filter((invoice) =>
          filters.status.includes(invoice.status || "")
        );
      }

      setFiltered(results);
    },
    [invoicesTable]
  );

  useEffect(() => {
    filterData(searchQuery, activeFilters);
  }, [invoicesTable, searchQuery, activeFilters, filterData]);

  // Update pagination when filtered invoices change
  useEffect(() => {
    paginationData.totalElements = filtered.length;
    paginationData.totalPages = Math.ceil(
      filtered.length / paginationState.pageSize
    );
  }, [filtered.length, paginationState.pageSize]);

  // Paginate filtered invoicesTable
  const paginated = filtered.slice(
    (paginationState.page - 1) * paginationState.pageSize,
    paginationState.page * paginationState.pageSize
  );

  const handleSearch = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();

      filterData(searchQuery, activeFilters);
    },
    [searchQuery, activeFilters, filterData]
  );

  const toggleFilter = (value: string, type: InvoiceFilterType) => {
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
    setFiltered(invoicesTable);
    closeMenuMenuFilters();
  };

  const applyFilters = () => {
    filterData(searchQuery, activeFilters);
    closeMenuMenuFilters();
  };

  const handleRefresh = () => {
    setActiveFilters(initialFilters);
    setSearchQuery("");
    getAllInvoices();
  };

  const [openMenuFilters, setOpenMenuFilters] = useState(false);
  const closeMenuMenuFilters = () => setOpenMenuFilters(false);

  const [data, setData] = useState<ExtendedUserData | null>(null);

  const defaultUser: ExtendedUserData = {
    id: "",
    plan: {} as IPlan,
    username: "",
    email: "",
    password: "",
    fullname: "",
    role: EUserRole.USER,
    status: EUserStatus.PENDING,
  };

  const handleChange = (
    field: keyof ExtendedUserData,
    value: string | string[] | boolean
  ) => {
    setData((prev) => {
      if (!prev) {
        return { ...defaultUser, [field]: value } as ExtendedUserData;
      }

      return { ...prev, [field]: value };
    });
  };

  if (invoicesTable === null) {
    return <TableDashboardSkeleton />;
  }

  return (
    <div className="space-y-4">
      <DashboardHeader
        title="User Dashboard"
      />

      <div className="space-y-4">
        <Card className="border-border/50 shadow-lg bg-linear-to-br from-card to-card/80 backdrop-blur-sm">
          <CardHeader className="pb-4 border-b border-border/30">
            <div className="flex items-center justify-between">
              <CardTitle />

              <div className="flex items-center gap-3">
                <TableSearch
                  handleSearch={handleSearch}
                  searchQuery={searchQuery}
                  setSearchQuery={setSearchQuery}
                  placeholder="Search Users..."
                />

                <Button
                  variant="secondary"
                  size="sm"
                  className="h-9 gap-2 px-4 bg-linear-to-br from-secondary/80 to-secondary hover:from-secondary hover:to-secondary/90 shadow-md hover:shadow-lg hover:shadow-secondary/20 transition-all duration-200 hover:scale-105"
                  onClick={async () => {
                    handleRefresh();
                  }}
                >
                  <RefreshCw className="h-4 w-4" />
                  Refresh
                </Button>

                <InvoiceFilter
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

          <InvoiceTable
            invoices={paginated}
            isLoading={false}
            showPagination={filtered.length > 10}
            paginationData={paginationData}
            onPageChange={setPage}
          />
        </Card>
      </div>
    </div>
  );
}
