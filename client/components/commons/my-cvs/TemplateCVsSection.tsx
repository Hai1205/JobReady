"use client";

import CVCard from "./CVCard";
import { GridPagination, PaginationData } from "@/components/commons/pagination/GridPagination";

interface Props {
  templateCVs: ICV[];
  handleDuplicate: (cvId: string) => void;
  onDownload: (cv: ICV) => void;
  isLoading?: boolean;
  paginationData?: PaginationData;
  onPageChange?: (page: number) => void;
  onPageSizeChange?: (size: number) => void;
  showPagination?: boolean;
}

export default function TemplateCVsSection({
  templateCVs,
  handleDuplicate,
  onDownload,
  isLoading = false,
  paginationData,
  onPageChange,
  onPageSizeChange,
  showPagination = false,
}: Props) {
  if (!isLoading && (!templateCVs || templateCVs.length === 0)) return null;

  return (
    <div className="mt-12 pt-8 border-t border-border">
      <div className="mb-8">
        <h1 className="text-3xl font-bold bg-linear-to-br from-primary to-secondary bg-clip-text text-transparent">
          CV mẫu
        </h1>
        <p className="text-muted-foreground mt-2">
          Khám phá và sử dụng các mẫu CV chuyên nghiệp
          {paginationData && ` (${paginationData.totalElements} mẫu)`}
        </p>
      </div>

      {isLoading ? (
        <div className="flex justify-center items-center min-h-[400px]">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
        </div>
      ) : (
        <>
          <div className="grid gap-6 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {templateCVs.map((cv) => (
              <CVCard
                key={cv.id}
                cv={cv}
                onDuplicate={handleDuplicate}
                onDownload={onDownload}
              />
            ))}
          </div>

          {showPagination && paginationData && onPageChange && (
            <GridPagination
              paginationData={paginationData}
              onPageChange={onPageChange}
              onPageSizeChange={onPageSizeChange}
              showPageSizeSelector={!!onPageSizeChange}
              pageSizeOptions={[8, 12, 16, 24]}
            />
          )}
        </>
      )}
    </div>
  );
}
