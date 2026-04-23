// ===== Validation =====
function validatePhone(phone) {
    if (!phone) {
        alert("휴대폰번호를 입력해주세요.");
        return false;
    }
    if (!/^01[0-9]{8,9}$/.test(phone)) {
        alert("휴대폰번호 형식이 올바르지 않습니다.\n예) 01012341234");
        return false;
    }
    return true;
}

function validateEmail(email) {
    if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        alert("이메일 형식이 올바르지 않습니다.\n예) example@email.com");
        return false;
    }
    return true;
}

function validateRequired(value, message) {
    if (!value) {
        alert(message);
        return false;
    }
    return true;
}

function validateBirthDate(birthDate) {
    if (!birthDate) {
        alert("생년월일을 입력해주세요.");
        return false;
    }
    if (!/^\d{4}-\d{2}-\d{2}$/.test(birthDate)) {
        alert("생년월일 형식이 올바르지 않습니다.\n예) 1990-01-01");
        return false;
    }
    return true;
}

// ===== 파일 업로드 =====
function checkFile(el, limit) {
    var file = el.files[0];
    if (!file) return;

    var allowedExt = ["jpg", "jpeg", "png", "gif", "webp", "pdf"];
    var fileName = file.name.toLowerCase();
    var lastDotIndex = fileName.lastIndexOf(".");
    var ext = lastDotIndex > -1 ? fileName.substring(lastDotIndex + 1) : "";

    if (!ext || !allowedExt.includes(ext)) {
        alert("jpg, jpeg, png, gif, webp, pdf 파일만 업로드 가능합니다.");
        el.outerHTML = el.outerHTML;
        return false;
    }

    if (file.size > 1024 * 1024 * limit) {
        alert(limit + "MB 이하 파일만 등록할 수 있습니다.");
        el.outerHTML = el.outerHTML;
        return false;
    }

    return true;
}

// ===== URL 이동 =====
function goToURL(type, comm, boardNum) {
    let url = "index.jsp?type=" + encodeURIComponent(type) + "&comm=" + encodeURIComponent(comm);
    if (boardNum) {
        url += "&board_num=" + encodeURIComponent(boardNum);
    }
    location.href = url;
}

// ===== 삭제 =====
function deleteFunc() {
    const checked = document.querySelectorAll('input[name="check"]:checked');
    if (checked.length === 0) {
        alert("삭제할 항목을 선택해주세요.");
        return;
    }
    if (!confirm("정말 삭제하시겠습니까?")) return;
    $("#deleteFrm").submit();
}

// ===== 전체선택 =====
$(document).ready(function () {
    $("#checkall").click(function () {
        $("input[name=check]").prop("checked", $(this).prop("checked"));
    });
});