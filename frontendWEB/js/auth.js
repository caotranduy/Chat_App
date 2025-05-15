// frontend/js/auth.js
export async function registerUser() {
    const username = document.getElementById('register-username').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    const registerError = document.getElementById('register-error');
    const registerSuccess = document.getElementById('register-success');

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, email, password }),
        });

        const data = await response.json();

        if (response.ok) {
            registerSuccess.textContent = window.lang.vi.REGISTER_SUCCESS;
            registerError.textContent = '';
            document.getElementById('register-form').reset();
            setTimeout(() => {
                window.location.href = '/login.html'; // Chuyển đến trang đăng nhập sau khi đăng ký thành công
            }, 1500);
        } else {
            registerError.textContent = window.lang.vi.USERNAME_OR_EMAIL_EXIST; // Cần xử lý các lỗi khác từ server nếu có
            registerSuccess.textContent = '';
        }
    } catch (error) {
        registerError.textContent = window.lang.vi.REGISTER_ERROR_GENERIC;
        registerSuccess.textContent = '';
        console.error('Lỗi đăng ký:', error);
    }
}

export async function loginUser() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;
    const loginError = document.getElementById('login-error');
    const loginSuccess = document.getElementById('login-success');

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password }),
        });

        const data = await response.json();

        if (response.ok) {
            loginSuccess.textContent = `${window.lang.vi.LOGIN_SUCCESS_PREFIX} ${window.lang.vi.LOGIN_SUCCESS_SUFFIX}`;
            loginError.textContent = '';
            document.getElementById('login-form').reset();
            // Lưu token vào localStorage hoặc cookie để sử dụng sau này
            if (data.token) {
                localStorage.setItem('authToken', data.token);
                alert(`${window.lang.vi.LOGIN_SUCCESS_PREFIX} ${window.lang.vi.LOGIN_SUCCESS_SUFFIX}`);
                // Chuyển hướng đến trang chat sau này (chúng ta sẽ làm sau)
            }
        } else {
            loginError.textContent = window.lang.vi.INVALID_CREDENTIALS; // Cần xử lý các lỗi khác từ server nếu có
            loginSuccess.textContent = '';
        }
    } catch (error) {
        loginError.textContent = window.lang.vi.LOGIN_ERROR_GENERIC;
        loginSuccess.textContent = '';
        console.error('Lỗi đăng nhập:', error);
    }
    //window.alert('Chức năng đăng nhập chưa được triển khai.');
}
window.addEventListener('DOMContentLoaded', () => {
  document.getElementById('login-button').addEventListener('click', loginUser);
});

function updateText() {
    const elements = document.querySelectorAll('[data-lang]');
    elements.forEach(element => {
        const key = element.getAttribute('data-lang');
        if (window.lang && window.lang.vi && window.lang.vi[key]) {
            element.textContent = window.lang.vi[key];
        }
    });
}

// Load ngôn ngữ mặc định và cập nhật text
import('/lang/vi.js').then(() => {
    updateText();
});