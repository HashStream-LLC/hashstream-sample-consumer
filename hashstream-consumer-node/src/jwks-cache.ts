export interface Jwk {
  kty: string;
  crv?: string;
  x?: string;
  y?: string;
  kid: string;
  use?: string;
  alg?: string;
  status?: "active" | "retired";
}

export interface Jwks {
  keys: Jwk[];
}

interface CacheEntry {
  jwks: Jwks;
  expiresAtMs: number;
}

export interface JwksFetcher {
  (): Promise<{ jwks: Jwks; cacheControlMaxAgeSeconds: number | null }>;
}

const FALLBACK_TTL_MS = 60 * 60 * 1000;
const MIN_TTL_MS = 60 * 1000;

export class JwksCache {
  private entry: CacheEntry | null = null;
  private inflight: Promise<Jwks> | null = null;

  constructor(
    private readonly fetcher: JwksFetcher,
    private readonly now: () => number = Date.now,
  ) {}

  async getKey(kid: string): Promise<Jwk | null> {
    let jwks = await this.getCachedOrFetch();
    let key = jwks.keys.find((k) => k.kid === kid);
    if (key) return key;

    jwks = await this.refresh();
    key = jwks.keys.find((k) => k.kid === kid);
    return key ?? null;
  }

  private async getCachedOrFetch(): Promise<Jwks> {
    if (this.entry && this.entry.expiresAtMs > this.now()) {
      return this.entry.jwks;
    }
    return this.refresh();
  }

  private async refresh(): Promise<Jwks> {
    if (this.inflight) return this.inflight;

    this.inflight = (async () => {
      try {
        const { jwks, cacheControlMaxAgeSeconds } = await this.fetcher();
        const ttlMs =
          cacheControlMaxAgeSeconds != null
            ? Math.max(cacheControlMaxAgeSeconds * 1000, MIN_TTL_MS)
            : FALLBACK_TTL_MS;
        this.entry = { jwks, expiresAtMs: this.now() + ttlMs };
        return jwks;
      } finally {
        this.inflight = null;
      }
    })();

    return this.inflight;
  }
}

export function parseMaxAgeSeconds(cacheControl: string | null): number | null {
  if (!cacheControl) return null;
  const match = cacheControl.match(/(?:^|[,\s])max-age=(\d+)/i);
  return match && match[1] ? parseInt(match[1], 10) : null;
}
