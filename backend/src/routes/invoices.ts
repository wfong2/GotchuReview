import { Router, Response } from 'express';
import crypto from 'crypto';
import multer from 'multer';
import { requireAuth, AuthRequest } from '../middleware/auth';
import { extractInvoiceData } from '../services/invoiceExtraction';
import { findMatchingContractors } from '../services/contractorMatching';
import prisma from '../config/database';

const router = Router();
const upload = multer({ storage: multer.memoryStorage(), limits: { fileSize: 10 * 1024 * 1024 } });

// POST /api/v1/invoices/extract — Send invoice image or PDF, return extracted data + contractor matches
router.post('/extract', requireAuth, upload.single('image'), async (req: AuthRequest, res: Response) => {
  try {
    if (!req.file) {
      res.status(400).json({ error: 'Invoice file is required' });
      return;
    }

    const fileBase64 = req.file.buffer.toString('base64');
    const mimeType = req.file.mimetype || 'image/jpeg';

    // Generate document hash for duplicate detection
    const documentHash = 'sha256:' + crypto.createHash('sha256').update(req.file.buffer).digest('hex');

    // Check for duplicate
    const existingInvoice = await prisma.invoice.findUnique({
      where: { documentHash },
    });
    if (existingInvoice) {
      res.status(409).json({ error: 'This invoice has already been scanned', invoiceId: existingInvoice.id });
      return;
    }

    // Extract data via OpenAI Vision
    const extracted = await extractInvoiceData(fileBase64, mimeType);

    // Find matching contractors
    const matches = await findMatchingContractors(
      extracted.contractorName || extracted.businessName,
      undefined,
      extracted.zipCode || undefined,
      extracted.contractorPhone || undefined,
      extracted.vendorFingerprint || undefined
    );

    // Check if same-contractor estimation is possible
    let estimatedBreakdown = false;
    if (extracted.laborCost === null && extracted.materialsCost === null && matches.length > 0) {
      const topMatch = matches[0];
      const existingInvoices = await prisma.invoice.findMany({
        where: {
          contractorId: topMatch.contractor.id,
          laborCost: { not: null },
        },
        take: 10,
      });

      if (existingInvoices.length > 0) {
        // Estimate breakdown using the contractor's typical labor ratio
        const avgLaborRatio = topMatch.contractor.pricingLaborRatio;
        if (avgLaborRatio > 0) {
          extracted.laborCost = Math.round(extracted.totalAmount * avgLaborRatio * 100) / 100;
          extracted.materialsCost = Math.round(extracted.totalAmount * (1 - avgLaborRatio) * 100) / 100;
          estimatedBreakdown = true;
        }
      }
    }

    // Image is NOT stored — only extracted data returned
    res.json({
      extracted,
      documentHash,
      vendorFingerprint: extracted.vendorFingerprint || null,
      vendorTemplate: extracted.vendorTemplate || null,
      contractorMatches: matches.map((m) => ({
        contractor: m.contractor,
        confidence: Math.round(m.score * 100),
      })),
      estimatedBreakdown,
    });
  } catch (error) {
    console.error('Invoice extraction error:', error);
    res.status(500).json({ error: 'Failed to extract invoice data' });
  }
});

export default router;
