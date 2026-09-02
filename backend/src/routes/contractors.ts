import { Router, Request, Response } from 'express';
import prisma from '../config/database';
import { optionalAuth, AuthRequest } from '../middleware/auth';
import { requireCredits } from '../middleware/creditCheck';

const router = Router();

// GET /api/v1/contractors — List/search contractors
router.get('/', optionalAuth, async (req: Request, res: Response) => {
  try {
    const {
      q,
      category,
      zipCode,
      city,
      state,
      minRating,
      minReviews,
      sortBy = 'ratingOverall',
      limit = '20',
      offset = '0',
    } = req.query;

    const where: Record<string, unknown> = {};

    if (category) where.category = category;
    if (zipCode) where.zipCode = zipCode as string;
    if (city) where.city = { contains: city as string, mode: 'insensitive' };
    if (state) where.state = (state as string).toUpperCase();
    if (minRating) where.ratingOverall = { gte: parseFloat(minRating as string) };
    if (minReviews) where.reviewCount = { gte: parseInt(minReviews as string, 10) };

    // Text search on name/businessName
    if (q) {
      where.OR = [
        { name: { contains: q as string, mode: 'insensitive' } },
        { businessName: { contains: q as string, mode: 'insensitive' } },
      ];
    }

    const orderBy: Record<string, string> = {};
    if (sortBy === 'reviewCount') {
      orderBy.reviewCount = 'desc';
    } else if (sortBy === 'pricingMedian') {
      orderBy.pricingMedian = 'asc';
    } else {
      orderBy.ratingOverall = 'desc';
    }

    const [contractors, total] = await Promise.all([
      prisma.contractor.findMany({
        where: where as any,
        orderBy,
        take: Math.min(parseInt(limit as string, 10), 50),
        skip: parseInt(offset as string, 10),
      }),
      prisma.contractor.count({ where: where as any }),
    ]);

    res.json({ contractors, total });
  } catch (error) {
    console.error('Contractor search error:', error);
    res.status(500).json({ error: 'Failed to search contractors' });
  }
});

// GET /api/v1/contractors/:id — Get contractor profile with reviews
router.get('/:id', optionalAuth, async (req: Request, res: Response) => {
  try {
    const contractor = await prisma.contractor.findUnique({
      where: { id: req.params.id },
    });

    if (!contractor) {
      res.status(404).json({ error: 'Contractor not found' });
      return;
    }

    const reviews = await prisma.review.findMany({
      where: { contractorId: contractor.id },
      orderBy: { createdAt: 'desc' },
      include: {
        invoice: {
          select: {
            totalAmount: true,
            laborCost: true,
            materialsCost: true,
            description: true,
            invoiceDate: true,
            source: true,
          },
        },
      },
    });

    res.json({ contractor, reviews });
  } catch (error) {
    console.error('Contractor detail error:', error);
    res.status(500).json({ error: 'Failed to get contractor' });
  }
});

export default router;
