import { NextRequest, NextResponse } from "next/server";
import puppeteer, { Browser } from "puppeteer-core";
import { getChromePath } from "@/lib/chromeFinder";

// Browser singleton - tái sử dụng instance
let browserInstance: Browser | null = null;
let browserPromise: Promise<Browser> | null = null;

async function getBrowser(): Promise<Browser> {
    // Nếu browser đang được khởi tạo, đợi nó
    if (browserPromise) {
        return browserPromise;
    }

    // Nếu browser đã tồn tại và đang chạy, trả về luôn
    if (browserInstance && browserInstance.isConnected()) {
        return browserInstance;
    }

    // Khởi tạo browser mới
    browserPromise = (async () => {
        const chromePath = getChromePath();
        
        const browser = await puppeteer.launch({
            executablePath: chromePath,
            headless: true,
            args: [
                "--no-sandbox",
                "--disable-setuid-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--force-color-profile=srgb",
                "--disable-background-timer-throttling",
                "--disable-renderer-backgrounding",
                // Thêm các optimization
                "--disable-features=IsolateOrigins,site-per-process",
                "--disable-blink-features=AutomationControlled",
                "--disable-extensions",
            ],
        });

        browserInstance = browser;
        
        // Cleanup khi browser đóng
        browser.on("disconnected", () => {
            browserInstance = null;
            browserPromise = null;
        });

        return browser;
    })();

    try {
        const browser = await browserPromise;
        browserPromise = null; // Reset promise sau khi hoàn thành
        return browser;
    } catch (error) {
        browserPromise = null;
        throw error;
    }
}

export async function POST(request: NextRequest) {
    let page;
    
    try {
        const body = await request.json();
        const { html, filename = "CV.pdf" } = body;

        if (!html || typeof html !== "string") {
            return NextResponse.json(
                { error: "HTML content is required" },
                { status: 400 }
            );
        }

        // Lấy browser đã tồn tại hoặc tạo mới
        const browser = await getBrowser();
        
        // Tạo page mới (nhanh hơn nhiều so với launch browser)
        page = await browser.newPage();

        // Optimization: Disable unnecessary features
        await page.setRequestInterception(true);
        page.on("request", (req) => {
            // Chặn các request không cần thiết
            if (["image", "stylesheet", "font"].includes(req.resourceType())) {
                req.continue();
            } else if (["media", "websocket", "manifest"].includes(req.resourceType())) {
                req.abort();
            } else {
                req.continue();
            }
        });

        await page.emulateMediaType("screen");

        await page.setViewport({
            width: 1200,
            height: 1600,
            deviceScaleFactor: 2,
        });

        // Optimization: Giảm timeout và chỉ đợi domcontentloaded
        await page.setContent(html, {
            waitUntil: "domcontentloaded", // Thay vì networkidle0 (chậm)
            timeout: 10000, // Giảm từ 30s xuống 10s
        });

        // Đợi fonts load (nếu cần)
        await Promise.race([
            page.evaluateHandle("document.fonts.ready"),
            new Promise((resolve) => setTimeout(resolve, 1000))
        ]);

        // Optimization: Giảm delay xuống
        await new Promise((resolve) => setTimeout(resolve, 200)); // Giảm từ 500ms

        const pdfBuffer = await page.pdf({
            format: "A4",
            printBackground: true,
            margin: {
                top: "0mm",
                right: "0mm",
                bottom: "0mm",
                left: "0mm",
            },
            preferCSSPageSize: true,
            displayHeaderFooter: false,
            scale: 1,
            omitBackground: false,
        });

        // Đóng page (KHÔNG đóng browser)
        await page.close();

        return new NextResponse(Buffer.from(pdfBuffer), {
            status: 200,
            headers: {
                "Content-Type": "application/pdf",
                "Content-Disposition": `attachment; filename="${encodeURIComponent(filename)}"`,
                "Content-Length": pdfBuffer.byteLength.toString(),
                "Cache-Control": "no-cache",
            },
        });
    } catch (error) {
        console.error("Puppeteer PDF generation error:", error);
        
        // Cleanup page nếu có lỗi
        if (page) {
            try {
                await page.close();
            } catch (closeError) {
                console.error("Error closing page:", closeError);
            }
        }
        
        return NextResponse.json(
            {
                error: "Failed to generate PDF",
                message: error instanceof Error ? error.message : "Unknown error",
            },
            { status: 500 }
        );
    }
}

// Optional: Cleanup function để gọi khi shutdown server
export async function closeBrowser() {
    if (browserInstance) {
        await browserInstance.close();
        browserInstance = null;
        browserPromise = null;
    }
}