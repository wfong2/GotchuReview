import { Router, Response } from 'express';
import { requireAuth, AuthRequest } from '../middleware/auth';
import { recomputeContractorPricing, recomputeContractorRatings } from '../services/pricing';
import { awardCredits } from '../services/credits';
import prisma from '../config/database';

const router = Router();

// POST /api/v1/reviews — Submit a review with invoice data
router.post('/', requireAuth, async (req: AuthRequest, res: Response) => {
  try {
    const {
      contractorId,
      newContractor, // If no existing contractor matched
      invoiceData,
      documentHash,
      vendorFingerprint,
      vendorTemplate,
      ratings,
      title,
      body,
      workType,
      estimatedBreakdown,
    } = req.body;

    const userId = req.user!.id;

    // Resolve or create contractor
    let resolvedContractorId = contractorId;

    if (!contractorId && newContractor) {
      const contractor = await prisma.contractor.create({
        data: {
          name: newContractor.name,
          businessName: newContractor.businessName || '',
          nameVariants: [newContractor.name, newContractor.businessName].filter(Boolean),
          category: newContractor.category || 'general',
          phone: newContractor.phone || '',
          email: newContractor.email || '',
          city: newContractor.city || '',
          state: newContractor.state || '',
          zipCode: newContractor.zipCode || '',
        },
      });
      resolvedContractorId = contractor.id;
    }

    if (!resolvedContractorId) {
      res.status(400).json({ error: 'contractorId or newContractor is required' });
      return;
    }

    // Determine credit type: first invoice for this contractor from this user = invoice_scan, else additional
    const existingUserInvoice = await prisma.invoice.findFirst({
      where: { userId, contractorId: resolvedContractorId },
    });
    const creditType = existingUserInvoice ? 'additional_invoice' as const : 'invoice_scan' as const;

    // Create invoice record
    const invoice = await prisma.invoice.create({
      data: {
        contractorId: resolvedContractorId,
        userId,
        documentHash,
        totalAmount: invoiceData.totalAmount,
        currency: invoiceData.currency || 'USD',
        laborCost: invoiceData.laborCost,
        materialsCost: invoiceData.materialsCost,
        hourlyRate: invoiceData.hourlyRate,
        projectDuration: invoiceData.projectDuration,
        invoiceDate: invoiceData.invoiceDate ? new Date(invoiceData.invoiceDate) : null,
        description: invoiceData.description || '',
        zipCode: invoiceData.zipCode || '',
        lineItems: invoiceData.lineItems || [],
        source: estimatedBreakdown ? 'estimated_same_contractor' : 'invoice',
        vendorFingerprint: vendorFingerprint || null,
        vendorTemplateData: vendorTemplate || undefined,
      },
    });

    // Append vendor fingerprint to contractor if not already present
    if (vendorFingerprint) {
      const currentContractor = await prisma.contractor.findUnique({
        where: { id: resolvedContractorId },
        select: { vendorFingerprints: true },
      });
      if (currentContractor && !currentContractor.vendorFingerprints.includes(vendorFingerprint)) {
        await prisma.contractor.update({
          where: { id: resolvedContractorId },
          data: {
            vendorFingerprints: { push: vendorFingerprint },
          },
        });
      }
    }

    // Create review
    const overallRating = (ratings.quality + ratings.communication + ratings.timeliness + ratings.value) / 4;

    const review = await prisma.review.create({
      data: {
        contractorId: resolvedContractorId,
        userId,
        ratingQuality: ratings.quality,
        ratingCommunication: ratings.communication,
        ratingTimeliness: ratings.timeliness,
        ratingValue: ratings.value,
        overallRating: Math.round(overallRating * 100) / 100,
        title,
        body,
        workType,
        isVerified: true,
        invoiceId: invoice.id,
      },
    });

    // Recompute contractor aggregates
    await Promise.all([
      recomputeContractorPricing(resolvedContractorId),
      recomputeContractorRatings(resolvedContractorId),
    ]);

    // Award credits
    const contractor = await prisma.contractor.findUnique({ where: { id: resolvedContractorId } });
    const newBalance = await awardCredits(
      userId,
      creditType,
      `Scanned invoice for ${contractor?.name || 'contractor'}`,
      invoice.id
    );

    res.status(201).json({ review, invoice, creditBalance: newBalance });
  } catch (error: any) {
    if (error?.code === 'P2002' && error?.meta?.target?.includes('documentHash')) {
      res.status(409).json({ error: 'This invoice has already been submitted' });
      return;
    }
    console.error('Review submission error:', error);
    res.status(500).json({ error: 'Failed to submit review' });
  }
});

export default router;
