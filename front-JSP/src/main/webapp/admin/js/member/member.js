let idChecked = false;

// 아이디 중복체크
async function checkId() {
    const loginId = document.getElementById('loginId').value;
    if (!loginId) {
        alert("아이디를 입력해주세요.");
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/members/check-id?loginId=${loginId}`, {
            method: 'GET'
        });
        const data = await response.json();

        if (data) {
            alert("사용 가능한 아이디입니다.");
            idChecked = true;
        } else {
            alert("이미 사용 중인 아이디입니다.");
            idChecked = false;
        }
    } catch (e) {
        alert("중복확인 중 오류가 발생했습니다.");
    }
}

// 회원가입
async function join() {
    if (!idChecked)     { alert("아이디 중복확인을 해주세요."); return; }
    if (!validateRequired(document.getElementById('loginId').value,   "아이디를 입력해주세요."))   return;
    if (!validateRequired(document.getElementById('password').value,  "비밀번호를 입력해주세요.")) return;
    if (!validateRequired(document.getElementById('name').value,      "이름을 입력해주세요."))     return;
    if (!validateRequired(document.getElementById('gender').value,    "성별을 선택해주세요."))     return;
    if (!validateRequired(document.getElementById('address').value,   "주소를 입력해주세요."))     return;
    if (!validateBirthDate(document.getElementById('birthDate').value))          return;
    if (!validatePhone(document.getElementById('phone').value))                  return;
    if (!validateEmail(document.getElementById('email').value))                  return;

    try {
        const response = await fetch(`${API_BASE_URL}/api/members`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                loginId  : document.getElementById('loginId').value,
                password : document.getElementById('password').value,
                name     : document.getElementById('name').value,
                birthDate: document.getElementById('birthDate').value,
                gender   : document.getElementById('gender').value,
                phone    : document.getElementById('phone').value,
                address  : document.getElementById('address').value,
                email    : document.getElementById('email').value
            })
        });

        if (response.ok) {
            alert("회원가입이 완료되었습니다.");
            window.location.href = 'admin.jsp';
        } else {
            const error = await response.json();
            alert(error.message || "회원가입 중 오류가 발생했습니다.");
        }
    } catch (e) {
        alert("회원가입 중 오류가 발생했습니다.");
    }
}

// 로그인
async function login() {
    const loginId = document.getElementById('ad_id').value;
    const password = document.getElementById('ad_pw').value;

    if (!loginId) { alert("아이디를 입력해주세요."); return; }
    if (!password) { alert("비밀번호를 입력해주세요."); return; }

    try {
        const response = await fetch(`${API_BASE_URL}/api/members/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ loginId, password })
        });

        const data = await response.json();

        if (response.ok && data.token) {
            localStorage.setItem('token', data.token);
            alert("로그인 되었습니다.");
            window.location.href = 'index.jsp?type=counsel&comm=select';
        } else {
            alert("아이디 또는 비밀번호를 다시 확인하세요.");
        }
    } catch (e) {
        alert("아이디 또는 비밀번호를 다시 확인하세요.");
    }
}