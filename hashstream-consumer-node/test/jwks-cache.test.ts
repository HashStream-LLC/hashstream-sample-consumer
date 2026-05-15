import { Jwks, JwksCache, parseMaxAgeSeconds } from "../src/jwks-cache";

describe("parseMaxAgeSeconds", () => {
  it("parses single max-age directive", () => {
    expect(parseMaxAgeSeconds("max-age=3600")).toBe(3600);
  });

  it("parses max-age alongside other directives", () => {
    expect(parseMaxAgeSeconds("public, max-age=120, immutable")).toBe(120);
  });

  it("returns null when missing", () => {
    expect(parseMaxAgeSeconds("public")).toBeNull();
    expect(parseMaxAgeSeconds(null)).toBeNull();
  });
});

const KEY_A = { kty: "EC", crv: "P-256", x: "x", y: "y", kid: "A", alg: "ES256" } as const;
const KEY_B = { kty: "EC", crv: "P-256", x: "x", y: "y", kid: "B", alg: "ES256" } as const;

describe("JwksCache", () => {
  let nowMs = 0;
  const now = () => nowMs;

  beforeEach(() => {
    nowMs = 1_000_000;
  });

  it("fetches once and serves from cache within TTL", async () => {
    const fetcher = jest.fn().mockResolvedValue({
      jwks: { keys: [KEY_A] } as Jwks,
      cacheControlMaxAgeSeconds: 3600,
    });
    const cache = new JwksCache(fetcher, now);

    await cache.getKey("A");
    await cache.getKey("A");

    expect(fetcher).toHaveBeenCalledTimes(1);
  });

  it("refetches once when kid is unknown", async () => {
    const fetcher = jest
      .fn()
      .mockResolvedValueOnce({
        jwks: { keys: [KEY_A] } as Jwks,
        cacheControlMaxAgeSeconds: 3600,
      })
      .mockResolvedValueOnce({
        jwks: { keys: [KEY_A, KEY_B] } as Jwks,
        cacheControlMaxAgeSeconds: 3600,
      });
    const cache = new JwksCache(fetcher, now);

    const found = await cache.getKey("B");

    expect(found?.kid).toBe("B");
    expect(fetcher).toHaveBeenCalledTimes(2);
  });

  it("returns null when the kid is unknown after refresh", async () => {
    const fetcher = jest.fn().mockResolvedValue({
      jwks: { keys: [KEY_A] } as Jwks,
      cacheControlMaxAgeSeconds: 3600,
    });
    const cache = new JwksCache(fetcher, now);

    expect(await cache.getKey("missing")).toBeNull();
    expect(fetcher).toHaveBeenCalledTimes(2);
  });

  it("throttles repeated unknown-kid refreshes within the cooldown", async () => {
    const fetcher = jest.fn().mockResolvedValue({
      jwks: { keys: [KEY_A] } as Jwks,
      cacheControlMaxAgeSeconds: 3600,
    });
    const cache = new JwksCache(fetcher, now);

    expect(await cache.getKey("missing")).toBeNull();
    expect(fetcher).toHaveBeenCalledTimes(2);

    nowMs += 30_000;
    expect(await cache.getKey("also-missing")).toBeNull();
    expect(fetcher).toHaveBeenCalledTimes(2);

    nowMs += 31_000;
    expect(await cache.getKey("still-missing")).toBeNull();
    expect(fetcher).toHaveBeenCalledTimes(3);
  });

  it("refetches after cache-control TTL expires", async () => {
    const fetcher = jest.fn().mockResolvedValue({
      jwks: { keys: [KEY_A] } as Jwks,
      cacheControlMaxAgeSeconds: 60,
    });
    const cache = new JwksCache(fetcher, now);

    await cache.getKey("A");
    nowMs += 61_000;
    await cache.getKey("A");

    expect(fetcher).toHaveBeenCalledTimes(2);
  });

  it("falls back to default TTL when cache-control is missing", async () => {
    const fetcher = jest.fn().mockResolvedValue({
      jwks: { keys: [KEY_A] } as Jwks,
      cacheControlMaxAgeSeconds: null,
    });
    const cache = new JwksCache(fetcher, now);

    await cache.getKey("A");
    nowMs += 30 * 60 * 1000;
    await cache.getKey("A");

    expect(fetcher).toHaveBeenCalledTimes(1);
  });
});
