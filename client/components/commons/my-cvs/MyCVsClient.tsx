"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/authStore";
import { useCVStore } from "@/stores/cvStore";
import PageHeader from "@/components/commons/my-cvs/PageHeader";
import UserCVsSkeleton from "@/components/commons/layout/UserCVsSkeleton";
import UserCVsSection from "@/components/commons/my-cvs/UserCVsSection";
import TemplateCVsSection from "@/components/commons/my-cvs/TemplateCVsSection";
import ConfirmationDialog from "../layout/ConfirmationDialog";
import { toast } from "react-toastify";
import DraggingOnPage from "../layout/DraggingOnPage";

export default function MyCVsClient() {
  const { userAuth } = useAuthStore();
  const {
    createCV,
    deleteCV,
    duplicateCV,
    fetchUserCVsInBackground,
    fetchAllCVsInBackground,
    handleGeneratePDF,
    handleSetCurrentCV,
    userCVs,
    CVsTable,
    isLoadingUserCVs,
    importCV,
  } = useCVStore();

  const router = useRouter();
  const [templateCVs, setTemplateCVs] = useState<ICV[]>([]);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [cvToDelete, setCvToDelete] = useState<string | null>(null);
  const [isDraggingOnPage, setIsDraggingOnPage] = useState(false);
  const [importDialogOpen, setImportDialogOpen] = useState(false);
  const [droppedFile, setDroppedFile] = useState<File | null>(null);

  // Pagination for user CVs
  const [userCVsPage, setUserCVsPage] = useState(1);
  const userCVsPageSize = 12;
  const userCVsTotalPages = Math.ceil(userCVs.length / userCVsPageSize);
  const userCVsStartIndex = (userCVsPage - 1) * userCVsPageSize;
  const userCVsEndIndex = userCVsStartIndex + userCVsPageSize;

  const userCVsPagination = {
    paginationState: { page: userCVsPage, pageSize: userCVsPageSize },
    paginationData: {
      totalElements: userCVs.length,
      totalPages: userCVsTotalPages,
      currentPage: userCVsPage,
      pageSize: userCVsPageSize,
      hasNext: userCVsPage < userCVsTotalPages,
      hasPrevious: userCVsPage > 1,
    },
    setPage: setUserCVsPage,
  };

  // Pagination for template CVs
  const [templateCVsPage, setTemplateCVsPage] = useState(1);
  const templateCVsPageSize = 12;
  const templateCVsTotalPages = Math.ceil(
    templateCVs.length / templateCVsPageSize
  );
  const templateCVsStartIndex = (templateCVsPage - 1) * templateCVsPageSize;
  const templateCVsEndIndex = templateCVsStartIndex + templateCVsPageSize;

  const templateCVsPagination = {
    paginationState: { page: templateCVsPage, pageSize: templateCVsPageSize },
    paginationData: {
      totalElements: templateCVs.length,
      totalPages: templateCVsTotalPages,
      currentPage: templateCVsPage,
      pageSize: templateCVsPageSize,
      hasNext: templateCVsPage < templateCVsTotalPages,
      hasPrevious: templateCVsPage > 1,
    },
    setPage: setTemplateCVsPage,
  };

  // Paginate user CVs in memory
  const paginatedUserCVs = userCVs.slice(userCVsStartIndex, userCVsEndIndex);

  // Paginate template CVs in memory
  const paginatedTemplateCVs = templateCVs.slice(
    templateCVsStartIndex,
    templateCVsEndIndex
  );

  useEffect(() => {
    if (userAuth) {
      fetchUserCVsInBackground(userAuth.id);
      console.log("Fetching user CVs for user:", userAuth.id);
    }
    fetchAllCVsInBackground();
  }, [userAuth]);

  useEffect(() => {
    // Filter public CVs from CVsTable
    const publicCvs = CVsTable.filter((cv) => cv.isVisibility === true);
    setTemplateCVs(publicCvs);
  }, [CVsTable]);

  const handleCreate = async () => {
    try {
      router.push("/cv-builder");
      await createCV(userAuth);
    } catch (error) {
      // Error toast already shown by createCV or plan limit check
      console.error("Failed to create CV:", error);
      // Optional: navigate back if plan limit exceeded
      const errorMessage = (error as Error).message || "";
      if (errorMessage.includes("CV limit exceeded")) {
        router.back();
      }
    }
  };

  const handleImport = async (file: File | null) => {
    try {
      const res = await importCV(userAuth?.id || "", file);
      console.log("Import CV result:", res);

      if (res.data && res.data.success) {
        console.log("Navigating to cv-builder...");
        toast.success("Import CV thành công!");
        router.push("/cv-builder");
        return true;
      }

      console.log("Import failed or no CV data");
      return false;
    } catch (error) {
      console.error("Import error:", error);
      return false;
    }
  };

  const handleEdit = (cv: ICV) => {
    handleSetCurrentCV(cv);
    router.push(`/cv-builder`);
  };

  const handleDeleteClick = (cvId: string) => {
    setCvToDelete(cvId);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (!cvToDelete) return;

    toast.success("Xóa CV thành công!");
    await deleteCV(cvToDelete);
    setDeleteDialogOpen(false);
    setCvToDelete(null);
  };

  const handleDuplicate = (cvId: string) => {
    duplicateCV(cvId, userAuth?.id || "");
    router.push("/cv-builder");
  };

  // Page-level drag and drop handlers
  const handlePageDragEnter = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.dataTransfer.types.includes("Files")) {
      setIsDraggingOnPage(true);
    }
  };

  const handlePageDragLeave = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    // Only hide overlay if leaving the container entirely
    if (e.currentTarget === e.target) {
      setIsDraggingOnPage(false);
    }
  };

  const handlePageDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
  };

  const handlePageDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDraggingOnPage(false);

    const file = e.dataTransfer.files?.[0];
    if (file && file.type === "application/pdf") {
      setDroppedFile(file);
      setImportDialogOpen(true);
    } else if (file) {
      toast.error("Chỉ chấp nhận file PDF!");
    }
  };

  useEffect(() => {
    if (!userAuth) {
      router.push("/auth/login");
    }
  }, [userAuth]);

  // Show loading only when there's no cached data
  if (isLoadingUserCVs) {
    return <UserCVsSkeleton />;
  }

  return (
    <div
      className="min-h-screen flex items-center justify-center py-12 relative"
      onDragEnter={handlePageDragEnter}
      onDragLeave={handlePageDragLeave}
      onDragOver={handlePageDragOver}
      onDrop={handlePageDrop}
    >
      {/* Drag Overlay */}
      {isDraggingOnPage && (
        <DraggingOnPage
          title="Thả file PDF vào đây"
          subtitle="để import CV của bạn"
        />
      )}

      <div className="container max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col gap-8">
          <PageHeader
            onCreateNew={handleCreate}
            onImport={handleImport}
            externalFile={droppedFile}
            isImportDialogOpen={importDialogOpen}
            onImportDialogOpenChange={setImportDialogOpen}
          />

          <UserCVsSection
            userCVs={paginatedUserCVs}
            onCreateNew={handleCreate}
            onUpdate={handleEdit}
            onDuplicate={handleDuplicate}
            onDownload={handleGeneratePDF}
            onDelete={handleDeleteClick}
            isLoading={isLoadingUserCVs}
            showPagination={userCVs.length > 12}
            paginationData={userCVsPagination.paginationData}
            onPageChange={(page) => {
              userCVsPagination.setPage(page);
              window.scrollTo({ top: 0, behavior: "smooth" });
            }}
            onPageSizeChange={userCVsPagination.setPageSize}
          />

          <TemplateCVsSection
            templateCVs={paginatedTemplateCVs}
            handleDuplicate={handleDuplicate}
            onDownload={handleGeneratePDF}
            showPagination={templateCVs.length > 12}
            paginationData={templateCVsPagination.paginationData}
            onPageChange={(page) => {
              templateCVsPagination.setPage(page);
              window.scrollTo({ top: 0, behavior: "smooth" });
            }}
            onPageSizeChange={templateCVsPagination.setPageSize}
          />
        </div>
      </div>

      <ConfirmationDialog
        open={deleteDialogOpen}
        description={
          "Hành động này không thể hoàn tác. Điều này sẽ xóa vĩnh viễn CV của bạn và loại bỏ nó khỏi máy chủ của chúng tôi."
        }
        onOpenChange={setDeleteDialogOpen}
        onConfirm={handleDeleteConfirm}
        isDestructive={true}
      />
    </div>
  );
}
