import { Router, Request, Response } from 'express';
import { getFirebaseAuth } from '../config/firebase';
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

export default router;
