"use client";

import { Shield } from "lucide-react";

export default function PrivacyTab() {
  return (
    <div className="text-center py-12">
      <Shield className="h-16 w-16 mx-auto text-muted-foreground mb-4" />
      <p className="text-muted-foreground">
        Cài đặt quyền riêng tư đang được phát triển
      </p>
    </div>
  );
}
