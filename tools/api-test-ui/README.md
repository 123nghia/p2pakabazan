# P2P Quick Test UI

Lightweight static page for manually exercising the P2P backend endpoints. No build step is required: open `index.html`
in a browser and point it at a running API instance (defaults to `http://localhost:8080`).

## Features

- Environment panel for configuring base URL, bearer token, and default credentials.
- One-click helpers for common actions:
  - Authentication, order management, trades, trade chat, disputes.
  - Wallet balances, fiat account management, market queries, master data.
  - Notifications, integration sync, user summary.
- Custom request panel to hit any path with arbitrary method/query/body.
- Logs panel with request/response payloads and timings.

## Usage

1. Start the backend (`mvn spring-boot:run` on the `p2p_p2p` module).
2. Open `tools/api-test-ui/index.html` in your browser.
3. Login with a test account; the token will populate automatically for subsequent calls.
4. Explore other sections or craft ad-hoc requests from the “Custom Request” panel.

> Tip: because the interface is static, CORS must be permitted by the backend when hitting remote environments. For local
> testing the default Spring Boot dev configuration is sufficient.
