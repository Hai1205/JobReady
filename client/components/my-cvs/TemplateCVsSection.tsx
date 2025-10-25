"use client";

import CVCard from "@/components/my-cvs/CVCard";

interface Props {
  templateCVs: ICV[];
  onDuplicateTemplate: (cv: ICV) => void;
  onDownload: (cv: ICV) => void;
}

export default function TemplateCVsSection({
  templateCVs,
  onDuplicateTemplate,
  onDownload,
}: Props) {
  if (!templateCVs || templateCVs.length === 0) return null;

  return (
    <div className="mt-12 pt-8 border-t border-border">
      <div className="mb-6">
        <h2 className="text-3xl font-bold tracking-tight">CV mẫu</h2>
        <p className="text-muted-foreground mt-2">
          Khám phá và sử dụng các mẫu CV chuyên nghiệp
        </p>
      </div>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        {templateCVs.map((cv) => (
          <CVCard
            key={cv.id}
            cv={cv}
            onDuplicate={() => onDuplicateTemplate(cv)}
            onDownload={() => onDownload(cv)}
          />
        ))}
      </div>
    </div>
  );
}
