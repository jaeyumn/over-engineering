document.addEventListener('DOMContentLoaded', function () {
    const signupForm = document.getElementById('signupForm');

    if (signupForm) {
        signupForm.addEventListener('submit', async function (e) {
            e.preventDefault();

            const username = document.getElementById('username').value.trim();
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;
            const passwordConfirm = document.getElementById('passwordConfirm').value;

            // 에러 메시지 초기화
            clearErrors();

            // 유효성 검사
            let isValid = true;

            if (username.length < 3 || username.length > 50) {
                showError('username-error', '사용자 이름은 3~50자 사이여야 합니다.');
                isValid = false;
            }

            if (!isValidEmail(email)) {
                showError('email-error', '유효한 이메일 주소를 입력해주세요.');
                isValid = false;
            }

            if (password.length < 3) {
                showError('password-error', '비밀번호는 3자 이상이어야 합니다.');
                isValid = false;
            }

            if (password !== passwordConfirm) {
                showError('passwordConfirm-error', '비밀번호가 일치하지 않습니다.');
                isValid = false;
            }

            if (!isValid) return;

            try {
                const response = await fetch('/api/users/signup', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.getAttribute('content'),
                    },
                    body: JSON.stringify({
                        username,
                        email,
                        password
                    })
                });

                if (response.ok) {
                    showSnackbar('회원가입 성공!', 'success');
                    setTimeout(() => {
                        window.location.href = '/login';
                    }, 500);
                } else {
                    return response.json().then(data => {
                        showSnackbar(data.error?.message || '회원가입 처리 중 오류가 발생했습니다.', 'error');
                    })
                }
            } catch (error) {
                console.error('회원가입 에러:', error);
                showError('passwordConfirm-error', '회원가입 처리 중 오류가 발생했습니다.');
            }
        });
    }

    function clearErrors() {
        document.querySelectorAll('.error-message').forEach(el => {
            el.textContent = '';
        });
    }

    function showError(id, message) {
        const errorElement = document.getElementById(id);
        if (errorElement) {
            errorElement.textContent = message;
        }
    }

    function isValidEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }
});