"use client"

import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { HighlightableTextarea } from "@/components/cv-builder/highlightable-textarea"
import { useCVStore } from "@/stores/cvStore"

export function PersonalInfoStep() {
  const { currentCV, handleUpdateCV, aiSuggestions } = useCVStore()

  if (!currentCV) return null

  const handleChange = (field: string, value: string) => {
    handleUpdateCV({
      personalInfo: {
        ...currentCV.personalInfo,
        [field]: value,
      },
    })
  }

  const summaryHasSuggestion = aiSuggestions.some(
    (s) => s.section === "Personal Info" && !s.applied && s.lineNumber === 1,
  )

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h2 className="text-2xl font-bold">Personal Information</h2>
        <p className="text-muted-foreground">Tell us about yourself and how to contact you</p>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        <div className="flex flex-col gap-2">
          <Label htmlFor="fullName">Full Name *</Label>
          <Input
            id="fullName"
            value={currentCV.personalInfo.fullName}
            onChange={(e) => handleChange("fullName", e.target.value)}
            placeholder="John Doe"
          />
        </div>

        <div className="flex flex-col gap-2">
          <Label htmlFor="email">Email *</Label>
          <Input
            id="email"
            type="email"
            value={currentCV.personalInfo.email}
            onChange={(e) => handleChange("email", e.target.value)}
            placeholder="john@example.com"
          />
        </div>

        <div className="flex flex-col gap-2">
          <Label htmlFor="phone">Phone Number</Label>
          <Input
            id="phone"
            type="tel"
            value={currentCV.personalInfo.phone}
            onChange={(e) => handleChange("phone", e.target.value)}
            placeholder="+1 (555) 123-4567"
          />
        </div>

        <div className="flex flex-col gap-2">
          <Label htmlFor="location">Location</Label>
          <Input
            id="location"
            value={currentCV.personalInfo.location}
            onChange={(e) => handleChange("location", e.target.value)}
            placeholder="New York, NY"
          />
        </div>
      </div>

      <div className="flex flex-col gap-2">
        <Label htmlFor="summary">Professional Summary</Label>
        <HighlightableTextarea
          id="summary"
          value={currentCV.personalInfo.summary}
          onChange={(value) => handleChange("summary", value)}
          placeholder="A brief summary of your professional background and career goals..."
          rows={6}
          highlighted={summaryHasSuggestion}
        />
        <p className="text-xs text-muted-foreground">Write 2-3 sentences highlighting your key skills and experience</p>
      </div>
    </div>
  )
}
