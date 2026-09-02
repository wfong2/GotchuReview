import { Router, Response } from 'express';
import { requireAuth, AuthRequest } from '../middleware/auth';
import prisma from '../config/database';

const router = Router();

// GET /api/v1/users/me — Get current user profile
router.get('/me', requireAuth, async (req: AuthRequest, res: Response) => {
  res.json({ user: req.user });
});

// PATCH /api/v1/users/me — Update user preferences
router.patch('/me', requireAuth, async (req: AuthRequest, res: Response) => {
  try {
    const {
      displayName,
      fcmToken,
      notificationPreferences,
      preferredLocation,
    } = req.body;

    const data: Record<string, unknown> = {};
    if (displayName) data.displayName = displayName;
    if (fcmToken !== undefined) data.fcmToken = fcmToken;
    if (notificationPreferences) {
      if (notificationPreferences.newReviewOnMyContractor !== undefined)
        data.notifyNewReview = notificationPreferences.newReviewOnMyContractor;
      if (notificationPreferences.priceUpdateInArea !== undefined)
        data.notifyPriceUpdate = notificationPreferences.priceUpdateInArea;
      if (notificationPreferences.invoiceProcessed !== undefined)
        data.notifyInvoiceProcessed = notificationPreferences.invoiceProcessed;
      if (notificationPreferences.draftReminder !== undefined)
        data.notifyDraftReminder = notificationPreferences.draftReminder;
    }
    if (preferredLocation) {
      if (preferredLocation.zipCode !== undefined) data.prefZipCode = preferredLocation.zipCode;
      if (preferredLocation.city !== undefined) data.prefCity = preferredLocation.city;
      if (preferredLocation.state !== undefined) data.prefState = preferredLocation.state;
      if (preferredLocation.country !== undefined) data.prefCountry = preferredLocation.country;
    }

    const user = await prisma.user.update({
      where: { id: req.user!.id },
      data,
    });

    res.json({ user });
  } catch (error) {
    console.error('User update error:', error);
    res.status(500).json({ error: 'Failed to update user' });
  }
});

// GET /api/v1/users/me/history — Get user's service history
router.get('/me/history', requireAuth, async (req: AuthRequest, res: Response) => {
  try {
    const invoices = await prisma.invoice.findMany({
      where: { userId: req.user!.id },
      orderBy: { createdAt: 'desc' },
      include: {
        contractor: {
          select: { id: true, name: true, businessName: true, category: true },
        },
        review: {
          select: { id: true, overallRating: true, title: true },
        },
      },
    });

    // Group by year
    const history: Record<string, typeof invoices> = {};
    for (const invoice of invoices) {
      const year = invoice.createdAt.getFullYear().toString();
      if (!history[year]) history[year] = [];
      history[year].push(invoice);
    }

    const totalSpent = invoices.reduce((sum, i) => sum + i.totalAmount, 0);

    res.json({
      history,
      summary: {
        totalInvoices: invoices.length,
        totalSpent,
        contractorCount: new Set(invoices.map((i) => i.contractorId)).size,
      },
    });
  } catch (error) {
    console.error('History error:', error);
    res.status(500).json({ error: 'Failed to get history' });
  }
});

// POST /api/v1/users/me/history/report — Generate service history report
router.post('/me/history/report', requireAuth, async (req: AuthRequest, res: Response) => {
  try {
    const invoices = await prisma.invoice.findMany({
      where: { userId: req.user!.id },
      orderBy: { invoiceDate: 'desc' },
      include: {
        contractor: true,
        review: {
          select: { overallRating: true, title: true, workType: true },
        },
      },
    });

    const totalSpent = invoices.reduce((sum, i) => sum + i.totalAmount, 0);
    const byCategory: Record<string, { count: number; total: number }> = {};

    for (const invoice of invoices) {
      const cat = invoice.contractor.category;
      if (!byCategory[cat]) byCategory[cat] = { count: 0, total: 0 };
      byCategory[cat].count++;
      byCategory[cat].total += invoice.totalAmount;
    }

    res.json({
      report: {
        generatedAt: new Date().toISOString(),
        user: req.user!.displayName,
        totalSpent,
        totalJobs: invoices.length,
        contractorCount: new Set(invoices.map((i) => i.contractorId)).size,
        byCategory,
        jobs: invoices.map((i) => ({
          contractor: i.contractor.name,
          category: i.contractor.category,
          amount: i.totalAmount,
          date: i.invoiceDate,
          description: i.description,
          rating: i.review?.overallRating,
        })),
      },
    });
  } catch (error) {
    console.error('Report generation error:', error);
    res.status(500).json({ error: 'Failed to generate report' });
  }
});

export default router;
