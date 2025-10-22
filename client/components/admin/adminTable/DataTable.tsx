import { Button } from "@/components/ui/button";
import { CardContent } from "@/components/ui/card";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { ScrollArea } from "@/components/ui/scroll-area";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { MoreHorizontal } from "lucide-react";
import { TableSkeleton } from "./TableSkeleton";
import { cn } from "@/lib/utils";

interface DataTableProps<T> {
  data: T[];
  isLoading: boolean;
  columns: {
    header: string;
    accessor: (item: T, index: number) => React.ReactNode;
    className?: string;
  }[];
  actions?: {
    label: string;
    onClick: (item: T) => void;
  }[];
  onRowClick?: (item: T) => void;
  emptyMessage?: string;
}

export function DataTable<T>({
  data,
  isLoading,
  columns,
  actions,
  onRowClick,
  emptyMessage = "No data found",
}: DataTableProps<T>) {
  return (
    <ScrollArea className="h-[calc(100vh-220px)] w-full rounded-xl bg-gradient-to-br from-card to-card/80 backdrop-blur-sm border border-border/50 shadow-lg">
      <CardContent>
        <Table className="border-collapse [&_tr]:border-b [&_tr]:border-border/30">
          <TableHeader>
            <TableRow className="border-b-2 border-primary/20 bg-gradient-to-r from-primary/5 to-secondary/5 hover:from-primary/10 hover:to-secondary/10 transition-colors">
              {columns.map((column, index) => (
                <TableHead
                  key={index}
                  className={`text-center font-bold text-foreground/90 ${
                    column.className || ""
                  }`}
                >
                  {column.header}
                </TableHead>
              ))}
              {actions && actions.length > 0 && (
                <TableHead className="text-right font-bold text-foreground/90">
                  Actions
                </TableHead>
              )}
            </TableRow>
          </TableHeader>

          <TableBody>
            {isLoading ? (
              <TableRow>
                <TableCell colSpan={columns.length + (actions ? 1 : 0)}>
                  <TableSkeleton />
                </TableCell>
              </TableRow>
            ) : data.length > 0 ? (
              data.map((item, index) => (
                <TableRow
                  key={index}
                  className={cn(
                    "transition-all duration-200 hover:bg-gradient-to-r hover:from-primary/5 hover:to-secondary/5",
                    onRowClick &&
                      "cursor-pointer hover:shadow-md hover:shadow-primary/10"
                  )}
                  onClick={() => onRowClick?.(item)}
                >
                  {columns.map((column, colIndex) => (
                    <TableCell
                      key={colIndex}
                      className={`text-center ${column.className || ""}`}
                    >
                      {column.accessor(item, index)}
                    </TableCell>
                  ))}
                  {actions && actions.length > 0 && (
                    <TableCell className="text-right">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button
                            variant="ghost"
                            size="sm"
                            className="h-8 w-8 p-0 hover:bg-primary/10 dark:hover:bg-primary/20 rounded-lg transition-all hover:scale-110"
                            onClick={(e) => e.stopPropagation()}
                          >
                            <MoreHorizontal className="h-4 w-4 text-foreground/70 hover:text-primary" />
                          </Button>
                        </DropdownMenuTrigger>

                        <DropdownMenuContent
                          align="end"
                          className="bg-card/95 backdrop-blur-sm border border-border/50 shadow-xl"
                        >
                          <DropdownMenuLabel className="text-foreground font-semibold bg-gradient-to-r from-primary/10 to-secondary/10">
                            Actions
                          </DropdownMenuLabel>

                          <DropdownMenuSeparator className="bg-border/50" />

                          {actions.map((action, actionIndex) => (
                            <DropdownMenuItem
                              key={actionIndex}
                              onClick={(e) => {
                                e.stopPropagation();
                                action.onClick(item);
                              }}
                              className="text-foreground cursor-pointer hover:bg-gradient-to-r hover:from-primary/10 hover:to-secondary/10 hover:text-primary focus:bg-gradient-to-r focus:from-primary/10 focus:to-secondary/10 active:bg-primary/20 transition-all duration-200 rounded-lg font-medium"
                            >
                              {action.label}
                            </DropdownMenuItem>
                          ))}
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </TableCell>
                  )}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell
                  colSpan={columns.length + (actions ? 1 : 0)}
                  className="text-center"
                >
                  {emptyMessage}
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </CardContent>
    </ScrollArea>
  );
}
