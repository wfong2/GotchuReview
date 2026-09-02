import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import morgan from 'morgan';
import { config } from './config';
import { initializeFirebase } from './config/firebase';

// Route imports
import authRoutes from './routes/auth';
import contractorRoutes from './routes/contractors';
import invoiceRoutes from './routes/invoices';
import reviewRoutes from './routes/reviews';
import userRoutes from './routes/users';
import creditRoutes from './routes/credits';

const app = express();

// Initialize Firebase
initializeFirebase();

// Middleware
app.use(helmet());
app.use(cors());
app.use(morgan('dev'));
app.use(express.json({ limit: '15mb' }));

// Health check
app.get('/health', (_req, res) => {
  res.json({ status: 'ok', version: '1.0.0' });
});

// API v1 routes
app.use('/api/v1/auth', authRoutes);
app.use('/api/v1/contractors', contractorRoutes);
app.use('/api/v1/invoices', invoiceRoutes);
app.use('/api/v1/reviews', reviewRoutes);
app.use('/api/v1/users', userRoutes);
app.use('/api/v1/credits', creditRoutes);

// 404 handler
app.use((_req, res) => {
  res.status(404).json({ error: 'Not found' });
});

// Error handler
app.use((err: Error, _req: express.Request, res: express.Response, _next: express.NextFunction) => {
  console.error('Unhandled error:', err);
  res.status(500).json({ error: 'Internal server error' });
});

app.listen(config.port, () => {
  console.log(`Gotchu Reviews API running on port ${config.port}`);
  console.log(`Environment: ${config.nodeEnv}`);
  console.log(`Credit system: ${config.creditSystemEnabled ? 'enabled' : 'disabled'}`);
});

export default app;
