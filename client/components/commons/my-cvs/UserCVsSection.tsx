"use client";

import CVCard from "@/components/commons/my-cvs/CVCard";
import EmptyState from "@/components/commons/my-cvs/EmptyState";
import { GridPagination, PaginationData } from "@/components/commons/pagination/GridPagination";

interface Props {
  userCVs: ICV[];
  onCreateNew: () => void;
  onUpdate: (cv: ICV) => void;
  onDuplicate: (cvId: string) => void;
  onDelete: (cvId: string) => void;
  onDownload: (cv: ICV) => void;
  isLoading?: boolean;
  paginationData?: PaginationData;
  onPageChange?: (page: number) => void;
  onPageSizeChange?: (size: number) => void;
  showPagination?: boolean;
}

export default function UserCVsSection({
  userCVs,
  onCreateNew,
  onUpdate,
  onDuplicate,
  onDelete,
  onDownload,
  isLoading = false,
  paginationData,
  onPageChange,
  onPageSizeChange,
  showPagination = false,
}: Props) {
  return (
    <div className="space-y-6">
      {/* Loading state */}
      {isLoading && (
        <div className="flex justify-center items-center min-h-[400px]">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
        </div>
      )}

      {/* Empty state */}
      {!isLoading && userCVs.length === 0 && (
        <EmptyState onCreateNew={onCreateNew} />
      )}

      {/* CV Grid */}
      {!isLoading && userCVs.length > 0 && (
        <>
          <div className="grid gap-6 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {userCVs.map((cv) => (
              <CVCard
                key={cv.id}
                cv={cv}
                onDownload={onDownload}
                onUpdate={onUpdate}
                onDuplicate={onDuplicate}
                onDelete={onDelete}
              />
            ))}
          </div>

          {/* Pagination Controls */}
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
