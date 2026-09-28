/**
 * Decrypts JWE-encrypted FI data from the Account Aggregator ecosystem.
 * Uses the jose library (RFC 7516 compliant).
 *
 * Setup:
 *  1. Generate an RSA key-pair for your FIU.
 *  2. Register the public key with Setu / Sahamati Central Registry.
 *  3. Store the private key PEM in env var: AA_FIU_PRIVATE_KEY
 */
import { compactDecrypt, importPKCS8 } from 'jose';

const PRIVATE_KEY_PEM = process.env.AA_FIU_PRIVATE_KEY ?? '';

let _privateKey: Awaited<ReturnType<typeof importPKCS8>> | null = null;

async function getPrivateKey() {
  if (_privateKey) return _privateKey;
  if (!PRIVATE_KEY_PEM) {
    throw new Error('AA_FIU_PRIVATE_KEY env var is not set. Generate an RSA key pair and register the public key with Setu.');
  }
  _privateKey = await importPKCS8(PRIVATE_KEY_PEM, 'RSA-OAEP-256');
  return _privateKey;
}

/**
 * Decrypt a JWE compact-serialised string returned by the AA data session.
 * Returns the plaintext payload as a parsed object.
 */
export async function decryptFIData(jweToken: string): Promise<unknown> {
  const privateKey = await getPrivateKey();
  const { plaintext } = await compactDecrypt(jweToken, privateKey);
  const text = new TextDecoder().decode(plaintext);
  return JSON.parse(text);
}

/** If the data is not JWE (sandbox may return plain JSON), handle both */
export async function decryptOrParsePayload(rawPayload: string): Promise<unknown> {
  // JWE compact has 5 base64url parts separated by dots
  const parts = rawPayload.split('.');
  if (parts.length === 5) {
    return decryptFIData(rawPayload);
  }
  // Plain JSON (sandbox mode)
  return JSON.parse(rawPayload);
}
