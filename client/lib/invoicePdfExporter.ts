import { jsPDF } from "jspdf";
import { saveAs } from "file-saver";

const statusLabels: Record<string, string> = {
    paid: "Đã thanh toán",
    pending: "Đang xử lý",
    failed: "Thất bại",
    refunded: "Đã hoàn tiền",
};

export const exportInvoiceToPDF = (invoice: IInvoice) => {
    try {
        // Create new PDF document (A4 size)
        const doc = new jsPDF({
            orientation: "portrait",
            unit: "mm",
            format: "a4",
        });

        const pageWidth = doc.internal.pageSize.getWidth();
        const pageHeight = doc.internal.pageSize.getHeight();
        const margin = 20;
        let yPosition = margin;

        // Helper function to add text
        const addText = (text: string, x: number, y: number, options: any = {}) => {
            doc.text(text, x, y, options);
        };

        // Helper function to draw line
        const drawLine = (y: number) => {
            doc.setDrawColor(200, 200, 200);
            doc.line(margin, y, pageWidth - margin, y);
        };

        // Helper function to format date
        const formatDate = (dateString: string) => {
            return new Date(dateString).toLocaleDateString("vi-VN", {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit",
            });
        };

        // ===== HEADER =====
        doc.setFillColor(79, 70, 229); // Primary color
        doc.rect(0, 0, pageWidth, 40, "F");

        doc.setTextColor(255, 255, 255);
        doc.setFontSize(24);
        doc.setFont("helvetica", "bold");
        addText("JobReady", margin, 20);

        doc.setFontSize(14);
        doc.setFont("helvetica", "normal");
        addText("Hoa don thanh toan", margin, 30);

        yPosition = 50;

        // ===== INVOICE INFO =====
        doc.setTextColor(0, 0, 0);
        doc.setFontSize(10);
        doc.setFont("helvetica", "normal");
        addText(`Ma hoa don: ${invoice.id}`, margin, yPosition);
        addText(
            `Ngay: ${formatDate(invoice.billingDate)}`,
            pageWidth - margin - 50,
            yPosition,
            { align: "right" }
        );

        yPosition += 7;
        addText(`Ma giao dich: ${invoice.transactionId}`, margin, yPosition);

        yPosition += 10;
        drawLine(yPosition);
        yPosition += 10;

        // ===== SERVICE INFO SECTION =====
        doc.setFontSize(12);
        doc.setFont("helvetica", "bold");
        addText("THONG TIN DICH VU", margin, yPosition);
        yPosition += 8;

        doc.setFontSize(10);
        doc.setFont("helvetica", "normal");

        // Service box
        doc.setFillColor(245, 247, 250);
        doc.rect(margin, yPosition, pageWidth - 2 * margin, 25, "F");

        yPosition += 7;
        doc.setFont("helvetica", "bold");
        doc.setFontSize(14);
        addText(invoice.planTitle, margin + 5, yPosition);

        yPosition += 7;
        doc.setFont("helvetica", "normal");
        doc.setFontSize(9);
        doc.setTextColor(100, 100, 100);
        addText(invoice.description, margin + 5, yPosition);

        // Price on the right side
        doc.setFont("helvetica", "bold");
        doc.setFontSize(16);
        doc.setTextColor(79, 70, 229);
        addText(
            `${invoice.currency}${invoice.amount.toLocaleString("vi-VN")}`,
            pageWidth - margin - 5,
            yPosition - 7,
            { align: "right" }
        );

        yPosition += 15;
        doc.setTextColor(0, 0, 0);

        // ===== PAYMENT DETAILS SECTION =====
        yPosition += 5;
        doc.setFontSize(12);
        doc.setFont("helvetica", "bold");
        addText("CHI TIET THANH TOAN", margin, yPosition);
        yPosition += 8;

        doc.setFontSize(10);
        doc.setFont("helvetica", "normal");

        const details = [
            ["Phuong thuc thanh toan:", invoice.paymentMethod.toUpperCase()],
            ["Trang thai:", statusLabels[invoice.status] || invoice.status],
            ["Ma nguoi dung:", invoice.userId],
            ["Ma goi dich vu:", invoice.planId],
        ];

        details.forEach(([label, value]) => {
            doc.setFillColor(250, 250, 250);
            doc.rect(margin, yPosition, pageWidth - 2 * margin, 8, "F");

            doc.setTextColor(100, 100, 100);
            addText(label, margin + 5, yPosition + 5);

            doc.setFont("helvetica", "bold");
            doc.setTextColor(0, 0, 0);
            addText(value, pageWidth - margin - 5, yPosition + 5, { align: "right" });

            doc.setFont("helvetica", "normal");
            yPosition += 9;
        });

        // ===== PERIOD SECTION =====
        yPosition += 5;
        doc.setFontSize(12);
        doc.setFont("helvetica", "bold");
        addText("CHU KY SU DUNG", margin, yPosition);
        yPosition += 8;

        doc.setFontSize(10);
        doc.setFont("helvetica", "normal");

        // Period boxes
        const boxWidth = (pageWidth - 2 * margin - 5) / 2;
        const boxHeight = 18;

        // Start date box
        doc.setFillColor(219, 234, 254); // Blue tint
        doc.rect(margin, yPosition, boxWidth, boxHeight, "F");
        doc.setTextColor(29, 78, 216); // Blue
        doc.setFontSize(8);
        addText("Bat dau", margin + 3, yPosition + 5);
        doc.setFontSize(10);
        doc.setFont("helvetica", "bold");
        doc.setTextColor(0, 0, 0);
        addText(formatDate(invoice.periodStart), margin + 3, yPosition + 12);

        // End date box
        doc.setFillColor(243, 232, 255); // Purple tint
        doc.rect(margin + boxWidth + 5, yPosition, boxWidth, boxHeight, "F");
        doc.setTextColor(126, 34, 206); // Purple
        doc.setFontSize(8);
        doc.setFont("helvetica", "normal");
        addText("Ket thuc", margin + boxWidth + 8, yPosition + 5);
        doc.setFontSize(10);
        doc.setFont("helvetica", "bold");
        doc.setTextColor(0, 0, 0);
        addText(formatDate(invoice.periodEnd), margin + boxWidth + 8, yPosition + 12);

        yPosition += boxHeight + 10;

        // ===== TOTAL SECTION =====
        doc.setFillColor(79, 70, 229);
        doc.rect(margin, yPosition, pageWidth - 2 * margin, 20, "F");

        doc.setTextColor(255, 255, 255);
        doc.setFontSize(12);
        doc.setFont("helvetica", "bold");
        addText("TONG CONG", margin + 5, yPosition + 8);

        doc.setFontSize(18);
        addText(
            `${invoice.currency}${invoice.amount.toLocaleString("vi-VN")}`,
            pageWidth - margin - 5,
            yPosition + 13,
            { align: "right" }
        );

        yPosition += 25;
        doc.setTextColor(0, 0, 0);
        doc.setFontSize(8);
        doc.setFont("helvetica", "normal");
        doc.setTextColor(100, 100, 100);
        addText("Da bao gom thue VAT", pageWidth / 2, yPosition, {
            align: "center",
        });

        // ===== FOOTER =====
        yPosition = pageHeight - 30;
        drawLine(yPosition);
        yPosition += 5;

        doc.setFontSize(8);
        doc.setTextColor(100, 100, 100);
        addText(
            "Hoa don nay duoc tao tu dong boi he thong.",
            pageWidth / 2,
            yPosition,
            { align: "center" }
        );
        yPosition += 4;
        addText(
            "Vui long lien he bo phan ho tro neu co bat ky thac mac nao.",
            pageWidth / 2,
            yPosition,
            { align: "center" }
        );

        yPosition += 8;
        doc.setFontSize(9);
        doc.setTextColor(79, 70, 229);
        addText("JobReady - Trinh tao CV chuyen nghiep", pageWidth / 2, yPosition, {
            align: "center",
        });

        // Save PDF
        const filename = `invoice-${invoice.id}.pdf`;
        doc.save(filename);

        return { success: true, filename };
    } catch (error) {
        console.error("Error exporting invoice to PDF:", error);
        throw error;
    }
};
