document.addEventListener('DOMContentLoaded', function () {
    // 게시글 옵션 메뉴 토글
    const postOptions = document.querySelector('.post-options');
    if (postOptions) {
        postOptions.addEventListener('click', function (e) {
            e.stopPropagation();
            const menu = this.querySelector('.post-options-menu');
            if (menu) {
                menu.classList.toggle('active');
            }
        });
    }

    // 좋아요 토글 기능
    const likeButton = document.querySelector('.post-likes');
    if (likeButton) {
        likeButton.addEventListener('click', function () {
            const postId = this.getAttribute('data-post-id');
            toggleLike(postId);
        });
    }

    function toggleLike(postId) {
        const likeButton = document.querySelector('.post-likes');
        const likeCountSpan = likeButton.querySelector('span');
        let likeCount = parseInt(likeCountSpan.textContent);

        fetch(`/api/posts/${postId}/like`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.getAttribute('content')
            }
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(error => {
                        showSnackbar(error.message || '좋아요 처리 중 오류가 발생했습니다.', 'error');
                    });

                } else {
                    likeButton.classList.toggle('active');
                    if (likeButton.classList.contains('active')) {
                        likeButton.style.color = '#ff3b30';
                        likeCountSpan.textContent = String(likeCount + 1);
                    } else {
                        likeButton.style.color = '#666';
                        likeCountSpan.textContent = String(likeCount - 1);
                    }
                }
            })
            .catch(error => console.error('Error:', error));
    }

    document.addEventListener('click', function () {
        const activeMenu = document.querySelector('.post-options-menu.active');
        if (activeMenu) {
            activeMenu.classList.remove('active');
        }
    });

    // 게시글 삭제
    const deletePostBtn = document.querySelector('.delete-post');
    if (deletePostBtn) {
        deletePostBtn.addEventListener('click', function () {
            if (confirm('게시글을 삭제하시겠습니까?')) {
                const postId = document.querySelector('.post-likes').getAttribute('data-post-id');
                fetch(`/api/posts/${postId}`, {
                    method: 'DELETE',
                    headers: {
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.getAttribute('content')
                    }
                })
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(error => {
                                showSnackbar(error.message || '게시글 삭제 처리 중 오류가 발생했습니다.', 'error');
                            });

                        } else {
                            window.location.href = '/posts';
                        }
                    })
                    .catch(error => console.error('Error:', error));
            }
        });
    }

    // 게시글 수정
    const editPostBtn = document.querySelector('.edit-post');
    if (editPostBtn) {
        editPostBtn.addEventListener('click', function () {
            const postId = document.querySelector('.post-likes').getAttribute('data-post-id');
            window.location.href = `/posts/${postId}/edit`;
        });
    }

    // 답글 달기 버튼 클릭 시 답글 입력 영역 표시
    const replyButtons = document.querySelectorAll('.comment-reply');
    replyButtons.forEach(button => {
        button.addEventListener('click', function () {
            document.querySelectorAll('.reply-input-area').forEach(area => {
                area.classList.remove('active');
                area.style.display = 'none';
            });
            const replyArea = this.closest('.comment').querySelector('.reply-input-area');
            replyArea.classList.add('active');
            replyArea.style.display = 'flex';
        });
    });

    // 댓글 제출 이벤트
    const commentSubmitBtn = document.querySelector('.comment-submit');
    if (commentSubmitBtn) {
        commentSubmitBtn.addEventListener('click', function () {
            const commentInput = document.querySelector('.comment-input');
            const postId = commentInput.getAttribute('data-post-id');
            const content = commentInput.value.trim();

            if (content) {
                submitComment(postId, content);
                commentInput.value = '';
            }
        });
    }

    // 답글 제출 이벤트
    const replySubmitBtns = document.querySelectorAll('.reply-submit');
    replySubmitBtns.forEach(button => {
        button.addEventListener('click', function () {
            const replyInput = this.closest('.reply-input-area')?.querySelector('.reply-input');
            const content = replyInput?.value.trim();
            const commentId = replyInput.getAttribute('data-comment-id');

            if (replyInput && content) {
                submitReply(commentId, content);
                replyInput.value = '';
                this.closest('.reply-input-area').style.display = 'none';
            }
        });
    });

    // 댓글/답글 수정 처리
    document.querySelectorAll('.comment-modify, .reply-modify').forEach(btn => {
        btn.addEventListener('click', function () {
            const commentId = this.getAttribute('data-comment-id');
            const commentContent = document.querySelector(`.comment-content[data-comment-id="${commentId}"]`);
            const editContainer = document.querySelector(`.comment-edit-container[data-comment-id="${commentId}"]`);
            const editInput = editContainer.querySelector('.comment-edit-input');

            commentContent.style.display = 'none';
            editContainer.classList.add('active');
            editInput.value = commentContent.textContent;
            editInput.focus();
        });
    });

    // 댓글/답글 수정 취소 처리
    document.querySelectorAll('.comment-edit-cancel').forEach(btn => {
        btn.addEventListener('click', function () {
            const commentId = this.closest('.comment-edit-container').getAttribute('data-comment-id');
            const commentContent = document.querySelector(`.comment-content[data-comment-id="${commentId}"]`);
            const editContainer = document.querySelector(`.comment-edit-container[data-comment-id="${commentId}"]`);

            commentContent.style.display = 'inline';
            editContainer.classList.remove('active');
        });
    });

    // 댓글/답글 수정 제출 처리
    document.querySelectorAll('.comment-edit-submit').forEach(btn => {
        btn.addEventListener('click', function () {
            const editContainer = this.closest('.comment-edit-container');
            const commentId = editContainer.getAttribute('data-comment-id');
            const editInput = editContainer.querySelector('.comment-edit-input');
            const newContent = editInput.value.trim();

            if (newContent) {
                fetch(`/api/comments/${commentId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.getAttribute('content')
                    },
                    body: JSON.stringify({content: newContent})
                })
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(error => {
                                showSnackbar(error.message || '제출 처리 중 오류가 발생했습니다.', 'error');
                            });

                        } else {
                            window.location.reload();
                        }
                    })
                    .catch(error => console.error('Error:', error));
            }
        });
    });

    // 댓글 삭제 처리 (기존 댓글과 답글 모두 포함)
    document.querySelectorAll('.comment-delete, .reply-delete').forEach(btn => {
        btn.addEventListener('click', function () {
            const commentId = this.getAttribute('data-comment-id');
            if (confirm('댓글을 삭제하시겠습니까?')) {
                fetch(`/api/comments/${commentId}`, {
                    method: 'DELETE',
                    headers: {
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.getAttribute('content')
                    }
                })
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(error => {
                                showSnackbar(error.message || '삭제 처리 중 오류가 발생했습니다.', 'error');
                            });

                        } else {
                            window.location.reload();
                        }
                    })
                    .catch(error => console.error('Error:', error));
            }
        });
    });
});

// 댓글 작성
function submitComment(postId, content) {
    fetch(`/api/posts/${postId}/comments`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.getAttribute('content')
        },
        body: JSON.stringify({content: content})
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(error => {
                    showSnackbar(error.message || '작성 처리 중 오류가 발생했습니다.', 'error');
                });

            } else {
                window.location.reload();
            }
        })
        .catch(error => console.error('Error:', error));
}

// 답글 작성
function submitReply(commentId, content) {
    fetch(`/api/comments/${commentId}/replies`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.getAttribute('content')
        },
        body: JSON.stringify({content: content})
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(error => {
                    showSnackbar(error.message || '작성 처리 중 오류가 발생했습니다.', 'error');
                });

            } else {
                window.location.reload();
            }
        })
        .catch(error => console.error('Error:', error));
}