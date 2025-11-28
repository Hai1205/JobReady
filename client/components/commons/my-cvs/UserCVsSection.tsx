"use client";

import CVCard from "@/components/commons/my-cvs/CVCard";
import EmptyState from "@/components/commons/my-cvs/EmptyState";

interface Props {
  userCVs: ICV[];
  onCreateNew: () => void;
  onUpdate: (cv: ICV) => void;
  onDuplicate: (cvId: string) => void;
  onDelete: (cvId: string) => void;
  onDownload: (cv: ICV) => void;
}

export default function UserCVsSection({
  userCVs,
  onCreateNew,
  onUpdate,
  onDuplicate,
  onDelete,
  onDownload,
}: Props) {
  return (
    <>
      {userCVs.length === 0 ? (
        <EmptyState onCreateNew={onCreateNew} />
      ) : (
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
      )}
    </>
  );
}
