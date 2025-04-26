document.addEventListener('DOMContentLoaded', function () {
    const writeForm = document.querySelector('#writeForm');

    writeForm.addEventListener('submit', function (e) {
        e.preventDefault();

        const title = document.querySelector('#title').value.trim();
        const content = document.querySelector('#content').value.trim();

        if (!title || !content) {
            showSnackbar('제목과 내용을 모두 입력해주세요.', 'warning');
            return;
        }

        const formData = {title, content}

        fetch('/api/posts', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]')?.value
            },
            credentials: 'include',
            body: JSON.stringify(formData)
        })
            .then(response => {
                if (response.ok) {
                    showSnackbar('게시글이 등록되었습니다.', 'success');
                    setTimeout(() => window.location.href = '/posts', 500);
                } else {
                    return response.json().then(res => {
                        showSnackbar(res.error?.message || '등록 실패', 'error');
                    });
                }
            })
            .catch(() => {
                showSnackbar('오류가 발생했습니다.', 'error');
            });
    });

    // 사용자 메뉴 드롭다운 토글
    const userMenu = document.querySelector('.user-menu');
    const dropdown = document.querySelector('.dropdown-menu');
    if (userMenu && dropdown) {
        userMenu.addEventListener('click', () => {
            dropdown.classList.toggle('show');
        });
    }
});