"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/authStore";
import { useCVStore } from "@/stores/cvStore";
import PageHeader from "@/components/comons/my-cvs/PageHeader";
import DeleteConfirmationDialog from "@/components/comons/my-cvs/DeleteConfirmationDialog";
import LoadingPage from "@/components/comons/layout/LoadingPage";
import UserCVsSection from "@/components/comons/my-cvs/UserCVsSection";
import TemplateCVsSection from "@/components/comons/my-cvs/TemplateCVsSection";
import { EPrivacy } from "@/types/enum";

export default function MyCVsPageClient() {
  const { userAuth } = useAuthStore();
  const {
    handleSetCurrentStep,
    createCV,
    deleteCV,
    duplicateCV,
    getUserCVs,
    getAllCVs,
    handleGeneratePDF,
    handleSetCurrentCV,
  } = useCVStore();

  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [userCVs, setUserCVs] = useState<ICV[]>([]);
  const [templateCVs, setTemplateCVs] = useState<ICV[]>([]);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [cvToDelete, setCvToDelete] = useState<string | null>(null);

  useEffect(() => {
    const loadData = async () => {
      await loadCVs();
      await loadTemplateCVs();
    };
    loadData();
  }, [userAuth, router]);

  const loadCVs = async () => {
    if (!userAuth) return;

    setLoading(true);

    const response = await getUserCVs(userAuth.id);
    if (response?.data) {
      setUserCVs(response.data.cvs || []);
    }

    setLoading(false);
  };

  const loadTemplateCVs = async () => {
    const response = await getAllCVs();
    if (response?.data) {
      const allCvs = response.data.cvs || [];
      const publicCvs = allCvs.filter((cv) => cv.privacy === EPrivacy.PUBLIC);
      setTemplateCVs(publicCvs);
    }
  };

  const handleCreate = async () => {
    await createCV(userAuth?.id || "");
    handleSetCurrentStep(0);
    router.push("/cv-builder");
  };

  const handleEdit = (cv: ICV) => {
    handleSetCurrentStep(0);
    handleSetCurrentCV(cv);
    router.push(`/cv-builder`);
  };

  const handleDeleteClick = (cvId: string) => {
    setCvToDelete(cvId);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (!cvToDelete) return;

    deleteCV(cvToDelete);
  };

  const handleDuplicate = (cvId: string) => {
    duplicateCV(cvId);
    router.push("/cv-builder");
  };

  if (!userAuth) {
    return null;
  }

  if (loading) {
    return <LoadingPage />;
  }

  return (
    <div className="min-h-screen flex items-center justify-center py-12">
      <div className="container max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col gap-8">
          <PageHeader onCreateNew={handleCreate} />

          <UserCVsSection
            userCVs={userCVs}
            onCreateNew={handleCreate}
            onUpdate={handleEdit}
            onDuplicate={handleDuplicate}
            onDelete={handleDeleteClick}
          />

          <TemplateCVsSection
            templateCVs={templateCVs}
            handleDuplicate={handleDuplicate}
            onDownload={handleGeneratePDF}
          />
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
