"use client";

import { Textarea } from "@/components/ui/textarea";
import { cn } from "@/lib/utils";

interface HighlightableTextareaProps {
  id?: string;
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  rows?: number;
  highlighted?: boolean;
  className?: string;
}

export function HighlightableTextarea({
  id,
  value,
  onChange,
  placeholder,
  rows = 4,
  highlighted = false,
  className,
}: HighlightableTextareaProps) {
  return (
    <div className="relative">
      <Textarea
        id={id}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        rows={rows}
        className={cn(
          "resize-none transition-all",
          highlighted &&
            "border-yellow-500 bg-yellow-500/5 ring-2 ring-yellow-500/20",
          className
        )}
      />
      {highlighted && (
        <div className="absolute right-2 top-2">
          <div className="flex items-center gap-1 rounded-md bg-yellow-500/20 px-2 py-1 text-xs font-medium text-yellow-600 dark:text-yellow-400">
            <span className="relative flex h-2 w-2">
              <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-yellow-400 opacity-75"></span>
              <span className="relative inline-flex h-2 w-2 rounded-full bg-yellow-500"></span>
            </span>
            AI Gợi Ý
          </div>
        </div>
      )}
    </div>
  );
}
