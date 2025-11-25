"use client";

import CVCard from "@/components/commons/my-cvs/CVCard";
import EmptyState from "@/components/commons/my-cvs/EmptyState";

interface Props {
  userCVs: ICV[];
  onCreateNew: () => void;
  onUpdate: (cv: ICV) => void;
  onDuplicate: (cvId: string) => void;
  onDelete: (cvId: string) => void;
}

export default function UserCVsSection({
  userCVs,
  onCreateNew,
  onUpdate,
  onDuplicate,
  onDelete,
}: Props) {
  return (
    <>
      {userCVs.length === 0 ? (
        <EmptyState onCreateNew={onCreateNew} />
      ) : (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {userCVs.map((cv) => (
            <CVCard
              key={cv.id}
              cv={cv}
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
