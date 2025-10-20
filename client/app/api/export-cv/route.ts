import { NextRequest, NextResponse } from "next/server";
import puppeteer from "puppeteer-core";
import { getChromePath } from "@/lib/chromeFinder";

export async function POST(request: NextRequest) {
    try {
        const body = await request.json();
        const { html, filename = "CV.pdf" } = body;

        if (!html || typeof html !== "string") {
            return NextResponse.json(
                { error: "HTML content is required" },
                { status: 400 }
            );
        }

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
            ],
        });

        const page = await browser.newPage();

        await page.emulateMediaType("screen");

        await page.setViewport({
            width: 1200,
            height: 1600,
            deviceScaleFactor: 2,
        });

        await page.setContent(html, {
            waitUntil: ["networkidle0", "load"],
            timeout: 30000,
        });

        await page.evaluateHandle("document.fonts.ready");
        await new Promise((resolve) => setTimeout(resolve, 500));

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

        await browser.close();

        return new NextResponse(Buffer.from(pdfBuffer), {
            status: 200,
            headers: {
                "Content-Type": "application/pdf",
                "Content-Disposition": `attachment; filename="${encodeURIComponent(filename)}"`,
                "Content-Length": pdfBuffer.byteLength.toString(),
            },
        });
    } catch (error) {
        console.error("Puppeteer PDF generation error:", error);
        return NextResponse.json(
            {
                error: "Failed to generate PDF",
                message: error instanceof Error ? error.message : "Unknown error",
            },
            { status: 500 }
        );
    }
}
