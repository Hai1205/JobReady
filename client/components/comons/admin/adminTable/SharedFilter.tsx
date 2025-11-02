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

export type FilterType = "status" | "privacy" | "role";
export const initialFilters = { status: [] as string[], role: [] as string[], privacy: [] as string[] };

interface SharedFilterProps {
  openMenuFilters: boolean;
  setOpenMenuFilters: (open: boolean) => void;
  activeFilters: { status?: string[]; privacy?: string[]; role?: string[] };
  toggleFilter: (value: string, type: FilterType) => void;
  clearFilters: () => void;
  applyFilters: () => void;
  closeMenuMenuFilters: () => void;
  filterOptions?: {
    status?: { label: string; value: string }[];
    privacy?: { label: string; value: string }[];
    role?: { label: string; value: string }[];
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
                    checked={
                      activeFilters.status?.includes(status.value) || false
                    }
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

        {filterOptions?.privacy && (
          <div className="p-3">
            <h4 className="mb-3 text-sm font-semibold text-foreground">
              Privacy
            </h4>

            <div className="space-y-3">
              {filterOptions.privacy.map((privacy) => (
                <div
                  key={privacy.value}
                  className="flex items-center hover:bg-primary/5 p-1.5 rounded-lg transition-colors"
                >
                  <Checkbox
                    id={`privacy-${privacy.value}`}
                    checked={
                      activeFilters.privacy?.includes(privacy.value) || false
                    }
                    onCheckedChange={() =>
                      toggleFilter(privacy.value, "privacy")
                    }
                    className="mr-2 border-primary/50"
                  />

                  <label
                    htmlFor={`privacy-${privacy.value}`}
                    className="text-foreground text-sm cursor-pointer flex-1"
                  >
                    {privacy.label}
                  </label>
                </div>
              ))}
            </div>
          </div>
        )}

        {filterOptions?.role && (
          <div className="p-3">
            <h4 className="mb-3 text-sm font-semibold text-foreground">Role</h4>

            <div className="space-y-3">
              {filterOptions.role.map((role) => (
                <div
                  key={role.value}
                  className="flex items-center hover:bg-primary/5 p-1.5 rounded-lg transition-colors"
                >
                  <Checkbox
                    id={`role-${role.value}`}
                    checked={activeFilters.role?.includes(role.value) || false}
                    onCheckedChange={() => toggleFilter(role.value, "role")}
                    className="mr-2 border-primary/50"
                  />

                  <label
                    htmlFor={`role-${role.value}`}
                    className="text-foreground text-sm cursor-pointer flex-1"
                  >
                    {role.label}
                  </label>
                </div>
              ))}
            </div>
          </div>
        )}

        {((filterOptions?.status && filterOptions?.privacy) ||
          (filterOptions?.status && filterOptions?.role) ||
          (filterOptions?.privacy && filterOptions?.role)) && (
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
