document.addEventListener('DOMContentLoaded', function () {
    // 글쓰기 버튼 클릭
    const writeBtn = document.querySelector('.write-btn');
    if (writeBtn) {
        writeBtn.addEventListener('click', () => {
            window.location.href = '/posts/write';
        });
    }

    // 검색 폼 제출
    const searchForm = document.querySelector('.search-form');
    if (searchForm) {
        searchForm.addEventListener('submit', function (e) {
            e.preventDefault();
            const searchTerm = document.querySelector('.search-input').value.trim();
            if (searchTerm) {
                window.location.href = `/search?q=${encodeURIComponent(searchTerm)}`;
            }
        });
    }

    // 게시글 클릭
    document.querySelectorAll('.post-card').forEach(card => {
        card.addEventListener('click', function (e) {
            if (!e.target.closest('.post-comments, .post-views')) {
                const postId = this.dataset.postId;
                window.location.href = `/posts/${postId}`;
            }
        });
    });

    // 사용자 메뉴 드롭다운 토글
    const userMenu = document.querySelector('.user-menu');
    const dropdown = document.querySelector('.dropdown-menu');
    if (userMenu && dropdown) {
        userMenu.addEventListener('click', (e) => {
            e.stopPropagation();
            dropdown.classList.toggle('show');
        });

        document.addEventListener('click', () => {
            dropdown.classList.remove('show');
        });
    }

    // 로그아웃
    const logoutBtn = document.querySelector('.logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async function (e) {
            e.preventDefault();
            e.stopPropagation();

            try {
                const response = await fetch('/api/auth/logout', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.getAttribute('content'),
                    },
                    credentials: 'include'
                })

                if (!response.ok) {
                    return response.json().then(error => {
                        showSnackbar(error.message || '로그아웃 처리 중 오류가 발생했습니다.', 'error');
                    });

                } else {
                    window.location.href = '/login';
                }
            } catch (error) {
                console.error('로그아웃 에러: ', error);
                alert('로그아웃 처리 중 오류가 발생했습니다.');
            }
        });
    }
});