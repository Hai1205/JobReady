"use client";

import {
  Home,
  Users,
  FileText,
  MessageSquare,
  Briefcase,
  Menu,
  X,
  LogOut,
} from "lucide-react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { cn } from "@/lib/utils";
import { useAuthStore } from "@/stores/authStore";

interface AdminSidebarProps {
  collapsed: boolean;
  width: number;
  onToggle: () => void;
  onStartResizing: (e: React.MouseEvent) => void;
}

export default function AdminSidebar({
  collapsed,
  width,
  onToggle,
  onStartResizing,
}: AdminSidebarProps) {
  const { logout } = useAuthStore();

  const pathname = usePathname();
  const router = useRouter();

  const handleLogout = async () => {
    try {
      const response = await logout();
      if (response) {
        router.push("/auth/login");
      }
    } catch (error) {
      console.error("Logout failed:", error);
    }
  };

  const menuItems = [
    { icon: Home, label: "Dashboard", href: "/admin" },
    {
      icon: FileText,
      label: "Program Dashboard",
      href: "/admin/program-dashboard",
    },
    { icon: Briefcase, label: "Job Dashboard", href: "/admin/job-dashboard" },
    {
      icon: MessageSquare,
      label: "Contact Dashboard",
      href: "/admin/contact-dashboard",
    },
    { icon: FileText, label: "CV Dashboard", href: "/admin/cv-dashboard" },
    { icon: Users, label: "User Dashboard", href: "/admin/user-dashboard" },
  ];

  return (
    <aside
      className={cn(
        "bg-white dark:bg-gray-800 border-2 border-gray-300 dark:border-gray-700 flex flex-col transition-all duration-300 ease-in-out z-30 rounded-tr-3xl rounded-br-3xl",
        collapsed ? "shadow-md" : "shadow-lg"
      )}
      style={{ width: collapsed ? 80 : width, height: "100%" }}
    >
      {/* Sidebar Header */}
      <div className="flex items-center justify-between p-4 border-b-2 border-gray-300 dark:border-gray-700">
        {!collapsed && (
          <div className="flex items-center space-x-2">
            <div className="bg-primary w-8 h-8 rounded-lg flex items-center justify-center">
              <Home className="h-5 w-5 text-white" />
            </div>
            <span className="text-xl font-bold text-gray-800 dark:text-white">
              Admin
            </span>
          </div>
        )}
        <button
          onClick={onToggle}
          className="ml-auto p-1 rounded-md hover:bg-gray-100 dark:hover:bg-gray-700"
        >
          {collapsed ? (
            <Menu className="h-5 w-5 text-gray-800 dark:text-white" />
          ) : (
            <X className="h-5 w-5 text-gray-800 dark:text-white" />
          )}
        </button>
      </div>

      {/* Sidebar Menu */}
      <nav className="flex-1 overflow-y-auto py-4">
        <ul className="space-y-1 px-2">
          {menuItems.map((item) => {
            const Icon = item.icon;
            const isActive = pathname === item.href;

            return (
              <li key={item.href}>
                <Link
                  href={item.href}
                  className={cn(
                    "flex items-center rounded-lg px-3 py-2 text-sm font-medium transition-colors",
                    isActive
                      ? "bg-primary text-white"
                      : "text-gray-700 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700"
                  )}
                >
                  <Icon className="h-5 w-5" />
                  {!collapsed && <span className="ml-3">{item.label}</span>}
                </Link>
              </li>
            );
          })}
        </ul>
      </nav>

      {/* Logout Button */}
      <div className="mt-auto px-2 pb-4">
        <button
          onClick={handleLogout}
          className={cn(
            "flex w-full items-center rounded-lg px-3 py-2 text-sm font-medium transition-colors",
            "text-red-600 hover:bg-red-50 dark:text-red-400 dark:hover:bg-gray-700"
          )}
        >
          <LogOut className="h-5 w-5" />
          {!collapsed && <span className="ml-3">Đăng xuất</span>}
        </button>
      </div>

      {/* Resize Handle */}
      <div
        className="absolute top-0 right-0 h-full w-1.5 cursor-col-resize hover:bg-primary transition-colors"
        onMouseDown={onStartResizing}
      />
    </aside>
  );
}
