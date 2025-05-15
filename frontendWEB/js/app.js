// frontendWEB/js/app.js
const contentDiv = document.getElementById('content');
const defaultLang = 'en';
let currentLang = localStorage.getItem('lang') || defaultLang;
let translations = {};

async function loadTranslations(lang) {
    try {
        const authModule = await import(`../lang/${lang}/auth.js`);
        translations = { ...translations, ...authModule.default };
        // Load thêm các module ngôn ngữ khác (ví dụ: chat) ở đây nếu cần
    } catch (error) {
        console.error(`Failed to load translations for ${lang}:`, error);
        if (lang !== defaultLang) {
            currentLang = defaultLang;
            localStorage.setItem('lang', defaultLang);
            await loadTranslations(defaultLang);
        }
    }
}

function updateText() {
    const elements = document.querySelectorAll('[data-lang]');
    elements.forEach(element => {
        const key = element.getAttribute('data-lang');
        if (translations[key]) {
            element.textContent = translations[key];
        }
    });
    document.title = translations.APP_TITLE || 'Chat App'; // Cập nhật title trang
}

async function changeLanguage(lang) {
    currentLang = lang;
    localStorage.setItem('lang', lang);
    await loadTranslations(lang);
    updateText();
}

async function loadPage(url) {
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const html = await response.text();
        contentDiv.innerHTML = html;

        // Sau khi tải trang, cần thiết lập event listeners cho form
        if (url === '/login') {
            const loginForm = document.getElementById('login-form');
            if (loginForm) {
                loginForm.addEventListener('submit', handleLogin);
            }
        } else if (url === '/register') {
            const registerForm = document.getElementById('register-form');
            if (registerForm) {
                registerForm.addEventListener('submit', handleRegister);
            }
        }
    } catch (error) {
        console.error('Failed to load page:', error);
        contentDiv.innerHTML = '<p>Failed to load content.</p>';
    }
}

function handleNavigation(event) {
    event.preventDefault();
    const url = event.target.getAttribute('href');
    if (url) {
        history.pushState({ page: url }, '', url);
        loadPage(url);
    }
}

// Gắn event listener cho các liên kết trong navbar
document.addEventListener('DOMContentLoaded', async () => {
    await loadTranslations(currentLang);
    updateText();

    const navLinks = document.querySelectorAll('.nav-links a');
    navLinks.forEach(link => {
        link.addEventListener('click', handleNavigation);
    });

    // Tải trang mặc định hoặc trang từ URL hiện tại
    const initialURL = window.location.pathname === '/' ? '/login' : window.location.pathname;
    loadPage(initialURL);

    // Gắn hàm changeLanguage vào window để có thể gọi từ HTML
    window.changeLanguage = changeLanguage;
});

// Xử lý lịch sử điều hướng (nút back/forward của trình duyệt)
window.addEventListener('popstate', (event) => {
    loadPage(event.state?.page || '/login');
});

// Các hàm xử lý submit form
async function handleLogin(event) {
    event.preventDefault();
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;
    const loginError = document.getElementById('login-error');

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        });
        const data = await response.json();
        if (response.ok) {
            localStorage.setItem('authToken', data.token);
            alert(translations.LOGIN_SUCCESS_PREFIX + ' ' + translations.LOGIN_SUCCESS_SUFFIX);
            window.location.href = '/chat'; // Chuyển hướng đến trang chat sau này
        } else {
            loginError.textContent = translations.INVALID_CREDENTIALS;
        }
    } catch (error) {
        console.error('Login error:', error);
        loginError.textContent = translations.LOGIN_ERROR_GENERIC;
    }
}

async function handleRegister(event) {
    event.preventDefault();
    const username = document.getElementById('register-username').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    const registerError = document.getElementById('register-error');
    const registerSuccess = document.getElementById('register-success');

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password }),
        });
        const data = await response.json();
        if (response.ok) {
            registerSuccess.textContent = translations.REGISTER_SUCCESS;
            registerError.textContent = '';
            document.getElementById('register-form').reset();
            setTimeout(() => {
                window.location.href = '/login';
            }, 1500);
        } else {
            registerError.textContent = translations.USERNAME_OR_EMAIL_EXIST;
        }
    } catch (error) {
        console.error('Registration error:', error);
        registerError.textContent = translations.REGISTER_ERROR_GENERIC;
    }
}