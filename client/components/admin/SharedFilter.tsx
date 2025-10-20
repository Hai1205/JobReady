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
          className="h-9 gap-2 px-4 bg-gradient-to-r from-secondary/80 to-secondary hover:from-secondary hover:to-secondary/90 shadow-md hover:shadow-lg hover:shadow-secondary/20 transition-all duration-200 hover:scale-105"
          onClick={() => setOpenMenuFilters(!openMenuFilters)}
        >
          <Filter className="h-4 w-4" />
          Bộ lọc
        </Button>
      </DropdownMenuTrigger>

      <DropdownMenuContent
        align="end"
        className="w-[250px] bg-card/95 backdrop-blur-sm border border-border/50 shadow-xl"
      >
        <DropdownMenuLabel className="text-foreground font-semibold bg-gradient-to-r from-primary/10 to-secondary/10">
          Bộ lọc theo
        </DropdownMenuLabel>

        <DropdownMenuSeparator className="bg-border/50" />

        {filterOptions?.status && (
          <div className="p-3">
            <h4 className="mb-3 text-sm font-semibold text-foreground">
              Status
            </h4>

            <div className="space-y-3">
              {filterOptions.status.map((status) => (
                <div
                  key={status.value}
                  className="flex items-center hover:bg-primary/5 p-1.5 rounded-lg transition-colors"
                >
                  <Checkbox
                    id={`status-${status.value}`}
                    checked={activeFilters.status.includes(status.value)}
                    onCheckedChange={() => toggleFilter(status.value, "status")}
                    className="mr-2 border-primary/50"
                  />

                  <label
                    htmlFor={`status-${status.value}`}
                    className="text-foreground text-sm cursor-pointer flex-1"
                  >
                    {status.label}
                  </label>
                </div>
              ))}
            </div>
          </div>
        )}

        {filterOptions?.status && filterOptions?.contentType && (
          <DropdownMenuSeparator className="bg-border/50" />
        )}

        <DropdownMenuSeparator className="bg-border/50" />

        <div className="p-3 flex justify-between gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={clearFilters}
            className="flex-1 border-border/50 hover:bg-muted/50 transition-all"
          >
            Xóa bộ lọc
          </Button>

          <Button
            size="sm"
            onClick={applyFilters}
            className="flex-1 bg-gradient-to-r from-primary to-secondary hover:from-primary/90 hover:to-secondary/90 shadow-md hover:shadow-lg transition-all"
          >
            Lọc
          </Button>
        </div>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};
