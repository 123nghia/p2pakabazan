const baseUrlInput = document.getElementById('base-url');
const tokenInput = document.getElementById('auth-token');
const defaultEmailInput = document.getElementById('default-email');
const defaultPasswordInput = document.getElementById('default-password');
const logsContainer = document.getElementById('logs');
const clearLogsButton = document.getElementById('clear-logs');

function normaliseBaseUrl() {
    const raw = baseUrlInput.value.trim();
    if (!raw) {
        return '';
    }
    return raw.endsWith('/') ? raw.slice(0, -1) : raw;
}

function buildUrl(path, query) {
    const base = normaliseBaseUrl();
    const finalPath = path.startsWith('/') ? path : `/${path}`;
    const url = new URL(base + finalPath);
    if (query) {
        Object.entries(query).forEach(([key, value]) => {
            if (value === undefined || value === null || value === '') return;
            if (Array.isArray(value)) {
                value.forEach(v => url.searchParams.append(key, v));
            } else {
                url.searchParams.append(key, value);
            }
        });
    }
    return url.toString();
}

async function sendRequest(options) {
    const {
        method = 'GET',
        path,
        body,
        query,
        headers = {},
        skipAuth = false,
    } = options;

    const url = buildUrl(path, query);
    const requestHeaders = {
        Accept: 'application/json',
        ...headers,
    };

    const token = tokenInput.value.trim();
    if (token && !skipAuth) {
        requestHeaders.Authorization = `Bearer ${token}`;
    }

    const fetchOptions = {
        method,
        headers: requestHeaders,
    };

    if (body !== undefined && body !== null && method !== 'GET' && method !== 'HEAD') {
        if (body instanceof FormData) {
            fetchOptions.body = body;
            delete fetchOptions.headers['Content-Type'];
        } else if (typeof body === 'string') {
            fetchOptions.body = body;
            if (!fetchOptions.headers['Content-Type']) {
                fetchOptions.headers['Content-Type'] = 'application/json';
            }
        } else {
            fetchOptions.body = JSON.stringify(body);
            fetchOptions.headers['Content-Type'] = fetchOptions.headers['Content-Type'] || 'application/json';
        }
    }

    const started = performance.now();
    let response;
    let rawText = '';
    let data;
    let error;

    try {
        response = await fetch(url, fetchOptions);
        rawText = await response.text();
        try {
            data = rawText ? JSON.parse(rawText) : null;
        } catch (parseErr) {
            data = rawText;
        }
        if (!response.ok) {
            error = new Error(`HTTP ${response.status}`);
            error.response = response;
            error.data = data;
        }
    } catch (networkError) {
        error = networkError;
    }

    const duration = Math.round(performance.now() - started);

    return {
        request: {
            method,
            url,
            headers: requestHeaders,
            body: fetchOptions.body ?? null,
        },
        response: response
            ? {
                  status: response.status,
                  ok: response.ok,
                  headers: Object.fromEntries(response.headers.entries()),
              }
            : null,
        data,
        rawText,
        error,
        duration,
    };
}

function appendLog(label, result) {
    const entry = document.createElement('article');
    entry.className = 'log-entry';

    const meta = document.createElement('div');
    meta.className = 'meta';
    const time = new Date().toLocaleTimeString();
    const statusText = result.response
        ? `${result.response.status} ${result.response.ok ? 'OK' : 'ERROR'}`
        : 'NO RESPONSE';
    meta.innerHTML = `<span>${time} · ${label}</span><span>${statusText} · ${result.duration} ms</span>`;

    const requestPre = document.createElement('pre');
    requestPre.textContent = JSON.stringify({
        method: result.request.method,
        url: result.request.url,
        headers: result.request.headers,
        body: normaliseBodyForLog(result.request.body),
    }, null, 2);

    const responsePre = document.createElement('pre');
    responsePre.textContent = JSON.stringify(
        {
            status: result.response?.status ?? null,
            ok: result.response?.ok ?? null,
            data: normaliseBodyForLog(result.data),
            error: result.error ? result.error.message : null,
        },
        null,
        2
    );

    entry.append(meta, requestPre, responsePre);
    logsContainer.append(entry);
}

