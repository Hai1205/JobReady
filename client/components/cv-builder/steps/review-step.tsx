"use client"

import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Separator } from "@/components/ui/separator"
import { Mail, Phone, MapPin, Calendar } from "lucide-react"
import { useCVStore } from "@/stores/cvStore"

export function ReviewStep() {
  const { currentCV } = useCVStore()

  if (!currentCV) return null

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h2 className="text-2xl font-bold">Review Your CV</h2>
        <p className="text-muted-foreground">Review all information before saving</p>
      </div>

      <Card className="p-8">
        {/* Personal Info */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold">{currentCV.personalInfo.fullName || "Your Name"}</h1>
          <div className="mt-4 flex flex-wrap gap-4 text-sm text-muted-foreground">
            {currentCV.personalInfo.email && (
              <div className="flex items-center gap-2">
                <Mail className="h-4 w-4" />
                {currentCV.personalInfo.email}
              </div>
            )}
            {currentCV.personalInfo.phone && (
              <div className="flex items-center gap-2">
                <Phone className="h-4 w-4" />
                {currentCV.personalInfo.phone}
              </div>
            )}
            {currentCV.personalInfo.location && (
              <div className="flex items-center gap-2">
                <MapPin className="h-4 w-4" />
                {currentCV.personalInfo.location}
              </div>
            )}
          </div>
          {currentCV.personalInfo.summary && <p className="mt-4 leading-relaxed">{currentCV.personalInfo.summary}</p>}
        </div>

        <Separator className="my-8" />

        {/* Experience */}
        {currentCV.experience.length > 0 && (
          <>
            <div className="mb-6">
              <h2 className="text-xl font-bold">Work Experience</h2>
            </div>
            <div className="flex flex-col gap-6">
              {currentCV.experience.map((exp) => (
                <div key={exp.id}>
                  <div className="flex items-start justify-between">
                    <div>
                      <h3 className="font-semibold">{exp.position}</h3>
                      <p className="text-muted-foreground">{exp.company}</p>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <Calendar className="h-4 w-4" />
                      {exp.startDate} - {exp.endDate || "Present"}
                    </div>
                  </div>
                  {exp.description && <p className="mt-2 text-sm leading-relaxed">{exp.description}</p>}
                </div>
              ))}
            </div>
            <Separator className="my-8" />
          </>
        )}

        {/* Education */}
        {currentCV.education.length > 0 && (
          <>
            <div className="mb-6">
              <h2 className="text-xl font-bold">Education</h2>
            </div>
            <div className="flex flex-col gap-6">
              {currentCV.education.map((edu) => (
                <div key={edu.id}>
                  <div className="flex items-start justify-between">
                    <div>
                      <h3 className="font-semibold">
                        {edu.degree} {edu.field && `in ${edu.field}`}
                      </h3>
                      <p className="text-muted-foreground">{edu.school}</p>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <Calendar className="h-4 w-4" />
                      {edu.startDate} - {edu.endDate || "Present"}
                    </div>
                  </div>
                </div>
              ))}
            </div>
            <Separator className="my-8" />
          </>
        )}

        {/* Skills */}
        {currentCV.skills.length > 0 && (
          <>
            <div className="mb-6">
              <h2 className="text-xl font-bold">Skills</h2>
            </div>
            <div className="flex flex-wrap gap-2">
              {currentCV.skills.map((skill, index) => (
                <Badge key={index} variant="secondary">
                  {skill}
                </Badge>
              ))}
            </div>
          </>
        )}
      </Card>
    </div>
  )
}
