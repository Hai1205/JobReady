import { Button } from "@/components/ui/button";
import { Plus } from "lucide-react";

interface DashboardHeaderProps {
  title: string;
  onCreateClick?: () => void;
  createButtonText?: string;
  children?: React.ReactNode;
}

export const DashboardHeader = ({
  title,
  onCreateClick,
  createButtonText = "Create",
  children,
}: DashboardHeaderProps) => {
  return (
    <div className="flex items-center justify-between">
      <h2 className="text-3xl font-bold tracking-tight">{title}</h2>

      <div className="flex items-center gap-2">
        {children}
        {onCreateClick && (
          <Button size="sm" className="h-8 gap-1" onClick={onCreateClick}>
            <Plus className="h-4 w-4" />
            {createButtonText}
          </Button>
        )}
      </div>
    </div>
  );
};
