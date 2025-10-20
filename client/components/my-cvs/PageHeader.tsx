"use client";

import { Button } from "@/components/ui/button";
import { Plus } from "lucide-react";

interface PageHeaderProps {
  onCreateNew: () => void;
}

export default function PageHeader({ onCreateNew }: PageHeaderProps) {
  return (
    <div className="flex items-center justify-between">
      <div>
        <h1 className="text-3xl font-bold bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
          My CVs
        </h1>
        <p className="text-muted-foreground">
          Manage all your CVs in one place
        </p>
      </div>
      <Button
        onClick={onCreateNew}
        className="bg-gradient-to-r from-primary to-secondary hover:from-primary/90 hover:to-secondary/90 shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 transition-all duration-200 hover:scale-105"
      >
        <Plus className="mr-2 h-4 w-4" />
        Create New CV
      </Button>
    </div>
  );
}
