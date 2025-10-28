"use client";

import CVCard from "@/components/comons/my-cvs/CVCard";
import EmptyState from "@/components/comons/my-cvs/EmptyState";

interface Props {
  userCVs: ICV[];
  onCreateNew: () => void;
  onEdit: (cv: ICV) => void;
  onDuplicate: (cvId: string) => void;
  onDelete: (cvId: string) => void;
}

export default function UserCVsSection({
  userCVs,
  onCreateNew,
  onEdit,
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
