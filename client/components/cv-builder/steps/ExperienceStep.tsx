"use client";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { HighlightableTextarea } from "@/components/cv-builder/HighlightableTextarea";
import { Card } from "@/components/ui/card";
import { Plus, Trash2 } from "lucide-react";
import { useCVStore } from "@/stores/cvStore";

export function ExperienceStep() {
  const { currentCV, handleUpdateCV, aiSuggestions } = useCVStore();

  if (!currentCV) return null;

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
          <h2 className="text-2xl font-bold">Work Experience</h2>
          <p className="text-muted-foreground">
            Add your professional work history
          </p>
        </div>
        <Button onClick={addExperience} size="sm">
          <Plus className="mr-2 h-4 w-4" />
          Add Experience
        </Button>
      </div>

      {currentCV.experiences.length === 0 ? (
        <Card className="p-8 text-center">
          <p className="text-muted-foreground">
            No work experiences added yet. Click "Add Experience" to get
            started.
          </p>
        </Card>
      ) : (
        <div className="flex flex-col gap-4">
          {currentCV.experiences.map((exp, index) => (
            <Card key={exp.id} className="p-6">
              <div className="flex flex-col gap-4">
                <div className="flex items-center justify-between">
                  <h3 className="font-semibold">Experience {index + 1}</h3>
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
                    <Label>Company *</Label>
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
                    <Label>Position *</Label>
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
                    <Label>Start Date</Label>
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
                    <Label>End Date</Label>
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
                  <Label>Description</Label>
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
