import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Filter } from "lucide-react";

interface SharedFilterProps {
  openMenuFilters: boolean;
  setOpenMenuFilters: (open: boolean) => void;
  activeFilters: { status: string[] };
  toggleFilter: (value: string, type: "status") => void;
  clearFilters: () => void;
  applyFilters: () => void;
  closeMenuMenuFilters: () => void;
  filterOptions?: {
    status?: { label: string; value: string }[];
    contentType?: { label: string; value: string }[];
  };
}

export const SharedFilter = ({
  openMenuFilters,
  setOpenMenuFilters,
  activeFilters,
  toggleFilter,
  clearFilters,
  applyFilters,
  closeMenuMenuFilters,
  filterOptions,
}: SharedFilterProps) => {
  return (
    <DropdownMenu open={openMenuFilters} onOpenChange={closeMenuMenuFilters}>
      <DropdownMenuTrigger asChild>
        <Button
          variant="secondary"
          size="sm"
          className="h-8 gap-1 text-gray-800 dark:text-gray-100 hover:bg-gray-200 dark:hover:bg-gray-700"
          onClick={() => setOpenMenuFilters(!openMenuFilters)}
        >
          <Filter className="h-4 w-4 text-gray-700 dark:text-gray-300" />
          Bộ lọc
        </Button>
      </DropdownMenuTrigger>

      <DropdownMenuContent
        align="end"
        className="w-[250px] bg-white dark:bg-[#1e2735] border border-gray-200 dark:border-gray-700"
      >
        <DropdownMenuLabel className="text-gray-900 dark:text-gray-100">
          Bộ lọc theo
        </DropdownMenuLabel>

        <DropdownMenuSeparator className="bg-gray-200 dark:bg-gray-600" />

        {filterOptions?.status && (
          <div className="p-2">
            <h4 className="mb-2 text-sm font-medium text-gray-900 dark:text-gray-100">
              Status
            </h4>

            <div className="space-y-2">
              {filterOptions.status.map((status) => (
                <div key={status.value} className="flex items-center">
                  <Checkbox
                    id={`status-${status.value}`}
                    checked={activeFilters.status.includes(status.value)}
                    onCheckedChange={() => toggleFilter(status.value, "status")}
                    className="mr-2"
                  />

                  <label
                    htmlFor={`status-${status.value}`}
                    className="text-gray-900 dark:text-gray-100"
                  >
                    {status.label}
                  </label>
                </div>
              ))}
            </div>
          </div>
        )}

        {filterOptions?.status && filterOptions?.contentType && (
          <DropdownMenuSeparator className="bg-gray-200 dark:bg-gray-600" />
        )}

        <DropdownMenuSeparator className="bg-gray-200 dark:bg-gray-600" />

        <div className="p-2 flex justify-between">
          <Button
            variant="outline"
            size="sm"
            onClick={clearFilters}
            className="text-gray-700 dark:text-gray-300 border-gray-300 dark:border-gray-600 hover:bg-gray-100 dark:hover:bg-gray-800"
          >
            Xóa bộ lọc
          </Button>

          <Button
            size="sm"
            onClick={applyFilters}
            variant="secondary"
            className="text-gray-800 dark:text-gray-100 hover:bg-gray-200 dark:hover:bg-gray-700"
          >
            Lọc
          </Button>
        </div>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};
