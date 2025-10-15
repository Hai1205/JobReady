"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Moon, Sun, FileText, Menu } from "lucide-react";
import { useTheme } from "next-themes";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { useState, useEffect } from "react";
import { useAuthStore } from "@/stores/authStore";

export function Navbar() {
  const pathname = usePathname();
  const { theme, setTheme } = useTheme();
  const authStore = useAuthStore();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [isHydrated, setIsHydrated] = useState(false);

  // Ensure hydration is complete
  useEffect(() => {
    setIsHydrated(true);
  }, []);

  // Safe destructuring with fallback values
  const userAuth = authStore?.userAuth || null;
  const isAdmin = authStore?.isAdmin || false;
  const logout = authStore?.logout || (() => {});

  // Don't render auth-dependent content until hydrated
  if (!isHydrated) {
    return (
      <nav className="sticky top-0 z-50 w-full border-b border-border bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex h-16 items-center justify-between">
          <div className="flex items-center gap-8">
            <Link
              href="/"
              className="flex items-center gap-2 font-bold text-xl"
            >
              <FileText className="h-6 w-6 text-primary" />
              <span className="bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
                JobReady
              </span>
            </Link>
          </div>
          <div className="flex items-center gap-4">
            <Button
              variant="ghost"
              size="icon"
              onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
            >
              <Sun className="h-5 w-5 rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
              <Moon className="absolute h-5 w-5 rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
              <span className="sr-only">Toggle theme</span>
            </Button>
          </div>
        </div>
      </nav>
    );
  }

  const navLinks = [
    { href: "/", label: "Home" },
    { href: "/cv-builder", label: "CV Builder" },
    { href: "/my-cvs", label: "My CVs" },
  ];

  const allNavLinks = isAdmin
    ? [...navLinks, { href: "/admin/dashboard", label: "Admin Dashboard" }]
    : navLinks;

  const handleLogout = () => {
    logout();
    setMobileMenuOpen(false);
  };

  return (
    <nav className="sticky top-0 z-50 w-full border-b border-border bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex h-16 items-center justify-between">
        <div className="flex items-center gap-8">
          <Link href="/" className="flex items-center gap-2 font-bold text-xl">
            <FileText className="h-6 w-6 text-primary" />
            <span className="bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
              JobReady
            </span>
          </Link>

          <div className="hidden md:flex items-center gap-6">
            {allNavLinks.map((link) => (
              <Link
                key={link.href}
                href={link.href}
                className={cn(
                  "text-sm font-medium transition-colors hover:text-primary",
                  pathname === link.href
                    ? "text-foreground"
                    : "text-muted-foreground"
                )}
              >
                {link.label}
              </Link>
            ))}
          </div>
        </div>

        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
          >
            <Sun className="h-5 w-5 rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
            <Moon className="absolute h-5 w-5 rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
            <span className="sr-only">Toggle theme</span>
          </Button>

          {userAuth ? (
            <div className="hidden md:flex items-center gap-2">
              <div className="flex flex-col items-end">
                <span className="text-sm font-medium">{userAuth.fullname}</span>
                {isAdmin && (
                  <span className="text-xs text-muted-foreground">Admin</span>
                )}
              </div>
              <Button variant="outline" size="sm" onClick={handleLogout}>
                Logout
              </Button>
            </div>
          ) : (
            <div className="hidden md:flex items-center gap-2">
              <Link href="/auth/login">
                <Button variant="ghost" size="sm">
                  Login
                </Button>
              </Link>
              <Link href="/auth/register">
                <Button size="sm">Register</Button>
              </Link>
            </div>
          )}

          <Sheet open={mobileMenuOpen} onOpenChange={setMobileMenuOpen}>
            <SheetTrigger asChild className="md:hidden">
              <Button variant="ghost" size="icon">
                <Menu className="h-5 w-5" />
              </Button>
            </SheetTrigger>
            <SheetContent>
              <div className="flex flex-col gap-6 mt-8">
                {allNavLinks.map((link) => (
                  <Link
                    key={link.href}
                    href={link.href}
                    onClick={() => setMobileMenuOpen(false)}
                    className={cn(
                      "text-lg font-medium transition-colors hover:text-primary",
                      pathname === link.href
                        ? "text-foreground"
                        : "text-muted-foreground"
                    )}
                  >
                    {link.label}
                  </Link>
                ))}
                {userAuth ? (
                  <div className="flex flex-col gap-4 pt-4 border-t">
                    <div>
                      <p className="text-sm font-medium">{userAuth.fullname}</p>
                      <p className="text-xs text-muted-foreground">
                        {userAuth.email}
                      </p>
                      {isAdmin && (
                        <p className="text-xs text-primary mt-1">Admin</p>
                      )}
                    </div>
                    <Button variant="outline" onClick={handleLogout}>
                      Logout
                    </Button>
                  </div>
                ) : (
                  <div className="flex flex-col gap-2 pt-4 border-t">
                    <Link
                      href="/auth/login"
                      onClick={() => setMobileMenuOpen(false)}
                    >
                      <Button variant="ghost" className="w-full">
                        Login
                      </Button>
                    </Link>
                    <Link
                      href="/auth/register"
                      onClick={() => setMobileMenuOpen(false)}
                    >
                      <Button className="w-full">Register</Button>
                    </Link>
                  </div>
                )}
              </div>
            </SheetContent>
          </Sheet>
        </div>
      </div>
    </nav>
  );
}
