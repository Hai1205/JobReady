"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ScrollArea } from "@/components/ui/scroll-area";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Plus, X } from "lucide-react";

interface UserFormProps {
  data: IPlan | null;
  onChange: (field: keyof IPlan, value: string | string[]) => void;
  showFooterButtons?: boolean;
}

const PlanForm: React.FC<UserFormProps> = ({ data, onChange }) => {
  const [featuresInput, setFeaturesInput] = useState<string>("");
  const [features, setFeatures] = useState<string[]>([]);

  const handleReqKey = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      e.preventDefault();
      addFeatures();
    }
  };

  const addFeatures = () => {
    const v = featuresInput.trim();
    if (!v) return;
    const next = [...features, v];
    setFeatures(next);
    onChange("features", next);
    setFeaturesInput("");
  };

  const removeFeature = (index: number) => {
    const next = features.filter((_, i) => i !== index);
    setFeatures(next);
    onChange("features", next);
  };

  return (
    <div className="space-y-6 pr-2">
      {/* Name */}
      <div className="space-y-2">
        <Label htmlFor="form-name" className="text-sm font-medium">
          Tên gói <span className="text-destructive">*</span>
        </Label>
        <Input
          id="form-name"
          value={data?.name || ""}
          onChange={(e) => onChange("name", e.target.value)}
          className="bg-background/50 border-border/50 focus:border-primary transition-colors"
          placeholder="Nhập tên gói"
          required
        />
      </div>

      {/* Type */}
      <div className="space-y-2">
        <Label htmlFor="form-type" className="text-sm font-medium">
          Loại gói <span className="text-destructive">*</span>
        </Label>
        <Input
          id="form-type"
          value={data?.type || ""}
          onChange={(e) => onChange("type", e.target.value)}
          className="bg-background/50 border-border/50 focus:border-primary transition-colors"
          placeholder="Nhập loại gói"
          required
        />
      </div>

      {/* Price */}
      <div className="space-y-2">
        <Label htmlFor="form-price" className="text-sm font-medium">
          Giá
        </Label>
        <Input
          id="form-price"
          type="number"
          value={data?.price || ""}
          onChange={(e) => onChange("price", e.target.value)}
          className="bg-background/50 border-border/50 focus:border-primary transition-colors"
          placeholder="Nhập giá"
        />
      </div>

      {/* Currency */}
      <div className="space-y-2">
        <Label htmlFor="form-currency" className="text-sm font-medium">
          Tiền tệ
        </Label>
        <Input
          id="form-currency"
          value={data?.currency || ""}
          onChange={(e) => onChange("currency", e.target.value)}
          className="bg-background/50 border-border/50 focus:border-primary transition-colors"
          placeholder="Nhập tiền tệ"
        />
      </div>

      {/* Period */}
      <div className="space-y-2">
        <Label htmlFor="form-period" className="text-sm font-medium">
          Kỳ hạn
        </Label>
        <Input
          id="form-period"
          type="text"
          value={data?.period || ""}
          onChange={(e) => onChange("period", e.target.value)}
          className="bg-background/50 border-border/50 focus:border-primary transition-colors"
        />
      </div>

      {/* Description */}
      <div className="space-y-2">
        <div className="flex items-center justify-between">
          <Label htmlFor="form-description" className="text-sm font-medium">
            Mô tả
          </Label>
          <span className="text-xs text-muted-foreground">
            {data?.description?.length || 0}/2000
          </span>
        </div>
        <ScrollArea className="h-[120px] w-full rounded-md border border-border/50 bg-background/50">
          <textarea
            id="form-description"
            value={data?.description || ""}
            onChange={(e) => {
              const value = e.target.value;
              const truncatedValue =
                value.length > 2000 ? value.slice(0, 2000) : value;
              onChange("description", truncatedValue);
            }}
            className="w-full min-h-[120px] px-3 py-2 text-sm bg-transparent focus:outline-none transition-colors resize-none border-0 overflow-hidden"
            placeholder="Nhập mô tả về gói (tối đa 2000 ký tự)"
            style={{ height: "auto" }}
            onInput={(e) => {
              e.currentTarget.style.height = "auto";
              e.currentTarget.style.height =
                e.currentTarget.scrollHeight + "px";
            }}
          />
        </ScrollArea>
      </div>

      {/* Features */}
      <div className="space-y-2">
        <Label htmlFor="form-features" className="text-sm font-medium">
          Tính năng
        </Label>
        <div className="flex gap-2">
          <Input
            id="form-features"
            value={featuresInput}
            onChange={(e) => setFeaturesInput(e.target.value)}
            onKeyDown={handleReqKey}
            placeholder="Nhập tính năng và nhấn Enter hoặc nhấn Thêm"
            className="h-10"
          />
          <Button
            onClick={addFeatures}
            size="sm"
            className="bg-primary text-primary-foreground shadow-md hover:shadow-lg transform hover:-translate-y-px transition-all"
          >
            <Plus className="h-4 w-4" />
          </Button>
        </div>
        {features.length > 0 ? (
          <div className="flex flex-wrap gap-2 mt-2">
            {features.map((feature, index) => (
              <Badge
                key={index}
                variant="secondary"
                className="gap-2 px-3 py-1.5 text-sm"
              >
                {feature}
                <button
                  onClick={() => removeFeature(index)}
                  className="ml-2 hover:text-destructive"
                  aria-label={`Remove requirement ${feature}`}
                >
                  <X className="h-3 w-3" />
                </button>
              </Badge>
            ))}
          </div>
        ) : (
          <div className="text-muted-foreground text-sm mt-2">
            Chưa có yêu cầu nào được thêm.
          </div>
        )}
      </div>

      {/* isRecommended and isPopular */}
      <div className="flex gap-4">
        <div className="flex-1 space-y-2">
          <Label htmlFor="form-status" className="text-sm font-medium">
            Khuyến nghị
          </Label>
          <Select
            value={data?.isRecommended || false}
            onValueChange={(value) =>
              onChange("isRecommended", value as string)
            }
          >
            <SelectTrigger
              id="form-status"
              className="bg-background/50 border-border/50"
            >
              <SelectValue placeholder="Chọn trạng thái" />
            </SelectTrigger>
            <SelectContent>
              {[
                { value: true, label: "Có" },
                { value: false, label: "Không" },
              ].map((item) => (
                <SelectItem key={item.value} value={item.value}>
                  {item.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        <div className="flex-1 space-y-2">
          <Label htmlFor="form-isPopular" className="text-sm font-medium">
            Phổ biến
          </Label>
          <Select
            value={data?.isPopular || false}
            onValueChange={(value) => onChange("isPopular", value as string)}
          >
            <SelectTrigger
              id="form-isPopular"
              className="bg-background/50 border-border/50"
            >
              <SelectValue placeholder="Chọn vai trò" />
            </SelectTrigger>
            <SelectContent>
              {[
                { value: true, label: "Có" },
                { value: false, label: "Không" },
              ].map((item) => (
                <SelectItem key={item.value} value={item.value}>
                  {item.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>
    </div>
  );
};

export default PlanForm;
