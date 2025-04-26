document.addEventListener('DOMContentLoaded', function () {
    // 로그인 폼 제출
    const loginForm = document.querySelector('#loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', (e) => {
            e.preventDefault();

            const username = document.querySelector('#username').value.trim();
            const password = document.querySelector('#password').value.trim();

            if (!username || !password) {
                showSnackbar('사용자 이름과 비밀번호를 모두 입력해주세요.', 'warning');
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
                console.log(response)
                if (!response.ok) {
                    return response.json().then(data => {
                        showSnackbar(data.error?.message || '로그인 처리 중 오류가 발생했습니다.', 'error');
                    })

                } else {
                    showSnackbar('로그인 성공!', 'success');
                    setTimeout(() => {
                        window.location.href = '/posts';
                    }, 500);
                }

            }).catch(error => {
                console.error('Login Error: ', error);
                showSnackbar('로그인 처리 중 오류가 발생했습니다.', 'error');
            });
        });
    }

    // 소셜 로그인 버튼 클릭 이벤트
    const socialIcons = document.querySelectorAll('.social-icon');
    socialIcons.forEach(icon => {
        icon.addEventListener('click', function () {
            // TODO 소셜 로그인 처리
            showSnackbar('미구현 기능입니다.', 'info')
            // const platform = this.querySelector('img').alt.toLowerCase();
            // console.log(`${platform} 로그인 시도`);
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
});