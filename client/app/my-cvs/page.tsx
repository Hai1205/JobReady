"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/authStore";
import { useCVStore } from "@/stores/cvStore";
// Import my-cvs components
import PageHeader from "@/components/my-cvs/PageHeader";
import CVCard from "@/components/my-cvs/CVCard";
import EmptyState from "@/components/my-cvs/EmptyState";
import DeleteConfirmationDialog from "@/components/my-cvs/DeleteConfirmationDialog";

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
    //   title: `${cv.title} (Copy)`,
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
        <p className="text-muted-foreground">Đang tải CV của bạn...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center py-12">
      <div className="container max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col gap-8">
          <PageHeader onCreateNew={handleCreateNew} />

          {cvList.length === 0 ? (
            <EmptyState onCreateNew={handleCreateNew} />
          ) : (
            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
              {cvList.map((cv) => (
                <CVCard
                  key={cv.id}
                  cv={cv}
                  onEdit={handleEdit}
                  onDuplicate={handleDuplicate}
                  onDelete={handleDeleteClick}
                />
              ))}
            </div>
          )}
        </div>
      </div>

      <DeleteConfirmationDialog
        open={deleteDialogOpen}
        onOpenChange={setDeleteDialogOpen}
        onConfirm={handleDeleteConfirm}
      />
    </div>
  );
}
