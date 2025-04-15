document.addEventListener('DOMContentLoaded', function () {
    // 유효성 검증
    const snackbar = document.querySelector("#snackbar");
    if (snackbar && snackbar.textContent.trim() !== '') {
        showSnackbar(snackbar.textContent);
    }

    // 로그인 폼 제출
    const loginForm = document.querySelector('#loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', (e) => {
            e.preventDefault();

            const username = document.querySelector('#username').value.trim();
            const password = document.querySelector('#password').value.trim();

            if (!username || !password) {
                showSnackbar('사용자 이름과 비밀번호를 모두 입력해주세요.');
                return;
            }

            const formData = {
                username: username,
                password: password
            };
            fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify(formData),

            }).then(response => {
                if (!response.ok) {
                    return response.json().then(data => {
                        showSnackbar(data.error?.message || '로그인 처리 중 오류가 발생했습니다.');
                    })

                } else {
                    window.location.href = '/posts';
                }

            }).catch(error => {
                console.error('Login Error: ', error);
                showSnackbar('로그인 처리 중 오류가 발생했습니다.');
            });
        });
    }

    // 소셜 로그인 버튼 클릭 이벤트
    const socialIcons = document.querySelectorAll('.social-icon');
    socialIcons.forEach(icon => {
        icon.addEventListener('click', function () {
            const platform = this.querySelector('img').alt.toLowerCase();
            // TODO 소셜 로그인 처리
            console.log(`${platform} 로그인 시도`);
            // window.location.href = `/oauth2/authorization/${platform}`;
        });
    });

    // 기타 UI 효과
    const formControls = document.querySelectorAll('.form-control');
    formControls.forEach(control => {
        control.addEventListener('focus', function () {
            this.parentElement.querySelector('label').style.color = '#4285f4';
        });

        control.addEventListener('blur', function () {
            this.parentElement.querySelector('label').style.color = '#555';
        });
    });

    function showSnackbar(message) {
        const snackbar = document.createElement('div');
        snackbar.id = 'snackbar';
        snackbar.textContent = message;
        document.body.appendChild(snackbar);

        snackbar.classList.add('show');
        setTimeout(() => {
            snackbar.classList.remove('show');
            setTimeout(() => snackbar.remove(), 500);
        }, 3000);
    }
});