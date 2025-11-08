"use client";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card } from "@/components/ui/card";
import { Plus, Trash2 } from "lucide-react";
import { useCVStore } from "@/stores/cvStore";

export function EducationStep() {
  const { currentCV, handleUpdateCV } = useCVStore();

  if (!currentCV) {
    return <div>Loading...</div>;
  }

  const addEducation = () => {
    handleUpdateCV({
      educations: [
        ...currentCV.educations,
        {
          id: crypto.randomUUID(),
          school: "",
          degree: "",
          field: "",
          startDate: "",
          endDate: "",
        },
      ],
    });
  };

  const removeEducation = (id: string) => {
    handleUpdateCV({
      educations: currentCV.educations.filter((edu) => edu.id !== id),
    });
  };

  const updateEducation = (id: string, field: string, value: string) => {
    handleUpdateCV({
      educations: currentCV.educations.map((edu) =>
        edu.id === id ? { ...edu, [field]: value } : edu
      ),
    });
  };

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold">Học vấn</h2>
          <p className="text-muted-foreground">
            Thêm thông tin học vấn của bạn
          </p>
        </div>
        <Button onClick={addEducation} size="sm">
          <Plus className="mr-2 h-4 w-4" />
          Thêm Học vấn
        </Button>
      </div>

      {currentCV.educations.length === 0 ? (
        <Card className="p-8 text-center">
          <p className="text-muted-foreground">
            Chưa có Học vấn nào được thêm. Nhấn "Thêm Học vấn" để bắt đầu.
          </p>
        </Card>
      ) : (
        <div className="flex flex-col gap-4">
          {currentCV.educations.map((edu, index) => (
            <Card key={edu.id} className="p-6">
              <div className="flex flex-col gap-4">
                <div className="flex items-center justify-between">
                  <h3 className="font-semibold">Học vấn {index + 1}</h3>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => removeEducation(edu.id || "")}
                  >
                    <Trash2 className="h-4 w-4 text-destructive" />
                  </Button>
                </div>

                <div className="grid gap-4 md:grid-cols-2">
                  <div className="flex flex-col gap-2">
                    <Label>Trường/Đại học *</Label>
                    <Input
                      value={edu.school}
                      onChange={(e) =>
                        updateEducation(edu.id || "", "school", e.target.value)
                      }
                      placeholder="Tên Trường"
                    />
                  </div>

                  <div className="flex flex-col gap-2">
                    <Label>Bằng cấp *</Label>
                    <Input
                      value={edu.degree}
                      onChange={(e) =>
                        updateEducation(edu.id || "", "degree", e.target.value)
                      }
                      placeholder="Cử nhân, Thạc sĩ, v.v."
                    />
                  </div>

                  <div className="flex flex-col gap-2">
                    <Label>Chuyên ngành</Label>
                    <Input
                      value={edu.field}
                      onChange={(e) =>
                        updateEducation(edu.id || "", "field", e.target.value)
                      }
                      placeholder="Computer Science, Business, etc."
                    />
                  </div>

                  <div className="flex flex-col gap-2">
                    <Label>Ngày nhập học</Label>
                    <Input
                      type="month"
                      value={edu.startDate}
                      onChange={(e) =>
                        updateEducation(
                          edu.id || "",
                          "startDate",
                          e.target.value
                        )
                      }
                    />
                  </div>

                  <div className="flex flex-col gap-2">
                    <Label>Ngày tốt nghiệp</Label>
                    <Input
                      type="month"
                      value={edu.endDate}
                      onChange={(e) =>
                        updateEducation(edu.id || "", "endDate", e.target.value)
                      }
                      placeholder="Để trống nếu hiện tại"
                    />
                  </div>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
