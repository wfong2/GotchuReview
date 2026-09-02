import admin from 'firebase-admin';
import { config } from './index';

let firebaseApp: admin.app.App;

export function initializeFirebase(): admin.app.App {
  if (!firebaseApp) {
    firebaseApp = admin.initializeApp({
      credential: admin.credential.cert({
        projectId: config.firebase.projectId,
        clientEmail: config.firebase.clientEmail,
        privateKey: config.firebase.privateKey,
      }),
    });
  }
  return firebaseApp;
}

export function getFirebaseAuth(): admin.auth.Auth {
  return admin.auth(firebaseApp);
}

export function getFirebaseMessaging(): admin.messaging.Messaging {
  return admin.messaging(firebaseApp);
}
