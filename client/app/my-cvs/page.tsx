"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Plus,
  FileText,
  Calendar,
  Edit,
  Trash2,
  Download,
  Copy,
} from "lucide-react";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { Badge } from "@/components/ui/badge";
import { useAuthStore } from "@/stores/authStore";
import { useCVStore } from "@/stores/cvStore";
import { formatDateAgo } from "@/lib/utils";

export default function MyCVsPage() {
  const { userAuth } = useAuthStore();
  const {
    cvList,
    handleSetCurrentCV,
    handleSetCurrentStep,
    deleteCV,
    createCV,
    getUserCVs,
  } = useCVStore();

  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [cvToDelete, setCvToDelete] = useState<string | null>(null);

  useEffect(() => {
    loadCVs();
  }, [userAuth, router]);

  const loadCVs = async () => {
    if (!userAuth) return;

    setLoading(true);

    await getUserCVs(userAuth.id);

    setLoading(false);
  };

  const handleCreateNew = () => {
    handleSetCurrentCV(null);
    handleSetCurrentStep(0);
    router.push("/cv-builder");
  };

  const handleEdit = (cv: ICV) => {
    handleSetCurrentCV(cv);
    handleSetCurrentStep(0);
    router.push("/cv-builder");
  };

  const handleDeleteClick = (id: string) => {
    setCvToDelete(id);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (!cvToDelete) return;

    deleteCV(cvToDelete);
  };

  const handleDuplicate = (cv: ICV) => {
    // const duplicatedCV: ICV = {
    //   ...cv,
    //   id: crypto.randomUUID(),
    //   tittle: `${cv.tittle} (Copy)`,
    //   createdAt: new Date().toISOString(),
    //   updatedAt: new Date().toISOString(),
    // };
    // createCV(duplicatedCV);
  };

  if (!userAuth) {
    return null;
  }

  if (loading) {
    return (
      <div className="container flex items-center justify-center min-h-[calc(100vh-4rem)] py-12">
        <p className="text-muted-foreground">Loading your CVs...</p>
      </div>
    );
  }

  return (
    <div className="container py-12">
      <div className="flex flex-col gap-8">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold">My CVs</h1>
            <p className="text-muted-foreground">
              Manage all your CVs in one place
            </p>
          </div>
          <Button onClick={handleCreateNew}>
            <Plus className="mr-2 h-4 w-4" />
            Create New CV
          </Button>
        </div>

        {cvList.length === 0 ? (
          <Card className="p-12 text-center">
            <div className="flex flex-col items-center gap-4">
              <div className="flex h-20 w-20 items-center justify-center rounded-full bg-muted">
                <FileText className="h-10 w-10 text-muted-foreground" />
              </div>
              <div>
                <h3 className="text-xl font-semibold">No CVs yet</h3>
                <p className="text-muted-foreground mt-2">
                  Create your first CV to get started
                </p>
              </div>
              <Button onClick={handleCreateNew} size="lg">
                <Plus className="mr-2 h-5 w-5" />
                Create Your First CV
              </Button>
            </div>
          </Card>
        ) : (
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {cvList.map((cv) => (
              <Card
                key={cv.id}
                className="group relative overflow-hidden transition-all hover:shadow-lg"
              >
                <CardHeader>
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <CardTitle className="line-clamp-1">
                        {cv.tittle}
                      </CardTitle>
                      <CardDescription className="mt-2 flex items-center gap-2">
                        <Calendar className="h-3 w-3" />
                        Updated {formatDateAgo(cv.updatedAt)}
                      </CardDescription>
                    </div>
                    <FileText className="h-5 w-5 text-muted-foreground" />
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="flex flex-col gap-4">
                    <div className="flex flex-col gap-2">
                      <p className="text-sm font-medium">
                        {cv.personalInfo.fullname || "Untitled"}
                      </p>
                      <p className="text-xs text-muted-foreground line-clamp-2">
                        {cv.personalInfo.summary || "No summary added"}
                      </p>
                    </div>

                    <div className="flex flex-wrap gap-2">
                      {cv.experience.length > 0 && (
                        <Badge variant="secondary" className="text-xs">
                          {cv.experience.length} Experience
                        </Badge>
                      )}
                      {cv.education.length > 0 && (
                        <Badge variant="secondary" className="text-xs">
                          {cv.education.length} Education
                        </Badge>
                      )}
                      {cv.skills.length > 0 && (
                        <Badge variant="secondary" className="text-xs">
                          {cv.skills.length} Skills
                        </Badge>
                      )}
                    </div>

                    <div className="flex gap-2 pt-2 border-t border-border">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleEdit(cv)}
                        className="flex-1"
                      >
                        <Edit className="mr-2 h-3 w-3" />
                        Edit
                      </Button>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleDuplicate(cv)}
                      >
                        <Copy className="h-3 w-3" />
                      </Button>
                      <Button variant="outline" size="sm">
                        <Download className="h-3 w-3" />
                      </Button>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleDeleteClick(cv.id)}
                      >
                        <Trash2 className="h-3 w-3 text-destructive" />
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>

      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Are you sure?</AlertDialogTitle>
            <AlertDialogDescription>
              This action cannot be undone. This will permanently delete your CV
              and remove it from our servers.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDeleteConfirm}
              className="bg-destructive text-destructive-foreground"
            >
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
