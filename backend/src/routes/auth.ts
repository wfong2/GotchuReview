import { Router, Request, Response } from 'express';
import { getFirebaseAuth } from '../config/firebase';
import { config } from '../config';
import prisma from '../config/database';

const router = Router();

// POST /api/v1/auth/google — Verify Google/Firebase token, create or return user
router.post('/google', async (req: Request, res: Response) => {
  try {
    const { idToken } = req.body;
    if (!idToken) {
      res.status(400).json({ error: 'idToken is required' });
      return;
    }

    const decodedToken = await getFirebaseAuth().verifyIdToken(idToken);
    const { uid, email, name } = decodedToken;

    if (!email) {
      res.status(400).json({ error: 'Google account must have an email' });
      return;
    }

    // Upsert user
    const user = await prisma.user.upsert({
      where: { firebaseUid: uid },
      update: { displayName: name || email },
      create: {
        firebaseUid: uid,
        googleEmail: email,
        displayName: name || email,
      },
    });

    res.json({ user });
  } catch (error) {
    console.error('Auth error:', error);
    res.status(401).json({ error: 'Invalid token' });
  }
});

// POST /api/v1/auth/dev — Dev-only auth bypass (non-production)
if (config.nodeEnv !== 'production') {
  router.post('/dev', async (_req: Request, res: Response) => {
    try {
      const user = await prisma.user.upsert({
        where: { firebaseUid: 'dev-test-uid-001' },
        update: {},
        create: {
          firebaseUid: 'dev-test-uid-001',
          googleEmail: 'dev@gotchureviews.com',
          displayName: 'Dev User',
          creditBalance: 50,
        },
      });

      res.json({ user, token: 'dev-token-gotchu-2024' });
    } catch (error) {
      console.error('Dev auth error:', error);
      res.status(500).json({ error: 'Failed to create dev user' });
    }
  });
}

export default router;
