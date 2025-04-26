document.addEventListener('DOMContentLoaded', () => {
    /* 스낵바 */
    const snackbar = document.querySelector('#snackbar');
    if (snackbar) {
        const message = snackbar.getAttribute('data-message');
        const type = snackbar.getAttribute('data-type') || 'error';
        if (message) {
            showSnackbar(message, type)
        }
    }

    function showSnackbar(message, type = 'error') {
        // 기존 스낵바 제거
        const existingSnackbar = document.querySelector('#dynamic-snackbar')
        if (existingSnackbar) {
            existingSnackbar.remove();
        }

        // 스낵바 요소 생성
        const snackbar = document.createElement('div');
        snackbar.id = 'dynamic-snackbar';
        snackbar.className = `snackbar ${type}`;
        snackbar.textContent = message;

        document.body.appendChild(snackbar);

        requestAnimationFrame(() => {
            snackbar.classList.add('show');
        })

        // 애니메이션 적용
        setTimeout(() => {
            snackbar.classList.remove('show');
            setTimeout(() => snackbar.remove(), 300); // transition 시간 이후 제거
        }, 3000);
    }

    window.showSnackbar = showSnackbar;
})