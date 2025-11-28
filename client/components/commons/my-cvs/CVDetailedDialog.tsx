"use client";

import { Button } from "@/components/ui/button";
import { Trash2, Download, Copy, Edit } from "lucide-react";
import { CVPreviewCard } from "../cv-builder/CVPreviewCard";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";

interface CVDetailedDialogProps {
  cv: ICV;
  onUpdate?: (cv: ICV) => void;
  onDuplicate: (cvId: string) => void;
  onDelete?: (cvId: string) => void;
  onDownload: (cv: ICV) => void;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export default function CVDetailedDialog({
  cv,
  onUpdate,
  onDuplicate,
  onDelete,
  onDownload,
  open,
  onOpenChange,
}: CVDetailedDialogProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-[90vw] max-h-[90vh] flex flex-col p-0">
        <DialogHeader className="px-6 pt-6 pb-4">
          <DialogTitle>{cv.title || "Untitled CV"}</DialogTitle>
        </DialogHeader>
        
        {/* Scrollable content */}
        <div className="flex-1 overflow-auto scrollbar-hide px-6">
          <div className="flex justify-center pb-4">
            <div className="relative w-full max-w-4xl aspect-[210/297] bg-white border rounded-lg shadow-lg">
              <CVPreviewCard
                currentCV={cv}
                className="shadow-none border-none w-full h-full"
              />
            </div>
          </div>
        </div>

        {/* Fixed footer */}
        <DialogFooter className="flex gap-2 justify-center px-6 py-4 border-t bg-background">
          <Button
            onClick={() => {
              onDuplicate(cv?.id);
              onOpenChange(false);
            }}
            size="sm"
            variant="secondary"
            className="gap-1.5"
          >
            <Copy className="h-4 w-4" />
            Sao chép
          </Button>

          {onUpdate && (
            <Button
              variant="outline"
              size="sm"
              onClick={() => {
                onUpdate(cv);
                onOpenChange(false);
              }}
              className="hover:bg-primary/10 hover:text-primary"
            >
              <Edit className="mr-2 h-4 w-4" />
              Sửa
            </Button>
          )}

          <Button
            onClick={() => {
              onDownload(cv);
              onOpenChange(false);
            }}
            size="sm"
            variant="outline"
            className="hover:bg-primary/10 hover:text-primary hover:border-primary/50 transition-all duration-200"
          >
            <Download className="h-4 w-4" />
          </Button>

          {onDelete && (
            <Button
              variant="outline"
              size="sm"
              onClick={() => {
                onDelete(cv.id);
                onOpenChange(false);
              }}
              className="hover:bg-destructive/10 hover:text-destructive hover:border-destructive/50 transition-all duration-200"
            >
              <Trash2 className="h-4 w-4" />
            </Button>
          )}
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}