JSON.parseSafe = (value) => {
    try {
        return JSON.parse(value);
    } catch {
        return null;
    }
};

function normaliseBodyForLog(body) {
    if (body === null || body === undefined) {
        return null;
    }
    if (typeof body === 'string') {
        if (!body.length) return null;
        const parsed = JSON.parseSafe(body);
        return parsed ?? body;
    }
    if (body instanceof FormData) {
        const obj = {};
        body.forEach((value, key) => {
            obj[key] = value;
        });
        return obj;
    }
    if (typeof body === 'object') {
        try {
            return JSON.parse(JSON.stringify(body));
        } catch {
            return body;
        }
    }
    return body;
}

function registerForm(formId, builder, afterSubmit) {
    const form = document.getElementById(formId);
    if (!form) return;
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        const submitButton = form.querySelector('button[type="submit"]');
        if (submitButton) submitButton.disabled = true;
        try {
            const formData = new FormData(form);
            const request = builder(formData);
            if (!request) return;
            const label = form.dataset.action || formId;
            const result = await sendRequest(request);
            appendLog(label, result);
            if (afterSubmit) {
                afterSubmit(formData, result);
            }
        } catch (err) {
            appendLog(form.dataset.action || formId, {
                request: { method: 'N/A', url: 'N/A', headers: {}, body: null },
                response: null,
                data: null,
                rawText: '',
                error: err,
                duration: 0,
            });
        } finally {
            if (submitButton) submitButton.disabled = false;
        }
    });
}

function valueOrUndefined(value) {
    const trimmed = typeof value === 'string' ? value.trim() : value;
    return trimmed === '' ? undefined : trimmed;
}

function splitCsv(value) {
    if (!value) return undefined;
    return value
        .split(',')
        .map((item) => item.trim())
        .filter(Boolean);
}

// Register forms
registerForm('login-form', (formData) => {
    const email = formData.get('email') || defaultEmailInput.value;
    const password = formData.get('password') || defaultPasswordInput.value;
    return {
        method: 'POST',
        path: '/auth/login',
        body: { email, password },
        skipAuth: true,
    };
}, (formData, result) => {
    const tokenFromResponse =
        result.data?.data?.token || result.data?.token || null;
    if (tokenFromResponse) {
        tokenInput.value = tokenFromResponse;
    }
    if (!defaultEmailInput.value) defaultEmailInput.value = formData.get('email');
    if (!defaultPasswordInput.value) defaultPasswordInput.value = formData.get('password');
});

registerForm('register-form', (formData) => ({
    method: 'POST',
    path: '/auth/register',
    body: {
        email: formData.get('email'),
        password: formData.get('password'),
    },
    skipAuth: true,
}));

registerForm('create-order-form', (formData) => ({
    method: 'POST',
    path: '/p2p/orders',
    body: {
        type: formData.get('type'),
        token: formData.get('token'),
        fiat: formData.get('fiat'),
        priceMode: formData.get('priceMode'),
        price: Number(formData.get('price')),
        amount: Number(formData.get('amount')),
        minLimit: Number(formData.get('minLimit')),
        maxLimit: Number(formData.get('maxLimit')),
        paymentMethod: formData.get('paymentMethod'),
        fiatAccountId: valueOrUndefined(formData.get('fiatAccountId')),
    },
}));

registerForm('my-orders-form', (formData) => ({
    method: 'GET',
    path: '/p2p/orders/me',
    query: {
        status: valueOrUndefined(formData.get('status')),
        type: valueOrUndefined(formData.get('type')),
        token: valueOrUndefined(formData.get('token')),
    },
}));

