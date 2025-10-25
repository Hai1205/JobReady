"use client";

import CVCard from "@/components/my-cvs/CVCard";
import EmptyState from "@/components/my-cvs/EmptyState";

interface Props {
  cvList: ICV[];
  onCreateNew: () => void;
  onEdit: (cv: ICV) => void;
  onDuplicate: (cvId: string) => void;
  onDelete: (cvId: string) => void;
}

export default function UserCVsSection({
  cvList,
  onCreateNew,
  onEdit,
  onDuplicate,
  onDelete,
}: Props) {
  return (
    <>
      {cvList.length === 0 ? (
        <EmptyState onCreateNew={onCreateNew} />
      ) : (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {cvList.map((cv) => (
            <CVCard
              key={cv.id}
              cv={cv}
              onEdit={onEdit}
              onDuplicate={onDuplicate}
              onDelete={onDelete}
            />
          ))}
        </div>
      )}
    </>
  );
}
