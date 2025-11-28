"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Trash2, Download, Copy, Edit } from "lucide-react";
import { CVPreviewCard } from "../cv-builder/CVPreviewCard";
import CVDetailedDialog from "./CVDetailedDialog";

interface CVCardProps {
  cv: ICV;
  onUpdate?: (cv: ICV) => void;
  onDuplicate: (cvId: string) => void;
  onDelete?: (cvId: string) => void;
  onDownload: (cv: ICV) => void;
}

export default function CVCard({
  cv,
  onUpdate,
  onDuplicate,
  onDelete,
  onDownload,
}: CVCardProps) {
  const [dialogOpen, setDialogOpen] = useState(false);

  return (
    <div key={cv.id} className="group flex flex-col gap-3">
      {/* CV Preview Container */}
      <div
        className="relative aspect-[210/297] w-full rounded-lg border border-border bg-white shadow-md transition-all duration-300 hover:shadow-xl hover:border-primary/50 overflow-hidden cursor-pointer"
        onClick={() => setDialogOpen(true)}
      >
        {/* Badge template */}
        <div className="absolute top-2 right-2 z-20 bg-primary/90 text-primary-foreground px-2 py-0.5 rounded text-xs font-medium backdrop-blur-sm">
          {cv.template || "Template 1"}
        </div>

        {/* CV Preview Wrapper */}
        <div className="absolute inset-0">
          <div
            className="relative w-full h-full"
            style={{
              transform: "scale(0.32)",
              transformOrigin: "top left",
              width: "312.5%",
              height: "312.5%",
            }}
          >
            <CVPreviewCard
              currentCV={cv}
              className="shadow-none border-none w-full h-full"
            />
          </div>
        </div>

        {/* Overlay khi hover với buttons */}
        <div className="absolute inset-0 bg-gradient-to-t from-black/70 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-all duration-300 z-10">
          <div className="absolute bottom-0 left-0 right-0 p-3 flex gap-2">
            <Button
              onClick={(e) => {
                e.stopPropagation();
                onDuplicate(cv?.id);
              }}
              size="sm"
              variant="secondary"
              className="flex-1 gap-1.5 text-xs h-8"
            >
              <Copy className="h-3 w-3" />
              Sao chép
            </Button>

            {onUpdate && (
              <Button
                variant="outline"
                size="sm"
                onClick={(e) => {
                  e.stopPropagation();
                  onUpdate(cv);
                }}
                className="flex-1 hover:bg-primary/10 hover:text-primary"
              >
                <Edit className="mr-2 h-3 w-3" />
                Sửa
              </Button>
            )}

            <Button
              onClick={(e) => {
                e.stopPropagation();
                onDownload(cv);
              }}
              size="sm"
              variant="outline"
              className="w-8 h-8 p-0 hover:bg-primary/10 hover:text-primary hover:border-primary/50 transition-all duration-200"
            >
              <Download className="h-3 w-3" />
            </Button>

            {onDelete && (
              <Button
                variant="outline"
                size="sm"
                onClick={(e) => {
                  e.stopPropagation();
                  onDelete(cv.id);
                }}
                className="hover:bg-destructive/10 hover:text-destructive hover:border-destructive/50 transition-all duration-200"
              >
                <Trash2 className="h-3 w-3" />
              </Button>
            )}
          </div>
        </div>

        {cv.title && (
          <div className="px-1">
            <h3 className="font-semibold text-sm line-clamp-1">{cv.title}</h3>
          </div>
        )}
      </div>

      <div className="px-1 mt-2">
        <h3 className="font-semibold text-sm">
          {(cv.title || "Untitled CV").length > 35
            ? (cv.title || "Untitled CV").substring(0, 35) + "..."
            : cv.title || "Untitled CV"}
        </h3>
      </div>

      <CVDetailedDialog
        cv={cv}
        onUpdate={onUpdate}
        onDuplicate={onDuplicate}
        onDelete={onDelete}
        onDownload={onDownload}
        open={dialogOpen}
        onOpenChange={setDialogOpen}
      />
    </div>
  );
}
