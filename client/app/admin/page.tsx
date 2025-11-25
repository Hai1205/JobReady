"use client";

import { useEffect, useState } from "react";
import dynamic from "next/dynamic";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  Users,
  Eye,
  FileText,
  TrendingUp,
  Clock,
  UserPlus,
  Download,
} from "lucide-react";
import { DashboardHeader } from "@/components/commons/admin/DashboardHeader";
import { ReportViewerDialog } from "@/components/commons/admin/ReportViewerDialog";
import { useStatsStore } from "@/stores/statsStore";
import { toast } from "react-toastify";
import DashboardSkeleton from "@/components/commons/layout/DashboardSkeleton";
import { useAuthStore } from "@/stores/authStore";
import { useRouter } from "next/navigation";
import { EUserRole } from "@/types/enum";

function AdminDashboardPage() {
  const { dashboardStats, fetchDashboardStatsInBackground, isLoading, error } =
    useStatsStore();
  const { userAuth } = useAuthStore();
  const router = useRouter();
  const [showReportDialog, setShowReportDialog] = useState(false);

  // Security check: redirect to login if not authenticated or not admin
  useEffect(() => {
    if (!userAuth) {
      router.push("/auth/login");
    } else if (userAuth.role !== EUserRole.ADMIN) {
      router.push("/");
    }
  }, [userAuth]);

  if (!userAuth || userAuth.role !== EUserRole.ADMIN) return null;

  useEffect(() => {
    // Fetch stats in background
    fetchDashboardStatsInBackground();
  }, []);

  // Show loading only if there's no cached data
  if (!dashboardStats && isLoading) {
    return <DashboardSkeleton />;
  }

  if (!dashboardStats) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <p className="text-muted-foreground mb-4">
            Failed to load dashboard statistics.
          </p>
          {error && <p className="text-sm text-destructive mb-4">{error}</p>}
          <div className="flex items-center justify-center gap-3">
            <Button
              onClick={() => fetchDashboardStatsInBackground()}
              className="px-4"
            >
              Retry
            </Button>
            <Button
              variant="secondary"
              onClick={() => window.location.reload()}
              className="px-4"
            >
              Refresh page
            </Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <DashboardHeader title="Dashboard">
        <Button
          onClick={() => setShowReportDialog(true)}
          className="gap-2 bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 shadow-lg shadow-blue-500/30 transition-all duration-200 hover:shadow-xl hover:shadow-blue-500/40 hover:scale-105"
          size="sm"
        >
          <Eye className="h-4 w-4" />
          View Report
        </Button>
      </DashboardHeader>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {/* Total Users */}
        <Card className="bg-gradient-to-br from-primary to-primary/80 text-primary-foreground border-0 shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 transition-all duration-300 hover:scale-105">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-semibold">Total Users</CardTitle>
            <div className="bg-primary-foreground/20 p-2 rounded-lg backdrop-blur-sm">
              <Users className="h-4 w-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">
              {dashboardStats.totalUsers.toLocaleString()}
            </div>
            <p className="text-xs opacity-90 mt-1 flex items-center gap-1">
              <UserPlus className="h-3 w-3" />
              {dashboardStats.usersCreatedThisMonth} this month
            </p>
          </CardContent>
        </Card>

        {/* Active Users */}
        <Card className="bg-gradient-to-br from-green-500 to-green-600 text-primary-foreground border-0 shadow-lg shadow-green-500/30 hover:shadow-xl hover:shadow-green-500/40 transition-all duration-300 hover:scale-105">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-semibold">
              Active Users
            </CardTitle>
            <div className="bg-primary-foreground/20 p-2 rounded-lg backdrop-blur-sm">
              <Users className="h-4 w-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">
              {dashboardStats.activeUsers.toLocaleString()}
            </div>
            <p className="text-xs opacity-90 mt-1">
              {dashboardStats.pendingUsers} pending,{" "}
              {dashboardStats.bannedUsers} banned
            </p>
          </CardContent>
        </Card>

        {/* Total CVs */}
        <Card className="bg-gradient-to-br from-blue-500 to-blue-600 text-primary-foreground border-0 shadow-lg shadow-blue-500/30 hover:shadow-xl hover:shadow-blue-500/40 transition-all duration-300 hover:scale-105">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-semibold">Total CVs</CardTitle>
            <div className="bg-primary-foreground/20 p-2 rounded-lg backdrop-blur-sm">
              <FileText className="h-4 w-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">
              {dashboardStats.totalCVs.toLocaleString()}
            </div>
            <p className="text-xs opacity-90 mt-1 flex items-center gap-1">
              <TrendingUp className="h-3 w-3" />
              {dashboardStats.cvsCreatedThisMonth} this month
            </p>
          </CardContent>
        </Card>

        {/* Public CVs */}
        <Card className="bg-gradient-to-br from-purple-500 to-purple-600 text-primary-foreground border-0 shadow-lg shadow-purple-500/30 hover:shadow-xl hover:shadow-purple-500/40 transition-all duration-300 hover:scale-105">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-semibold">Public CVs</CardTitle>
            <div className="bg-primary-foreground/20 p-2 rounded-lg backdrop-blur-sm">
              <Eye className="h-4 w-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">
              {dashboardStats.publicCVs.toLocaleString()}
            </div>
            <p className="text-xs opacity-90 mt-1">
              {dashboardStats.privateCVs} private
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Recent Activity */}
      <div className="grid grid-cols-1 gap-6">
        <Card className="border-border/50 shadow-lg bg-gradient-to-br from-card to-card/80 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="text-xl font-bold bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
              Recent Activity
            </CardTitle>
            <CardDescription>Latest actions on your platform</CardDescription>
          </CardHeader>
          <CardContent>
            {dashboardStats.recentActivities.length > 0 ? (
              <div className="space-y-4">
                {dashboardStats.recentActivities.map((activity) => {
                  const isUserActivity = activity.type === "user_registered";
                  const isCVActivity = activity.type === "cv_created";

                  return (
                    <div
                      key={activity.id}
                      className="flex items-start group hover:bg-gradient-to-r hover:from-primary/5 hover:to-secondary/5 p-3 rounded-lg transition-all duration-200"
                    >
                      <div className="bg-gradient-to-br from-primary/20 to-secondary/20 p-3 rounded-xl mr-3 group-hover:scale-110 group-hover:shadow-lg group-hover:shadow-primary/20 transition-all duration-200">
                        {isUserActivity && (
                          <UserPlus className="h-4 w-4 text-primary" />
                        )}
                        {isCVActivity && (
                          <FileText className="h-4 w-4 text-primary" />
                        )}
                        {!isUserActivity && !isCVActivity && (
                          <Clock className="h-4 w-4 text-primary" />
                        )}
                      </div>
                      <div className="flex-1">
                        <p className="font-semibold text-foreground group-hover:text-primary transition-colors">
                          {activity.description}
                        </p>
                        <p className="text-xs text-muted-foreground font-medium mt-1">
                          {new Date(activity.timestamp).toLocaleString()}
                        </p>
                      </div>
                    </div>
                  );
                })}
              </div>
            ) : (
              <p className="text-center text-muted-foreground py-8">
                No recent activities
              </p>
            )}
          </CardContent>
        </Card>
      </div>

      {/* Report Viewer Dialog */}
      <ReportViewerDialog
        open={showReportDialog}
        onOpenChange={setShowReportDialog}
      />
    </div>
  );
}

export default dynamic(() => Promise.resolve(AdminDashboardPage), {
  ssr: false,
});
