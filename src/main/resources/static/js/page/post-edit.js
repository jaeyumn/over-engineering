document.addEventListener('DOMContentLoaded', function () {
    const editForm = document.getElementById('editForm');
    const postId = editForm.dataset.postId;

    editForm.addEventListener('submit', function (e) {
        e.preventDefault();

        const title = document.getElementById('title').value.trim();
        const content = document.getElementById('content').value.trim();

        if (!title || !content) {
            showSnackbar('제목과 내용을 모두 입력해주세요.', 'warning');
            return;
        }

        const formData = {title, content};
        fetch(`/api/posts/${postId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]')?.value
            },
            credentials: 'include',
            body: JSON.stringify(formData)
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(error => {
                        showSnackbar(error.message || '수정 처리 중 오류가 발생했습니다.', 'error');
                    });

                } else {
                    showSnackbar('게시글이 수정되었습니다.', 'success');
                    setTimeout(() => window.location.href = `/posts/${postId}`, 500);
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