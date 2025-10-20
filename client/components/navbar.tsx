"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Moon, Sun, FileText, Menu } from "lucide-react";
import { useTheme } from "next-themes";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import {
  Sheet,
  SheetContent,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { useAuthStore } from "@/stores/authStore";

export function Navbar() {
  const authStore = useAuthStore();

  const router = useRouter();
  const pathname = usePathname();
  const { theme, setTheme } = useTheme();

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
    router.push("/");
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
            <div className="hidden md:flex items-center gap-3">
              <Link href="/settings">
                <div className="flex items-center gap-3 px-3 py-2 rounded-lg bg-gradient-to-r from-primary/5 to-secondary/5 border border-border/50">
                  <Avatar className="h-8 w-8 border-2 border-primary/20 shadow-md">
                    <AvatarImage
                      src={userAuth.avatarUrl}
                      alt={userAuth.fullname || "User"}
                      className="object-cover"
                    />
                    <AvatarFallback className="bg-gradient-to-br from-primary to-secondary text-primary-foreground font-bold text-sm">
                      {userAuth.fullname?.charAt(0).toUpperCase() || "U"}
                    </AvatarFallback>
                  </Avatar>
                  <div className="flex flex-col items-start">
                    <span className="text-sm font-semibold">
                      {userAuth.fullname}
                    </span>
                    {isAdmin && (
                      <span className="text-xs text-primary font-medium">
                        Admin
                      </span>
                    )}
                  </div>
                </div>
              </Link>
              <Button
                variant="outline"
                size="sm"
                onClick={handleLogout}
                className="hover:bg-destructive/10 hover:text-destructive hover:border-destructive/50 transition-all duration-200"
              >
                Logout
              </Button>
            </div>
          ) : (
            <div className="hidden md:flex items-center gap-2">
              <Link href="/auth/login">
                <Button
                  variant="ghost"
                  size="sm"
                  className="hover:bg-primary/10 hover:text-primary transition-all duration-200"
                >
                  Login
                </Button>
              </Link>
              <Link href="/auth/register">
                <Button
                  size="sm"
                  className="bg-gradient-to-r from-primary to-secondary hover:from-primary/90 hover:to-secondary/90 shadow-md shadow-primary/20 hover:shadow-lg hover:shadow-primary/30 transition-all duration-200"
                >
                  Register
                </Button>
              </Link>
            </div>
          )}

          <Sheet open={mobileMenuOpen} onOpenChange={setMobileMenuOpen}>
            <SheetTrigger asChild className="md:hidden">
              <Button
                variant="ghost"
                size="icon"
                className="hover:bg-primary/10"
              >
                <Menu className="h-5 w-5" />
              </Button>
            </SheetTrigger>
            <SheetContent className="w-[300px] sm:w-[400px] bg-gradient-to-br from-card to-card/80 backdrop-blur-xl border-border/50">
              <SheetTitle className="sr-only">Navigation Menu</SheetTitle>

              {/* Mobile Header */}
              <div className="flex items-center gap-2 mb-8 pb-6 border-b border-border/50">
                <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br from-primary to-secondary shadow-lg">
                  <FileText className="h-5 w-5 text-primary-foreground" />
                </div>
                <span className="text-xl font-bold bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
                  JobReady
                </span>
              </div>

              {/* User Info Section (if logged in) */}
              {userAuth && (
                <div className="mb-6 p-4 rounded-xl bg-gradient-to-r from-primary/10 to-secondary/10 border border-border/50">
                  <div className="flex items-center gap-3">
                    <Avatar className="h-12 w-12 border-2 border-primary/20 shadow-lg">
                      <AvatarImage
                        src={userAuth.avatarUrl}
                        alt={userAuth.fullname || "User"}
                        className="object-cover"
                      />
                      <AvatarFallback className="bg-gradient-to-br from-primary to-secondary text-primary-foreground font-bold text-lg">
                        {userAuth.fullname?.charAt(0).toUpperCase() || "U"}
                      </AvatarFallback>
                    </Avatar>
                    <div className="flex-1">
                      <p className="text-sm font-semibold text-foreground">
                        {userAuth.fullname}
                      </p>
                      <p className="text-xs text-muted-foreground line-clamp-1">
                        {userAuth.email}
                      </p>
                      {isAdmin && (
                        <div className="inline-flex items-center gap-1 mt-1 px-2 py-0.5 rounded-md bg-primary/20 text-primary text-xs font-medium">
                          Admin
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              )}

              {/* Navigation Links */}
              <div className="flex flex-col gap-2 mb-6">
                {allNavLinks.map((link) => (
                  <Link
                    key={link.href}
                    href={link.href}
                    onClick={() => setMobileMenuOpen(false)}
                    className={cn(
                      "flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-semibold transition-all duration-200",
                      pathname === link.href
                        ? "bg-gradient-to-r from-primary to-secondary text-primary-foreground shadow-lg shadow-primary/30"
                        : "text-muted-foreground hover:text-foreground hover:bg-accent/50"
                    )}
                  >
                    {pathname === link.href && (
                      <div className="h-2 w-2 rounded-full bg-primary-foreground animate-pulse" />
                    )}
                    {link.label}
                  </Link>
                ))}
              </div>

              {/* Auth Buttons */}
              {userAuth ? (
                <div className="mt-auto pt-6 border-t border-border/50">
                  <Button
                    variant="outline"
                    onClick={handleLogout}
                    className="w-full bg-gradient-to-r from-destructive/10 to-destructive/5 hover:from-destructive/20 hover:to-destructive/10 text-destructive hover:text-destructive border-destructive/30 hover:border-destructive/50 transition-all duration-200"
                  >
                    Logout
                  </Button>
                </div>
              ) : (
                <div className="mt-auto pt-6 border-t border-border/50 flex flex-col gap-3">
                  <Link
                    href="/auth/login"
                    onClick={() => setMobileMenuOpen(false)}
                  >
                    <Button
                      variant="outline"
                      className="w-full hover:bg-accent/50 transition-all duration-200"
                    >
                      Login
                    </Button>
                  </Link>
                  <Link
                    href="/auth/register"
                    onClick={() => setMobileMenuOpen(false)}
                  >
                    <Button className="w-full bg-gradient-to-r from-primary to-secondary hover:from-primary/90 hover:to-secondary/90 shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 transition-all duration-200">
                      Register
                    </Button>
                  </Link>
                </div>
              )}
            </SheetContent>
          </Sheet>
        </div>
      </div>
    </nav>
  );
}
