import admin from 'firebase-admin';
import { config } from './index';

let firebaseApp: admin.app.App | null = null;

export function initializeFirebase(): admin.app.App | null {
  if (firebaseApp) return firebaseApp;

  const { projectId, clientEmail, privateKey } = config.firebase;
  if (!projectId || !clientEmail || !privateKey) {
    console.warn('[Firebase] Missing credentials — Firebase auth disabled. Use the dev auth endpoint instead.');
    return null;
  }

  firebaseApp = admin.initializeApp({
    credential: admin.credential.cert({ projectId, clientEmail, privateKey }),
  });
  return firebaseApp;
}

export function getFirebaseAuth(): admin.auth.Auth {
  if (!firebaseApp) {
    throw new Error('Firebase is not initialized. Set FIREBASE_PROJECT_ID, FIREBASE_CLIENT_EMAIL, and FIREBASE_PRIVATE_KEY, or use the dev auth endpoint.');
  }
  return admin.auth(firebaseApp);
}

export function getFirebaseMessaging(): admin.messaging.Messaging {
  if (!firebaseApp) {
    throw new Error('Firebase is not initialized. Cannot send push notifications without Firebase credentials.');
  }
  return admin.messaging(firebaseApp);
}
