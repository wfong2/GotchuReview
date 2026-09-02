import { Response, NextFunction } from 'express';
import { AuthRequest } from './auth';
import { config } from '../config';

export function requireCredits(amount: number) {
  return (req: AuthRequest, res: Response, next: NextFunction): void => {
    if (!config.creditSystemEnabled) {
      next();
      return;
    }

    if (!req.user) {
      res.status(401).json({ error: 'Authentication required' });
      return;
    }

    if (req.user.creditBalance < amount) {
      res.status(403).json({
        error: 'Insufficient credits',
        required: amount,
        balance: req.user.creditBalance,
      });
      return;
    }

    next();
  };
}
