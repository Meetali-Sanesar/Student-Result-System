/**
 * SRMS - Core Application Module
 * Handles routing, API calls, authentication state, and shared utilities.
 */
const App = (() => {
    // Base API URL (relative to context path)
    const API_BASE = getContextPath() + '/api';
    let currentUser = null;

    function getContextPath() {
        const path = window.location.pathname;
        const idx = path.indexOf('/', 1);
        if (idx > 0) {
            const ctx = path.substring(0, idx);
            // Check if it looks like a context path
            if (ctx !== '/pages' && ctx !== '/css' && ctx !== '/js') {
                return ctx;
            }
        }
        return '';
    }

    /**
     * Central API call function with error handling.
     */
    async function apiCall(method, endpoint, body = null) {
        const url = API_BASE + endpoint;
        const options = {
            method,
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin'
        };
        if (body && method !== 'GET') {
            options.body = JSON.stringify(body);
        }
        try {
            const resp = await fetch(url, options);
            // Handle file downloads
            const contentType = resp.headers.get('content-type');
            if (contentType && (contentType.includes('pdf') || contentType.includes('spreadsheet'))) {
                if (!resp.ok) throw new Error('File download failed');
                return { success: true, blob: await resp.blob() };
            }
            const data = await resp.json();
            if (resp.status === 401) {
                if (endpoint !== '/auth/login') {
                    showToast('Session expired. Redirecting to login...', 'error');
                    setTimeout(() => navigateTo('login'), 1500);
                }
                return data;
            }
            return data;
        } catch (error) {
            console.error('API Error:', error);
            return { success: false, message: error.message || 'Network error' };
        }
    }

    /**
     * Upload a file via multipart form data.
     */
    async function apiUpload(endpoint, formData) {
        const url = API_BASE + endpoint;
        try {
            const resp = await fetch(url, {
                method: 'POST',
                credentials: 'same-origin',
                body: formData // No Content-Type header; browser sets multipart boundary
            });
            return await resp.json();
        } catch (error) {
            console.error('Upload Error:', error);
            return { success: false, message: error.message };
        }
    }

    /**
     * Download a file from API.
     */
    async function downloadFile(endpoint, filename) {
        const url = API_BASE + endpoint;
        try {
            const resp = await fetch(url, { credentials: 'same-origin' });
            if (!resp.ok) throw new Error('Download failed');
            const blob = await resp.blob();
            const a = document.createElement('a');
            a.href = URL.createObjectURL(blob);
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(a.href);
        } catch (error) {
            showToast('Download failed: ' + error.message, 'error');
        }
    }

    /**
     * Navigate to a page.
     */
    function navigateTo(page) {
        const base = getContextPath();
        if (page === 'login') {
            window.location.href = base + '/pages/login.html';
        } else {
            window.location.href = base + '/pages/' + page + '.html';
        }
    }

    /**
     * Get current user from session.
     */
    async function getCurrentUser() {
        if (currentUser) return currentUser;
        const result = await apiCall('GET', '/auth/me');
        if (result.success) {
            currentUser = result.data;
            return currentUser;
        }
        return null;
    }

    /**
     * Check authentication and redirect if needed.
     */
    async function requireAuth(requiredRole = null) {
        const user = await getCurrentUser();
        if (!user) {
            navigateTo('login');
            return null;
        }
        if (requiredRole && user.role !== requiredRole) {
            showToast('Access denied', 'error');
            if (user.role === 'ADMIN') navigateTo('admin-dashboard');
            else navigateTo('student-dashboard');
            return null;
        }
        return user;
    }

    /**
     * Show a toast notification.
     */
    function showToast(message, type = 'info', duration = 4000) {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            container.className = 'toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `
            <span class="toast-message">${message}</span>
            <button class="toast-close" onclick="this.parentElement.remove()">✕</button>
        `;
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(30px)';
            setTimeout(() => toast.remove(), 300);
        }, duration);
    }

    /**
     * Show a confirmation dialog.
     */
    function confirm(title, message) {
        return new Promise((resolve) => {
            const overlay = document.createElement('div');
            overlay.className = 'confirm-overlay';
            overlay.innerHTML = `
                <div class="confirm-dialog">
                    <h3>${title}</h3>
                    <p>${message}</p>
                    <div class="confirm-actions">
                        <button class="btn btn-secondary" id="confirm-cancel">Cancel</button>
                        <button class="btn btn-danger" id="confirm-ok">Confirm</button>
                    </div>
                </div>
            `;
            document.body.appendChild(overlay);

            overlay.querySelector('#confirm-ok').onclick = () => { overlay.remove(); resolve(true); };
            overlay.querySelector('#confirm-cancel').onclick = () => { overlay.remove(); resolve(false); };
        });
    }

    /**
     * Open a modal.
     */
    function openModal(id) {
        const modal = document.getElementById(id);
        if (modal) modal.classList.add('active');
    }

    /**
     * Close a modal.
     */
    function closeModal(id) {
        const modal = document.getElementById(id);
        if (modal) modal.classList.remove('active');
    }

    /**
     * Format a date string.
     */
    function formatDate(dateStr) {
        if (!dateStr) return 'N/A';
        const date = new Date(dateStr);
        return date.toLocaleDateString('en-IN', {
            day: '2-digit', month: 'short', year: 'numeric',
            hour: '2-digit', minute: '2-digit'
        });
    }

    /**
     * Get CSS class for grade badge.
     */
    function getGradeClass(grade) {
        switch(grade) {
            case 'A+': return 'badge-emerald grade-aplus';
            case 'A': return 'badge-blue grade-a';
            case 'B': return 'badge-amber grade-b';
            case 'C': return 'badge-purple grade-c';
            case 'F': return 'badge-rose grade-f';
            default: return 'badge-blue';
        }
    }

    /**
     * Initialize sidebar navigation active state.
     */
    function initSidebar() {
        const currentPage = window.location.pathname.split('/').pop().replace('.html', '');
        document.querySelectorAll('.nav-item').forEach(item => {
            const href = item.getAttribute('href') || '';
            if (href.includes(currentPage)) {
                item.classList.add('active');
            }
        });

        // Hamburger toggle for mobile
        const hamburger = document.getElementById('hamburger');
        const sidebar = document.querySelector('.sidebar');
        if (hamburger && sidebar) {
            hamburger.addEventListener('click', () => sidebar.classList.toggle('open'));
            // Close sidebar on nav item click (mobile)
            sidebar.querySelectorAll('.nav-item').forEach(item => {
                item.addEventListener('click', () => sidebar.classList.remove('open'));
            });
        }
    }

    /**
     * Set up the sidebar user info.
     */
    function setupUserInfo(user) {
        const nameEl = document.getElementById('user-name');
        const roleEl = document.getElementById('user-role');
        const avatarEl = document.getElementById('user-avatar');

        if (nameEl) nameEl.textContent = user.username || 'User';
        if (roleEl) roleEl.textContent = user.role || 'Unknown';
        if (avatarEl) avatarEl.textContent = (user.username || 'U')[0].toUpperCase();
    }

    /**
     * Logout handler.
     */
    async function logout() {
        await apiCall('POST', '/auth/logout');
        currentUser = null;
        navigateTo('login');
    }

    /**
     * Escape HTML to prevent XSS in dynamic content.
     */
    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    /**
     * Show loading spinner.
     */
    function showLoading() {
        let overlay = document.getElementById('loading-overlay');
        if (!overlay) {
            overlay = document.createElement('div');
            overlay.id = 'loading-overlay';
            overlay.className = 'loading-overlay';
            overlay.innerHTML = '<div class="spinner"></div>';
            document.body.appendChild(overlay);
        }
        overlay.style.display = 'flex';
    }

    function hideLoading() {
        const overlay = document.getElementById('loading-overlay');
        if (overlay) overlay.style.display = 'none';
    }

    // Public API
    return {
        apiCall, apiUpload, downloadFile, navigateTo, getCurrentUser, requireAuth,
        showToast, confirm, openModal, closeModal, formatDate, getGradeClass,
        initSidebar, setupUserInfo, logout, escapeHtml, showLoading, hideLoading,
        getContextPath, API_BASE
    };
})();
