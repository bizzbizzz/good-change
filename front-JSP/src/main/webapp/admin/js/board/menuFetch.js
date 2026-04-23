function updateMenu() {
    const menuNo    = document.getElementById("board_num").value;
    const mName     = document.getElementById("mName").value.trim();
    const mUrl      = document.getElementById("mUrl").value.trim();
    const sortOrder = document.getElementById("sortOrder").value.trim();
    const mUse      = document.getElementById("mUse").value;

    if(mName === "") {
        alert("메뉴명을 입력해주세요.");
        return false;
    }

    fetch("subMain/menu/dbProc/updateMenu.jsp", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `menuNo=${menuNo}&mName=${encodeURIComponent(mName)}&mUrl=${encodeURIComponent(mUrl)}&sortOrder=${sortOrder}&mUse=${mUse}`
    })
    .then(res => res.json())
    .then(data => {
        if(data.result === "success") {
            alert("수정되었습니다.");
            goToURL("menu", "select");
        } else {
            alert("수정에 실패했습니다.");
        }
    })
    .catch(err => console.error(err));
}


function insertMenu() {
    const parentNo  = document.getElementById("parentNo").value;
    const mName     = document.getElementById("mName").value.trim();
    const mUrl      = document.getElementById("mUrl").value.trim();
    const sortOrder = document.getElementById("sortOrder").value.trim();
    const mUse      = document.getElementById("mUse").value;

    if(mName === "") {
        alert("메뉴명을 입력해주세요.");
        return false;
    }

    fetch("subMain/menu/dbProc/insertMenu.jsp", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `parentNo=${parentNo}&mName=${encodeURIComponent(mName)}&mUrl=${encodeURIComponent(mUrl)}&sortOrder=${sortOrder}&mUse=${mUse}`
    })
    .then(res => res.json())
    .then(data => {
        if(data.result === "success") {
            alert("등록되었습니다.");
            goToURL("menu", "select");
        } else {
            alert("등록에 실패했습니다.");
        }
    })
    .catch(err => console.error(err));
}

function updateMenu() {
    const menuNo    = document.getElementById("board_num").value;
    const mName     = document.getElementById("mName").value.trim();
    const mUrl      = document.getElementById("mUrl").value.trim();
    const sortOrder = document.getElementById("sortOrder").value.trim();
    const mUse      = document.getElementById("mUse").value;

    if(mName === "") {
        alert("메뉴명을 입력해주세요.");
        return false;
    }

    fetch("subMain/menu/dbProc/updateMenu.jsp", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `menuNo=${menuNo}&mName=${encodeURIComponent(mName)}&mUrl=${encodeURIComponent(mUrl)}&sortOrder=${sortOrder}&mUse=${mUse}`
    })
    .then(res => res.json())
    .then(data => {
        if(data.result === "success") {
            alert("수정되었습니다.");
            goToURL("menu", "select");
        } else {
            alert("수정에 실패했습니다.");
        }
    })
    .catch(err => console.error(err));
}


function deleteMenu(menuNo) {
    if(!confirm("삭제하시겠습니까?")) return false;

    fetch("subMain/menu/dbProc/deleteMenu.jsp", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `menuNo=${menuNo}`
    })
    .then(res => res.json())
    .then(data => {
        if(data.result === "success") {
            alert("삭제되었습니다.");
            goToURL("menu", "select");
        } else {
            alert("삭제에 실패했습니다.");
        }
    })
    .catch(err => console.error(err));
}