registerForm('cancel-order-form', (formData) => {
    const orderId = formData.get('orderId');
    return {
        method: 'POST',
        path: `/p2p/${orderId}/cancel`,
    };
});

registerForm('create-trade-form', (formData) => ({
    method: 'POST',
    path: '/p2p/trades',
    body: {
        orderId: formData.get('orderId'),
        amount: Number(formData.get('amount')),
        fiatAccountId: valueOrUndefined(formData.get('fiatAccountId')),
        chatMessage: valueOrUndefined(formData.get('chatMessage')),
        type: valueOrUndefined(formData.get('type')),
    },
}));

registerForm('confirm-payment-form', (formData) => ({
    method: 'POST',
    path: `/p2p/trades/${formData.get('tradeId')}/confirm-payment`,
}));

registerForm('confirm-received-form', (formData) => ({
    method: 'POST',
    path: `/p2p/trades/${formData.get('tradeId')}/confirm-received`,
}));

registerForm('cancel-trade-form', (formData) => ({
    method: 'POST',
    path: `/p2p/trades/${formData.get('tradeId')}/cancel`,
}));

registerForm('cancel-trade-code-form', (formData) => ({
    method: 'POST',
    path: `/p2p/trades/code/${formData.get('tradeCode')}/cancel`,
}));

registerForm('trade-info-form', (formData) => ({
    method: 'GET',
    path: `/p2p/trades/${formData.get('tradeId')}`,
}));

registerForm('trades-by-order-form', (formData) => ({
    method: 'GET',
    path: `/p2p/orders/${formData.get('orderId')}/trades`,
}));

registerForm('my-trades-form', () => ({
    method: 'GET',
    path: '/p2p/trades/me',
}));

registerForm('send-chat-form', (formData) => ({
    method: 'POST',
    path: `/p2p/trades/${formData.get('tradeId')}/chat`,
    body: { messages: formData.get('messages') },
}));

registerForm('get-chat-form', (formData) => ({
    method: 'GET',
    path: `/p2p/trades/${formData.get('tradeId')}/chat`,
}));

registerForm('chat-threads-form', () => ({
    method: 'GET',
    path: '/p2p/trades/chat/threads',
}));

registerForm('open-dispute-form', (formData) => ({
    method: 'POST',
    path: `/p2p/trades/${formData.get('tradeId')}/dispute`,
    query: {
        reason: formData.get('reason'),
        evidence: valueOrUndefined(formData.get('evidence')),
    },
}));

registerForm('list-disputes-form', (formData) => ({
    method: 'GET',
    path: '/p2p/disputes',
    query: {
        status: valueOrUndefined(formData.get('status')),
        onlyMine: formData.get('onlyMine') ? 'true' : undefined,
    },
}));

registerForm('disputes-by-trade-form', (formData) => ({
    method: 'GET',
    path: `/p2p/trades/${formData.get('tradeId')}/disputes`,
}));

registerForm('assign-dispute-form', (formData) => {
    const adminId = valueOrUndefined(formData.get('adminId'));
    return {
        method: 'POST',
        path: `/p2p/disputes/${formData.get('disputeId')}/assign`,
        body: adminId ? { adminId } : {},
    };
});

registerForm('resolve-dispute-form', (formData) => ({
    method: 'POST',
    path: `/p2p/disputes/${formData.get('disputeId')}/resolve`,
    body: {
        outcome: formData.get('outcome'),
        note: valueOrUndefined(formData.get('note')),
    },
}));

registerForm('reject-dispute-form', (formData) => ({
    method: 'POST',
    path: `/p2p/disputes/${formData.get('disputeId')}/reject`,
    body: {
        note: valueOrUndefined(formData.get('note')),
    },
}));

