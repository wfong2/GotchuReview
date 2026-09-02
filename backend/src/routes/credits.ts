import { Router, Response } from 'express';
import { requireAuth, AuthRequest } from '../middleware/auth';
import { addPurchasedCredits } from '../services/credits';
import prisma from '../config/database';

const router = Router();

// GET /api/v1/credits/balance — Get credit balance + recent transactions
router.get('/balance', requireAuth, async (req: AuthRequest, res: Response) => {
  try {
    const transactions = await prisma.creditTransaction.findMany({
      where: { userId: req.user!.id },
      orderBy: { createdAt: 'desc' },
      take: 20,
    });

    res.json({
      balance: req.user!.creditBalance,
      recentTransactions: transactions,
    });
  } catch (error) {
    console.error('Credit balance error:', error);
    res.status(500).json({ error: 'Failed to get credit balance' });
  }
});

// POST /api/v1/credits/purchase — Record a StoreKit purchase
router.post('/purchase', requireAuth, async (req: AuthRequest, res: Response) => {
  try {
    const { receiptData, credits } = req.body;

    if (!receiptData || !credits || credits <= 0) {
      res.status(400).json({ error: 'receiptData and credits (positive) are required' });
      return;
    }

    // TODO: Verify Apple StoreKit receipt with Apple's servers
    // For MVP, trust the client-reported purchase

    const newBalance = await addPurchasedCredits(
      req.user!.id,
      credits,
      `Purchased ${credits} credits`
    );

    res.json({ balance: newBalance });
  } catch (error) {
    console.error('Credit purchase error:', error);
    res.status(500).json({ error: 'Failed to record purchase' });
  }
});

export default router;
