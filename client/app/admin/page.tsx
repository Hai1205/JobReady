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
import {
  Users,
  Eye,
  FileText,
  MessageSquare,
  Briefcase,
  TrendingUp,
} from "lucide-react";
import { DashboardHeader } from "@/components/comons/admin/DashboardHeader";
import { mockStats } from "@/services/mockData";

function AdminDashboardPage() {
  const [stats] = useState(mockStats);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Simulate API call
    const timer = setTimeout(() => {
      setLoading(false);
    }, 1000);

    return () => clearTimeout(timer);
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <DashboardHeader title="Dashboard" />

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card className="bg-gradient-to-br from-primary to-primary/80 text-primary-foreground border-0 shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 transition-all duration-300 hover:scale-105">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-semibold">
              Total Visitors
            </CardTitle>
            <div className="bg-primary-foreground/20 p-2 rounded-lg backdrop-blur-sm">
              <Users className="h-4 w-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">
              {stats.totalVisitors.toLocaleString()}
            </div>
            <p className="text-xs opacity-90 mt-1 flex items-center gap-1">
              <TrendingUp className="h-3 w-3" />
              +12% from last month
            </p>
          </CardContent>
        </Card>

        <Card className="bg-gradient-to-br from-secondary to-secondary/80 text-secondary-foreground border-0 shadow-lg shadow-secondary/30 hover:shadow-xl hover:shadow-secondary/40 transition-all duration-300 hover:scale-105">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-semibold">
              Unique Visitors
            </CardTitle>
            <div className="bg-secondary-foreground/20 p-2 rounded-lg backdrop-blur-sm">
              <Eye className="h-4 w-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">
              {stats.uniqueVisitors.toLocaleString()}
            </div>
            <p className="text-xs opacity-90 mt-1 flex items-center gap-1">
              <TrendingUp className="h-3 w-3" />
              +8% from last month
            </p>
          </CardContent>
        </Card>

        <Card className="bg-gradient-to-br from-chart-1 to-chart-1/80 text-primary-foreground border-0 shadow-lg shadow-chart-1/30 hover:shadow-xl hover:shadow-chart-1/40 transition-all duration-300 hover:scale-105">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-semibold">Page Views</CardTitle>
            <div className="bg-primary-foreground/20 p-2 rounded-lg backdrop-blur-sm">
              <FileText className="h-4 w-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">
              {stats.pageViews.toLocaleString()}
            </div>
            <p className="text-xs opacity-90 mt-1 flex items-center gap-1">
              <TrendingUp className="h-3 w-3" />
              +18% from last month
            </p>
          </CardContent>
        </Card>

        <Card className="bg-gradient-to-br from-chart-2 to-chart-2/80 text-primary-foreground border-0 shadow-lg shadow-chart-2/30 hover:shadow-xl hover:shadow-chart-2/40 transition-all duration-300 hover:scale-105">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-semibold">
              Avg. Session
            </CardTitle>
            <div className="bg-primary-foreground/20 p-2 rounded-lg backdrop-blur-sm">
              <TrendingUp className="h-4 w-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{stats.avgSessionDuration}</div>
            <p className="text-xs opacity-90 mt-1 flex items-center gap-1">
              <TrendingUp className="h-3 w-3" />
              +24s from last month
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Charts and Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Top Pages */}
        <Card className="lg:col-span-2 border-border/50 shadow-lg bg-gradient-to-br from-card to-card/80 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="text-xl font-bold bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
              Top Pages
            </CardTitle>
            <CardDescription>Most visited pages this month</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-5">
              {stats.topPages.map((page, index) => (
                <div
                  key={index}
                  className="flex items-center group hover:scale-[1.02] transition-transform duration-200"
                >
                  <div className="flex-1">
                    <p className="font-semibold text-foreground group-hover:text-primary transition-colors">
                      {page.name}
                    </p>
                    <div className="w-full bg-gradient-to-r from-muted to-muted/50 rounded-full h-3 mt-2 overflow-hidden shadow-inner">
                      <div
                        className="bg-gradient-to-r from-primary to-secondary h-3 rounded-full transition-all duration-500 shadow-lg shadow-primary/30"
                        style={{
                          width: `${
                            (page.views / stats.topPages[0].views) * 100
                          }%`,
                        }}
                      ></div>
                    </div>
                  </div>
                  <div className="ml-6 text-right">
                    <p className="font-bold text-lg text-primary">
                      {page.views.toLocaleString()}
                    </p>
                    <p className="text-xs text-muted-foreground font-medium">
                      views
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Recent Activity */}
        <Card className="border-border/50 shadow-lg bg-gradient-to-br from-card to-card/80 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="text-xl font-bold bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
              Recent Activity
            </CardTitle>
            <CardDescription>Latest actions on your site</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {stats.recentActivity.map((activity) => (
                <div
                  key={activity.id}
                  className="flex items-start group hover:bg-gradient-to-r hover:from-primary/5 hover:to-secondary/5 p-3 rounded-lg transition-all duration-200"
                >
                  <div className="bg-gradient-to-br from-primary/20 to-secondary/20 p-3 rounded-xl mr-3 group-hover:scale-110 group-hover:shadow-lg group-hover:shadow-primary/20 transition-all duration-200">
                    {activity.id === 1 && (
                      <Users className="h-4 w-4 text-primary" />
                    )}
                    {activity.id === 2 && (
                      <Briefcase className="h-4 w-4 text-primary" />
                    )}
                    {activity.id === 3 && (
                      <MessageSquare className="h-4 w-4 text-primary" />
                    )}
                    {activity.id === 4 && (
                      <FileText className="h-4 w-4 text-primary" />
                    )}
                  </div>
                  <div className="flex-1">
                    <p className="font-semibold text-foreground group-hover:text-primary transition-colors">
                      {activity.action}
                    </p>
                    <p className="text-xs text-muted-foreground font-medium mt-1">
                      {activity.time}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

export default dynamic(() => Promise.resolve(AdminDashboardPage), {
  ssr: false,
});
