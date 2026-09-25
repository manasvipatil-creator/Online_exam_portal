/**
 * ONLINE EXAM PORTAL - APPLICATION JAVASCRIPT
 * Dynamic UI interactions, client-side validation, quiz timer, and dashboard utilities
 */

document.addEventListener('DOMContentLoaded', function () {
    
    // 1. SIDEBAR TOGGLE FOR MOBILE RESPONSIVENESS
    const toggleBtn = document.querySelector('.sidebar-toggle');
    const sidebar = document.querySelector('.sidebar');
    
    if (toggleBtn && sidebar) {
        // Create overlay if it doesn't exist
        let overlay = document.querySelector('.sidebar-overlay');
        if (!overlay) {
            overlay = document.createElement('div');
            overlay.className = 'sidebar-overlay';
            document.body.appendChild(overlay);
        }
        
        toggleBtn.addEventListener('click', function () {
            sidebar.classList.add('show');
            overlay.classList.add('show');
        });
        
        overlay.addEventListener('click', function () {
            sidebar.classList.remove('show');
            overlay.classList.remove('show');
        });
    }

    // 2. PASSWORD MATCH VALIDATION FOR REGISTRATION & PROFILE UPDATE
    const registerForm = document.querySelector('.needs-validation');
    if (registerForm) {
        registerForm.addEventListener('submit', function (event) {
            const password = document.getElementById('password');
            const confirmPassword = document.getElementById('confirmPassword');
            
            let passwordsMatch = true;
            if (password && confirmPassword) {
                if (password.value !== confirmPassword.value) {
                    confirmPassword.setCustomValidity('Passwords do not match');
                    passwordsMatch = false;
                } else {
                    confirmPassword.setCustomValidity('');
                }
            }

            if (!registerForm.checkValidity() || !passwordsMatch) {
                event.preventDefault();
                event.stopPropagation();
            }
            
            registerForm.classList.add('was-validated');
        }, false);
    }

   

    // 4. ADMIN DASHBOARD CHARTS MOCK (Using beautiful custom SVG drawing)
    const adminChartCanvas = document.getElementById('adminStatsChart');
    if (adminChartCanvas) {
        drawStatsChart(adminChartCanvas);
    }
});

/**
 * Draw a beautiful clean dashboard SVG chart dynamically
 */
