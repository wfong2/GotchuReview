import { Request, Response, NextFunction } from 'express';
import { User } from '@prisma/client';
import { getFirebaseAuth } from '../config/firebase';
import prisma from '../config/database';

export interface AuthRequest extends Request {
  user?: User;
}

export async function requireAuth(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  const authHeader = req.headers.authorization;
  if (!authHeader?.startsWith('Bearer ')) {
    res.status(401).json({ error: 'Missing or invalid authorization header' });
    return;
  }

  const idToken = authHeader.split('Bearer ')[1];

  try {
    const decodedToken = await getFirebaseAuth().verifyIdToken(idToken);
    const user = await prisma.user.findUnique({
      where: { firebaseUid: decodedToken.uid },
    });

    if (!user) {
      res.status(401).json({ error: 'User not found. Please sign up first.' });
      return;
    }

    req.user = user;
    next();
  } catch {
    res.status(401).json({ error: 'Invalid or expired token' });
  }
}

export async function optionalAuth(req: AuthRequest, _res: Response, next: NextFunction): Promise<void> {
  const authHeader = req.headers.authorization;
  if (!authHeader?.startsWith('Bearer ')) {
    next();
    return;
  }

  const idToken = authHeader.split('Bearer ')[1];

  try {
    const decodedToken = await getFirebaseAuth().verifyIdToken(idToken);
    const user = await prisma.user.findUnique({
      where: { firebaseUid: decodedToken.uid },
    });
    if (user) {
      req.user = user;
    }
  } catch {
    // Token invalid — continue without auth
  }

  next();
}
