// import { NextRequest, NextResponse } from "next/server";
// import puppeteer from "puppeteer-core";
// import { getChromePath } from "@/lib/chromeFinder";

// /**
//  * API Route để convert HTML sang PDF qua Puppeteer với Chrome có sẵn
//  *
//  * @route POST /api/export-cv-puppeteer
//  * @body { html: string, filename?: string }
//  * @returns PDF file blob
//  *
//  * ✅ Advantages:
//  * - FREE, no watermark
//  * - Full control over rendering
//  * - High quality output
//  * - Sử dụng Chrome đã cài đặt (không cần tải Chromium)
//  *
//  * ⚠️ Requirements:
//  * - npm install puppeteer-core
//  * - Cần có Google Chrome đã cài đặt trên hệ thống
//  */
// export async function POST(request: NextRequest) {
//     try {
//         // 1. Parse request body
//         const body = await request.json();
//         const { html, filename = "CV.pdf" } = body;

//         if (!html || typeof html !== "string") {
//             return NextResponse.json(
//                 { error: "HTML content is required" },
//                 { status: 400 }
//             );
//         }

//         // 2. Tìm Chrome path
//         const chromePath = getChromePath();
//         console.log("Using Chrome at:", chromePath);

//         // 3. Launch Puppeteer với Chrome có sẵn
//         const browser = await puppeteer.launch({
//             executablePath: chromePath,
//             headless: true,
//             args: [
//                 "--no-sandbox",
//                 "--disable-setuid-sandbox",
//                 "--disable-dev-shm-usage",
//                 "--disable-gpu",
//             ],
//         });

//         const page = await browser.newPage();

//         // 4. Set viewport
//         await page.setViewport({
//             width: 1200,
//             height: 1600,
//             deviceScaleFactor: 2, // High DPI for better quality
//         });

//         // 5. Set content
//         await page.setContent(html, {
//             waitUntil: ["networkidle0", "load"],
//             timeout: 30000,
//         });

//         // 6. Generate PDF
//         const pdfBuffer = await page.pdf({
//             format: "A4",
//             printBackground: true,
//             margin: {
//                 top: "0mm",
//                 right: "0mm",
//                 bottom: "0mm",
//                 left: "0mm",
//             },
//             preferCSSPageSize: true,
//         });

//         // 7. Close browser
//         await browser.close();

//         // 8. Return PDF
//         return new NextResponse(Buffer.from(pdfBuffer), {
//             status: 200,
//             headers: {
//                 "Content-Type": "application/pdf",
//                 "Content-Disposition": `attachment; filename="${encodeURIComponent(filename)}"`,
//                 "Content-Length": pdfBuffer.byteLength.toString(),
//             },
//         });
//     } catch (error) {
//         console.error("Puppeteer PDF generation error:", error);
//         return NextResponse.json(
//             {
//                 error: "Failed to generate PDF",
//                 message: error instanceof Error ? error.message : "Unknown error",
//             },
//             { status: 500 }
//         );
//     }
// }
