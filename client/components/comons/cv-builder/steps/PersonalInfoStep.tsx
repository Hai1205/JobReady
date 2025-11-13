"use client";

import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Card } from "@/components/ui/card";
import { HighlightableTextarea } from "@/components/comons/cv-builder/HighlightableTextarea";
import { Upload, X } from "lucide-react";
import { useCVStore } from "@/stores/cvStore";
import { useAIStore } from "@/stores/aiStore";
import { useRef } from "react";
import { toast } from "react-toastify";

export function PersonalInfoStep() {
  const { currentCV, handleUpdateCV } = useCVStore();
  const { aiSuggestions } = useAIStore();
  const fileInputRef = useRef<HTMLInputElement>(null);

  if (!currentCV) {
    return <div>Loading...</div>;
  }

  const handleChange = (field: string, value: string) => {
    handleUpdateCV({
      personalInfo: {
        ...currentCV.personalInfo,
        [field]: value,
      },
    });
  };

  const handleAvatarUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      // Check file size (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        toast.error("File quá lớn. Vui lòng chọn file nhỏ hơn 5MB.");
        return;
      }

      // Check file type
      if (!file.type.startsWith("image/")) {
        toast.error("Vui lòng chọn file ảnh.");
        return;
      }

      // Store File object for upload and base64 for preview
      const reader = new FileReader();
      reader.onload = (e) => {
        handleUpdateCV({
          avatar: file, // Store File object for backend
          personalInfo: {
            ...currentCV.personalInfo,
            avatarUrl: e.target?.result as string, // Store base64 for preview
          },
        });
      };
      reader.readAsDataURL(file);
    }
  };

  const removeAvatar = () => {
    handleUpdateCV({
      avatar: null,
      personalInfo: {
        ...currentCV.personalInfo,
        avatarUrl: undefined,
      },
    });
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  const summaryHasSuggestion = aiSuggestions.some(
    (s) => s.section === "Personal Info" && !s.applied && s.lineNumber === 1
  );

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h2 className="text-2xl font-bold">Thông Tin Cá Nhân</h2>
        <p className="text-muted-foreground">
          Hãy cho chúng tôi biết về bản thân và cách liên hệ với bạn
        </p>
      </div>

      {/* Avatar Upload Section */}
      <Card className="p-6">
        <div className="flex items-center gap-6">
          <div className="flex flex-col items-center gap-4">
            <Avatar className="h-24 w-24">
              {(currentCV?.personalInfo?.avatarUrl ||
                currentCV?.personalInfo?.avatarPublicId) && (
                <AvatarImage
                  src={
                    currentCV.personalInfo.avatarUrl ||
                    currentCV.personalInfo.avatarPublicId ||
                    ""
                  }
                />
              )}
              <AvatarFallback className="text-lg">
                {currentCV?.personalInfo?.fullname
                  ? currentCV?.personalInfo?.fullname
                      .split(" ")
                      .map((n) => n[0])
                      .join("")
                      .toUpperCase()
                  : "CV"}
              </AvatarFallback>
            </Avatar>
            <div className="flex gap-2">
              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={() => fileInputRef.current?.click()}
              >
                <Upload className="h-4 w-4 mr-2" />
                Tải ảnh lên
              </Button>
              {currentCV?.avatar && (
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={removeAvatar}
                >
                  <X className="h-4 w-4 mr-2" />
                  Xóa ảnh
                </Button>
              )}
            </div>
            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              onChange={handleAvatarUpload}
              className="hidden"
            />
          </div>
          <div className="flex-1">
            <h3 className="font-semibold mb-2">Ảnh đại diện</h3>
            <p className="text-sm text-muted-foreground mb-2">
              Tải lên ảnh đại diện để CV của bạn trông chuyên nghiệp hơn.
            </p>
            <ul className="text-xs text-muted-foreground space-y-1">
              <li>• Định dạng: JPG, PNG, GIF</li>
              <li>• Kích thước tối đa: 5MB</li>
            </ul>
          </div>
        </div>
      </Card>

      <div className="grid gap-6 md:grid-cols-2">
        <div className="flex flex-col gap-2">
          <Label htmlFor="fullname">Họ và Tên *</Label>
          <Input
            id="fullname"
            value={currentCV?.personalInfo?.fullname}
            onChange={(e) => handleChange("fullname", e.target.value)}
            placeholder="Nguyễn Hoàng Hải"
          />
        </div>

        <div className="flex flex-col gap-2">
          <Label htmlFor="email">Email *</Label>
          <Input
            id="email"
            type="email"
            value={currentCV?.personalInfo?.email}
            onChange={(e) => handleChange("email", e.target.value)}
            placeholder="hainguyenhoang1205@gmail.com"
          />
        </div>

        <div className="flex flex-col gap-2">
          <Label htmlFor="phone">Số Điện Thoại</Label>
          <Input
            id="phone"
            type="tel"
            value={currentCV?.personalInfo?.phone}
            onChange={(e) => handleChange("phone", e.target.value)}
            placeholder="0782748863"
          />
        </div>

        <div className="flex flex-col gap-2">
          <Label htmlFor="location">Địa Chỉ</Label>
          <Input
            id="location"
            value={currentCV?.personalInfo?.location}
            onChange={(e) => handleChange("location", e.target.value)}
            placeholder="Hà Nội, Việt Nam"
          />
        </div>
      </div>

      <div className="flex flex-col gap-2">
        <div className="flex items-center justify-between">
          <Label htmlFor="summary">Mục Tiêu Nghề Nghiệp</Label>
          <span className="text-xs text-muted-foreground">
            {currentCV?.personalInfo?.summary?.length || 0}/2000
          </span>
        </div>
        <HighlightableTextarea
          id="summary"
          value={currentCV?.personalInfo?.summary}
          onChange={(value) => {
            // Truncate to 2000 characters if exceeded
            const truncatedValue =
              value.length > 2000 ? value.slice(0, 2000) : value;
            handleChange("summary", truncatedValue);
          }}
          placeholder="Mô tả ngắn gọn về kinh nghiệm chuyên môn và mục tiêu nghề nghiệp của bạn..."
          rows={6}
          highlighted={summaryHasSuggestion}
        />
        <p className="text-xs text-muted-foreground">
          Viết 2-3 câu nổi bật về kỹ năng và kinh nghiệm chính của bạn
        </p>
      </div>
    </div>
  );
}
