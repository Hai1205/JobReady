"use client";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { HighlightableTextarea } from "@/components/comons/cv-builder/HighlightableTextarea";
import { Card } from "@/components/ui/card";
import { Plus, Trash2 } from "lucide-react";
import { useCurrentCV } from "@/hooks/use-cv-mode";

export function ExperienceStep() {
  const { currentCV, handleUpdateCV, aiSuggestions } = useCurrentCV();

  const addExperience = () => {
    handleUpdateCV({
      experiences: [
        ...currentCV.experiences,
        {
          id: crypto.randomUUID(),
          company: "",
          position: "",
          startDate: "",
          endDate: "",
          description: "",
        },
      ],
    });
  };

  const removeExperience = (id: string) => {
    handleUpdateCV({
      experiences: currentCV.experiences.filter((exp) => exp.id !== id),
    });
  };

  const updateExperience = (id: string, field: string, value: string) => {
    handleUpdateCV({
      experiences: currentCV.experiences.map((exp) =>
        exp.id === id ? { ...exp, [field]: value } : exp
      ),
    });
  };

  const hasExperienceSuggestion = (index: number) => {
    return aiSuggestions.some(
      (s) =>
        s.section === "Experience" && !s.applied && s.lineNumber === index + 1
    );
  };

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold">Kinh nghiệm làm việc</h2>
          <p className="text-muted-foreground">
            Thêm thông tin kinh nghiệm làm việc của bạn
          </p>
        </div>
        <Button onClick={addExperience} size="sm">
          <Plus className="mr-2 h-4 w-4" />
          Thêm Kinh nghiệm
        </Button>
      </div>

      {currentCV.experiences.length === 0 ? (
        <Card className="p-8 text-center">
          <p className="text-muted-foreground">
            Chưa có kinh nghiệm làm việc nào được thêm. Nhấn "Thêm Kinh nghiệm"
            để bắt đầu.
          </p>
        </Card>
      ) : (
        <div className="flex flex-col gap-4">
          {currentCV.experiences.map((exp, index) => (
            <Card key={exp.id} className="p-6">
              <div className="flex flex-col gap-4">
                <div className="flex items-center justify-between">
                  <h3 className="font-semibold">Kinh nghiệm {index + 1}</h3>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => removeExperience(exp.id || "")}
                  >
                    <Trash2 className="h-4 w-4 text-destructive" />
                  </Button>
                </div>

                <div className="grid gap-4 md:grid-cols-2">
                  <div className="flex flex-col gap-2">
                    <Label>Công ty *</Label>
                    <Input
                      value={exp.company}
                      onChange={(e) =>
                        updateExperience(
                          exp.id || "",
                          "company",
                          e.target.value
                        )
                      }
                      placeholder="Company Name"
                    />
                  </div>

                  <div className="flex flex-col gap-2">
                    <Label>Chức vụ *</Label>
                    <Input
                      value={exp.position}
                      onChange={(e) =>
                        updateExperience(
                          exp.id || "",
                          "position",
                          e.target.value
                        )
                      }
                      placeholder="Job title"
                    />
                  </div>

                  <div className="flex flex-col gap-2">
                    <Label>Ngày bắt đầu</Label>
                    <Input
                      type="month"
                      value={exp.startDate}
                      onChange={(e) =>
                        updateExperience(
                          exp.id || "",
                          "startDate",
                          e.target.value
                        )
                      }
                    />
                  </div>

                  <div className="flex flex-col gap-2">
                    <Label>Ngày kết thúc</Label>
                    <Input
                      type="month"
                      value={exp.endDate}
                      onChange={(e) =>
                        updateExperience(
                          exp.id || "",
                          "endDate",
                          e.target.value
                        )
                      }
                      placeholder="Leave empty if current"
                    />
                  </div>
                </div>

                <div className="flex flex-col gap-2">
                  <Label>Mô tả</Label>
                  <HighlightableTextarea
                    value={exp.description}
                    onChange={(value) =>
                      updateExperience(exp.id || "", "description", value)
                    }
                    placeholder="Describe your responsibilities and achievements..."
                    rows={4}
                    highlighted={hasExperienceSuggestion(index)}
                  />
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
