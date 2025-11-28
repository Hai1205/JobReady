"use client";

import CVCard from "./CVCard";

interface Props {
  templateCVs: ICV[];
  handleDuplicate: (cvId: string) => void;
  onDownload: (cv: ICV) => void;
}

export default function TemplateCVsSection({
  templateCVs,
  handleDuplicate,
  onDownload,
}: Props) {
  if (!templateCVs || templateCVs.length === 0) return null;

  return (
    <div className="mt-12 pt-8 border-t border-border">
      <div className="mb-8">
        <h1 className="text-3xl font-bold bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">CV mẫu</h1>
        <p className="text-muted-foreground mt-2">
          Khám phá và sử dụng các mẫu CV chuyên nghiệp
        </p>
      </div>

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
    </div>
  );
}