registerForm('create-fiat-account-form', (formData) => ({
    method: 'POST',
    path: '/p2p/fiat-accounts',
    body: {
        bankName: formData.get('bankName'),
        accountNumber: formData.get('accountNumber'),
        accountHolder: formData.get('accountHolder'),
        branch: valueOrUndefined(formData.get('branch')),
        paymentType: formData.get('paymentType'),
    },
}));

registerForm('list-fiat-accounts-form', () => ({
    method: 'GET',
    path: '/p2p/fiat-accounts',
}));

registerForm('wallet-balances-form', () => ({
    method: 'GET',
    path: '/p2p/wallets',
}));

registerForm('market-price-form', (formData) => ({
    method: 'GET',
    path: '/market/price',
    query: {
        token: valueOrUndefined(formData.get('token')),
        fiat: valueOrUndefined(formData.get('fiat')),
        tradeType: valueOrUndefined(formData.get('tradeType')),
        top: valueOrUndefined(formData.get('top')),
    },
}));

registerForm('market-orders-form', (formData) => ({
    method: 'GET',
    path: '/market/orders',
    query: {
        type: valueOrUndefined(formData.get('type')),
        token: valueOrUndefined(formData.get('token')),
        fiat: valueOrUndefined(formData.get('fiat')),
        paymentMethods: splitCsv(formData.get('paymentMethods')),
        sortByPrice: valueOrUndefined(formData.get('sortByPrice')),
        page: valueOrUndefined(formData.get('page')),
        size: valueOrUndefined(formData.get('size')),
    },
}));

registerForm('currencies-form', (formData) => ({
    method: 'GET',
    path: '/p2p/masterdata/currencies',
    query: { type: valueOrUndefined(formData.get('type')) },
}));

registerForm('currencies-grouped-form', () => ({
    method: 'GET',
    path: '/p2p/masterdata/currencies/grouped',
}));

registerForm('payment-methods-form', (formData) => ({
    method: 'GET',
    path: '/p2p/masterdata/payment-methods',
    query: { type: valueOrUndefined(formData.get('type')) },
}));

registerForm('notifications-form', (formData) => ({
    method: 'GET',
    path: '/p2p/notifications',
    query: formData.get('unreadOnly') ? { unreadOnly: 'true' } : {},
}));

registerForm('notification-read-form', (formData) => ({
    method: 'POST',
    path: `/p2p/notifications/${formData.get('notificationId')}/read`,
}));

registerForm('current-user-form', () => ({
    method: 'GET',
    path: '/me',
}));

registerForm('my-activities-form', () => ({
    method: 'GET',
    path: '/my-activities',
}));

registerForm('integration-sync-form', (formData) => {
    let payload = {};
    const raw = formData.get('payload');
    try {
        payload = JSON.parse(raw);
    } catch (err) {
        throw new Error('Invalid JSON payload');
    }
    return {
        method: 'POST',
        path: '/integration/users/sync',
        body: payload,
    };
});

registerForm('custom-request-form', (formData) => {
    const method = formData.get('method').toUpperCase();
    const path = formData.get('path') || '/';
    const queryRaw = formData.get('query');
    const bodyRaw = formData.get('body');
    const query = queryRaw ? JSON.parseSafe(queryRaw) ?? {} : undefined;
    const body = bodyRaw ? JSON.parseSafe(bodyRaw) ?? bodyRaw : undefined;
    return { method, path, query, body };
});

// Date/time helper to parse JSON safely on request logs
clearLogsButton.addEventListener('click', () => {
    logsContainer.innerHTML = '';
});

// Prefill login form fields from environment defaults
const loginForm = document.getElementById('login-form');
if (loginForm) {
    const emailInput = loginForm.querySelector('input[name="email"]');
    const passwordInput = loginForm.querySelector('input[name="password"]');
    if (emailInput && !emailInput.value && defaultEmailInput.value) {
        emailInput.value = defaultEmailInput.value;
    }
    if (passwordInput && !passwordInput.value && defaultPasswordInput.value) {
        passwordInput.value = defaultPasswordInput.value;
    }
}
