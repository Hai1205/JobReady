"use client";
import { useState, useEffect } from "react";
import { useRouter, usePathname } from "next/navigation";
import { useTheme } from "next-themes";
import { Button } from "@/components/ui/button";
import { useAuthStore } from "@/stores/authStore";
import { Sun, Moon } from "lucide-react";
import { NavbarLogo } from "./NavbarLogo";
import { NavbarLinks } from "./NavbarLinks";
import { NavbarUserMenu } from "./NavbarUserMenu";
import { NavbarAuthButtons } from "./NavbarAuthButtons";
import { NavbarMobileMenu } from "./NavbarMobileMenu";

export function Navbar() {
  const authStore = useAuthStore();
  const router = useRouter();
  const pathname = usePathname();
  const { theme, setTheme } = useTheme();

  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [isHydrated, setIsHydrated] = useState(false);

  useEffect(() => setIsHydrated(true), []);

  const userAuth = authStore?.userAuth || null;
  const isAdmin = authStore?.isAdmin || false;
  const logout = authStore?.logout || (() => {});

  const navLinks = [
    { href: "/", label: "Trang Chủ" },
    { href: "/cv-builder", label: "Tạo CV" },
    { href: "/my-cvs", label: "CV Của Tôi" },
  ];
  const allNavLinks = isAdmin
    ? [...navLinks, { href: "/admin", label: "Admin Dashboard" }]
    : navLinks;

  const handleLogout = () => {
    logout();
    setMobileMenuOpen(false);
    router.push("/");
  };

  if (!isHydrated)
    return (
      <nav className="sticky top-0 z-50 w-full border-b border-border bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="max-w-7xl mx-auto flex h-16 items-center justify-between px-4 sm:px-6 lg:px-8">
          <NavbarLogo />
          <Button
            variant="ghost"
            size="icon"
            onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
          >
            <Sun className="h-5 w-5 rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
            <Moon className="absolute h-5 w-5 rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
          </Button>
        </div>
      </nav>
    );

  return (
    <nav className="sticky top-0 z-50 w-full border-b border-border bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="max-w-7xl mx-auto flex h-16 items-center justify-between px-4 sm:px-6 lg:px-8">
        <div className="flex items-center gap-8">
          <NavbarLogo />
          <NavbarLinks pathname={pathname} links={allNavLinks} />
        </div>

        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
          >
            <Sun className="h-5 w-5 rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
            <Moon className="absolute h-5 w-5 rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
          </Button>

          {userAuth ? (
            <NavbarUserMenu
              userAuth={userAuth}
              isAdmin={isAdmin}
              onLogout={handleLogout}
            />
          ) : (
            <NavbarAuthButtons />
          )}

          <NavbarMobileMenu
            pathname={pathname}
            links={allNavLinks}
            userAuth={userAuth}
            isAdmin={isAdmin}
            open={mobileMenuOpen}
            onOpenChange={setMobileMenuOpen}
            onLogout={handleLogout}
          />
        </div>
      </div>
    </nav>
  );
}
