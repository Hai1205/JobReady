"use client";

import { Bell } from "lucide-react";

export default function NotificationsTab() {
  return (
    <div className="text-center py-12">
      <Bell className="h-16 w-16 mx-auto text-muted-foreground mb-4" />
      <p className="text-muted-foreground">
        Tính năng thông báo đang được phát triển
      </p>
    </div>
  );
}
