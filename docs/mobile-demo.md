# Mobile QR Demo

This guide explains how to make the UniScheduler demo accessible on mobile phones through a QR code.

The public page is:

```text
/mobile-demo
```

It displays:

- Demo URL
- Generated QR code
- Demo credentials
- Short instructions for phone users

The QR code points to `PUBLIC_DEMO_URL` when it is configured. If `PUBLIC_DEMO_URL` is empty, the page uses the current browser origin as a local/tunnel fallback.

## Demo Credentials

Use these only for local or university demonstrations:

```text
Read-only demo user
username: user
password: user

Admin demo account
username: admin
password: admin
```

Do not commit real secrets, tunnel tokens, Cloudflare tokens, private keys, or production passwords.

## Option A: Local Tunnel Demo

Use this option when presenting from a laptop.

### 1. Prepare Local Environment

Copy the example environment file:

```bash
cp .env.example .env
```

Start the full app:

```bash
docker compose up --build
```

Open the app locally:

```text
http://localhost:4200
```

### 2. Start A Public HTTPS Tunnel

Expose the frontend/nginx container, not the backend directly:

```bash
ngrok http 4200
```

Cloudflare Tunnel alternative:

```bash
cloudflared tunnel --url http://localhost:4200
```

Copy the public HTTPS URL:

```text
https://<your-tunnel-domain>
```

### 3. Configure `PUBLIC_DEMO_URL`

Edit `.env`:

```bash
PUBLIC_DEMO_URL=https://<your-tunnel-domain>
```

Rebuild the frontend container so the Angular bundle receives the configured URL:

```bash
docker compose up --build frontend
```

If you run the frontend locally without Docker, build it with:

```bash
PUBLIC_DEMO_URL=https://<your-tunnel-domain> npm run webapp:build:prod
```

For development server demos, open the app through the tunnel and the QR page will fall back to the current browser origin if `PUBLIC_DEMO_URL` is empty.

### 4. Show The QR Code

Open:

```text
https://<your-tunnel-domain>/mobile-demo
```

Tell users:

```text
Scan the QR code to open UniScheduler on your phone.
```

Users can log in with:

```text
user / user
```

Use:

```text
admin / admin
```

only when loading demo data or generating schedules during the presentation.

## Option B: Production Deployment

Use this option for a stable public demo.

### 1. Deploy Services

Deploy:

- Angular frontend
- Spring Boot backend
- PostgreSQL database

Recommended deployment shape:

```text
https://your-domain.example
  /        -> Angular frontend
  /api     -> Spring Boot backend
  /management -> Spring Boot backend
```

Serving frontend and backend from the same origin avoids mobile browser CORS issues.

### 2. Configure The Public URL

Before building the frontend:

```bash
PUBLIC_DEMO_URL=https://your-domain.example npm run webapp:build:prod
```

With Docker Compose:

```bash
PUBLIC_DEMO_URL=https://your-domain.example docker compose up --build
```

### 3. Verify

Open:

```text
https://your-domain.example/mobile-demo
```

Check that:

- The displayed Demo URL is the public production URL.
- The QR code opens the same public URL on a phone.
- Login works from the phone.
- Authenticated pages load backend data.

## CORS Notes

The default Docker setup serves the Angular frontend through nginx and proxies `/api` to the backend from the same origin. This is the preferred demo setup.

If frontend and backend are deployed on different domains, configure Spring Boot/JHipster CORS for the frontend origin. Example environment variable:

```bash
JHIPSTER_CORS_ALLOWED_ORIGINS=https://your-frontend-domain.example
```

Use HTTPS for both frontend and backend in a public demo.

## Troubleshooting

### Phone Cannot Open `localhost`

Phones cannot access your laptop's `localhost`. `localhost` on the phone means the phone itself, not your laptop.

Use:

```bash
ngrok http 4200
```

or:

```bash
cloudflared tunnel --url http://localhost:4200
```

Then scan the public HTTPS tunnel URL.

### App Works On Laptop But Not Phone

Common causes:

- The QR code points to `localhost`.
- The tunnel is not running.
- The phone is blocked by the network.
- The backend is exposed separately and is not reachable from the phone.

Recommended fix: expose the nginx frontend URL through the tunnel and let nginx proxy `/api` to the backend.

### Backend API Blocked By CORS

This usually happens when the frontend and backend use different domains.

Preferred fix: use one public origin and reverse-proxy `/api` to the backend.

Alternative fix: configure Spring Boot/JHipster CORS:

```bash
JHIPSTER_CORS_ALLOWED_ORIGINS=https://your-frontend-domain.example
```

Restart the backend after changing CORS configuration.

### Tunnel URL Changed

Free tunnel URLs often change after restart.

Update `.env`:

```bash
PUBLIC_DEMO_URL=https://new-tunnel-url.example
```

Then rebuild:

```bash
docker compose up --build frontend
```

Open `/mobile-demo` again and verify the new QR code.

### Mixed HTTP/HTTPS Issue

Mobile browsers may block HTTPS frontend pages that call HTTP backend APIs.

Use HTTPS for the public URL and backend access. The recommended nginx proxy setup avoids this because the browser calls only the public HTTPS frontend origin.

### Login Works Only With Correct Backend URL

Login calls `/api/authenticate`. If the frontend is public but `/api` does not reach the backend, login fails.

Check:

- The public URL serves the frontend.
- `/api/authenticate` is reverse-proxied to the backend.
- The backend container is healthy.
- The database container is healthy.
- CORS is configured if frontend/backend are split across domains.