function drawStatsChart(canvasContainer) {
    const data = [45, 80, 52, 95, 120, 150, 185]; // Dummy exam attempt stats
    const labels = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    
    let svgContent = `
        <svg viewBox="0 0 500 220" width="100%" height="100%" xmlns="http://www.w3.org/2000/svg">
            <defs>
                <linearGradient id="chartGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stop-color="#4f46e5" stop-opacity="0.3"></stop>
                    <stop offset="100%" stop-color="#4f46e5" stop-opacity="0.0"></stop>
                </linearGradient>
            </defs>
            
            <!-- Grid Lines -->
            <line x1="30" y1="20" x2="480" y2="20" stroke="#f1f5f9" stroke-width="1" />
            <line x1="30" y1="70" x2="480" y2="70" stroke="#f1f5f9" stroke-width="1" />
            <line x1="30" y1="120" x2="480" y2="120" stroke="#f1f5f9" stroke-width="1" />
            <line x1="30" y1="170" x2="480" y2="170" stroke="#cbd5e1" stroke-width="1.5" />
            
            <!-- Graph Area Fill -->
            <path d="M 30 170 
                     L 30 ${170 - (data[0]/200)*150} 
                     C 105 ${170 - (data[0]/200)*150}, 105 ${170 - (data[1]/200)*150}, 105 ${170 - (data[1]/200)*150}
                     C 180 ${170 - (data[1]/200)*150}, 180 ${170 - (data[2]/200)*150}, 180 ${170 - (data[2]/200)*150}
                     C 255 ${170 - (data[2]/200)*150}, 255 ${170 - (data[3]/200)*150}, 255 ${170 - (data[3]/200)*150}
                     C 330 ${170 - (data[3]/200)*150}, 330 ${170 - (data[4]/200)*150}, 330 ${170 - (data[4]/200)*150}
                     C 405 ${170 - (data[4]/200)*150}, 405 ${170 - (data[5]/200)*150}, 405 ${170 - (data[5]/200)*150}
                     C 480 ${170 - (data[5]/200)*150}, 480 ${170 - (data[6]/200)*150}, 480 ${170 - (data[6]/200)*150}
                     L 480 170 Z" 
                  fill="url(#chartGradient)" />
            
            <!-- Graph Line -->
            <path d="M 30 ${170 - (data[0]/200)*150} 
                     Q 105 ${170 - (data[1]/200)*150} 180 ${170 - (data[2]/200)*150} 
                     T 330 ${170 - (data[4]/200)*150} 
                     T 480 ${170 - (data[6]/200)*150}" 
                  fill="none" stroke="#4f46e5" stroke-width="3" stroke-linecap="round" />
            
            <!-- Data Dots -->
            <circle cx="30" cy="${170 - (data[0]/200)*150}" r="5" fill="#4f46e5" stroke="#ffffff" stroke-width="1.5" />
            <circle cx="105" cy="${170 - (data[1]/200)*150}" r="5" fill="#4f46e5" stroke="#ffffff" stroke-width="1.5" />
            <circle cx="180" cy="${170 - (data[2]/200)*150}" r="5" fill="#4f46e5" stroke="#ffffff" stroke-width="1.5" />
            <circle cx="255" cy="${170 - (data[3]/200)*150}" r="5" fill="#4f46e5" stroke="#ffffff" stroke-width="1.5" />
            <circle cx="330" cy="${170 - (data[4]/200)*150}" r="5" fill="#4f46e5" stroke="#ffffff" stroke-width="1.5" />
            <circle cx="405" cy="${170 - (data[5]/200)*150}" r="5" fill="#4f46e5" stroke="#ffffff" stroke-width="1.5" />
            <circle cx="480" cy="${170 - (data[6]/200)*150}" r="5" fill="#4f46e5" stroke="#ffffff" stroke-width="1.5" />
            
            <!-- X Axis Labels -->
            <text x="30" y="195" fill="#94a3b8" font-size="10" text-anchor="middle" font-family="'Inter', sans-serif">Mon</text>
            <text x="105" y="195" fill="#94a3b8" font-size="10" text-anchor="middle" font-family="'Inter', sans-serif">Tue</text>
            <text x="180" y="195" fill="#94a3b8" font-size="10" text-anchor="middle" font-family="'Inter', sans-serif">Wed</text>
            <text x="255" y="195" fill="#94a3b8" font-size="10" text-anchor="middle" font-family="'Inter', sans-serif">Thu</text>
            <text x="330" y="195" fill="#94a3b8" font-size="10" text-anchor="middle" font-family="'Inter', sans-serif">Fri</text>
            <text x="405" y="195" fill="#94a3b8" font-size="10" text-anchor="middle" font-family="'Inter', sans-serif">Sat</text>
            <text x="480" y="195" fill="#94a3b8" font-size="10" text-anchor="middle" font-family="'Inter', sans-serif">Sun</text>
            
            <!-- Y Axis Labels -->
            <text x="20" y="173" fill="#94a3b8" font-size="10" text-anchor="end" font-family="'Inter', sans-serif">0</text>
            <text x="20" y="123" fill="#94a3b8" font-size="10" text-anchor="end" font-family="'Inter', sans-serif">50</text>
            <text x="20" y="73" fill="#94a3b8" font-size="10" text-anchor="end" font-family="'Inter', sans-serif">100</text>
            <text x="20" y="23" fill="#94a3b8" font-size="10" text-anchor="end" font-family="'Inter', sans-serif">150</text>
        </svg>
    `;
    
    canvasContainer.innerHTML = svgContent;
}
