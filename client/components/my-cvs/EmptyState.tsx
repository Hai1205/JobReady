"use client";

import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { FileText, Plus } from "lucide-react";

interface EmptyStateProps {
  onCreateNew: () => void;
}

export default function EmptyState({ onCreateNew }: EmptyStateProps) {
  return (
    <Card className="p-12 text-center border-border/50 shadow-lg bg-gradient-to-br from-card to-card/80 backdrop-blur-sm">
      <div className="flex flex-col items-center gap-4">
        <div className="flex h-20 w-20 items-center justify-center rounded-full bg-gradient-to-br from-primary/20 to-secondary/20 backdrop-blur-sm">
          <FileText className="h-10 w-10 text-primary" />
        </div>
        <div>
          <h3 className="text-xl font-semibold">No CVs yet</h3>
          <p className="text-muted-foreground mt-2">
            Create your first CV to get started
          </p>
        </div>
        <Button
          onClick={onCreateNew}
          size="lg"
          className="bg-gradient-to-r from-primary to-secondary hover:from-primary/90 hover:to-secondary/90 shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 transition-all duration-200 hover:scale-105"
        >
          <Plus className="mr-2 h-5 w-5" />
          Create Your First CV
        </Button>
      </div>
    </Card>
  );
}
